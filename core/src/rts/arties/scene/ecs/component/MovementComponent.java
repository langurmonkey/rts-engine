package rts.arties.scene.ecs.component;

import com.badlogic.ashley.core.Component;
import rts.arties.datastructure.geom.Vector3;
import rts.arties.util.Vector3Pool;

/**
 * Contains attributes pertaining to entity movement
 */
public class MovementComponent implements Component {
    // Velocity [m/s]
    public Vector3 vel = Vector3Pool.getObject(0f, 0f);

    // Heading vector
    public Vector3 heading = Vector3Pool.getObject(0, 1);

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

    public void updateMaxSpeed(HealthComponent hc){
        maxSpeed *= Math.max(hc.hp / hc.maxHp, 0.2f);
    }
}
