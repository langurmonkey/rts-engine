package arties.scene.unit.steeringbehaviour;

import arties.datastructure.geom.Vector3;
import arties.scene.unit.Unit;

/**
 * Creates a movement towards the interception of another unit.
 *
 * @author Toni Sagrista
 */
public class BehaviourPursuit extends AbstractSteeringBehaviour {

    private final Unit evader;

    public BehaviourPursuit(Unit unit, Unit evader) {
        super(unit);
        this.evader = evader;
    }

    @Override
    public Vector3 calculate() {
        Vector3 toEvader = evader.pos.clone().sub(unit.pos);

        float relativeHeading = unit.heading.dot(evader.heading);

        if (toEvader.dot(unit.heading) > 0 && relativeHeading < -0.95) {
            // acos(0.95) = 18 degrees
            return new BehaviourSeek(unit, evader.pos.clone()).calculate();
        }

        // Not considered ahead so we predict where the evader will be

        /*
         * The look-ahead time is proportional to the distance between the evader
         * and the pursuer and it is inversely proportional to the sum of the agents' velocities
         */
        float lookAheadTime = toEvader.len() / (unit.maxSpeed + evader.vel.len());

        // Now seek to the predicted future position of the evader
        return new BehaviourSeek(unit, evader.pos.clone().add(evader.vel.clone().scl(lookAheadTime))).calculate();
    }

}
