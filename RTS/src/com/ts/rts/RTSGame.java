package com.ts.rts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Logger;
import com.ts.rts.datastructure.geom.Vector2;
import com.ts.rts.image.TextureManager;
import com.ts.rts.input.KeyboardListener;
import com.ts.rts.input.PanListener;
import com.ts.rts.input.SelectionListener;
import com.ts.rts.scene.cam.Camera;
import com.ts.rts.scene.map.IRTSMap;
import com.ts.rts.scene.map.RTSGridMapTiledMap;
import com.ts.rts.scene.selection.Selection;
import com.ts.rts.scene.unit.Gunner;
import com.ts.rts.scene.unit.PhysicalObject;
import com.ts.rts.scene.unit.PositionPhysicalEntity;
import com.ts.rts.scene.unit.Tank;
import com.ts.rts.scene.unit.Unit;
import com.ts.rts.scene.unit.group.UnitGroup;
import com.ts.rts.scene.unit.group.UnitGroupManager;
import com.ts.rts.scene.unit.steeringbehaviour.Path;
import com.ts.rts.util.VectorPool;

/**
 * Rough implementation of the main engine handler. Obviously this is VERY provisional. A lot of utilities contained in
 * this class must be taken to proper model classes.
 * TODO A lot of stuff...
 * 
 * @author Toni Sagrista
 * 
 */
public class RTSGame implements ApplicationListener {

    private Logger logger;

    public OrthographicCamera orthoCamera;
    private SpriteBatch batch;

    /** My variables **/
    public static boolean debugRender = false;
    public static boolean debug = true;
    public static boolean drawShadows = true;

    private Camera camera;
    private IRTSMap map;
    private List<PositionPhysicalEntity> entities = new ArrayList<PositionPhysicalEntity>();
    public List<Unit> player = new ArrayList<Unit>();
    public Selection selection;

    /**
     * This is the global shape renderer used to render objects in the camera reference system
     */
    public ShapeRenderer cameraShapeRenderer;

    /**
     * Shape renderer to render objects in the screen reference system
     */
    public ShapeRenderer screenShapeRenderer;

    private Stage hud;

    long lastMillis;
    /** Is the game paused? **/
    private boolean paused = false;

    /** FPS logger **/
    FPSLogger fpsLogger;

    public static RTSGame game;
    public ShaderProgram objectsShader, mapShader;

    public static SpriteBatch getSpriteBatch() {
	return game.batch;
    }

    public void initShaders() {
	ShaderProgram.pedantic = false;
	objectsShader = new ShaderProgram(Gdx.files.internal("data/shaders/default.vert.glsl"),
		Gdx.files.internal("data/shaders/objects.frag.glsl"));
	mapShader = new ShaderProgram(Gdx.files.internal("data/shaders/default.vert.glsl"),
		Gdx.files.internal("data/shaders/map.frag.glsl"));

	if (!objectsShader.isCompiled()) {
	    logger.error(objectsShader.getLog());
	}

	if (!mapShader.isCompiled()) {
	    logger.error(mapShader.getLog());
	}
    }

