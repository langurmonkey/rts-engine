package rts.arties.scene.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Logger;
import rts.arties.RTSGame;
import rts.arties.datastructure.IMap;
import rts.arties.datastructure.IMapCell;
import rts.arties.datastructure.IMapRenderer;
import rts.arties.datastructure.astar.AStar;
import rts.arties.datastructure.astar.IAStar;
import rts.arties.datastructure.geom.Vector2;
import rts.arties.datastructure.mapgen.IMapGen;
import rts.arties.scene.cam.Camera;
import rts.arties.scene.unit.steeringbehaviour.IEntity;
import rts.arties.util.Vector2Pool;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Abstract class containing the features common to all {@link IRTSMap} objects.
 *
 * @author Toni Sagrista
 */
public abstract class RTSAbstractMap implements IRTSMap {
    public static final String BASE_LAYERS_PREFIX = "BaseLayer";
    public static final String OVERLAY_LAYERS_PREFIX = "OverlayLayer";
    public static final String OBJECTS_LAYER_NAME = "ObjectsLayer";

    protected RTSGame game;

    protected IMapGen<IEntity> mapGen = null;
    protected IMap<IEntity> map;
    protected IAStar<IEntity> astar;
    protected TiledMap tiledMap;

    protected TiledMapTileLayer firstLayer;
    protected List<MapLayer> baseLayers;
    protected List<MapLayer> overlayLayers;
    protected int[] baseIndices, overlayIndices;
    protected MapObjects mapObjects = null;
    protected List<IMapCell<IEntity>> path = null;
    protected MapRenderer mapRenderer;
    protected IMapRenderer rtsMapRenderer;
    protected FogOfWar fogOfWar;
    public boolean useFogOfWar;

    protected SpriteBatch mapBatch;

    protected int renderTileWidth, renderTileHeight;

    public RTSAbstractMap(RTSGame game, String tiledMapPath) {
        this(game, tiledMapPath, false);
    }

    public RTSAbstractMap(RTSGame game, String tiledMapPath, boolean useFogOfWar) {
        super();
        this.game = game;

        // Lets initialize the tiled map
        tiledMap = new TmxMapLoader().load(tiledMapPath);

        MapLayers mapLayers = tiledMap.getLayers();
        baseLayers = getLayersByPrefix(mapLayers, BASE_LAYERS_PREFIX);
        overlayLayers = getLayersByPrefix(mapLayers, OVERLAY_LAYERS_PREFIX);

        if (baseLayers.size() == 0) {
            (new Logger(this.getClass().getSimpleName())).error("Map has no base layers: " + tiledMapPath);
            return;
        }

        firstLayer = (TiledMapTileLayer) baseLayers.get(0);
        baseIndices = getLayerIndices(mapLayers, baseLayers);
        overlayIndices = getLayerIndices(mapLayers, overlayLayers);

        MapLayer objectsLayer = mapLayers.get(OBJECTS_LAYER_NAME);
        if (objectsLayer != null)
            mapObjects = objectsLayer.getObjects();

        mapBatch = new SpriteBatch(1000, RTSGame.game.mapShader);

        // Initialize map renderer
        mapRenderer = new OrthogonalTiledMapRenderer(tiledMap, mapBatch);

        initializeMapGenAndRenderer(game.camera(), game.cameraShapeRenderer, game.spriteBatch);

        // And now let's initialize the quadtree
        map = mapGen.generateMap(tiledMap);

        astar = new AStar<>(map);

        // Number of tiles that fit in the canvas, to render.
        renderTileWidth = Gdx.graphics.getWidth() / firstLayer.getTileWidth();
        renderTileHeight = Gdx.graphics.getHeight() / firstLayer.getTileHeight();

        // Fog of war
        this.useFogOfWar = useFogOfWar;
        if (useFogOfWar) {
            fogOfWar = new FogOfWar(firstLayer.getWidth(), firstLayer.getHeight(), firstLayer.getTileWidth());
        }
    }

    @Override
    public void doneLoading(AssetManager assets) {
        if (this.useFogOfWar) {
            fogOfWar.doneLoading(assets);
        }
    }

    public static List<MapLayer> getLayersByPrefix(MapLayers mapLayers, String prefix) {
        List<MapLayer> res = new ArrayList<>(11);
        MapLayer ml = mapLayers.get(prefix);
        if (ml != null)
            res.add(ml);
        for (int i = 0; i < 10; i++) {
            ml = mapLayers.get(prefix + i);
            if (ml != null)
                res.add(ml);
        }
        return res;
    }

    public static int[] getLayerIndices(MapLayers mapLayers, List<MapLayer> layers) {
        int[] res = new int[layers.size()];
        int i = 0;
        for (MapLayer layer : layers) {
            res[i] = mapLayers.getIndex(layer);
            i++;
        }
        return res;
    }

    protected abstract void initializeMapGenAndRenderer(Camera camera, ShapeRenderer sr, SpriteBatch sb);

