package arties.scene.ecs.component;

import arties.datastructure.IMapCell;
import arties.scene.map.IRTSMap;
import arties.scene.unit.steeringbehaviour.IEntity;
import com.badlogic.ashley.core.Component;

/**
 * Contains a reference to the current map
 */
public class MapComponent implements Component {
    // The current map
    public IRTSMap map;

    // The cell we're in
    private IMapCell<IEntity> cell;
}
