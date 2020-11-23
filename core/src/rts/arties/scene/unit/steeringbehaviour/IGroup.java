package rts.arties.scene.unit.steeringbehaviour;

import rts.arties.datastructure.geom.Vector3;

/**
 * A group of entities, it only has the central position of the group.
 */
public interface IGroup {
    Vector3 pos();
}
