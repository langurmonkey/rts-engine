package com.ts.rts.scene.ecs.component;

import com.badlogic.ashley.core.Component;
import com.ts.rts.datastructure.geom.Vector3;

/**
 * Contains the position in pixel coordinates
 */
public class PositionComponent implements Component {

    public Vector3 pos;
}
