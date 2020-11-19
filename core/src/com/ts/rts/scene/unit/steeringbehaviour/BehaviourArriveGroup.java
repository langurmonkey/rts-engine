package com.ts.rts.scene.unit.steeringbehaviour;

import com.ts.rts.datastructure.geom.Vector2;
import com.ts.rts.datastructure.geom.Vector3;
import com.ts.rts.scene.unit.MovingEntity;
import com.ts.rts.scene.unit.group.UnitGroup;

/**
 * This steering behaviour drives a group to a gentle stop on the target.
 *
 * @author Toni Sagrista
 */
public class BehaviourArriveGroup extends BehaviourArrive {

    public BehaviourArriveGroup(MovingEntity unit, Vector3 targetPosition, UnitGroup group) {
        super(unit, targetPosition);
        this.pos = group.pos;
    }

}
