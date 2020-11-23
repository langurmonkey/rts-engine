package rts.arties;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.TimeUtils;
import rts.arties.datastructure.geom.Vector2;
import rts.arties.datastructure.geom.Vector3;
import rts.arties.input.KeyboardListener;
import rts.arties.input.PanListener;
import rts.arties.input.SelectionListener;
import rts.arties.scene.cam.Camera;
import rts.arties.scene.ecs.Mapper;
import rts.arties.scene.ecs.component.*;
import rts.arties.scene.ecs.entity.TankHelper;
import rts.arties.scene.ecs.system.*;
import rts.arties.scene.map.IRTSMap;
import rts.arties.scene.map.RTSGridMapTiledMap;
import rts.arties.scene.selection.Selection;
import rts.arties.scene.unit.*;
import rts.arties.scene.unit.group.UnitGroup;
import rts.arties.scene.unit.group.UnitGroupManager;
import rts.arties.scene.unit.steeringbehaviour.IEntity;
import rts.arties.scene.unit.steeringbehaviour.Path;
import rts.arties.ui.OwnLabel;
import rts.arties.util.TextUtils;
import rts.arties.util.Vector2Pool;
import rts.arties.util.Vector3Pool;
import rts.arties.util.parse.Parser;

import java.nio.file.Paths;
import java.util.*;

/**
 * Rough implementation of the main engine handler. Obviously this is VERY provisional. A lot of utilities contained in
 * this class must be taken to proper model classes.
 * TODO A lot of stuff...
 *
 * @author Toni Sagrista
 */
public class RTSGame implements ApplicationListener {

    private static AssetManager assets;

    public static AssetManager assets() {
        return assets;
    }

    private Logger logger;

    public OrthographicCamera orthoCamera;

    // Debug mode
    private static boolean debugInfo = false;
    // System information like FPS, etc.
    private static boolean systemInfo = true;

    public static void toggleDebug() {
        setDebug(!debugInfo);
    }

