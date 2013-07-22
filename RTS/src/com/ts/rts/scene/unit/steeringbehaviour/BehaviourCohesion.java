package com.ts.rts.scene.unit.steeringbehaviour;

import com.ts.rts.datastructure.geom.Vector2;
import com.ts.rts.scene.unit.MovingEntity;
import com.ts.rts.scene.unit.group.UnitGroup;

/**
 * The cohesion behavior is responsible for keeping the members of a group together. The vehicle is steered to the
 * average position of the neighboring vehicles. Cohesion is part of the flocking behavior.
 * 
 * @author Toni Sagrista
 * 
 */
public class BehaviourCohesion extends AbstractSteeringBehaviour {

    private UnitGroup group;
    private BehaviourSeek seek;

    public BehaviourCohesion(MovingEntity unit, UnitGroup group) {
	super(unit);
	this.group = group;
	this.seek = new BehaviourSeek(unit, group.pos.clone());
    }

    @Override
    public Vector2 calculate() {
	return seek.updateTarget(group.pos).calculate().multiply(.1f);
    }

    @Override
    public void renderBehaviour() {
	seek.renderBehaviour();
    }

}
