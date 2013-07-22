package com.ts.rts.scene.unit.steeringbehaviour;

import com.ts.rts.datastructure.geom.Vector2;
import com.ts.rts.scene.unit.MovingEntity;
import com.ts.rts.util.VectorPool;

/**
 * Flee produces a steering force to steer the agent away from a position.
 * 
 * @author Toni Sagrista
 * 
 */
public class BehaviourFlee extends AbstractSteeringBehaviour {
	private static final float panicDistanceSq = 100f * 100f;

	private Vector2 targetPosition;

	public BehaviourFlee(MovingEntity unit, Vector2 targetPosition) {
		super(unit);
		this.targetPosition = targetPosition;
	}

	@Override
	public Vector2 calculate() {
		// Only flee if the target is within panic distance
		if (unit.pos.distanceSq(targetPosition) > panicDistanceSq) {
			return VectorPool.getObject();
		} else {
			Vector2 desiredVelocity = unit.pos.clone().subtract(targetPosition).normalise().multiply(unit.maxSpeed);
			return desiredVelocity.subtract(unit.vel);
		}
	}

	public void updateTarget(Vector2 newTarget) {
		targetPosition.set(newTarget.x, newTarget.y);
	}
}