    public static void setDebug(boolean debug) {
        debugInfo = debug;
        if (game != null && status == AppStatus.READY) {
            if (debug)
                game.engine.addSystem(game.drs);
            else
                game.engine.removeSystem(game.drs);
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
    private final List<PositionPhysicalEntity> entities = new ArrayList<>();
    public List<IEntity> player = new ArrayList<>();
    public Selection selection;

    private long startTime;

    /**
     * Sprite batch
     */
    private SpriteBatch spriteBatch;
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

    private PanListener panListener;

    /**
     * Families
     */
    private Family renderableFamily, positionFamily, movementFamily, playerFamily, debugFamily;

    /**
     * Systems
     */
    private EntitySystem ibrs, brs, uirs, drs, uus;

    /**
     * Is the game paused?
     **/
    private boolean paused = false;

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
        logger = new Logger(this.getClass().getSimpleName(), Logger.INFO);
        game = this;
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        cameraShapeRenderer = new ShapeRenderer();
        screenShapeRenderer = new ShapeRenderer();

        // Vector pools
        Vector2Pool.initialize(100);
        Vector3Pool.initialize(5000);

        orthoCamera = new OrthographicCamera(w, h);
        orthoCamera.setToOrtho(false, w, h);
        orthoCamera.zoom = 1f;
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

        // Manage selection
        selection = new Selection(this);
        SelectionListener selectionListener = new SelectionListener(selection);
        multiplexer.addProcessor(selectionListener);

        // Manage camera pan
        camera = Camera.initialize(orthoCamera, w / 2, h / 2, map.getWidth(), map.getHeight(), w, h);
        panListener = new PanListener(camera, selection);
        multiplexer.addProcessor(panListener);

        // Keyboard input
        KeyboardListener keyboardListener = new KeyboardListener();
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

        // Entities
        Entity tank10 = TankHelper.createTank(engine, map, 330f, 210f, 100f);
        Entity tank11 = TankHelper.createTank(engine, map, 340f, 220f, 30f);

        // Add to engine
        engine.addEntity(tank10);
        engine.addEntity(tank11);

        positionFamily = Family.all(PositionComponent.class, MapComponent.class).get();
        movementFamily = Family.all(PositionComponent.class, MovementComponent.class, MapComponent.class).get();
        renderableFamily = Family.all(RenderableBaseComponent.class).get();
        playerFamily = Family.all(PlayerComponent.class, PositionComponent.class, RenderableBaseComponent.class).get();
        debugFamily = Family.all(PositionComponent.class, MovementComponent.class, BodyComponent.class, RenderableBaseComponent.class).get();

        // Init systems
        ibrs = new InitializeBaseRenderableSystem(renderableFamily, assets);

        // Update systems
        uus = new UnitUpdateSystem(movementFamily, 1);

        // Render systems
        brs = new BaseRenderSystem(renderableFamily, 100, spriteBatch);
        uirs = new UnitInfoRenderSystem(playerFamily, 120, cameraShapeRenderer, spriteBatch);
        drs = new DebugRenderSystem(debugFamily, 130, cameraShapeRenderer, spriteBatch);

        // Add initalization systems to engine
        engine.addSystem(ibrs);

        // Initialize units
        Unit tank1 = new Tank(200f, 260f, map);
        Unit tank2 = new Tank(240f, 260f, map);
        Unit tank3 = new Tank(280f, 260f, map);
        Unit tank4 = new Tank(200f, 220f, map);
        Unit tank5 = new Tank(240f, 220f, map);
        tank1.setHp(25f);
        tank5.setHp(75f);
        Unit tank6 = new Tank(280f, 220f, map);
        tank6.setHp(50f);

        Unit tank7 = new Tank(180f, 1500f, map);
        tank7.setHp(10f);

        Unit gooner1 = new Gunner(80f, 140f, map);
        Unit gooner2 = new Gunner(90f, 145f, map);
        Unit gooner3 = new Gunner(95f, 135f, map);
        Unit gooner4 = new Gunner(80f, 125f, map);

        entities.add(gooner1);
        entities.add(gooner2);
        entities.add(gooner3);
        entities.add(gooner4);
        entities.add(tank1);
        entities.add(tank2);
        entities.add(tank3);
        entities.add(tank4);
        entities.add(tank5);
        entities.add(tank6);
        entities.add(tank7);

        player.add(gooner1);
        player.add(gooner2);
        player.add(gooner3);
        player.add(gooner4);
        player.add(tank1);
        player.add(tank2);
        player.add(tank3);
        player.add(tank4);
        player.add(tank5);
        player.add(tank6);
        player.add(tank7);

        MapObjects mos = map.getMapObjects();
        if (mos != null) {
            Iterator<MapObject> it = mos.iterator();
            while (it.hasNext()) {
                MapObject mo = it.next();

                float x = mo.getProperties().get("x", Float.class);
                float y = mo.getProperties().get("y", Float.class);
                float ow = mo.getProperties().get("width", Float.class);

                float shadowOffsetY = 0;
                float weight = 1;
                String textureName;
                if (mo instanceof TiledMapTileMapObject) {
                    TiledMapTileMapObject tmtmo = (TiledMapTileMapObject) mo;
                    String fileName = Paths.get(tmtmo.getTextureRegion().getTexture().toString()).getFileName().toString();
                    textureName = TextUtils.removeExtension(fileName);
                    if (tmtmo.getTile().getProperties() != null) {
                        try {
                            shadowOffsetY = Parser.parseFloatException(tmtmo.getTile().getProperties().get("shadowOffsetY", "0", String.class));
                        } catch (NumberFormatException nfe) {
                            logger.debug("Could not parse 'shadowOffsetY' from tile: " + nfe.getLocalizedMessage());
                        }
                        try {
                            weight = Parser.parseFloatException(tmtmo.getTile().getProperties().get("weight", "1", String.class));
                        } catch (NumberFormatException nfe) {
                            logger.debug("Could not parse 'shadowOffsetY' from tile: " + nfe.getLocalizedMessage());
                        }
                    }
                } else {
                    textureName = mo.getName();
                }

                // Trees with an offsetY of 20
                PhysicalObject po = new PhysicalObject(x + ow / 2f, y, 0f, 25f, textureName, map);
                po.shadowOffsetY = shadowOffsetY;
                po.weight = weight;
                entities.add(po);
            }

        }

        startTime = TimeUtils.nanoTime();
    }

    public void doneLoading() {
        map.doneLoading(assets);
        for (PositionPhysicalEntity entity : entities) {
            entity.initAssets(assets);
        }
        // Update engine with initialization systems
        engine.update(0);

        // Remove initialization systems
        engine.removeSystem(ibrs);

        // Add update systems
        engine.addSystem(uus);

        // Add render systems
        engine.addSystem(brs);
        engine.addSystem(uirs);

        if (debugInfo)
            engine.addSystem(drs);
    }

    public boolean isVisible(Vector2 point) {
        boolean vis = false;
        for (IEntity u : player) {
            vis = vis || u.pos().dst(point.x, point.y, 0) < u.viewingDistance() * 2.5;
        }
        return vis;
    }

    public boolean isVisible(Vector3 point) {
        boolean vis = false;
        for (IEntity u : player) {
            vis = vis || u.pos().dst(point) < u.viewingDistance() * 1.1f;
        }
        return vis;
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
            if (!paused) {
                updateScene(deltaSecs);
            }
            renderScene(deltaSecs);
            // Update engine to update and render scene
            engine.update(deltaSecs);
        }
    }

    /**
     * Updates the scne
     *
     * @param deltaSecs The delta time in seconds
     */
    public void updateScene(float deltaSecs) {
        // Update camera position
        camera.update(deltaSecs);

        // And update all units
        entities.stream().forEach(entity -> entity.update(deltaSecs));

        UnitGroupManager.getInstance().update();

        stage.act(deltaSecs);
    }

