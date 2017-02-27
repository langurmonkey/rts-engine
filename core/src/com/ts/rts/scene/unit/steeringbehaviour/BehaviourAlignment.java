package com.ts.rts.scene.unit.steeringbehaviour;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.ts.rts.datastructure.geom.Vector2;
import com.ts.rts.scene.unit.MovingEntity;
import com.ts.rts.scene.unit.group.UnitGroup;

/**
 * Tries to align the unit with the movement of the group, given a target.
 * 
 * @author Toni Sagrista
 * 
 */
public class BehaviourAlignment extends AbstractSteeringBehaviour {
	protected static final float doneDistanceSq = 5 * 5;

	private UnitGroup group;
	private Vector2 targetPosition;
	private Vector2 desiredVelocity;

	public BehaviourAlignment(MovingEntity unit, UnitGroup group, Vector2 targetPosition) {
		super(unit);
		this.group = group;
		this.targetPosition = targetPosition;
	}

	@Override
	public Vector2 calculate() {
		desiredVelocity = targetPosition.clone().subtract(group.pos);
		return desiredVelocity;
	}

	@Override
	public boolean isDone() {
		return group.pos.distanceSq(targetPosition) < doneDistanceSq;
	}

	public void updateTarget(Vector2 newTarget) {
		targetPosition.set(newTarget);
	}

	@Override
	public void renderBehaviour() {
		if (desiredVelocity != null) {
			shapeRenderer.begin(ShapeType.Line);
			shapeRenderer.setColor(new Color(1f, 0f, 1f, 1f));
			shapeRenderer.line(group.pos.x, group.pos.y, group.pos.x + desiredVelocity.x, group.pos.y
					+ desiredVelocity.y);
			shapeRenderer.end();
		}
	}

}
