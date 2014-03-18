package com.ts.rts.scene.map;

import java.util.List;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
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

/**
 * Abstract class containing the features common to all {@link IRTSMap} objects.
 * 
 * @author Toni Sagrista
 * 
 */
public abstract class RTSAbstractMap implements IRTSMap {
    public static final String BASE_LAYER_NAME = "BaseLayer";
    public static final String BASE_LAYER_OVERLAY_NAME = "BaseLayerOverlay";
    public static final String OBJECTS_LAYER_NAME = "ObjectsLayer";
    public static final String OVERLAYS_LAYER_NAME = "Overlays";

    protected RTSGame game;

    protected IMapGen<IBoundsObject> mapGen = null;
    protected IMap<IBoundsObject> map = null;
    protected IAStar<IBoundsObject> astar = null;
    protected TiledMap tiledMap = null;

    protected TiledMapTileLayer baseLayer = null;
    protected TiledMapTileLayer baseLayerOverlay = null;
    protected MapObjects mapObjects = null;
    protected List<IMapCell<IBoundsObject>> path = null;
    protected MapRenderer mapRenderer;
    protected IMapRenderer rtsMapRenderer;
    protected FogOfWar fogOfWar;
    public boolean useFogOfWar;

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
	baseLayer = (TiledMapTileLayer) mapLayers.get(BASE_LAYER_NAME);
	baseLayerOverlay = (TiledMapTileLayer) mapLayers.get(BASE_LAYER_OVERLAY_NAME);
	MapLayer objectsLayer = mapLayers.get(OBJECTS_LAYER_NAME);
	if (objectsLayer != null)
	    mapObjects = objectsLayer.getObjects();
	// Initialize map renderer
	mapRenderer = new OrthogonalTiledMapRenderer(tiledMap, RTSGame.getSpriteBatch());

	initializeMapGenAndRenderer();

	// And now let's initialize the quadtree
	map = mapGen.generateMap(tiledMap);

	astar = new AStar<IBoundsObject>(map);

	// Number of tiles that fit in the canvas, to render.
	renderTileWidth = Gdx.graphics.getWidth() / (int) baseLayer.getTileWidth();
	renderTileHeight = Gdx.graphics.getHeight() / (int) baseLayer.getTileHeight();

	// Fog of war
	this.useFogOfWar = useFogOfWar;
	if (useFogOfWar) {
	    fogOfWar = new FogOfWar((int) (baseLayer.getWidth()), (int) (baseLayer.getHeight()),
		    (int) baseLayer.getTileWidth());
	}
    }

    protected abstract void initializeMapGenAndRenderer();

    public int getWidth() {
	return (int) (baseLayer.getWidth() * baseLayer.getTileWidth());
    }

    public int getHeight() {
	return (int) (baseLayer.getHeight() * baseLayer.getTileHeight());
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
	mapRenderer.render(new int[] { 0, 1 });
    }

    @Override
    public void renderOverlays(Camera camera) {
	// Check camera and canvas
	mapRenderer.setView((OrthographicCamera) camera.getLibgdxCamera());
	mapRenderer.render(new int[] { 2 });
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
    public MapObjects getMapObjects() {
	return mapObjects;
    }

}
