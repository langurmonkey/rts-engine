package rts.arties.scene.ecs.component;

import com.badlogic.ashley.core.Component;
import rts.arties.scene.map.IRTSMap;

/**
 * Contains a reference to the current map
 */
public class MapComponent implements Component {
    // Metre to pixel conversion
    public static final float M_TO_PX = 5;
    // Pixel to metre conversion
    public static final float PX_TO_M = 1 / M_TO_PX;

    // The current map
    public IRTSMap map;
}
