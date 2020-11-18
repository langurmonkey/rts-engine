package com.ts.rts.scene.unit.steeringbehaviour;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.ts.rts.datastructure.geom.Vector2;
import com.ts.rts.scene.unit.MovingEntity;

/**
 * Steers the agent in such a way that it decelerates onto the target position.
 *
 * @author Toni Sagrista
 */
public class BehaviourArrive extends AbstractSteeringBehaviour {
    protected static final float doneDistanceSq = 7 * 7;

    protected Vector2 targetPosition;
    protected Vector2 desiredVelocity;
    protected Vector2 pos;

    public BehaviourArrive(MovingEntity unit, Vector2 targetPosition) {
        super(unit);
        this.targetPosition = targetPosition;
        this.pos = unit.pos;
    }

    @Override
    public Vector2 calculate() {
        return impl2();
    }

    private Vector2 impl2() {
        desiredVelocity = targetPosition.clone().subtract(pos);
        float distance = desiredVelocity.length();
        if (distance < unit.slowingDistance) {
            // Inside the slowing area
            // desired_velocity = normalize(desired_velocity) * max_velocity * (distance / slowingRadius)
            desiredVelocity.normalise().multiply(unit.maxForce * distance / unit.slowingDistance);
        } else {
            // Outside the slowing area.
            // desired_velocity = normalize(desired_velocity) * max_velocity
            desiredVelocity.normalise().multiply(unit.maxForce);
        }
        return desiredVelocity.subtract(unit.vel);
    }

    @Override
    public boolean isDone() {
        return pos.distanceSq(targetPosition) < doneDistanceSq;
    }

    @Override
    public void renderBehaviour() {
        if (desiredVelocity != null) {
            // Target position circle
            shapeRenderer.begin(ShapeType.Line);
            shapeRenderer.setColor(new Color(0f, 1f, 0f, 1f));
            shapeRenderer.circle(targetPosition.x, targetPosition.y, 3);
            shapeRenderer.end();

            // Slowing distance circle
            shapeRenderer.begin(ShapeType.Line);
            shapeRenderer.setColor(new Color(.3f, .3f, 1f, .7f));
            shapeRenderer.circle(targetPosition.x, targetPosition.y, unit.slowingDistance);
            shapeRenderer.end();

            // Velocity line
            shapeRenderer.begin(ShapeType.Line);
            shapeRenderer.setColor(new Color(0f, 0f, 1f, 1f));
            shapeRenderer.line(pos.x, pos.y, pos.x + desiredVelocity.x, pos.y + desiredVelocity.y);
            shapeRenderer.end();

        }
    }

}
