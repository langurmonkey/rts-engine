package rts.arties.datastructure.grid;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import rts.arties.datastructure.IMapCell;
import rts.arties.datastructure.astar.AStarNode;
import rts.arties.scene.map.MapProperties;
import rts.arties.scene.map.MapProperties.TerrainType;
import rts.arties.scene.unit.steeringbehaviour.IEntity;

import java.util.HashSet;
import java.util.Set;

/**
 * A cell in the grid map.
 *
 * @param <T>
 * @author Toni Sagrista
 */
public class GridCell<T extends IEntity> extends AStarNode<T> implements IMapCell<T> {

    /**
     * The objects in this cell
     */
    public Set<T> objects;

    public Rectangle bounds;
    private TerrainType type = TerrainType.UNDEFINED;
    private boolean shadow = false;
    public int slopeh, slopev;
    public float slowdown = 0;
    public float x;
    public float y;
    public float z;
    public int col, row;
    // Each entity has a different weight, this sums up the weight of all entities in the cell
    public float weight = 0;

    private final GridMap<T> map;

    /**
     * Creates a grid cell with the given bounds
     *
     * @param bounds
     */
    public GridCell(Rectangle bounds, int col, int row, GridMap<T> parent) {
        this.map = parent;
        this.bounds = bounds;
        this.x = bounds.getX() + bounds.getWidth() / 2;
        this.y = bounds.getY() + bounds.getHeight() / 2;
        this.col = col;
        this.row = row;
        objects = new HashSet<>();
    }

    @Override
    public boolean hasObjects() {
        return !objects.isEmpty();
    }

    @Override
    public boolean hasObjectsDifferentThan(T o) {
        return !objects.contains(o);
    }

    @Override
    public void add(T o) {
        objects.add(o);
        recomputeWeight();
    }

    private void recomputeWeight(){
        weight = 0;
        for(IEntity e : objects)
            weight += e.weight();
    }

    @Override
    public float x() {
        return x;
    }

    @Override
    public float y() {
        return y;
    }

    @Override
    public float z() {
        return z;
    }

    @Override
    public float z(float x, float y) {
        if (slopeh != 0) {
            // We have a height gradient in the horizontal direction
            float zleft = map.getCell(col - 1, row) != null ? map.getCell(col - 1, row).z : Float.MIN_VALUE;
            float zright = map.getCell(col + 1, row) != null ? map.getCell(col + 1, row).z : Float.MAX_VALUE;

            if (zleft == Float.MIN_VALUE) {
                zleft = slopeh > 0 ? zright - 1 : zright + 1;
            }
            if (zright == Float.MIN_VALUE) {
                zright = slopeh > 0 ? zleft + 1 : zright - 1;
            }

            float zint = lint(bounds.x, zleft, bounds.x + bounds.width, zright, x);
            //Gdx.app.debug("GridCell", "Horizontal z gradient: " + zint);
            return zint;
        } else if (slopev != 0) {
            // We have a height gradient in the vertical direction
            float ztop = map.getCell(col, row + 1) != null ? map.getCell(col, row + 1).z : Float.MIN_VALUE;
            float zbottom = map.getCell(col, row - 1) != null ? map.getCell(col, row - 1).z : Float.MAX_VALUE;

            if (ztop == Float.MIN_VALUE) {
                ztop = slopev > 0 ? zbottom - 1 : zbottom + 1;
            }
            if (zbottom == Float.MIN_VALUE) {
                zbottom = slopev > 0 ? ztop + 1 : zbottom - 1;
            }

            float zint = lint(bounds.y + bounds.height, ztop, bounds.y, zbottom, y);
            //Gdx.app.debug("GridCell", "Vertical z gradient: " + zint);
            return zint;
        } else {
            return z;
        }
    }

    /**
     * Linear interpolation
     *
     * @param x0
     * @param y0
     * @param x1
     * @param y1
     * @param x
     * @return
     */
    private float lint(float x0, float y0, float x1, float y1, float x) {
        return y0 + (y1 - y0) * ((x - x0) / (x1 - x0));
    }

    @Override
    public Rectangle bounds() {
        return bounds;
    }

    @Override
    public boolean containsObject(T o) {
        return objects.contains(o);
    }

    @Override
    public Set<IMapCell<T>> findAdjacentCells() {
        // Set<IMapCell<T>> adjacent = map.getCellsAround(x, y, 1);
        // adjacent.remove(this);
        // return adjacent;
        Set<IMapCell<T>> adjacent = new HashSet<>();
        if (row > 0) {
            adjacent.add(map.cells[col][row - 1]);
        }
        if (row < map.rows - 1) {
            adjacent.add(map.cells[col][row + 1]);
        }
        if (col > 0) {
            adjacent.add(map.cells[col - 1][row]);
        }
        if (col < map.columns - 1) {
            adjacent.add(map.cells[col + 1][row]);
        }
        return adjacent;
    }

    @Override
    public void setTerrainType(TerrainType type) {
        this.type = type;
    }

    @Override
    public void block() {
        type = TerrainType.BLOCKED;
    }

    @Override
    public boolean isBlocked() {
        return MapProperties.isMapBlocked(type) || weight >= 2.5f;
    }

    @Override
    public boolean isEmpty() {
        return !isBlocked() && weight == 0;
    }

    @Override
    public TerrainType getTerrain() {
        return type;
    }

    @Override
    public Set<T> getObjects() {
        return objects;
    }

    @Override
    public boolean removeObject(T o) {
        boolean ret =  objects.remove(o);
        recomputeWeight();
        return ret;
    }

    @Override
    public boolean isShadow() {
        return shadow;
    }

    @Override public float getSlowdown() {
        return slowdown;
    }

    public void setShadow(boolean shadow) {
        this.shadow = shadow;
    }

}
