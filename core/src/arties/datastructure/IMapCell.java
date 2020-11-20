package arties.datastructure;

import arties.scene.map.MapProperties.TerrainType;
import arties.scene.unit.steeringbehaviour.IEntity;
import com.badlogic.gdx.math.Rectangle;

import java.util.Set;

/**
 * A map cell containing objects.
 *
 * @param <T>
 * @author Toni Sagrista
 */
public interface IMapCell<T extends IEntity> {

    /**
     * Gets all the objects contained in this cell
     *
     * @return
     */
    Set<T> getObjects();

    /**
     * Checks if the current cell has objects
     *
     * @return
     */
    boolean hasObjects();

    /**
     * Checks if the current cell has objects different than the given object
     *
     * @param o
     * @return
     */
    boolean hasObjectsDifferentThan(T o);

    /**
     * Checks if the object is in this cell
     *
     * @param o
     * @return
     */
    boolean containsObject(T o);

    /**
     * Removes the object from the cell
     *
     * @param o
     * @return True if the object was in the cell and has been removed, false otherwise
     */
    boolean removeObject(T o);

    /**
     * Adds the given object to the cell with the given bounds
     *
     * @param o
     */
    void add(T o);

    /**
     * Returns the x position of the center of this cell
     *
     * @return
     */
    float x();

    /**
     * Returns the y position of the center of this cell
     *
     * @return
     */
    float y();

    /**
     * Returns the z coordinate at the given position in this cell
     *
     * @return
     */
    float z(float x, float y);

    /**
     * Returns the bounds rectangle
     *
     * @return
     */
    Rectangle bounds();

    /**
     * Gets the list of adjacent cells to this cell
     *
     * @return
     */
    Set<IMapCell<T>> findAdjacentCells();

    void setTerrainType(TerrainType type);

    /**
     * Blocks the cell
     */
    void block();

    /**
     * Checks if the cell is blocked
     *
     * @return
     */
    boolean isBlocked();

    /**
     * Checks if the cell is empty of objects and is not blocked
     *
     * @return
     */
    boolean isEmpty();

    /**
     * Gets the terrain type
     *
     * @return
     */
    TerrainType getTerrain();

    /**
     * Is this in shadow?
     *
     * @return
     */
    boolean isShadow();

    /**
     * Gets the f in aStar
     *
     * @return
     */
    float f();

    /**
     * Gets the g in aStar
     *
     * @return
     */
    float g();

    /**
     * Sets the g in aStar
     *
     * @param g
     */
    void setG(float g);

    /**
     * Gets the heuristinc in aStar
     *
     * @return
     */
    float h();

    /**
     * Sets the heuristic in aStar
     *
     * @param h
     */
    void setH(float h);

    float getSlowdown();

    IMapCell<T> parent();

    void setParent(IMapCell<T> parent);

}
