package arties.scene.unit.state;

import arties.scene.unit.steeringbehaviour.IEntity;

/**
 * Represents a unit state.
 *
 * @author Toni Sagrista
 */
public interface IState {

    boolean isDone();

    void process();

    IEntity getEntity();

    /**
     * Cleans the unit of this state.
     */
    void removeState();
}
