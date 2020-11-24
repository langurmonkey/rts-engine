package rts.arties.scene.map;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import rts.arties.RTSGame;
import rts.arties.datastructure.mapgen.quadmap.QuadMapGen;
import rts.arties.datastructure.quadtree.render.QuadTreeRenderer;
import rts.arties.scene.cam.Camera;

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
    protected void initializeMapGenAndRenderer(Camera camera, ShapeRenderer sr, SpriteBatch sb) {
        mapGen = new QuadMapGen<>();
        // Initialize map renderer for debug
        rtsMapRenderer = new QuadTreeRenderer(camera, sr, sb);
    }

}
