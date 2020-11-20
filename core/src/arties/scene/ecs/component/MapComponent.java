package arties.scene.ecs.component;

import arties.datastructure.IMapCell;
import com.badlogic.ashley.core.Component;
import arties.scene.map.IRTSMap;
import arties.scene.unit.IBoundsObject;

/**
 * Contains a reference to the current map
 */
public class MapComponent implements Component {
    // The current map
    public IRTSMap map;

    // The cell we're in
    private IMapCell<IBoundsObject> cell;
}
