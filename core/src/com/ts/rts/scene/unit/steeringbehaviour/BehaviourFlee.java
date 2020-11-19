package com.ts.rts.scene.unit.steeringbehaviour;

import com.ts.rts.datastructure.geom.Vector2;
import com.ts.rts.datastructure.geom.Vector3;
import com.ts.rts.scene.unit.MovingEntity;
import com.ts.rts.util.Vector3Pool;

/**
 * Flee produces a steering force to steer the agent away from a position.
 *
 * @author Toni Sagrista
 */
public class BehaviourFlee extends AbstractSteeringBehaviour {
    private static final float panicDistanceSq = 100f * 100f;

    private final Vector3 targetPosition;

    public BehaviourFlee(MovingEntity unit, Vector3 targetPosition) {
        super(unit);
        this.targetPosition = targetPosition;
    }

    @Override
    public Vector3 calculate() {
        // Only flee if the target is within panic distance
        if (unit.pos.dst2(targetPosition) > panicDistanceSq) {
            return Vector3Pool.getObject();
        } else {
            Vector3 desiredVelocity = unit.pos.clone().sub(targetPosition).nor().scl(unit.maxSpeed);
            return desiredVelocity.sub(unit.vel);
        }
    }

    public void updateTarget(Vector3 newTarget) {
        targetPosition.set(newTarget);
    }
}
