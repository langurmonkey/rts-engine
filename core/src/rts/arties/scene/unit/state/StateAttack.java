package rts.arties.scene.unit.state;

import rts.arties.scene.unit.PositionPhysicalEntity;
import rts.arties.scene.unit.steeringbehaviour.IEntity;

/**
 * In this state the unit takes the necessary actions to attack a target, being it an entity or a position.
 *
 * @author Toni Sagrista
 */
public class StateAttack extends AbstractState {

    private final PositionPhysicalEntity target;

    public StateAttack(IEntity unit, PositionPhysicalEntity target) {
        super(unit);
        this.target = target;
    }

    @Override
    public boolean isDone() {
        // check target
        return target == null || target.isDead();
    }

    @Override
    public void process() {
        // TODO Auto-generated method stub

    }

    @Override
    public void removeState() {
        // TODO Auto-generated method stub

    }

}