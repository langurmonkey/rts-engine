package rts.arties.scene.ecs.component;

import com.badlogic.ashley.core.Component;
import rts.arties.datastructure.geom.Vector3;
import rts.arties.scene.unit.steeringbehaviour.SteeringBehaviours;

public class SteeringComponent implements Component {
    /**
     * Steering behaviours
     **/
    public SteeringBehaviours steeringBehaviours;
    public Vector3 targetHeading = new Vector3();
}
