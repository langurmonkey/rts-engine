package com.ts.rts.datastructure;

import java.util.Set;

import com.badlogic.gdx.math.Rectangle;
import com.ts.rts.scene.map.MapProperties.TerrainType;
import com.ts.rts.scene.unit.IBoundsObject;

/**
 * A map cell containing objects.
 * 
 * @author Toni Sagrista
 * 
 * @param <T>
 */
public interface IMapCell<T extends IBoundsObject> {

	/**
	 * Gets all the objects contained in this cell
	 * 
	 * @return
	 */
	public Set<T> getObjects();

	/**
	 * Checks if the current cell has objects
	 * 
	 * @return
	 */
	public boolean hasObjects();

	/**
	 * Checks if the current cell has objects different than the given object
	 * 
	 * @param o
	 * @return
	 */
	public boolean hasObjectsDifferentThan(T o);

	/**
	 * Checks if the object is in this cell
	 * 
	 * @param o
	 * @return
	 */
	public boolean containsObject(T o);

	/**
	 * Removes the object from the cell
	 * 
	 * @param o
	 * @return True if the object was in the cell and has been removed, false otherwise
	 */
	public boolean removeObject(T o);

	/**
	 * Adds the given object to the cell with the given bounds
	 * 
	 * @param o
	 * @param oBounds
	 */
	public void add(T o);

	/**
	 * Returns the x position of the center of this cell
	 * 
	 * @return
	 */
	public float x();

	/**
	 * Returns the y position of the center of this cell
	 * 
	 * @return
	 */
	public float y();

	/**
	 * Returns the z coordinate at the given position in this cell
	 * 
	 * @return
	 */
	public float z(float x, float y);

	/**
	 * Returns the bounds rectangle
	 * 
	 * @return
	 */
	public Rectangle bounds();

	/**
	 * Gets the list of adjacent cells to this cell
	 * 
	 * @return
	 */
	public Set<IMapCell<T>> findAdjacentCells();

	public void setTerrainType(TerrainType type);

	/**
	 * Blocks the cell
	 */
	public void block();

	/**
	 * Checks if the cell is blocked
	 * 
	 * @return
	 */
	public boolean isBlocked();

	/**
	 * Checks if the cell is empty of objects and is not blocked
	 * 
	 * @return
	 */
	public boolean isEmpty();

	/**
	 * Gets the terrain type
	 * 
	 * @return
	 */
	public TerrainType getTerrain();

	/**
	 * Is this in shadow?
	 * 
	 * @return
	 */
	public boolean isShadow();

	/**
	 * Gets the f in aStar
	 * 
	 * @return
	 */
	public float f();

	/**
	 * Gets the g in aStar
	 * 
	 * @return
	 */
	public float g();

	/**
	 * Sets the g in aStar
	 * 
	 * @param g
	 */
	public void setG(float g);

	/**
	 * Gets the heuristinc in aStar
	 * 
	 * @return
	 */
	public float h();

	/**
	 * Sets the heuristic in aStar
	 * 
	 * @param h
	 */
	public void setH(float h);

	public IMapCell<T> parent();

	public void setParent(IMapCell<T> parent);

}
