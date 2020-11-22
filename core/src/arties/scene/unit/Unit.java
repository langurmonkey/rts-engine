package arties.scene.unit;

import arties.datastructure.IMapCell;
import arties.datastructure.geom.Vector3;
import arties.scene.map.IRTSMap;
import arties.scene.unit.group.UnitGroup;
import arties.scene.unit.state.IState;
import arties.scene.unit.state.StateManager;
import arties.scene.unit.steeringbehaviour.IEntity;
import arties.scene.unit.steeringbehaviour.SteeringBehaviours;
import arties.util.Vector3Pool;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * A moving entity.
 * Rough conversion: 10px = 1m
 *
 * @author Toni Sagrista
 */
public abstract class Unit extends MovingEntity {

    /**
     * Metre to pixel conversion
     **/
    public static final float M_TO_PX = 5;

    /**
     * Pixel to metre conversion
     **/
    public static final float PX_TO_M = 1 / M_TO_PX;

    /**
     * Steering behaviours
     **/
    public SteeringBehaviours steeringBehaviours;

    public Vector3 targetHeading;

    protected boolean turning = false;

    /**
     * The unit group it belongs to, if any
     **/
    public UnitGroup group;

    /**
     * Is it selected?
     */
    protected boolean selected;

    /**
     * The radius of the selection circumference
     **/
    protected float selectionRadius;

    public StateManager stateManager;

    /**
     * State time in seconds
     **/
    protected float stateTime = 0f;

    /**
     * Creates a new unit with the given position in the map
     *
     * @param x
     * @param y
     * @param map
     */
    public Unit(float x, float y, float z, IRTSMap map) {
        super(x, y, z);
        this.map = map;
        this.steeringBehaviours = new SteeringBehaviours(this);
        this.stateManager = new StateManager(this);
        this.lastPos = new Vector3(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);

        steeringBehaviours.addSeparation(map);
        steeringBehaviours.addAvoidWalls(map);
    }

    public abstract String getName();

    public boolean isSelected() {
        return selected;
    }

    public void toggleSelection() {
        selected = !selected;
    }

    public void select() {
        selected = true;
    }

    public void unselect() {
        selected = false;
    }

    /**
     * The default behaviour for a unit is to only update the position
     */
    @Override
    public void update(float deltaSecs) {
        stateTime += deltaSecs;
        updateStates(deltaSecs);
        updatePosition(deltaSecs);
    }

    public void updateStates(float deltaSecs) {
        IState state = stateManager.getCurrentState();
        if (state != null) {
            if (!state.isDone()) {
                state.process();
            } else {
                stateManager.moveToNextState();
            }
        }
    }

    public void updatePosition(float deltaSecs) {
        /** PHYSICAL MOVEMENT IMPLEMENTATION **/
        // First, get terrain type
        IMapCell<IEntity> cell = map.getCell(pos.x, pos.y);

        if (!turning) {
            Vector3 steeringForce = steeringBehaviours.calculate().truncate(maxForce);
            if (!steeringForce.isZero()) {
                moving = true;

                float maxSpeedCell = maxSpeed;
                if (cell != null) {
                    maxSpeedCell *= (1f - cell.getSlowdown());
                }

                Vector3 acceleration = Vector3Pool.getObject(steeringForce.scl(1f / mass));
                vel.add(acceleration.scl(deltaSecs)).limit(maxSpeedCell);
                pos.add(acceleration.set(vel).scl(deltaSecs));
                Vector3Pool.returnObject(acceleration);

                // Update heading if the vehicle has a velocity greater than a very small value
                if (vel.len2() > 0.00000001f) {
                    targetHeading = Vector3Pool.getObject(vel).nor();

                    float currentTurn = targetHeading.angle2Rad(heading);
                    float maxTurn = maxTurnRate * deltaSecs;

                    if (currentTurn > maxTurn) {
                        // Approach heading to targetHeading
                        turning = true;
                    } else {
                        Vector3Pool.returnObject(heading);
                        heading = targetHeading;
                    }
                }
            } else {
                vel.setZero();
                moving = false;
            }
            Vector3Pool.returnObject(steeringForce);
        }

        if (turning) {
            // We're turning, update heading
            updateHeading(deltaSecs);
        }

        // Update height
        pos.z = cell != null ? cell.z(pos.x, pos.y) : 0;

        updateBounds(pos.x, pos.y);

        // Update entity in map if distance to last update is bigger than the unit's width
        if (Math.sqrt(Math.pow(pos.x - lastPos.x, 2) + Math.pow(pos.y - lastPos.y, 2)) > width / 4f) {
            map.updateEntity(this);
            touchLastUpdate();
            map.updateFogOfWar(pos, (int) (viewDistance));
        }

        // Update behaviour list
        steeringBehaviours.removeDoneBehaviours();
    }

