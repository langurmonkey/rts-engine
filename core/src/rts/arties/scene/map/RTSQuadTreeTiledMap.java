package rts.arties.scene.map;

import rts.arties.RTSGame;
import rts.arties.datastructure.mapgen.quadmap.QuadMapGen;
import rts.arties.datastructure.quadtree.render.QuadTreeRenderer;

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
