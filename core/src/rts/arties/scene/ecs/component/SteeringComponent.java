package rts.arties.scene.ecs.component;

import rts.arties.datastructure.geom.Vector3;
import rts.arties.scene.unit.steeringbehaviour.SteeringBehaviours;
import com.badlogic.ashley.core.Component;

public class SteeringComponent implements Component {
    /**
     * Steering behaviours
     **/
    public SteeringBehaviours steeringBehaviours;
    public Vector3 targetHeading = new Vector3();
}
