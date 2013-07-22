package com.ts.rts.scene.unit.steeringbehaviour;

import java.util.Random;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.ts.rts.datastructure.geom.Vector2;
import com.ts.rts.scene.unit.Unit;
import com.ts.rts.util.VectorPool;

/**
 * Creates a random walk through the agent's environment.
 * 
 * @author Toni Sagrista
 * 
 */
public class BehaviourWander extends AbstractSteeringBehaviour {
	private static final Random rand = new Random(2342456778l);

	float wanderRadius = 30f, wanderDistance = 40f, wanderJitter = 10f;
	float wanderAngle = 0f;
	Vector2 circleCenter, displacement;

	public BehaviourWander(Unit unit) {
		super(unit);
		displacement = VectorPool.getObject(1, 0);
	}

	@Override
	public Vector2 calculate() {
		return impl2();
	}

	private Vector2 impl2() {
		// Calculate circle center
		circleCenter = unit.heading.clone().normalise().multiply(wanderDistance);

		// Randomly change the vector direction
		// by making it change its current angle
		displacement.setAngle(wanderAngle);
		displacement.normalise().multiply(wanderRadius);
		//
		// Change wanderAngle just a bit, so it
		// won't have the same value in the
		// next game frame.
		wanderAngle += Math.toRadians(nextRand() * wanderJitter);

		return circleCenter.clone().add(displacement);
	}

	private static float nextRand() {
		return rand.nextFloat() * 2 - 1f;
	}

	@Override
	public void renderBehaviour() {
		if (circleCenter != null && displacement != null) {
			// Draw circle
			shapeRenderer.begin(ShapeType.Line);
			shapeRenderer.setColor(new Color(1f, 1f, 1f, 1f));
			shapeRenderer.circle(unit.pos.x + circleCenter.x, unit.pos.y + circleCenter.y, wanderRadius);
			shapeRenderer.end();

			// Draw displacement
			shapeRenderer.begin(ShapeType.Line);
			shapeRenderer.setColor(new Color(0f, 1f, 0f, 1f));
			shapeRenderer.line(unit.pos.x + circleCenter.x, unit.pos.y + circleCenter.y, unit.pos.x + circleCenter.x
					+ displacement.x, unit.pos.y + circleCenter.y + displacement.y);
			shapeRenderer.end();

			// Draw steering direction
			shapeRenderer.begin(ShapeType.Line);
			shapeRenderer.setColor(new Color(1f, 0f, 0f, 1f));
			shapeRenderer.line(unit.pos.x, unit.pos.y, unit.pos.x + circleCenter.x + displacement.x, unit.pos.y
					+ circleCenter.y + displacement.y);
			shapeRenderer.end();

		}
	}

	@Override
	public void dispose() {
		VectorPool.returnObject(displacement);
	}

}
