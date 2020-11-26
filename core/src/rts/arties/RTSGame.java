package rts.arties;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.TimeUtils;
import rts.arties.datastructure.geom.Vector3;
import rts.arties.input.KeyboardListener;
import rts.arties.input.PanListener;
import rts.arties.input.SelectionListener;
import rts.arties.input.ZoomListener;
import rts.arties.scene.cam.Camera;
import rts.arties.scene.ecs.EntityPositionComparator;
import rts.arties.scene.ecs.Mapper;
import rts.arties.scene.ecs.component.*;
import rts.arties.scene.ecs.entity.GunnerHelper;
import rts.arties.scene.ecs.entity.MapObjectHelper;
import rts.arties.scene.ecs.entity.TankHelper;
import rts.arties.scene.ecs.entity.WalkerHelper;
import rts.arties.scene.ecs.system.*;
import rts.arties.scene.map.IRTSMap;
import rts.arties.scene.map.RTSGridMapTiledMap;
import rts.arties.scene.selection.Selection;
import rts.arties.scene.unit.group.UnitGroup;
import rts.arties.scene.unit.group.UnitGroupManager;
import rts.arties.scene.unit.steeringbehaviour.IEntity;
import rts.arties.scene.unit.steeringbehaviour.Path;
import rts.arties.ui.OwnLabel;
import rts.arties.util.Vector2Pool;
import rts.arties.util.Vector3Pool;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Rough implementation of the main engine handler. Obviously this is VERY provisional. A lot of utilities contained in
 * this class must be taken to proper model classes.
 * TODO A lot of stuff...
 *
 * @author Toni Sagrista
 */
public class RTSGame implements ApplicationListener {
    private static final Logger logger = new Logger(RTSGame.class.getSimpleName(), Logger.INFO);

    private static AssetManager assets;

    public static AssetManager assets() {
        return assets;
    }

    // Debug mode
    private static boolean debugInfo = false;
    // System information like FPS, etc.
    private static final boolean systemInfo = true;

    public static void toggleDebug() {
        setDebug(!debugInfo);
    }

    public static void setDebug(boolean debug) {
        debugInfo = debug;
        if (game != null && status == AppStatus.READY) {
            if (debug) {
                game.engine.addSystem(game.ders);
                game.engine.addSystem(game.dmrs);
            } else {
                game.engine.removeSystem(game.ders);
                game.engine.removeSystem(game.dmrs);
            }
        }
    }

    public enum AppStatus {
        BOOTING,
        LOADING,
        READY
    }

    public static AppStatus status = AppStatus.BOOTING;

    private Camera camera;
    private IRTSMap map;
    private Engine engine;
    public Selection selection;
    private final float zoom = 1;

    private long startTime;

    /**
     * Sprite batch
     */
    public SpriteBatch spriteBatch;
    /**
     * This is the global shape renderer used to render objects in the camera reference system
     */
    public ShapeRenderer cameraShapeRenderer;

    /**
     * Shape renderer to render objects in the screen reference system
     */
    public ShapeRenderer screenShapeRenderer;

    private Skin skin;
    private Stage stage;
    private OwnLabel fps;

    private KeyboardListener keyboardListener;
    private PanListener panListener;
    private ZoomListener zoomListener;

    // Families
    private Family renderableFamily, renderableWalkerFamily, positionFamily, movementFamily, playerFamily, debugFamily, objectFamily, mapFamily;
    // Systems
    private EntitySystem ibrs, iwrs, imos, ders, dmrs, uus, ous, brs, uirs, mbrs, mors;

    // Is the game paused?
    private boolean paused = false;
    // Is the window focused
    private boolean focused = true;

    public static RTSGame game;
    public ShaderProgram objectsShader, mapShader;

    public static SpriteBatch getSpriteBatch() {
        return game.spriteBatch;
    }

    public void initShaders() {
        ShaderProgram.pedantic = false;
        objectsShader = new ShaderProgram(Gdx.files.internal("data/shaders/default.vert.glsl"), Gdx.files.internal("data/shaders/objects.frag.glsl"));
        mapShader = new ShaderProgram(Gdx.files.internal("data/shaders/default.vert.glsl"), Gdx.files.internal("data/shaders/map.frag.glsl"));

        if (!objectsShader.isCompiled()) {
            logger.error(objectsShader.getLog());
        }

        if (!mapShader.isCompiled()) {
            logger.error(mapShader.getLog());
        }
    }

