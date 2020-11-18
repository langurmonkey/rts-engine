package com.ts.rts.scene.unit.state;

import com.ts.rts.scene.unit.Unit;

/**
 * Represents a unit state.
 *
 * @author Toni Sagrista
 */
public interface IState {

    boolean isDone();

    void process();

    Unit getUnit();

    /**
     * Cleans the unit of this state.
     */
    void removeState();
}
