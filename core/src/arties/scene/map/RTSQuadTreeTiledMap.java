package arties.scene.map;

import arties.RTSGame;
import arties.datastructure.mapgen.quadmap.QuadMapGen;
import arties.datastructure.quadtree.render.QuadTreeRenderer;

/**
 * Materialization of an {@link IRTSMap} as a grid map.
 *
 * @author Toni Sagrista
 */
public class RTSQuadTreeTiledMap extends RTSAbstractMap {

    public RTSQuadTreeTiledMap(RTSGame game, String tiledMapPath) {
        super(game, tiledMapPath);
    }

    @Override
    protected void initializeMapGenAndRenderer() {
        mapGen = new QuadMapGen<>();
        // Initialize map renderer for debug
        rtsMapRenderer = new QuadTreeRenderer();
    }

}
