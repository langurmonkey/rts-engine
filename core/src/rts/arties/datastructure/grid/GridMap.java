package rts.arties.datastructure.grid;

import rts.arties.datastructure.IMap;
import rts.arties.datastructure.IMapCell;
import rts.arties.scene.unit.steeringbehaviour.IEntity;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * A grid map, which organizes the 2D space into a matrix of cells of the same size.
 *
 * @param <T>
 * @author Toni Sagrista
 */
public class GridMap<T extends IEntity> implements IMap<T> {

    public GridCell<T>[][] cells;
    public int columns, rows;

    public int cellWidth, cellHeight;

    /**
     * Creates a new grid map initializing all the cells
     *
     * @param columns
     * @param rows
     * @param cellWidth  The cell width n pixels
     * @param cellHeight The cell height in pixels
     */
    public GridMap(int columns, int rows, int cellWidth, int cellHeight) {
        this.columns = columns;
        this.rows = rows;
        this.cellHeight = cellHeight;
        this.cellWidth = cellWidth;
        // Create a column-first cell array
        cells = new GridCell[columns][rows];
        for (int col = 0; col < columns; col++) {
            for (int row = 0; row < rows; row++) {
                cells[col][row] = new GridCell<>(new Rectangle(col * cellWidth, row * cellHeight, cellWidth, cellHeight),
                    col, row, this);
            }
        }
    }

    public int getColumn(float x) {
        return (int) (x / cellWidth);
    }

    public int getRow(float y) {
        return (int) (y / cellHeight);
    }

    public GridCell<T> getCell(int col, int row) {
        if (col >= 0 && col < columns && row >= 0 && row < rows) {
            return cells[col][row];
        } else {
            return null;
        }
    }

    @Override
    public GridCell<T> getCell(float x, float y) {
        int col = getColumn(x);
        int row = getRow(y);
        return getCell(col, row);
    }

    @Override
    public GridCell<T> getCell(Vector2 p) {
        return getCell(p.x, p.y);
    }

    @Override
    public Set<IMapCell<T>> findNodesWith(T o) {
        float x = o.pos().x;
        float y = o.pos().y;
        IMapCell<T> cell = getCell(x, y);
        Set<IMapCell<T>> cells = cell.findAdjacentCells();
        cells.add(cell);

        Iterator<IMapCell<T>> it = cells.iterator();
        while (it.hasNext()) {
            IMapCell<T> candidate = it.next();
            if (!candidate.containsObject(o)) {
                it.remove();
            }
        }
        return cells;
    }

    @Override
    public Set<IMapCell<T>> findNearbyBlockedNodes(Vector2 p) {
        return findNearbyBlockedNodes(p.x, p.y);
    }

    @Override
    public Set<IMapCell<T>> findNearbyBlockedNodes(Vector3 p) {
        return findNearbyBlockedNodes(p.x, p.y);
    }

    @Override
    public Set<IMapCell<T>> findNearbyBlockedNodes(float x, float y) {
        IMapCell<T> cell = getCell(x, y);
        if (cell == null)
            return new HashSet<>();

        Set<IMapCell<T>> cells = cell.findAdjacentCells();
        cells.add(cell);

        Iterator<IMapCell<T>> it = cells.iterator();
        while (it.hasNext()) {
            IMapCell<T> candidate = it.next();
            if (!candidate.isBlocked()) {
                it.remove();
            }
        }
        return cells;
    }

    @Override
    public Set<T> findNearbyObjects(Vector2 p) {
        return findNearbyObjects(p.x, p.y);
    }

    @Override
    public Set<T> findNearbyObjects(Vector3 p) {
        return findNearbyObjects(p.x, p.y);
    }

    @Override
    public Set<T> findNearbyObjects(float x, float y) {
        IMapCell<T> pointCell = getCell(x, y);
        Set<T> objects = new HashSet<>();
        if (pointCell != null) {
            Set<IMapCell<T>> cells = pointCell.findAdjacentCells();
            cells.add(pointCell);

            for (IMapCell<T> cell : cells) {
                objects.addAll(cell.getObjects());
            }
        }
        return objects;
    }

    @Override
    public void remove(T o) {
        // Get a square of 5 cells around the position of the object
        Set<IMapCell<T>> candidateCells = getCellsAround(o.pos().x, o.pos().y, 2);
        for (IMapCell<T> cell : candidateCells) {
            cell.removeObject(o);
        }
    }

    /**
     * Gets a square of cells around this position. The square has a width and height of cellRadius*2+1
     *
     */
    public Set<IMapCell<T>> getCellsAround(float x, float y, int cellRadius) {
        float row = getRow(y);
        float col = getColumn(x);

        Set<IMapCell<T>> set = new HashSet<>();
        // check a radius of cellRadius*2+1 cells around the object
        for (int i = (int) Math.max(0, col - cellRadius); i <= Math.min(columns - 1, col + cellRadius); i++) {
            for (int j = (int) Math.max(0, row - cellRadius); j <= Math.min(rows - 1, row + cellRadius); j++) {
                set.add(cells[i][j]);
            }
        }
        return set;
    }

    @Override
    public void add(T o) {
        Set<IMapCell<T>> set = getCellsAround(o.pos().x, o.pos().y, 1);
        for (IMapCell<T> cell : set) {
            if (cell.bounds().overlaps(o.bounds())) {
                cell.add(o);
            }
        }
    }

    @Override
    public boolean isInBlocked(float x, float y) {
        IMapCell<T> cell = getCell(x, y);
        return (cell != null && cell.isBlocked());
    }

    @Override
    public IMapCell<T> getBlockedNode(float x, float y) {
        IMapCell<T> cell = getCell(x, y);
        return (cell != null && cell.isBlocked()) ? cell : null;
    }

    @Override
    public boolean overlapsWithBlocked(Rectangle r) {
        return isInBlocked(r.x, r.y) || isInBlocked(r.x + r.width, r.y) || isInBlocked(r.x + r.width, r.y + r.height)
            || isInBlocked(r.x, r.y + r.height);
    }

    @Override
    public void clearPath() {
        for (int col = 0; col < columns; col++) {
            for (int row = 0; row < rows; row++) {
                cells[col][row].setParent(null);
            }
        }
    }

    @Override
    public void reorganize() {
        // Void

    }

}