    public void renderScene(float deltaSecs) {
        Collections.sort(entities);

        // Clear screen
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
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

        mapShader.bind();
        mapShader.setUniform3fv("u_lights", lights, 0, i);
        mapShader.setUniformi("u_light_count", i / 3);
        mapShader.setUniformf("u_camera_offset", camera.pos.x - Gdx.graphics.getWidth() / 2f, camera.pos.y - Gdx.graphics.getHeight() / 2f);
        map.renderBase(camera);

        spriteBatch.setProjectionMatrix(camera.getLibgdxCamera().combined);
        objectsShader.bind();
        objectsShader.setUniformf("u_camera_offset", camera.pos.x - Gdx.graphics.getWidth() / 2f, camera.pos.y - Gdx.graphics.getHeight() / 2f);

        /** Entities **/
        spriteBatch.begin();
        for (PositionPhysicalEntity ppe : entities) {
            ppe.render(spriteBatch, objectsShader);
        }
        spriteBatch.end();

        map.renderOverlays(camera);
        map.renderFogOfWar(camera, cameraShapeRenderer, spriteBatch);

        UnitGroupManager.getInstance().render();
        // Render debug info
        if (debugInfo) {
            map.renderDebug();

            // Debug filled
            cameraShapeRenderer.begin(ShapeType.Filled);
            for (PositionPhysicalEntity ppe : entities) {
                ppe.renderDebugFilled(cameraShapeRenderer);
            }
            cameraShapeRenderer.end();

            // Debug line
            cameraShapeRenderer.begin(ShapeType.Line);
            for (PositionPhysicalEntity ppe : entities) {
                ppe.renderDebugLine(cameraShapeRenderer);
            }
            cameraShapeRenderer.end();
        }

        // Render shape renderer layers
        // Layer 0 - filled
        cameraShapeRenderer.begin(ShapeType.Filled);
        entities.forEach(ppe -> ppe.renderShapeFilledLayer0(cameraShapeRenderer));
        cameraShapeRenderer.end();
        // Layer 1 - line
        cameraShapeRenderer.begin(ShapeType.Line);
        entities.forEach(ppe -> ppe.renderShapeLineLayer1(cameraShapeRenderer));
        cameraShapeRenderer.end();
        // Layer 2 - filled
        cameraShapeRenderer.begin(ShapeType.Filled);
        entities.forEach(ppe -> ppe.renderShapeFilledLayer2(cameraShapeRenderer));
        cameraShapeRenderer.end();
        // Layer 3 - line
        cameraShapeRenderer.begin(ShapeType.Line);
        entities.forEach(ppe -> ppe.renderShapeLineLayer3(cameraShapeRenderer));
        cameraShapeRenderer.end();

        selection.render();

        stage.draw();

        Gdx.gl.glDisable(GL20.GL_BLEND);

    }

    @Override
    public void resize(int width, int height) {
        logger.debug("Resize called: [" + width + ", " + height + "]");

        // whenever our screen resizes, we need to update our uniform
        objectsShader.bind();
        objectsShader.setUniformf("u_viewport_size", width, height);

        mapShader.bind();
        mapShader.setUniformf("u_viewport_size", width, height);

        orthoCamera.setToOrtho(false, width, height);
        camera.resize(width, height);

        screenShapeRenderer.setProjectionMatrix(orthoCamera.combined);

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
        for (IEntity u : group) {
            if ((u.softRadius() * 2 + 10) > longestSide) {
                longestSide = u.softRadius() * 2 + 10;
            }
        }
        float side2 = side * longestSide / 2;
        int col = 0, row = 0;

        group.sortByPosition();
        for (IEntity u : group) {
            Vector2 targetPos = Vector2Pool.getObject(x - side2 + longestSide * col + longestSide / 2, y - side2 + longestSide * row + longestSide / 2);
            if (!map.overlapsWithBlocked(new Rectangle(x - u.hardRadius().width / 2, y - u.hardRadius().height / 2, u.hardRadius().width, u.hardRadius().height))) {
                // Calculate path
                Path path = new Path(map.findPath(u.pos().x, u.pos().y, targetPos.x, targetPos.y), u.pos().x, u.pos().y, targetPos.x, targetPos.y);
                path.smooth(u);
                if (path.size() > 0)
                    u.steeringBehaviours().addFollowPath(path);
            }
            Vector2Pool.returnObject(targetPos);

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
    public void moveUnit(IEntity unit, int x, int y) {
        Path path = new Path(map.findPath(unit.pos().x, unit.pos().y, (float) x, (float) y), unit.pos().x, unit.pos().y, (float) x, (float) y);
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
    public IEntity getCollidingUnitImage(int x, int y) {
        // Old units
        for (PositionPhysicalEntity ppe : entities) {
            if (ppe.isImageColliding(x, y)) {
                return ppe;
            }
        }
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
        // Old units
        Set<IEntity> list = new HashSet<>();
        for (IEntity u : player) {
            if (rect.overlaps(u.bounds())) {
                list.add(u);
            }
        }
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

    public static OrthographicCamera getGdxCamera() {
        return game.orthoCamera;
    }

}
