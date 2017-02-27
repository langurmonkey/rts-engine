package com.ts.rts.scene.unit.steeringbehaviour;

import com.ts.rts.datastructure.geom.Vector2;
import com.ts.rts.scene.unit.Unit;

/**
 * Creates a movement towards the interception of another unit.
 * 
 * @author Toni Sagrista
 * 
 */
public class BehaviourPursuit extends AbstractSteeringBehaviour {

	private Unit evader;

	public BehaviourPursuit(Unit unit, Unit evader) {
		super(unit);
		this.evader = evader;
	}

	@Override
	public Vector2 calculate() {
		Vector2 toEvader = evader.pos.clone().subtract(unit.pos);

		float relativeHeading = unit.heading.dotProduct(evader.heading);

		if (toEvader.dotProduct(unit.heading) > 0 && relativeHeading < -0.95) {
			// acos(0.95) = 18 degrees
			return new BehaviourSeek(unit, evader.pos.clone()).calculate();
		}

		// Not considered ahead so we predict where the evader will be

		/*
		 * The look-ahead time is proportional to the distance between the evader
		 * and the pursuer and it is inversely proportional to the sum of the agents' velocities
		 */
		float lookAheadTime = toEvader.length() / (unit.maxSpeed + evader.vel.length());

		// Now seek to the predicted future position of the evader
		return new BehaviourSeek(unit, evader.pos.clone().add(evader.vel.clone().multiply(lookAheadTime))).calculate();
	}

}