    @Override
    public void create() {
        game = this;
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        cameraShapeRenderer = new ShapeRenderer();
        screenShapeRenderer = new ShapeRenderer();

        // Vector pools
        Vector2Pool.initialize(100);
        Vector3Pool.initialize(5000);

        initShaders();
        spriteBatch = new SpriteBatch(5000, objectsShader);

        // Here we use genuine info in the map to find out blocked areas
        map = new RTSGridMapTiledMap(this, "data/maps/Snow01.tmx");

        InputMultiplexer multiplexer = new InputMultiplexer();

        // Initialize UI
        FileHandle fh = Gdx.files.internal("data/skins/dark-blue/dark-blue.json");
        skin = new Skin(fh);
        stage = new Stage();
        multiplexer.addProcessor(stage);

        // Initialize camera
        camera = Camera.initialize(w / 2, h / 2, map.getWidth(), map.getHeight(), w, h);

        // Manage selection
        selection = new Selection(this);
        SelectionListener selectionListener = new SelectionListener(camera, map, selection);
        multiplexer.addProcessor(selectionListener);

        // Manage camera pan/zoom
        panListener = new PanListener(camera, map, selection);
        zoomListener = new ZoomListener(camera);
        multiplexer.addProcessor(panListener);
        multiplexer.addProcessor(zoomListener);

        // Keyboard input
        keyboardListener = new KeyboardListener();
        multiplexer.addProcessor(keyboardListener);

        Gdx.input.setInputProcessor(multiplexer);

        /**
         * Initialize textures and scene entities
         */
        assets = new AssetManager();
        assets.load("data/tex/base-textures.atlas", TextureAtlas.class);
        status = AppStatus.LOADING;
        logger.info("Loading assets...");
        Table loadingTable = new Table(skin);
        loadingTable.setFillParent(true);
        loadingTable.add(new Label("Loading...", skin, "main-title-s"));
        stage.addActor(loadingTable);

        // Engine
        engine = new PooledEngine();

        // Initialize groups
        UnitGroupManager.initialize();

        // Map entity
        Entity mapEntity = engine.createEntity();
        MapComponent mc = engine.createComponent(MapComponent.class);
        mc.map = map;
        mapEntity.add(mc);

        // Add entities to engine
        engine.addEntity(mapEntity);
        engine.addEntity(TankHelper.create(engine, map, 300f, 220f, 100f));
        engine.addEntity(TankHelper.create(engine, map, 250f, 220f, 100f));
        engine.addEntity(TankHelper.create(engine, map, 200f, 220f, 100f));
        engine.addEntity(TankHelper.create(engine, map, 150f, 220f, 100f));
        engine.addEntity(TankHelper.create(engine, map, 100f, 220f, 100f));
        engine.addEntity(TankHelper.create(engine, map, 50f, 220f, 20f));

        engine.addEntity(GunnerHelper.create(engine, map, 280f, 320f, 30f));

        engine.addEntity(WalkerHelper.create(engine, map, 280f, 120f, 30f));
        engine.addEntity(WalkerHelper.create(engine, map, 250f, 120f, 30f));
        engine.addEntity(WalkerHelper.create(engine, map, 220f, 120f, 30f));
        engine.addEntity(WalkerHelper.create(engine, map, 190f, 120f, 30f));
        engine.addEntity(WalkerHelper.create(engine, map, 160f, 120f, 30f));
        engine.addEntity(WalkerHelper.create(engine, map, 130f, 120f, 30f));
        engine.addEntity(WalkerHelper.create(engine, map, 100f, 120f, 30f));
        engine.addEntity(WalkerHelper.create(engine, map, 70f, 120f, 30f));
        engine.addEntity(WalkerHelper.create(engine, map, 40f, 120f, 30f));
        engine.addEntity(WalkerHelper.create(engine, map, 280f, 90f, 30f));
        engine.addEntity(WalkerHelper.create(engine, map, 250f, 90f, 30f));
        engine.addEntity(WalkerHelper.create(engine, map, 220f, 90f, 30f));
        engine.addEntity(WalkerHelper.create(engine, map, 190f, 90f, 30f));
        engine.addEntity(WalkerHelper.create(engine, map, 160f, 90f, 30f));
        engine.addEntity(WalkerHelper.create(engine, map, 130f, 90f, 30f));
        engine.addEntity(WalkerHelper.create(engine, map, 100f, 90f, 30f));
        engine.addEntity(WalkerHelper.create(engine, map, 70f, 90f, 30f));
        engine.addEntity(WalkerHelper.create(engine, map, 40f, 90f, 30f));
        engine.addEntity(WalkerHelper.create(engine, map, 280f, 400f, 30f));
        engine.addEntity(WalkerHelper.create(engine, map, 250f, 400f, 30f));
        engine.addEntity(WalkerHelper.create(engine, map, 220f, 400f, 30f));
        engine.addEntity(WalkerHelper.create(engine, map, 190f, 400f, 30f));
        engine.addEntity(WalkerHelper.create(engine, map, 160f, 400f, 30f));
        engine.addEntity(WalkerHelper.create(engine, map, 130f, 400f, 30f));
        engine.addEntity(WalkerHelper.create(engine, map, 100f, 400f, 30f));
        engine.addEntity(WalkerHelper.create(engine, map, 70f, 400f, 30f));
        engine.addEntity(WalkerHelper.create(engine, map, 40f, 400f, 30f));
        engine.addEntity(WalkerHelper.create(engine, map, 280f, 450f, 30f));
        engine.addEntity(WalkerHelper.create(engine, map, 250f, 450f, 30f));
        engine.addEntity(WalkerHelper.create(engine, map, 220f, 450f, 30f));
        engine.addEntity(WalkerHelper.create(engine, map, 190f, 450f, 30f));
        engine.addEntity(WalkerHelper.create(engine, map, 160f, 450f, 30f));
        engine.addEntity(WalkerHelper.create(engine, map, 130f, 450f, 30f));
        engine.addEntity(WalkerHelper.create(engine, map, 100f, 450f, 30f));
        engine.addEntity(WalkerHelper.create(engine, map, 70f, 450f, 30f));
        engine.addEntity(WalkerHelper.create(engine, map, 40f, 450f, 30f));
        engine.addEntity(WalkerHelper.create(engine, map, 280f, 500f, 30f));
        engine.addEntity(WalkerHelper.create(engine, map, 250f, 500f, 30f));
        engine.addEntity(WalkerHelper.create(engine, map, 220f, 500f, 30f));
        engine.addEntity(WalkerHelper.create(engine, map, 190f, 500f, 30f));
        engine.addEntity(WalkerHelper.create(engine, map, 160f, 500f, 30f));
        engine.addEntity(WalkerHelper.create(engine, map, 130f, 500f, 30f));
        engine.addEntity(WalkerHelper.create(engine, map, 100f, 500f, 30f));
        engine.addEntity(WalkerHelper.create(engine, map, 70f, 500f, 30f));
        engine.addEntity(WalkerHelper.create(engine, map, 40f, 500f, 30f));
        engine.addEntity(WalkerHelper.create(engine, map, 280f, 550f, 30f));
        engine.addEntity(WalkerHelper.create(engine, map, 250f, 550f, 30f));
        engine.addEntity(WalkerHelper.create(engine, map, 220f, 550f, 30f));
        engine.addEntity(WalkerHelper.create(engine, map, 190f, 550f, 30f));
        engine.addEntity(WalkerHelper.create(engine, map, 160f, 550f, 30f));
        engine.addEntity(WalkerHelper.create(engine, map, 130f, 550f, 30f));
        engine.addEntity(WalkerHelper.create(engine, map, 100f, 550f, 30f));
        engine.addEntity(WalkerHelper.create(engine, map, 70f, 550f, 30f));
        engine.addEntity(WalkerHelper.create(engine, map, 90f, 150f, 30f));

        // Map objects
        MapObjects mos = map.getMapObjects();
        if (mos != null) {
            Iterator<MapObject> it = mos.iterator();
            while (it.hasNext()) {
                MapObject mo = it.next();
                Entity object = MapObjectHelper.create(engine, mo, map);
                engine.addEntity(object);
            }
        }

        // Families
        positionFamily = Family.all(PositionComponent.class, MapComponent.class).get();
        movementFamily = Family.all(PositionComponent.class, MovementComponent.class, MapComponent.class).get();
        objectFamily = Family.all(PositionComponent.class, BodyComponent.class, RenderableBaseComponent.class, VisibilityComponent.class).exclude(PlayerComponent.class, MovementComponent.class).get();
        renderableFamily = Family.all(RenderableBaseComponent.class).get();
        renderableWalkerFamily = Family.all(RenderableBaseComponent.class, RenderableWalkerComponent.class).get();
        playerFamily = Family.all(PlayerComponent.class, PositionComponent.class, RenderableBaseComponent.class).get();
        debugFamily = Family.all(PositionComponent.class, MovementComponent.class, BodyComponent.class, RenderableBaseComponent.class).get();
        mapFamily = Family.all(MapComponent.class).exclude(PositionComponent.class, MovementComponent.class, BodyComponent.class, RenderableBaseComponent.class).get();

        // Init systems
        ibrs = new InitializeBaseRenderableSystem(renderableFamily, 1, assets);
        iwrs = new InitializeWalkerRenderableSystem(renderableWalkerFamily, 2, assets);
        imos = new InitializeObjectsSystem(objectFamily, 3);

        // Update systems
        uus = new UnitUpdateSystem(movementFamily, 1);
        ous = new ObjectUpdateSystem(objectFamily, 0.5f, 2);

        // Render systems
        mbrs = new MapBaseRenderSystem(mapFamily, 100, camera, mapShader, playerFamily);
        brs = new BaseRenderSystem(renderableFamily, new EntityPositionComparator(), 110, spriteBatch, objectsShader);
        uirs = new UnitInfoRenderSystem(playerFamily, 120, cameraShapeRenderer, spriteBatch);
        mors = new MapOverlaysRenderSystem(mapFamily, 130);
        dmrs = new DebugMapRenderSystem(mapFamily, 140);
        ders = new DebugEntityRenderSystem(debugFamily, 150, cameraShapeRenderer, spriteBatch);

        // Add initalization systems to engine
        engine.addSystem(ibrs);
        engine.addSystem(iwrs);
        engine.addSystem(imos);

        startTime = TimeUtils.nanoTime();
    }

