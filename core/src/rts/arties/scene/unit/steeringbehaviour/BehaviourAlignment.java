package rts.arties.scene.unit.steeringbehaviour;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import rts.arties.datastructure.geom.Vector3;
import rts.arties.util.color.ColorUtils;

/**
 * Tries to align the unit with the movement of the group, given a target.
 *
 * @author Toni Sagrista
 */
public class BehaviourAlignment extends AbstractSteeringBehaviour {
    protected static final float doneDistanceSq = 5 * 5;

    private final IGroup group;
    private final Vector3 targetPosition;
    private Vector3 desiredVelocity;

    public BehaviourAlignment(IEntity unit, IGroup group, Vector3 targetPosition) {
        super(unit);
        this.group = group;
        this.targetPosition = targetPosition;
    }

    @Override
    public Vector3 calculate() {
        desiredVelocity = targetPosition.clone().sub(group.pos());
        return desiredVelocity;
    }

    @Override
    public boolean isDone() {
        return group.pos().dst2(targetPosition) < doneDistanceSq;
    }

    public void updateTarget(Vector3 newTarget) {
        targetPosition.set(newTarget);
    }

    @Override
    public void renderLine(ShapeRenderer sr) {
        if (desiredVelocity != null) {
            sr.setColor(new Color(ColorUtils.ddMagentaC));
            sr.line(group.pos().x, group.pos().y, group.pos().x + desiredVelocity.x, group.pos().y
                + desiredVelocity.y);
        }
    }

}
