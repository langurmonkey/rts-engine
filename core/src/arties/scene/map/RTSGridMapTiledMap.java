package arties.scene.map;

import arties.RTSGame;
import arties.datastructure.grid.render.GridMapRenderer;
import arties.datastructure.mapgen.gridmap.GridMapGen;

/**
 * Materialization of an {@link IRTSMap} as a grid map.
 *
 * @author Toni Sagrista
 */
public class RTSGridMapTiledMap extends RTSAbstractMap {

    public RTSGridMapTiledMap(RTSGame game, String tiledMapPath) {
        super(game, tiledMapPath);
    }

    @Override
    protected void initializeMapGenAndRenderer() {
        mapGen = new GridMapGen<>();
        // Initialize map renderer for debug
        rtsMapRenderer = new GridMapRenderer();

    }

}
