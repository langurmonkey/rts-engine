package com.ts.rts.scene.ecs.component;

import com.badlogic.ashley.core.Component;
import com.ts.rts.scene.map.IRTSMap;

/**
 * Contains a reference to the current map
 */
public class MapComponent implements Component {
    protected IRTSMap map;
}