    @Override
    public void create() {
	logger = new Logger(this.getClass().getSimpleName(), Logger.INFO);
	fpsLogger = new FPSLogger();
	game = this;
	float w = Gdx.graphics.getWidth();
	float h = Gdx.graphics.getHeight();

	cameraShapeRenderer = new ShapeRenderer();
	screenShapeRenderer = new ShapeRenderer();
	VectorPool.initialize(5000);

	orthoCamera = new OrthographicCamera(w, h);
	orthoCamera.setToOrtho(false, w, h);
	orthoCamera.zoom = 1f;
	initShaders();
	batch = new SpriteBatch(300, objectsShader);

	// Here we use genuine info in the map to find out blocked areas
	map = new RTSGridMapTiledMap(this, "data/maps/3030test.tmx");

	InputMultiplexer multiplexer = new InputMultiplexer();

	// Initialize hud
	hud = new Stage();
	multiplexer.addProcessor(hud);

	// Manage selection
	selection = new Selection(this);
	SelectionListener selectionListener = new SelectionListener(selection);
	multiplexer.addProcessor(selectionListener);

	// Manage camera pan
	camera = Camera.initialize(orthoCamera, w / 2, h / 2, map.getWidth(), map.getHeight(), w, h);
	PanListener panListener = new PanListener(camera, selection);
	multiplexer.addProcessor(panListener);

	// Keyboard input
	KeyboardListener keyboardListener = new KeyboardListener();
	multiplexer.addProcessor(keyboardListener);

	Gdx.input.setInputProcessor(multiplexer);

	/**
	 * Initialize textures and scene entities
	 */
	TextureManager.initialize();

	// Initialize groups
	UnitGroupManager.initialize();

	// Initialize unitsmessage
	Unit tank1 = new Tank(200f, 260f, map);
	Unit tank2 = new Tank(240f, 260f, map);
	Unit tank3 = new Tank(280f, 260f, map);
	Unit tank4 = new Tank(200f, 220f, map);
	Unit tank5 = new Tank(240f, 220f, map);
	tank5.setHp(75f);
	Unit tank6 = new Tank(280f, 220f, map);
	tank6.setHp(50f);

	Unit tank7 = new Tank(180f, 1500f, map);
	tank7.setHp(10f);

	Unit gooner = new Gunner(80f, 140f, map);

	entities.add(gooner);
	//	entities.add(tank1);
	//	entities.add(tank2);
	//	entities.add(tank3);
	//	entities.add(tank4);
	//	entities.add(tank5);
	//	entities.add(tank6);
	//	entities.add(tank7);

	player.add(gooner);
	//	player.add(tank1);
	//	player.add(tank2);
	//	player.add(tank3);
	//	player.add(tank4);
	//	player.add(tank5);
	//	player.add(tank6);
	//	player.add(tank7);

	MapObjects mos = map.getMapObjects();
	if (mos != null) {
	    Iterator<MapObject> it = mos.iterator();
	    while (it.hasNext()) {
		MapObject mo = it.next();
		String name = mo.getName();
		int x = mo.getProperties().get("x", Integer.class);
		int y = mo.getProperties().get("y", Integer.class);

		// Trees with an offsetY of 20
		PhysicalObject po = new PhysicalObject(x, y, 0f, 25f, name, map);
		entities.add(po);
	    }

	}

	lastMillis = System.currentTimeMillis();

    }

    public boolean isVisible(Vector2 point) {
	boolean vis = false;
	for (Unit u : player) {
	    vis = vis || u.pos.distance(point) < u.viewingDistance * 2.5;
	}
	return vis;
    }

    @Override
    public void dispose() {
	logger.info("Dispose called");
	batch.dispose();
	map.dispose();
    }

    @Override
    public void render() {
	float deltaSecs = Gdx.graphics.getDeltaTime();
	if (debug) {
	    if (Math.floor(System.nanoTime() / 1E9f) % 5f == 0) {
		fpsLogger.log();
	    }
	}

	deltaSecs = Math.min(deltaSecs, .4f);
	if (!paused) {
	    updateScene(deltaSecs);
	}
	renderScene(deltaSecs);
    }

    /**
     * Updates the scne
     * 
     * @param delta
     *            The delta time in seconds
     */
    public void updateScene(float deltaSecs) {
	// Update camera position
	camera.update(deltaSecs);

	// And update all units
	for (PositionPhysicalEntity e : entities)
	    e.update(deltaSecs);

	UnitGroupManager.getInstance().update();

	hud.act(deltaSecs);
    }

    public void renderScene(float deltaSecs) {
	Collections.sort(entities);

	// Clear screen
	Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
	Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
	cameraShapeRenderer.setProjectionMatrix(orthoCamera.combined);

	// Contains circular light positions and radius
	float[] lights = new float[entities.size() * 3];
	// Contains shadow positions and width and height
	int i = 0;
	for (PositionPhysicalEntity e : entities) {
	    if (e.viewingDistance > 0) {
		lights[i++] = e.pos.x;
		lights[i++] = e.pos.y;
		lights[i++] = e.viewingDistance;
	    }
	}

	mapShader.begin();
	mapShader.setUniform3fv("u_lights", lights, 0, i);
	mapShader.setUniformi("u_light_count", i / 3);
	mapShader.setUniformf("u_camera_offset", camera.pos.x - Gdx.graphics.getWidth() / 2, camera.pos.y - Gdx.graphics.getHeight() / 2);
	mapShader.end();

	map.renderBase(camera);

	batch.setProjectionMatrix(camera.getLibgdxCamera().combined);
	objectsShader.begin();
	objectsShader.setUniformf("u_camera_offset", camera.pos.x - Gdx.graphics.getWidth() / 2, camera.pos.y - Gdx.graphics.getHeight() / 2);
	objectsShader.setUniformi("u_draw_shadows", (drawShadows ? 1 : -1));
	objectsShader.end();

	/** Entities **/
	for (PositionPhysicalEntity ppe : entities) {
	    batch.begin();
	    ppe.render();
	    batch.end();
	}

	//map.renderOverlays(camera);

	// Render debug info
	if (debugRender) {
	    UnitGroupManager.getInstance().render();
	    map.renderDebug();
	    for (PositionPhysicalEntity ppe : entities) {
		ppe.renderDebug();
	    }
	}

	// Render selections
	for (PositionPhysicalEntity ppe : entities) {
	    ppe.renderSelection();
	}

	selection.render();

	hud.draw();

    }

