package com.ts.rts.scene.unit.state;

import com.ts.rts.scene.unit.Unit;

/**
 * Represents a unit state.
 * 
 * @author Toni Sagrista
 * 
 */
public interface IState {

	public boolean isDone();

	public void process();

	public Unit getUnit();

	/**
	 * Cleans the unit of this state.
	 */
	public void removeState();
}
