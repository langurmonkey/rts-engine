package rts.arties.scene.map;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import rts.arties.RTSGame;
import rts.arties.datastructure.grid.render.GridDebugRenderer;
import rts.arties.datastructure.mapgen.gridmap.GridMapGen;
import rts.arties.scene.cam.Camera;

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
    protected void initializeMapGenAndRenderer(Camera camera, ShapeRenderer sr, SpriteBatch sb) {
        mapGen = new GridMapGen<>();
        // Initialize map renderer for debug
        mapDebugRenderer = new GridDebugRenderer(camera, sr, sb);

    }

}
