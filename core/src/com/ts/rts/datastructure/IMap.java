package com.ts.rts.datastructure;

import java.util.Set;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.ts.rts.scene.unit.IBoundsObject;

/**
 * A 2D map spatially divided into cells. It contains {@link IMapCell} objects.
 * 
 * @author Toni Sagrista
 * 
 * @param <T>
 */
public interface IMap<T extends IBoundsObject> {

	/**
	 * Finds all the nodes containing the given object
	 * 
	 * @param o
	 * @return
	 */
	public Set<IMapCell<T>> findNodesWith(T o);

	/**
	 * Finds the nearby blocked nodes to this point
	 * 
	 * @param p
	 * @return
	 */
	public Set<IMapCell<T>> findNearbyBlockedNodes(Vector2 p);

	/**
	 * Finds the nearby objects to this point
	 * 
	 * @param p
	 * @return
	 */
	public Set<T> findNearbyObjects(Vector2 p);

	/**
	 * Gets the cell where the given point is located, null if it is outside the map
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public IMapCell<T> getCell(float x, float y);

	/**
	 * Gets the cell where this point is located, null if it is outside the map
	 * 
	 * @param p
	 * @return
	 */
	public IMapCell<T> getCell(Vector2 p);

	public void remove(T o);

	public void add(T o);

	/**
	 * Checks if the given point is in a blocked node
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean isInBlocked(float x, float y);

	/**
	 * Gets the blocked node this point is in, if it is in a blocked node
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public IMapCell<T> getBlockedNode(float x, float y);

	/**
	 * Checks if the given rectangle overlaps with a blocked node
	 * 
	 * @param r
	 * @return
	 */
	public boolean overlapsWithBlocked(Rectangle r);

	public void clearPath();

	public void reorganize();

}