    protected void updateHeading(float deltaSecs) {
        // We're turning, update heading

        if (maxTurnRate >= Math.PI * 2) {
            // Optimisation, instant rotation
            Vector3Pool.returnObject(heading);
            heading = targetHeading;
            turning = false;
            return;
        }

        float angle = heading.angle2();
        float targetAngle = targetHeading.angle2();
        float maxTurn = (float) (Math.toDegrees(maxTurnRate) * deltaSecs);

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
            Vector3Pool.returnObject(heading);
            heading = targetHeading;
            turning = false;
        } else {
            // Advance step
            heading.rotate2(maxTurn);
        }

    }

    @Override
    public void renderShapeFilledLayer0(ShapeRenderer sr) {
    }

    @Override
    public void renderShapeLineLayer1(ShapeRenderer sr) {
        if (selected) {
            // Selection box
            sr.setColor(new Color(0f, 0f, 0f, .9f));
            sr.circle(pos.x + spriteOffsetX, pos.y + spriteOffsetY, selectionRadius + 1);
            sr.setColor(new Color(1f, 1f, 1f, .8f));
            sr.circle(pos.x + spriteOffsetX, pos.y + spriteOffsetY, selectionRadius);
            sr.setColor(new Color(1f, 1f, 1f, .5f));
            sr.circle(pos.x + spriteOffsetX, pos.y + spriteOffsetY, selectionRadius - 1);
        }
    }

    int startx, starty;
    float healthLength;

    @Override
    public void renderShapeFilledLayer2(ShapeRenderer sr) {
        if (selected) {
            // Health bar
            startx = Math.round(pos.x - selectionRadius + spriteOffsetX);
            starty = Math.round(pos.y - selectionRadius + spriteOffsetY);
            healthLength = getHealthLength(selectionRadius * 2f);

            sr.setColor(new Color(0f, 0f, 0f, 1f));
            sr.rect(startx - 2f, starty - 1f, 3f, selectionRadius * 2f + 1f);
        }
    }

    @Override
    public void renderShapeLineLayer3(ShapeRenderer sr) {
        if (selected) {
            // Health outline
            float[] color = getHealthColor();
            sr.setColor(new Color(color[0], color[1], color[2], 1f));
            sr.line(startx, starty, startx, starty + healthLength - 1f);
        }
    }

    public void renderDebug(ShapeRenderer sr) {
        super.renderDebug(sr);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        steeringBehaviours.render();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    private void touchLastUpdate() {
        lastPos.set(pos);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (obj.getClass().equals(this.getClass())) {
            PositionEntity e = (PositionEntity) obj;
            return e.uniqueId == uniqueId;
        }
        return false;
    }

    /**
     * Updates the bounds position with the given position and the angle given the offset
     */
    public void updateBounds(float x, float y) {
        // Update hard and soft radius and image bounds
        hardRadius.setX(x - hardRadius.width / 2);
        hardRadius.setY(y - hardRadius.height / 2);
        imageBounds.setX(x - imageBounds.width / 2 + spriteOffsetX);
        imageBounds.setY(y - imageBounds.height / 2 + spriteOffsetY);
    }

    /**
     * Gets the length of the health bar normalized to max
     *
     * @param max
     * @return
     */
    protected float getHealthLength(float max) {
        return hp * max / maxHp;
    }

    protected float[] getHealthColor() {
        float norm = hp / maxHp;
        float[] color;
        if (norm > (2f / 3f)) {
            // Green
            color = new float[] { 0f, 1f, 0f };
        } else if (norm < (2f / 3f) && norm > (1f / 3f)) {
            // Yellow
            color = new float[] { 1f, 1f, 0f };
        } else {
            // Red
            color = new float[] { 1f, 0f, 0f };
        }
        return color;
    }

    @Override
    public String toString() {
        return getName() + " " + pos + " id" + uniqueId;
    }

    @Override
    public void dispose() {
        Vector3Pool.returnObjects(heading, vel);
        super.dispose();
    }

}
