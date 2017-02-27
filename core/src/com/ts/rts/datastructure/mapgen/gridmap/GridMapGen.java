package com.ts.rts.datastructure.mapgen.gridmap;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.ts.rts.datastructure.IMap;
import com.ts.rts.datastructure.grid.GridCell;
import com.ts.rts.datastructure.grid.GridMap;
import com.ts.rts.datastructure.mapgen.IMapGen;
import com.ts.rts.scene.map.MapProperties;
import com.ts.rts.scene.map.RTSAbstractMap;
import com.ts.rts.scene.unit.IBoundsObject;

/**
 * Grid map generator, which generates a {@link GridMap} from a TiledMap.
 * 
 * @author Toni Sagrista
 * 
 * @param <T>
 */
public class GridMapGen<T extends IBoundsObject> implements IMapGen<T> {

	private TiledMap tm;
	private TiledMapTileLayer mainLayer;
	private TiledMapTileLayer mainLayerOverlay;

	@Override
	public IMap<T> generateMap(TiledMap map) {
		tm = map;
		mainLayer = (TiledMapTileLayer) tm.getLayers().get(RTSAbstractMap.BASE_LAYER_NAME);
		mainLayerOverlay = (TiledMapTileLayer) tm.getLayers().get(RTSAbstractMap.BASE_LAYER_OVERLAY_NAME);

		int columns = mainLayer.getWidth();
		int rows = mainLayer.getHeight();

		GridMap<T> gridMap = new GridMap<T>(columns, rows, (int) mainLayer.getTileWidth(),
				(int) mainLayer.getTileHeight());

		for (int col = 0; col < columns; col++) {
			for (int row = 0; row < rows; row++) {
				Cell cell = mainLayer.getCell(col, row);
				String type = cell.getTile().getProperties().get(PROPERTY_TYPE, "undefined", String.class);
				boolean shadow = Boolean.valueOf(cell.getTile().getProperties()
						.get(PROPERTY_SHADOW, "false", String.class));
				if (mainLayerOverlay != null) {
					// Check if there's an overlay
					Cell over = mainLayerOverlay.getCell(col, row);
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
			}
		}

		generateHeights(gridMap);

		return gridMap;
	}

	/**
	 * This function sets the z value to all the cells in the map, where 0 is the top-left most cell
	 */
	private void generateHeights(GridMap<T> gridMap) {
		int columns = mainLayer.getWidth();

		generateRowHeights(gridMap, 0, 0);

		// First, sweep left to right
		for (int col = 0; col < columns; col++) {
			int startingHeight = (int) gridMap.getCell(col, 0).z;
			generateColumnHeights(gridMap, col, startingHeight);
		}

		normalizeHeights(gridMap);

	}

	private void generateColumnHeights(GridMap<T> gridMap, int col, int startingHeight) {
		int rows = mainLayer.getHeight();
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
		int cols = mainLayer.getWidth();
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
		int columns = mainLayer.getWidth();
		int rows = mainLayer.getHeight();
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
