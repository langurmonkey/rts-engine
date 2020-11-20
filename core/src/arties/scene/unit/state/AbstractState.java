package arties.scene.unit.state;

import arties.scene.unit.Unit;

/**
 * Abstract implementation common to all states.
 *
 * @author Toni Sagrista
 */
public abstract class AbstractState implements IState {

    protected Unit unit;

    public AbstractState(Unit unit) {
        this.unit = unit;
    }

    @Override
    public Unit getUnit() {
        return unit;
    }

}
