package rts.arties.datastructure.astar;

import rts.arties.datastructure.IMapCell;
import rts.arties.scene.unit.steeringbehaviour.IEntity;
import com.badlogic.gdx.math.Vector2;

import java.util.List;

/**
 * The A* algorithm for pathfinding.
 *
 * @param <T>
 * @author Toni Sagrista
 */
public interface IAStar<T extends IEntity> {

    List<IMapCell<T>> findPath(Vector2 ini, Vector2 end);

    void clear();
}
