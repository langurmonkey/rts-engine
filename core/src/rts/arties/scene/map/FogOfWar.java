package rts.arties.scene.map;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector3;
import rts.arties.datastructure.geom.Vector2;
import rts.arties.scene.cam.Camera;

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
        this.aux = new Vector2();
    }

    public void doneLoading(AssetManager assets) {
        TextureAtlas ta = assets.get("data/tex/base-textures.atlas");
        black = new Sprite(ta.findRegion("tile-black"));
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
    public void render(Camera camera, ShapeRenderer sr, SpriteBatch sb) {
        //renderWithShapeRenderer(camera, sr);
        renderWithSprites(camera, sb);
    }

    /**
     * Renders the fog of war with sprites
     */
    public void renderWithSprites(Camera camera, SpriteBatch sb) {
        sb.begin();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (fog[i][j] == F_HIDDEN) {
                    float x = i * tileSize;
                    float y = j * tileSize;
                    if (camera.containsPoint(x, y, tileSize)) {
                        sb.draw(black, x, y);
                    }
                }
            }
        }
        sb.end();
    }

    /**
     * Renders the fog of war with the shape renderer
     */
    public void renderWithShapeRenderer(Camera camera, ShapeRenderer sb) {
        sb.begin(ShapeType.Filled);
        sb.setColor(0f, 0f, 0f, 1f);

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (fog[i][j] == F_HIDDEN) {
                    float x = i * tileSize;
                    float y = j * tileSize;
                    if (camera.containsPoint(x, y)) {
                        sb.rect(i * tileSize, j * tileSize, tileSize, tileSize);
                    }
                }
            }
        }
        sb.end();
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
