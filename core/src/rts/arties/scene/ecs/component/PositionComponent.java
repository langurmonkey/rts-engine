package rts.arties.scene.ecs.component;

import rts.arties.datastructure.geom.Vector3;
import com.badlogic.ashley.core.Component;

/**
 * Contains the position in pixel coordinates and some other stuff
 */
public class PositionComponent implements Component {

    public Vector3 pos = new Vector3();
    public Vector3 lastPos = new Vector3(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);
    public float viewingDistance = 0;
}