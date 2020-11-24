package rts.arties.scene.ecs.system;

import rts.arties.datastructure.IMapCell;
import rts.arties.datastructure.geom.Vector3;
import rts.arties.scene.ecs.Mapper;
import rts.arties.scene.ecs.component.*;
import rts.arties.scene.unit.state.IState;
import rts.arties.scene.unit.steeringbehaviour.IEntity;
import rts.arties.util.Vector3Pool;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

public class UnitUpdateSystem extends IteratingSystem {
    public UnitUpdateSystem(Family family, int priority) {
        super(family, priority);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        updateStates(deltaTime, entity);
        updatePosition(deltaTime, entity);
    }

    private void updateStates(float deltaTime, Entity entity) {
        StateComponent stc = Mapper.state.get(entity);
        stc.stateTime += deltaTime;
        IState state = stc.stateManager.getCurrentState();
        if (state != null) {
            if (!state.isDone()) {
                state.process();
            } else {
                stc.stateManager.moveToNextState();
            }
        }
    }


    private void updatePosition(float deltaTime, Entity entity) {
        PositionComponent pc = Mapper.position.get(entity);
        MovementComponent mc = Mapper.movement.get(entity);
        BodyComponent bc = Mapper.body.get(entity);
        MapComponent mpc = Mapper.map.get(entity);
        SteeringComponent sc = Mapper.steering.get(entity);
        RenderableBaseComponent rbc = Mapper.rbase.get(entity);

        // PHYSICAL MOVEMENT IMPLEMENTATION
        // First, get terrain type
        IMapCell<IEntity> cell = mpc.map.getCell(pc.pos.x, pc.pos.y);

        if (!mc.turning) {
            Vector3 steeringForce = sc.steeringBehaviours.calculate().truncate(mc.maxForce);
            if (!steeringForce.isZero()) {
                mc.moving = true;

                float maxSpeedCell = mc.maxSpeed;
                if (cell != null) {
                    maxSpeedCell *= (1f - cell.getSlowdown());
                }

                Vector3 acceleration = Vector3Pool.getObject(steeringForce.scl(1f / bc.mass));
                mc.vel.add(acceleration.scl(deltaTime)).limit(maxSpeedCell);
                pc.pos.add(acceleration.set(mc.vel).scl(deltaTime));
                Vector3Pool.returnObject(acceleration);

                // Update heading if the vehicle has a velocity greater than a very small value
                if (mc.vel.len2() > 0.00000001f) {
                    sc.targetHeading = Vector3Pool.getObject(mc.vel).nor();

                    float currentTurn = sc.targetHeading.angle2Rad(mc.heading);
                    float maxTurn = mc.maxTurnRate * deltaTime;

                    if (currentTurn > maxTurn) {
                        // Approach heading to targetHeading
                        mc.turning = true;
                    } else {
                        Vector3Pool.returnObject(mc.heading);
                        mc.heading = sc.targetHeading;
                    }
                }
            } else {
                mc.vel.setZero();
                mc.moving = false;
            }
            Vector3Pool.returnObject(steeringForce);
        }

        if (mc.turning) {
            // We're turning, update heading
            updateHeading(deltaTime, mc, sc);
        }

        // Update height
        pc.pos.z = cell != null ? cell.z(pc.pos.x, pc.pos.y) : 0;

        updateBounds(pc.pos.x, pc.pos.y, bc, rbc);

        // Update entity in map if distance to last update is bigger than the unit's width
        if (Math.sqrt(Math.pow(pc.pos.x - pc.lastPos.x, 2) + Math.pow(pc.pos.y - pc.lastPos.y, 2)) > bc.width / 4f) {
            mpc.map.updateEntity(bc.me);
            // Update last pos
            pc.lastPos.set(pc.pos);
            mpc.map.updateFogOfWar(pc.pos, (int) (pc.viewingDistance));
        }

        // Update behaviour list
        sc.steeringBehaviours.removeDoneBehaviours();

    }


    private void updateHeading(float deltaTime, MovementComponent mc, SteeringComponent sc) {
        // We're turning, update heading

        if (false && mc.maxTurnRate >= Math.PI * 10) {
            // Optimisation, instant rotation
            Vector3Pool.returnObject(mc.heading);
            mc.heading = sc.targetHeading;
            mc.turning = false;
            return;
        }

        float angle = mc.heading.angle2();
        float targetAngle = sc.targetHeading.angle2();
        float maxTurn = (float) (Math.toDegrees(mc.maxTurnRate) * deltaTime);

        // Find out which side we're going
        float distNeg1 = 360 - targetAngle + angle;
        float distNeg2 = angle - targetAngle;
        distNeg1 = (distNeg1 < 0) ? Float.MAX_VALUE : distNeg1;
        distNeg2 = (distNeg2 < 0) ? Float.MAX_VALUE : distNeg2;
        float distNeg = Math.min(distNeg1, distNeg2);

        float distPos1 = targetAngle - angle;
        float distPos2 = 360 - angle + targetAngle;
        distPos1 = (distPos1 < 0) ? Float.MAX_VALUE : distPos1;
        distPos2 = (distPos2 < 0) ? Float.MAX_VALUE : distPos2;
        float distPos = Math.min(distPos1, distPos2);

        if (distNeg < distPos) {
            // Turning counter-clockwise
            maxTurn = -maxTurn;
        } else {
            // Turning clockwise
        }

        float futureAngle = angle + maxTurn;
        if (futureAngle < 0) {
            angle = 360f + angle;
            futureAngle = 360 + futureAngle;
        } else if (futureAngle > 360) {
            angle = 360 - angle;
            futureAngle = futureAngle - 360;
        }

        if ((futureAngle % 360 > targetAngle && angle < targetAngle) || (futureAngle % 360 < targetAngle & angle > targetAngle)) {
            // Finish
            Vector3Pool.returnObject(mc.heading);
            mc.heading = sc.targetHeading;
            mc.turning = false;
        } else {
            // Advance step
            mc.heading.rotate2(maxTurn);
        }

    }

    /**
     * Updates the bounds position with the given position and the angle given the offset
     */
    private void updateBounds(float x, float y, BodyComponent bc, RenderableBaseComponent rbc) {
        // Update hard and soft radius and image bounds
        bc.hardRadius.setX(x - bc.hardRadius.width / 2);
        bc.hardRadius.setY(y - bc.hardRadius.height / 2);
        rbc.imageBounds.setX(x - rbc.imageBounds.width / 2 + rbc.spriteOffsetX);
        rbc.imageBounds.setY(y - rbc.imageBounds.height / 2 + rbc.spriteOffsetY);
    }
}
