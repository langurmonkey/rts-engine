package arties.datastructure.mapgen.gridmap;

import arties.datastructure.IMap;
import arties.datastructure.grid.GridCell;
import arties.datastructure.grid.GridMap;
import arties.datastructure.mapgen.IMapGen;
import arties.scene.map.MapProperties;
import arties.scene.map.RTSAbstractMap;
import arties.scene.unit.steeringbehaviour.IEntity;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;

import java.util.List;

/**
 * Grid map generator, which generates a {@link GridMap} from a TiledMap.
 *
 * @author Toni Sagrista
 */
public class GridMapGen<T extends IEntity> implements IMapGen<T> {

    private TiledMap tm;
    private TiledMapTileLayer baseLayer;
    private TiledMapTileLayer overlayLayer;
    private List<MapLayer> baseLayers;
    private List<MapLayer> overlayLayers;

    @Override
    public IMap<T> generateMap(TiledMap map) {
        tm = map;

        baseLayers = RTSAbstractMap.getLayersByPrefix(tm.getLayers(), RTSAbstractMap.BASE_LAYERS_PREFIX);
        overlayLayers = RTSAbstractMap.getLayersByPrefix(tm.getLayers(), RTSAbstractMap.OVERLAY_LAYERS_PREFIX);

        baseLayer = (TiledMapTileLayer) baseLayers.get(0);
        overlayLayer = (TiledMapTileLayer) overlayLayers.get(0);

        int columns = baseLayer.getWidth();
        int rows = baseLayer.getHeight();

        GridMap<T> gridMap = new GridMap<>(columns, rows, baseLayer.getTileWidth(),
            baseLayer.getTileHeight());

        for (int col = 0; col < columns; col++) {
            for (int row = 0; row < rows; row++) {
                // Get topmost cell, if any
                Cell cell = getTopCell(baseLayers, col, row);
                String type = cell.getTile().getProperties().get(PROPERTY_TYPE, "undefined", String.class);
                boolean shadow = Boolean.valueOf(cell.getTile().getProperties()
                    .get(PROPERTY_SHADOW, "false", String.class));
                if (overlayLayers.size() > 0) {
                    // Check if there's an overlay
                    Cell over = getTopCell(overlayLayers, col, row);
                    if (over != null && over.getTile() != null) {
                        String typeaux = over.getTile().getProperties().get(PROPERTY_TYPE, "none", String.class);
                        if (!typeaux.equals("none")) {
                            type = typeaux;
                        }
                        if (Boolean.valueOf(over.getTile().getProperties().get(PROPERTY_SHADOW, "false", String.class))) {
                            shadow = true;
                        }
                    }
                }

                gridMap.getCell(col, row).setTerrainType(MapProperties.getTerrainType(type));
                gridMap.getCell(col, row).setShadow(shadow);
                gridMap.getCell(col, row).slopev = -Integer.valueOf(cell.getTile().getProperties()
                    .get(PROPERTY_SLOPEV, "0", String.class));
                gridMap.getCell(col, row).slopeh = Integer.valueOf(cell.getTile().getProperties()
                    .get(PROPERTY_SLOPEH, "0", String.class));
                gridMap.getCell(col, row).slowdown = Float.valueOf(cell.getTile().getProperties()
                    .get(PROPERTY_SLOWDOWN, "0", String.class));
            }
        }

        generateHeights(gridMap);

        return gridMap;
    }

    /**
     * Gets the topmost populated cell in all layers, top to bottom.
     * @param layers The layers.
     * @param col The column.
     * @param row The row.
     * @return The topmost populated cell at the given position.
     */
    private Cell getTopCell(List<MapLayer> layers, int col, int row){
        for(int i = layers.size() - 1; i >= 0; i--){
            Cell c = ((TiledMapTileLayer)layers.get(i)).getCell(col, row);
            if(c != null)
                return c;
        }
        return null;
    }

    /**
     * This function sets the z value to all the cells in the map, where 0 is the top-left most cell
     */
    private void generateHeights(GridMap<T> gridMap) {
        int columns = baseLayer.getWidth();

        generateRowHeights(gridMap, 0, 0);

        // First, sweep left to right
        for (int col = 0; col < columns; col++) {
            int startingHeight = (int) gridMap.getCell(col, 0).z;
            generateColumnHeights(gridMap, col, startingHeight);
        }

        normalizeHeights(gridMap);

    }

    private void generateColumnHeights(GridMap<T> gridMap, int col, int startingHeight) {
        int rows = baseLayer.getHeight();
        int prevHeight = startingHeight;
        for (int row = 0; row < rows; row++) {
            GridCell<T> gcell = gridMap.getCell(col, row);
            if (gcell.slopev < 0) {
                prevHeight += gcell.slopev;
            }
            gridMap.getCell(col, row).z = prevHeight;
            if (gcell.slopev > 0) {
                prevHeight += gcell.slopev;
            }
        }
    }

    private void generateRowHeights(GridMap<T> gridMap, int row, int startingHeight) {
        int cols = baseLayer.getWidth();
        int prevHeight = startingHeight;
        for (int col = 0; col < cols; col++) {
            GridCell<T> gcell = gridMap.getCell(col, row);
            if (gcell.slopeh < 0) {
                prevHeight += gcell.slopeh;
            }
            gridMap.getCell(col, row).z = prevHeight;
            if (gcell.slopeh > 0) {
                prevHeight += gcell.slopeh;
            }

        }
    }

    private void normalizeHeights(GridMap<T> gridMap) {
        int columns = baseLayer.getWidth();
        int rows = baseLayer.getHeight();
        float maxz = Float.MIN_VALUE;
        float minz = Float.MAX_VALUE;
        for (int col = 0; col < columns; col++) {
            for (int row = 0; row < rows; row++) {
                GridCell<T> cell = gridMap.getCell(col, row);
                if (cell.z < minz) {
                    minz = cell.z;
                }
                if (cell.z > maxz) {
                    maxz = cell.z;
                }
            }
        }

        if (minz != 0) {
            for (int col = 0; col < columns; col++) {
                for (int row = 0; row < rows; row++) {
                    GridCell<T> cell = gridMap.getCell(col, row);
                    cell.z += -minz;
                }
            }
        }

    }
}
