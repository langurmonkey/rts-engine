package arties.scene.unit.steeringbehaviour;

import arties.datastructure.geom.Vector3;
import arties.scene.unit.MovingEntity;
import arties.scene.unit.group.UnitGroup;

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
