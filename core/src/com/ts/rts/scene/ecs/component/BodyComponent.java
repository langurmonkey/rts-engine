package com.ts.rts.scene.ecs.component;

import com.badlogic.ashley.core.Component;

/**
 * Contains some physical properties of the entity such as the mass or the size
 */
public class BodyComponent implements Component {
    /**
     * Mass [kg]
     **/
    public float mass;

    /**
     * SIZE
     */
    public float width;
    public float height;
}
