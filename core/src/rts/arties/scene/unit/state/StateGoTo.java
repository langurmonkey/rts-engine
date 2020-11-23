package rts.arties.scene.unit.state;

import rts.arties.datastructure.geom.Vector2;
import rts.arties.scene.unit.steeringbehaviour.IEntity;

/**
 * In this state the unit must move to a given position.
 *
 * @author Toni Sagrista
 */
public class StateGoTo extends AbstractState {
    private final float doneDistanceSq;

    private final Vector2 target;

    public StateGoTo(IEntity unit, Vector2 target) {
        super(unit);
        this.doneDistanceSq = 7 * 7;
        this.target = target;

        // Calculate path and add follow path steering behaviour to unit

    }

    @Override
    public boolean isDone() {
        return unit.pos().dst2(target) < doneDistanceSq;
    }

    @Override
    public void process() {
        // Check if unit is stuck, if so, recalculate path avoiding other units or relax done distance

    }

    @Override
    public void removeState() {
        // Remove follow path behaviour
    }

}
