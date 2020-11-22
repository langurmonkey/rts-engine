package arties.scene.ecs.component;

import arties.datastructure.IMapCell;
import arties.scene.map.IRTSMap;
import arties.scene.unit.steeringbehaviour.IEntity;
import com.badlogic.ashley.core.Component;

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
