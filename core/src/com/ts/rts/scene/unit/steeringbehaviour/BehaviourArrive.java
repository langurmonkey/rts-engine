package com.ts.rts.scene.unit.steeringbehaviour;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.ts.rts.datastructure.geom.Vector2;
import com.ts.rts.datastructure.geom.Vector3;
import com.ts.rts.scene.unit.MovingEntity;
import com.ts.rts.util.Vector2Pool;
import com.ts.rts.util.Vector3Pool;

/**
 * Steers the agent in such a way that it decelerates onto the target position.
 *
 * @author Toni Sagrista
 */
public class BehaviourArrive extends AbstractSteeringBehaviour {
    protected static final float doneDistanceSq = 7 * 7;

    protected Vector3 targetPosition;
    protected Vector3 desiredVelocity;
    protected Vector3 pos;

    public BehaviourArrive(MovingEntity unit, Vector3 targetPosition) {
        super(unit);
        this.targetPosition = targetPosition;
        this.pos = unit.pos;
    }

    @Override
    public Vector3 calculate() {
        return impl2();
    }

    private Vector3 impl2() {
        desiredVelocity = Vector3Pool.getObject(targetPosition).sub(pos);
        float distance = desiredVelocity.len();
        if (distance < unit.slowingDistance) {
            // Inside the slowing area
            // desired_velocity = normalize(desired_velocity) * max_velocity * (distance / slowingRadius)
            desiredVelocity.nor().scl(unit.maxForce * distance / unit.slowingDistance);
        } else {
            // Outside the slowing area.
            // desired_velocity = normalize(desired_velocity) * max_velocity
            desiredVelocity.nor().scl(unit.maxForce);
        }
        return desiredVelocity.sub(unit.vel);
    }

    @Override
    public boolean isDone() {
        return pos.dst2(targetPosition) < doneDistanceSq;
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
