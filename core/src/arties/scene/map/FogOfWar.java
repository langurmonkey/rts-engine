package arties.scene.map;

import arties.RTSGame;
import arties.datastructure.geom.Vector2;
import arties.scene.cam.Camera;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector3;

/**
 * Fog of war implementation as a matrix of boolean values [visible|hidden].
 *
 * @author Toni Sagrista
 */
public class FogOfWar {
    private static final byte F_HIDDEN = 0;
    private static final byte F_VISIBLE = 1;

    private final byte[][] fog;
    private final int tileSize;
    private final int width;
    private final int height;

    private final ShapeRenderer shapeRenderer;
    private final SpriteBatch batch;
    private Vector2 aux;

    private Sprite black;

    /**
     * Creates a new fog of war with the given with and height (in tiles) and the tile size
     *
     * @param width
     * @param height
     * @param tileSize
     */
    public FogOfWar(int width, int height, int tileSize) {
        super();
        this.width = width;
        this.height = height;
        this.fog = new byte[width][height];
        this.tileSize = tileSize;
        this.shapeRenderer = RTSGame.game.cameraShapeRenderer;
        this.batch = RTSGame.game.getSpriteBatch();
        this.aux = new Vector2();
    }

    public void doneLoading(AssetManager assets) {
        Texture tex = assets.get("data/tileset/tile-black.png");
        black = new Sprite(tex);
    }

    /**
     * Updates the current fog of war with a visibility centered in the given position and with the given radius
     *
     * @param position
     * @param radius
     */
    public void update(Vector3 position, int radius) {
        int x = (int) (position.x / tileSize);
        int y = (int) (position.y / tileSize);
        int blocks = Math.round(radius / tileSize);

        for (int i = x - blocks; i <= x + blocks; i++) {
            for (int j = y - blocks; j <= y + blocks; j++) {
                float tx = i * tileSize;
                float ty = j * tileSize;
                if (i >= 0 && i < width && j >= 0 && j < height && aux.set(tx, ty).distance(position.x, position.y) <= radius) {
                    fog[i][j] = F_VISIBLE;
                }
            }
        }
    }

    /**
     * Renders the fog of war
     */
    public void render(Camera camera) {
        renderWithShapeRenderer(camera);
    }

    /**
     * Renders the fog of war with sprites
     *
     * @param camera
     */
    public void renderWithSprites(Camera camera) {
        float ts2 = tileSize / 2f;
        batch.begin();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                float x = i * tileSize;
                float y = j * tileSize;
                if (camera.containsPoint(x + ts2, y + ts2, tileSize)) {
                    if (fog[i][j] == F_HIDDEN) {
                        batch.draw(black, x, y);
                    }
                }
            }
        }
        batch.end();
    }

    /**
     * Renders the fog of war with the shape renderer
     */
    public void renderWithShapeRenderer(Camera camera) {
        float ts2 = tileSize / 2f;
        Gdx.gl.glEnable(GL20.GL_BLEND);
        shapeRenderer.setProjectionMatrix(camera.getLibgdxCamera().combined);
        shapeRenderer.begin(ShapeType.Filled);
        shapeRenderer.setColor(0f, 0f, 0f, 1f);

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                float x = i * tileSize;
                float y = j * tileSize;
                if (camera.containsPoint(x + ts2, y + ts2, tileSize)) {
                    if (fog[i][j] == F_HIDDEN) {
                        shapeRenderer.rect(i * tileSize, j * tileSize, tileSize, tileSize);
                    }
                }
            }
        }
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    public boolean checkSurroundings(int x, int y, byte value) {
        if (x > 0) {
            if (fog[x - 1][y] == value) {
                return true;
            }
            if (y > 0 && fog[x - 1][y - 1] == value) {
                return true;
            }
            if (y < height - 1 && fog[x - 1][y + 1] == value) {
                return true;
            }
        }
        if (x < width - 1) {
            // x == 0
            if (fog[x + 1][y] == value) {
                return true;
            }
            if (y > 0 && fog[x + 1][y - 1] == value) {
                return true;
            }
            if (y < height - 1 && fog[x + 1][y + 1] == value) {
                return true;
            }
        }
        if (y > 0) {
            if (fog[x][y - 1] == value) {
                return true;
            }
        }
        if (y < height - 1) {
            // y == 0
            return fog[x][y + 1] == value;
        }
        return false;
    }
}
