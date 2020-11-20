package arties.scene.unit.steeringbehaviour;

import arties.datastructure.geom.Vector3;
import arties.util.Vector3Pool;

/**
 * Same as BehaviourPursuit but now the evader flees from the estimated future position of the
 * pursuer.
 *
 * @author Toni Sagrista
 */
public class BehaviourEvade extends AbstractSteeringBehaviour {

    private final IEntity pursuer;

    public BehaviourEvade(IEntity unit, IEntity pursuer) {
        super(unit);
        this.pursuer = pursuer;
    }

    @Override
    public Vector3 calculate() {
        Vector3 toPursuer = pursuer.pos().clone().sub(unit.pos());

        /*
         * The look-ahead time is proportional to the distance between the pursuer
         * and the evader and it is inversely proportional to the sum of the agents' velocities
         */
        float lookAheadTime = toPursuer.len() / (unit.maxSpeed() + pursuer.vel().len());
        Vector3Pool.returnObject(toPursuer);

        // Now flee from the predicted future position of the pursuer
        return new BehaviourFlee(unit, pursuer.pos().clone().add(pursuer.vel().clone().scl(lookAheadTime)))
            .calculate();
    }

    @Override
    public void renderBehaviour() {

    }

}
