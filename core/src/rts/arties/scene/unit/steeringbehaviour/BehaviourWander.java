package rts.arties.scene.unit.steeringbehaviour;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import rts.arties.datastructure.geom.Vector3;
import rts.arties.util.Vector3Pool;
import rts.arties.util.color.ColorUtils;

import java.util.Random;

/**
 * Creates a random walk through the agent's environment.
 *
 * @author Toni Sagrista
 */
public class BehaviourWander extends AbstractSteeringBehaviour {
    private static final Random rand = new Random(2342456778l);

    float wanderRadius = 30f, wanderDistance = 50f, wanderJitter = 600f;
    float wanderAngle = 0f;
    Vector3 circleCenter, displacement;

    public BehaviourWander(IEntity unit) {
        super(unit);
        displacement = Vector3Pool.getObject(1, 0, 0).scl(wanderRadius);
    }

    @Override
    public Vector3 calculate() {
        return impl2();
    }

    private Vector3 impl2() {
        // Calculate circle center
        circleCenter = unit.heading().clone().nor().scl(wanderDistance);

        // Randomly change the vector direction
        // by making it change its current angle
        displacement.setAngle(wanderAngle);
        //displacement.normalise().multiply(wanderRadius);
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
    public void renderLine(ShapeRenderer sr) {
        if (circleCenter != null && displacement != null) {
            // Draw circle
            sr.setColor(ColorUtils.gWhiteC);
            sr.circle(unit.pos().x + circleCenter.x, unit.pos().y + circleCenter.y, wanderRadius);

            // Draw displacement
            sr.setColor(ColorUtils.gGreenC);
            sr.line(unit.pos().x + circleCenter.x, unit.pos().y + circleCenter.y, unit.pos().x + circleCenter.x
                + displacement.x, unit.pos().y + circleCenter.y + displacement.y);

            // Draw steering direction
            sr.setColor(ColorUtils.gRedC);
            sr.line(unit.pos().x, unit.pos().y, unit.pos().x + circleCenter.x + displacement.x, unit.pos().y
                + circleCenter.y + displacement.y);
        }
    }

    @Override
    public void dispose() {
        Vector3Pool.returnObject(displacement);
    }

}
