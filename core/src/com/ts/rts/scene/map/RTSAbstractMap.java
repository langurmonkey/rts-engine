package com.ts.rts.scene.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Logger;
import com.ts.rts.RTSGame;
import com.ts.rts.datastructure.IMap;
import com.ts.rts.datastructure.IMapCell;
import com.ts.rts.datastructure.IMapRenderer;
import com.ts.rts.datastructure.astar.AStar;
import com.ts.rts.datastructure.astar.IAStar;
import com.ts.rts.datastructure.geom.Vector2;
import com.ts.rts.datastructure.mapgen.IMapGen;
import com.ts.rts.scene.cam.Camera;
import com.ts.rts.scene.map.MapProperties.TerrainType;
import com.ts.rts.scene.unit.IBoundsObject;
import com.ts.rts.util.VectorPool;

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

    protected IMapGen<IBoundsObject> mapGen = null;
    protected IMap<IBoundsObject> map;
    protected IAStar<IBoundsObject> astar;
    protected TiledMap tiledMap;

    protected TiledMapTileLayer firstLayer;
    protected List<MapLayer> baseLayers;
    protected List<MapLayer> overlayLayers;
    protected int[] baseIndices, overlayIndices;
    protected MapObjects mapObjects = null;
    protected List<IMapCell<IBoundsObject>> path = null;
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

        initializeMapGenAndRenderer();

        // And now let's initialize the quadtree
        map = mapGen.generateMap(tiledMap);

        astar = new AStar<>(map);

        // Number of tiles that fit in the canvas, to render.
        renderTileWidth = Gdx.graphics.getWidth() / firstLayer.getTileWidth();
        renderTileHeight = Gdx.graphics.getHeight() / firstLayer.getTileHeight();

        // Fog of war
        this.useFogOfWar = useFogOfWar;
        if (useFogOfWar) {
            fogOfWar = new FogOfWar(firstLayer.getWidth(), firstLayer.getHeight(),
                firstLayer.getTileWidth());
        }
    }

    @Override
    public void doneLoading(AssetManager assets){
        if(this.useFogOfWar){
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

    public static int[] getLayerIndices(MapLayers mapLayers, List<MapLayer> layers){
        int[] res = new int[layers.size()];
        int i = 0;
        for(MapLayer layer : layers){
            res[i] = mapLayers.getIndex(layer);
            i++;
        }
        return res;
    }

    protected abstract void initializeMapGenAndRenderer();

    public int getWidth() {
        return firstLayer.getWidth() * firstLayer.getTileWidth();
    }

    public int getHeight() {
        return firstLayer.getHeight() * firstLayer.getTileHeight();
    }

    @Override
    public List<IMapCell<IBoundsObject>> findPath(Float inix, Float iniy, Float endx, Float endy) {
        path = null;
        map.clearPath();
        astar.clear();
        Vector2 ini = VectorPool.getObject(inix, iniy);
        Vector2 end = VectorPool.getObject(endx, endy);
        path = astar.findPath(ini, end);
        VectorPool.returnObjects(ini, end);
        return path;
    }

    @Override
    public void updateFogOfWar(Vector2 position, int radius) {
        if (fogOfWar != null) {
            fogOfWar.update(position, radius);
        }
    }

    @Override
    public Set<IMapCell<IBoundsObject>> findLeafNodesWith(IBoundsObject entity) {
        return map.findNodesWith(entity);
    }

    @Override
    public void renderBase(Camera camera) {
        // Check camera and canvas
        mapRenderer.setView((OrthographicCamera) camera.getLibgdxCamera());
        mapRenderer.render(baseIndices);
    }

    @Override
    public void renderOverlays(Camera camera) {
        // Check camera and canvas
        mapRenderer.setView((OrthographicCamera) camera.getLibgdxCamera());
        mapRenderer.render(overlayIndices);
    }

    @Override
    public void renderFogOfWar(Camera camera) {
        if (fogOfWar != null) {
            fogOfWar.render(camera);
        }
    }

    public void renderDebug() {
        // Enable transparencies
        Gdx.gl.glEnable(GL20.GL_BLEND);
        rtsMapRenderer.drawMap(map);
    }

    @Override
    public void addEntity(IBoundsObject entity) {
        map.add(entity);
    }

    @Override
    public void removeEntity(IBoundsObject entity) {
        map.remove(entity);
    }

    @Override
    public void updateEntity(IBoundsObject entity) {
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
    public Set<IBoundsObject> getNearbyEntities(Vector2 pos) {
        return map.findNearbyObjects(pos);
    }

    @Override
    public Set<IMapCell<IBoundsObject>> getNearbyBlockedNodes(Vector2 pos) {
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
    public boolean walkable(Vector2 ini, Vector2 end, IBoundsObject entity) {
        float step = entity.bounds().width / 2f;
        Rectangle r = new Rectangle(0, 0, entity.bounds().width, entity.bounds().height);
        Vector2 current = ini.clone();
        Vector2 advance = end.clone().subtract(ini);
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

        VectorPool.returnObjects(current, advance);

        return true;
    }

    @Override
    public TerrainType getTerrainType(Vector2 point) {
        return map.getCell(point).getTerrain();
    }

    @Override
    public IMapCell<IBoundsObject> getCell(Vector2 point) {
        return map.getCell(point);
    }

    @Override
    public IMapCell<IBoundsObject> getCell(float x, float y) {
        return map.getCell(x, y);
    }

    @Override
    public MapObjects getMapObjects() {
        return mapObjects;
    }

}
