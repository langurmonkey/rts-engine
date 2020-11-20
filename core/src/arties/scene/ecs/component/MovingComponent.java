package arties.scene.ecs.component;

import arties.datastructure.geom.Vector3;
import com.badlogic.ashley.core.Component;

/**
 * Contains attributes pertaining to entity movement
 */
public class MovingComponent implements Component {
    // Velocity [m/s]
    public Vector3 vel;

    // Heading vector
    public Vector3 heading;

    // Max speed [m/s]
    public float maxSpeed;

    // Max force [Kg*m/s^2]
    public float maxForce;

    // Maximum turn rate [rad/s]
    public float maxTurnRate;

    // Are we turning?
    public boolean turning = false;

    // This tells us if we're moving
    public boolean moving;

    // The distance from the target at which the unit starts to slow, for the arrive behaviour
    public float slowingDistance;

    float lastUpdateX, lastUpdateY;
}
