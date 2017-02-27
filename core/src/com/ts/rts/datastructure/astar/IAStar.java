package com.ts.rts.datastructure.astar;

import java.util.List;

import com.badlogic.gdx.math.Vector2;
import com.ts.rts.datastructure.IMapCell;
import com.ts.rts.scene.unit.IBoundsObject;

/**
 * The A* algorithm for pathfinding.
 * 
 * @author Toni Sagrista
 * 
 * @param <T>
 */
public interface IAStar<T extends IBoundsObject> {

	public List<IMapCell<T>> findPath(Vector2 ini, Vector2 end);

	public void clear();
}
