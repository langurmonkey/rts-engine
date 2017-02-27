package com.ts.rts.scene.unit.steeringbehaviour;

import com.ts.rts.datastructure.geom.Vector2;
import com.ts.rts.scene.unit.MovingEntity;
import com.ts.rts.util.VectorPool;

/**
 * Same as BehaviourPursuit but now the evader flees from the estimated future position of the
 * pursuer.
 * 
 * @author Toni Sagrista
 * 
 */
public class BehaviourEvade extends AbstractSteeringBehaviour {

	private MovingEntity pursuer;

	public BehaviourEvade(MovingEntity unit, MovingEntity pursuer) {
		super(unit);
		this.pursuer = pursuer;
	}

	@Override
	public Vector2 calculate() {
		Vector2 toPursuer = pursuer.pos.clone().subtract(unit.pos);

		/*
		 * The look-ahead time is proportional to the distance between the pursuer
		 * and the evader and it is inversely proportional to the sum of the agents' velocities
		 */
		float lookAheadTime = toPursuer.length() / (unit.maxSpeed + pursuer.vel.length());
		VectorPool.returnObject(toPursuer);

		// Now flee from the predicted future position of the pursuer
		return new BehaviourFlee(unit, pursuer.pos.clone().add(pursuer.vel.clone().multiply(lookAheadTime)))
				.calculate();
	}

	@Override
	public void renderBehaviour() {

	}

}
