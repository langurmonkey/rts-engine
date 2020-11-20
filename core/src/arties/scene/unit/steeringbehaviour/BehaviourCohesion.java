package arties.scene.unit.steeringbehaviour;

import arties.datastructure.geom.Vector3;
import arties.scene.unit.MovingEntity;
import arties.scene.unit.group.UnitGroup;
import arties.util.Vector3Pool;

/**
 * The cohesion behavior is responsible for keeping the members of a group together. The vehicle is steered to the
 * average position of the neighboring vehicles. Cohesion is part of the flocking behavior.
 *
 * @author Toni Sagrista
 */
public class BehaviourCohesion extends AbstractSteeringBehaviour {

    private final UnitGroup group;
    private final BehaviourSeek seek;

    public BehaviourCohesion(MovingEntity unit, UnitGroup group) {
        super(unit);
        this.group = group;
        this.seek = new BehaviourSeek(unit, Vector3Pool.getObject(group.pos));
    }

    @Override
    public Vector3 calculate() {
        return seek.updateTarget(group.pos.x, group.pos.y).calculate().scl(.1f);
    }

    @Override
    public void renderBehaviour() {
        seek.renderBehaviour();
    }

}
