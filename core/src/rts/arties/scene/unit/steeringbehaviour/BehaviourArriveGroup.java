package rts.arties.scene.unit.steeringbehaviour;

import rts.arties.datastructure.geom.Vector3;

/**
 * This steering behaviour drives a group to a gentle stop on the target.
 *
 * @author Toni Sagrista
 */
public class BehaviourArriveGroup extends BehaviourArrive {

    public BehaviourArriveGroup(IEntity unit, Vector3 targetPosition, IGroup group) {
        super(unit, targetPosition);
        this.pos = group.pos();
    }

}