    public int getWidth() {
        return firstLayer.getWidth() * firstLayer.getTileWidth();
    }

    public int getHeight() {
        return firstLayer.getHeight() * firstLayer.getTileHeight();
    }

    @Override
    public List<IMapCell<IEntity>> findPath(Float inix, Float iniy, Float endx, Float endy) {
        path = null;
        map.clearPath();
        astar.clear();
        Vector2 ini = Vector2Pool.getObject(inix, iniy);
        Vector2 end = Vector2Pool.getObject(endx, endy);
        path = astar.findPath(ini, end);
        Vector2Pool.returnObjects(ini, end);
        return path;
    }

    @Override
    public void updateFogOfWar(Vector3 position, int radius) {
        if (fogOfWar != null) {
            fogOfWar.update(position, radius);
        }
    }

    @Override
    public Set<IMapCell<IEntity>> findLeafNodesWith(IEntity entity) {
        return map.findNodesWith(entity);
    }

    @Override
    public void renderBase(Camera camera) {
        // Check camera and canvas
        mapRenderer.setView((OrthographicCamera) camera.getOrthoCamera());
        mapRenderer.render(baseIndices);
    }

    @Override
    public void renderOverlays(Camera camera) {
        // Check camera and canvas
        mapRenderer.setView((OrthographicCamera) camera.getOrthoCamera());
        mapRenderer.render(overlayIndices);
    }

    @Override
    public void renderFogOfWar(Camera camera, ShapeRenderer sr, SpriteBatch sb) {
        if (fogOfWar != null) {
            fogOfWar.render(camera, sr, sb);
        }
    }

    public void renderDebug() {
        // Enable transparencies
        Gdx.gl.glEnable(GL20.GL_BLEND);
        rtsMapRenderer.drawMap(map);
    }

    @Override
    public void addEntity(IEntity entity) {
        map.add(entity);
    }

    @Override
    public void removeEntity(IEntity entity) {
        map.remove(entity);
    }

    @Override
    public void updateEntity(IEntity entity) {
        removeEntity(entity);
        addEntity(entity);
    }

    @Override
    public void reorganize() {
        map.reorganize();
    }

    @Override
    public void dispose() {
        tiledMap.dispose();
    }

    @Override
    public Set<IEntity> getNearbyEntities(Vector2 pos) {
        return map.findNearbyObjects(pos);
    }

    @Override
    public Set<IEntity> getNearbyEntities(Vector3 pos) {
        return map.findNearbyObjects(pos);
    }

    @Override
    public Set<IMapCell<IEntity>> getNearbyBlockedNodes(Vector2 pos) {
        return map.findNearbyBlockedNodes(pos);
    }

    @Override
    public Set<IMapCell<IEntity>> getNearbyBlockedNodes(Vector3 pos) {
        return map.findNearbyBlockedNodes(pos);
    }

    /**
     * Checks if the given rectangle overlaps with a blocked node
     *
     * @param r
     * @return
     */
    public boolean overlapsWithBlocked(Rectangle r) {
        return map.overlapsWithBlocked(r);
    }

    @Override
    public boolean walkable(Vector2 ini, Vector2 end, IEntity entity) {
        return walkable(ini.x, ini.y, end.x, end.y, entity);
    }

    @Override
    public boolean walkable(Vector3 ini, Vector3 end, IEntity entity) {
        return walkable(ini.x, ini.y, end.x, end.y, entity);
    }

    public boolean walkable(float inix, float iniy, float endx, float endy, IEntity entity) {
        float step = entity.bounds().width / 2f;
        Rectangle r = new Rectangle(0, 0, entity.bounds().width, entity.bounds().height);
        Vector2 current = Vector2Pool.getObject(inix, iniy);
        Vector2 advance = Vector2Pool.getObject(endx, endy).subtract(inix, iniy);
        float len = advance.len();
        int nsteps = (int) (len / step);

        advance.normalise().multiply(step);
        for (int i = 0; i < nsteps; i++) {
            r.setX(current.x - r.width / 2);
            r.setY(current.y - r.height / 2);
            if (map.overlapsWithBlocked(r)) {
                return false;
            }
            // Advance to next step
            current.add(advance);
        }

        Vector2Pool.returnObjects(current, advance);

        return true;
    }

    @Override
    public MapProperties.TerrainType getTerrainType(Vector2 point) {
        return map.getCell(point).getTerrain();
    }

    @Override
    public MapProperties.TerrainType getTerrainType(Vector3 point) {
        return null;
    }

    @Override
    public IMapCell<IEntity> getCell(Vector2 point) {
        return map.getCell(point);
    }

    @Override
    public IMapCell<IEntity> getCell(Vector3 point) {
        return null;
    }

    @Override
    public IMapCell<IEntity> getCell(float x, float y) {
        return map.getCell(x, y);
    }

    @Override
    public MapObjects getMapObjects() {
        return mapObjects;
    }

}
