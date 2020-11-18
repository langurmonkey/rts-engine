package com.ts.rts.datastructure.astar;

import com.badlogic.gdx.math.Vector2;
import com.ts.rts.datastructure.IMapCell;
import com.ts.rts.scene.unit.IBoundsObject;

import java.util.List;

/**
 * The A* algorithm for pathfinding.
 *
 * @param <T>
 * @author Toni Sagrista
 */
public interface IAStar<T extends IBoundsObject> {

    List<IMapCell<T>> findPath(Vector2 ini, Vector2 end);

    void clear();
}
