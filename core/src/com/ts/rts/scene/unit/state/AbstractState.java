package com.ts.rts.scene.unit.state;

import com.ts.rts.scene.unit.Unit;

/**
 * Abstract implementation common to all states.
 * 
 * @author Toni Sagrista
 * 
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