    public void doneLoading() {
        map.doneLoading(assets);

        // Runi initialization systems
        engine.update(0);

        // Remove initialization systems from engine
        engine.removeSystem(ibrs);
        engine.removeSystem(iwrs);
        engine.removeSystem(imos);

        // Add update systems
        engine.addSystem(uus);
        engine.addSystem(ous);

        // Add render systems
        engine.addSystem(mbrs);
        engine.addSystem(brs);
        engine.addSystem(uirs);
        engine.addSystem(mors);

        if (debugInfo) {
            engine.addSystem(dmrs);
            engine.addSystem(ders);
        }
    }

    @Override
    public void dispose() {
        logger.info("Dispose called");
        spriteBatch.dispose();
        map.dispose();
    }

    @Override
    public void render() {
        float deltaSecs = Gdx.graphics.getDeltaTime();
        if (systemInfo) {
            // FPS
            final long nanoTime = TimeUtils.nanoTime();
            // Every 1 seconds
            if (nanoTime - startTime > 1000000000) /* 1,000,000,000ns == one second */ {
                if (fps != null)
                    fps.setText(Gdx.graphics.getFramesPerSecond() + " FPS");
            }
        }

        deltaSecs = Math.min(deltaSecs, .4f);

        if (status == AppStatus.LOADING) {
            assets.update();
            stage.act(deltaSecs);
            stage.draw();

            if (assets.isFinished()) {
                doneLoading();
                status = AppStatus.READY;
                stage.clear();
                Table fpsTable = new Table(skin);
                fpsTable.setFillParent(true);
                fpsTable.top().right();
                fps = new OwnLabel("", skin);
                fpsTable.add(fps).padRight(5f).padTop(5f);
                stage.addActor(fpsTable);
                logger.info("Loading finished");

            }
        } else if (status == AppStatus.READY) {
            assets.update();

            spriteBatch.setProjectionMatrix(camera.combined());
            cameraShapeRenderer.setProjectionMatrix(camera.combined());

            if (!paused) {
                // Update camera position
                camera.update(deltaSecs);
                UnitGroupManager.getInstance().update();
                stage.act(deltaSecs);

                // Clear screen
                Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
                Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
                // Enable blending
                Gdx.gl.glEnable(GL20.GL_BLEND);
                Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

                // Update engine to update and render scene
                engine.update(deltaSecs);

                // Draw current selection
                selection.render();

                // Draw user interface
                stage.draw();
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        logger.debug("Resize called: [" + width + ", " + height + "]");

        camera.resize(width, height);

        screenShapeRenderer.setProjectionMatrix(camera.combined());

        panListener.resize(width, height);
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

    public Camera camera() {
        return camera;
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
     * @param group
     * @param x
     * @param y
     */
    public void moveUnits(UnitGroup group, int x, int y, int z) {
        if (!group.isEmpty()) {
            if (group.size() == 1) {
                moveUnit(group.iterator().next(), x, y, z);
            } else {
                moveUnitGroup(group, x, y, z);
            }
        }
    }

    public void moveUnitGroup(UnitGroup group, int x, int y, int z) {
        // calculate width and height of target square
        int side = 1;
        for (; group.size() > side * side; side++) {
        }
        float longestSide = 0;
        for (IEntity u : group) {
            if ((u.softRadius() * 2 + 10) > longestSide) {
                longestSide = u.softRadius() * 2 + 10;
            }
        }
        float side2 = side * longestSide / 2;
        int col = 0, row = 0;

        group.sortByPosition();
        for (IEntity u : group) {
            Vector3 targetPos = Vector3Pool.getObject(x - side2 + longestSide * col + longestSide / 2, y - side2 + longestSide * row + longestSide / 2, z);
            if (!map.overlapsWithBlocked(new Rectangle(x - u.hardRadius().width / 2, y - u.hardRadius().height / 2, u.hardRadius().width, u.hardRadius().height))) {
                // Calculate path
                Path path = new Path(map.findPath(u.pos().x, u.pos().y, targetPos.x, targetPos.y), u.pos().x, u.pos().y, u.pos().z, targetPos.x, targetPos.y, targetPos.z);
                path.smooth(u);
                if (path.size() > 0)
                    u.steeringBehaviours().addFollowPath(path);
            }
            Vector3Pool.returnObject(targetPos);

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
    public void moveUnit(IEntity unit, int x, int y, int z) {
        Path path = new Path(map.findPath(unit.pos().x, unit.pos().y, (float) x, (float) y), unit.pos().x, unit.pos().y, unit.pos().z, (float) x, (float) y, (float) z);
        path.smooth(unit);
        unit.steeringBehaviours().addFollowPath(path);
    }

    /**
     * Gets the unit whose image is colliding with the given position.
     *
     * @param x The x in map coordinates
     * @param y The y in map coordinates
     * @return
     */
    public IEntity getCollidingUnitImage(int x, int y, int z) {
        // New units - ECS
        ImmutableArray<Entity> player = engine.getEntitiesFor(playerFamily);
        for (Entity e : player) {
            RenderableBaseComponent rbc = Mapper.rbase.get(e);
            if (rbc != null && rbc.imageBounds.contains(x, y))
                return Mapper.body.get(e).me;
        }

        return null;
    }

    /**
     * Gets the player units inside the given rectangle
     *
     * @param rect The rectangle in map coordinates
     * @return
     */
    public Set<IEntity> getInsideUnits(Rectangle rect) {
        Set<IEntity> list = new HashSet<>();
        // New units - ECS
        ImmutableArray<Entity> player = engine.getEntitiesFor(playerFamily);
        for (Entity e : player) {
            RenderableBaseComponent rbc = Mapper.rbase.get(e);
            if (rbc != null && rect.overlaps(rbc.imageBounds))
                list.add(Mapper.body.get(e).me);
        }
        return list;
    }

    public static Camera getCamera() {
        return game.camera;
    }

    public void focusLost() {
        this.focused = false;
        if (camera != null) {
            camera.stop();
        }
    }

    public void focusGained() {
        this.focused = true;
    }

    public boolean focused() {
        return focused;
    }

}
