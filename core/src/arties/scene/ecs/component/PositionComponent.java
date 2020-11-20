package arties.scene.ecs.component;

import arties.datastructure.geom.Vector3;
import com.badlogic.ashley.core.Component;

/**
 * Contains the position in pixel coordinates and some other stuff
 */
public class PositionComponent implements Component {

    public Vector3 pos = new Vector3();
    public float viewingDistance = 0;
}
