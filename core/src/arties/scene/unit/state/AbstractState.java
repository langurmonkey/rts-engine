package arties.scene.unit.state;

import arties.scene.unit.steeringbehaviour.IEntity;

/**
 * Abstract implementation common to all states.
 *
 * @author Toni Sagrista
 */
public abstract class AbstractState implements IState {

    protected IEntity unit;

    public AbstractState(IEntity unit) {
        this.unit = unit;
    }

    @Override
    public IEntity getEntity() {
        return unit;
    }

}
