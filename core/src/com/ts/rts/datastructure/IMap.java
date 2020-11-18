package com.ts.rts.datastructure;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.ts.rts.scene.unit.IBoundsObject;

import java.util.Set;

/**
 * A 2D map spatially divided into cells. It contains {@link IMapCell} objects.
 *
 * @param <T>
 * @author Toni Sagrista
 */
public interface IMap<T extends IBoundsObject> {

    /**
     * Finds all the nodes containing the given object
     *
     * @param o
     * @return
     */
    Set<IMapCell<T>> findNodesWith(T o);

    /**
     * Finds the nearby blocked nodes to this point
     *
     * @param p
     * @return
     */
    Set<IMapCell<T>> findNearbyBlockedNodes(Vector2 p);

    /**
     * Finds the nearby objects to this point
     *
     * @param p
     * @return
     */
    Set<T> findNearbyObjects(Vector2 p);

    /**
     * Gets the cell where the given point is located, null if it is outside the map
     *
     * @param x
     * @param y
     * @return
     */
    IMapCell<T> getCell(float x, float y);

    /**
     * Gets the cell where this point is located, null if it is outside the map
     *
     * @param p
     * @return
     */
    IMapCell<T> getCell(Vector2 p);

    void remove(T o);

    void add(T o);

    /**
     * Checks if the given point is in a blocked node
     *
     * @param x
     * @param y
     * @return
     */
    boolean isInBlocked(float x, float y);

    /**
     * Gets the blocked node this point is in, if it is in a blocked node
     *
     * @param x
     * @param y
     * @return
     */
    IMapCell<T> getBlockedNode(float x, float y);

    /**
     * Checks if the given rectangle overlaps with a blocked node
     *
     * @param r
     * @return
     */
    boolean overlapsWithBlocked(Rectangle r);

    void clearPath();

    void reorganize();

}
