package rts.arties.scene.unit.steeringbehaviour;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import rts.arties.datastructure.geom.Vector2;
import rts.arties.datastructure.geom.Vector3;
import rts.arties.util.Vector3Pool;
import rts.arties.util.color.ColorUtils;

/**
 * Returns a force that directs the agent toward a target position.
 *
 * @author Toni Sagrista
 */
public class BehaviourSeek extends AbstractSteeringBehaviour {
    private final Vector3 targetPosition;
    private Vector3 desiredVelocity;

    public BehaviourSeek(IEntity unit, Vector3 targetPosition) {
        super(unit);
        this.targetPosition = targetPosition;
    }

    @Override
    public Vector3 calculate() {
        desiredVelocity = targetPosition.clone().sub(unit.pos()).nor().scl(unit.maxForce());
        return desiredVelocity.sub(unit.vel());
    }

    public ISteeringBehaviour updateTarget(Vector2 newTarget) {
        return updateTarget(newTarget.x, newTarget.y);
    }

    public ISteeringBehaviour updateTarget(Vector3 newTarget) {
        return updateTarget(newTarget.x, newTarget.y);
    }

    public ISteeringBehaviour updateTarget(float x, float y) {
        targetPosition.set(x, y);
        return this;
    }

    @Override
    public void renderLine(ShapeRenderer sr) {
        if (desiredVelocity != null) {
            sr.setColor(ColorUtils.gGreenC);
            sr.circle(targetPosition.x, targetPosition.y, 3);

            sr.setColor(ColorUtils.gBlueC);
            sr.line(unit.pos().x, unit.pos().y, unit.pos().x + desiredVelocity.x, unit.pos().y + desiredVelocity.y);

        }
    }

    @Override
    public void dispose() {
        Vector3Pool.returnObject(targetPosition);
    }
}
