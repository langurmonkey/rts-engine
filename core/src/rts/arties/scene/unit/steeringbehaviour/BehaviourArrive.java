package rts.arties.scene.unit.steeringbehaviour;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import rts.arties.datastructure.geom.Vector3;
import rts.arties.util.Vector3Pool;
import rts.arties.util.color.ColorUtils;

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

    public BehaviourArrive(IEntity unit, Vector3 targetPosition) {
        super(unit);
        this.targetPosition = targetPosition;
        this.pos = unit.pos();
    }

    @Override
    public Vector3 calculate() {
        return impl2();
    }

    private Vector3 impl2() {
        desiredVelocity = Vector3Pool.getObject(targetPosition).sub(pos);
        float distance = desiredVelocity.len();
        if (distance < unit.slowingDistance()) {
            // Inside the slowing area
            // desired_velocity = normalize(desired_velocity) * max_velocity * (distance / slowingRadius)
            desiredVelocity.nor().scl(unit.maxForce() * distance / unit.slowingDistance());
        } else {
            // Outside the slowing area.
            // desired_velocity = normalize(desired_velocity) * max_velocity
            desiredVelocity.nor().scl(unit.maxForce());
        }
        return desiredVelocity.sub(unit.vel());
    }

    @Override
    public boolean isDone() {
        return pos.dst2(targetPosition) < doneDistanceSq;
    }

    private Color slowDistCol =new Color(.3f, .3f, 1f, .7f);
    @Override
    public void renderLine(ShapeRenderer sr) {
        if (desiredVelocity != null) {
            // Target position circle
            sr.setColor(ColorUtils.gGreenC);
            sr.circle(targetPosition.x, targetPosition.y, 3);

            // Slowing distance circle
            sr.setColor(slowDistCol);
            sr.circle(targetPosition.x, targetPosition.y, unit.slowingDistance());

            // Velocity line
            sr.setColor(ColorUtils.gBlueC);
            sr.line(pos.x, pos.y, pos.x + desiredVelocity.x, pos.y + desiredVelocity.y);
        }
    }

}
