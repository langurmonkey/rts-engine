package rts.arties.scene.unit.steeringbehaviour;

import rts.arties.datastructure.geom.Vector3;

/**
 * Interface for a steering behaviour.
 *
 * @author Toni Sagrista
 */
public interface ISteeringBehaviour {

    Vector3 calculate();

    boolean isDone();

    void render();

    void dispose();
}
