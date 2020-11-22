package arties.scene.unit.steeringbehaviour;

import arties.datastructure.geom.Vector3;
import arties.scene.map.IRTSMap;
import com.badlogic.gdx.math.Rectangle;

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
    float slowingDistance();
    float maxSpeed();
    float maxForce();
    IRTSMap map();
    Rectangle bounds();
}