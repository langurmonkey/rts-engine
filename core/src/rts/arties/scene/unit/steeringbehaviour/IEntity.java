package rts.arties.scene.unit.steeringbehaviour;

import rts.arties.datastructure.geom.Vector3;
import rts.arties.scene.map.IRTSMap;
import com.badlogic.gdx.math.Rectangle;
import rts.arties.scene.unit.group.UnitGroup;

/**
 * An entity subject to steering behaviours.
 */
public interface IEntity {
    // Current position
    Vector3 pos();
    // Current velocity
    Vector3 vel();
    // Current heading direction
    Vector3 heading();
    float softRadius();
    Rectangle hardRadius();
    float slowingDistance();
    float maxSpeed();
    float maxForce();
    IRTSMap map();
    Rectangle bounds();
    void group(UnitGroup group);
    UnitGroup group();
    void select();
    void unselect();
    SteeringBehaviours steeringBehaviours();
    float weight();
}
