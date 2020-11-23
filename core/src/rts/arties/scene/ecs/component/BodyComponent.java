package rts.arties.scene.ecs.component;

import rts.arties.scene.unit.steeringbehaviour.IEntity;
import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Rectangle;

/**
 * Contains some physical properties of the entity such as the mass or the size
 */
public class BodyComponent implements Component {
    private static long uniqueIdSeq = 0;
    public long uniqueId = getUniqueId();

    //Mass [kg]
    public float mass;

    // Weight for map cells
    public float weight;

    public float width;
    public float height;

    // The soft radius for flocking/steering
    public float softRadius;
    // Hard radius
    public Rectangle hardRadius;

    // Reference to IEntity of my owner
    public IEntity me;

    private static synchronized long getUniqueId() {
        return uniqueIdSeq++;
    }

}
