package com.ts.rts.scene.unit.steeringbehaviour;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.ts.rts.datastructure.geom.Vector2;
import com.ts.rts.scene.unit.MovingEntity;
import com.ts.rts.util.VectorPool;

/**
 * Returns a force that directs the agent toward a target position.
 *
 * @author Toni Sagrista
 */
public class BehaviourSeek extends AbstractSteeringBehaviour {
    private final Vector2 targetPosition;
    private Vector2 desiredVelocity;

    public BehaviourSeek(MovingEntity unit, Vector2 targetPosition) {
        super(unit);
        this.targetPosition = targetPosition;
    }

    @Override
    public Vector2 calculate() {
        desiredVelocity = targetPosition.clone().subtract(unit.pos).normalise().multiply(unit.maxForce);
        return desiredVelocity.subtract(unit.vel);
    }

    public ISteeringBehaviour updateTarget(Vector2 newTarget) {
        targetPosition.set(newTarget);
        return this;
    }

    @Override
    public void renderBehaviour() {
        if (desiredVelocity != null) {
            shapeRenderer.begin(ShapeType.Line);
            shapeRenderer.setColor(new Color(0f, 1f, 0f, 1f));
            shapeRenderer.circle(targetPosition.x, targetPosition.y, 3);
            shapeRenderer.end();

            shapeRenderer.begin(ShapeType.Line);
            shapeRenderer.setColor(new Color(0f, 0f, 1f, 1f));
            shapeRenderer.line(unit.pos.x, unit.pos.y, unit.pos.x + desiredVelocity.x, unit.pos.y + desiredVelocity.y);
            shapeRenderer.end();

        }
    }

    @Override
    public void dispose() {
        VectorPool.returnObject(targetPosition);
    }
}