    @Override
    public void resize(int width, int height) {
	logger.info("Resize called");

	// whenever our screen resizes, we need to update our uniform
	objectsShader.begin();
	objectsShader.setUniformf("u_viewport_size", Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	objectsShader.end();

	mapShader.begin();
	mapShader.setUniformf("u_viewport_size", Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	mapShader.end();
    }

    @Override
    public void pause() {
	logger.info("Pause called");
	paused = true;
    }

    @Override
    public void resume() {
	logger.info("Resume called");

	paused = false;
    }

    public IRTSMap getMap() {
	return map;
    }

    public int getMapWidth() {
	return map.getWidth();
    }

    public int getMapHeight() {
	return map.getHeight();
    }

    /**
     * Commands a group of units to move to the given point in the map
     * 
     * @param units
     * @param x
     * @param y
     */
    public void moveUnits(UnitGroup group, int x, int y) {
	if (!group.isEmpty()) {
	    if (group.size() == 1) {
		moveUnit(group.iterator().next(), x, y);
	    } else {
		moveUnitGroup(group, x, y);
	    }
	}
    }

    public void moveUnitGroup(UnitGroup group, int x, int y) {
	// calculate width and height of target square
	int side = 1;
	for (; group.size() > side * side; side++) {
	}
	float longestSide = 0;
	for (Unit u : group) {
	    if ((u.softRadius.radius * 2 + 10) > longestSide) {
		longestSide = u.softRadius.radius * 2 + 10;
	    }
	}
	float side2 = side * longestSide / 2;
	int col = 0, row = 0;

	Collections.sort(group);
	for (Unit u : group) {
	    Vector2 targetPos = VectorPool.getObject(x - side2 + longestSide * col + longestSide / 2, y - side2
		    + longestSide * row + longestSide / 2);
	    if (!map.overlapsWithBlocked(new Rectangle(x - u.hardRadius.width / 2, y - u.hardRadius.height / 2,
		    u.hardRadius.width, u.hardRadius.height))) {
		// Calculate path
		Path path = new Path(map.findPath(u.pos.x, u.pos.y, targetPos.x, targetPos.y), u.pos, targetPos.x,
			targetPos.y);
		path.smooth(u, map);
		if (path.size() > 0)
		    u.steeringBehaviours.addFollowPath(path);
	    }
	    VectorPool.returnObject(targetPos);

	    // Advance position in grid
	    col++;
	    if (col >= side) {
		col = 0;
		row++;
	    }
	}

    }

    /**
     * Commands a unit to move to the given point in the map
     * 
     * @param unit
     * @param x
     * @param y
     */
    public void moveUnit(Unit unit, int x, int y) {
	Path path = new Path(map.findPath(unit.pos.x, unit.pos.y, (float) x, (float) y), unit.pos, (float) x, (float) y);
	path.smooth(unit, map);
	unit.steeringBehaviours.addFollowPath(path);
    }

    /**
     * Gets the unit whose hard radius is colliding with the given position.
     * 
     * @param x
     *            The x in map coordinates
     * @param y
     *            The y in map coordinates
     * @return
     */
    public PositionPhysicalEntity getCollidingUnit(int x, int y) {

	for (PositionPhysicalEntity ppe : entities) {
	    if (ppe.isColliding(x, y)) {
		return ppe;
	    }
	}

	return null;
    }

    /**
     * Gets the unit whose image is colliding with the given position.
     * 
     * @param x
     *            The x in map coordinates
     * @param y
     *            The y in map coordinates
     * @return
     */
    public PositionPhysicalEntity getCollidingUnitImage(int x, int y) {

	for (PositionPhysicalEntity ppe : entities) {
	    if (ppe.isImageColliding(x, y)) {
		return ppe;
	    }
	}

	return null;
    }

    /**
     * Gets the units inside the given rectangle
     * 
     * @param rect
     *            The rectangle in map coordinates
     * @return
     */
    public Set<PositionPhysicalEntity> getInsideUnits(Rectangle rect) {
	Set<PositionPhysicalEntity> list = new HashSet<PositionPhysicalEntity>();
	for (PositionPhysicalEntity u : entities) {
	    if (rect.overlaps(u.imageBounds)) {
		list.add(u);
	    }
	}
	return list;
    }

    public static Camera getCamera() {
	return game.camera;
    }

    public static OrthographicCamera getGdxCamera() {
	return game.orthoCamera;
    }

}
