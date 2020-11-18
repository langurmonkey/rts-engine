package com.ts.rts.scene.unit.steeringbehaviour;

import com.ts.rts.datastructure.geom.Vector2;

/**
 * Interface for a steering behaviour.
 *
 * @author Toni Sagrista
 */
public interface ISteeringBehaviour {

    Vector2 calculate();

    boolean isDone();

    void render();

    void dispose();
}
