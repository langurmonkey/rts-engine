package rts.arties.scene.map;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import rts.arties.datastructure.IMapCell;
import rts.arties.datastructure.geom.Vector2;
import rts.arties.scene.cam.Camera;
import rts.arties.util.MathUtilsd;

/**
 * Tile-based fog of war implementation
 *
 * @author Toni Sagrista
 */
public class FogOfWar {
    private static final byte F_HIDDEN = 0;
    private static final byte F_VISIBLE = 1;
    private static final byte F_FOGGY = 2;

    private static final long LAST_VISITED_0 = 3000;
    private static final long LAST_VISITED_1 = 6000;
    private static final float FOGGY_ALPHA = 0.5f;

    private final IRTSMap map;
    private final byte[][] fog;
    private final long[][] lastVisited;
    private final int tileSize;
    private final int width;
    private final int height;

    private final Vector2 aux;
    private final Vector3 aux3;

    private Sprite black;

    /**
     * Creates a new fog of war with the given with and height (in tiles) and the tile size
     *
     * @param width
     * @param height
     * @param tileSize
     */
    public FogOfWar(IRTSMap map, int width, int height, int tileSize) {
        super();
        this.map = map;
        this.width = width;
        this.height = height;
        this.fog = new byte[width][height];
        this.lastVisited = new long[width][height];
        this.tileSize = tileSize;
        this.aux = new Vector2();
        this.aux3 = new Vector3();

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                this.fog[i][j] = F_HIDDEN;
                this.lastVisited[i][j] = Long.MIN_VALUE;
            }
        }
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
        long now = System.currentTimeMillis();
        int x = (int) (position.x / tileSize);
        int y = (int) (position.y / tileSize);
        int blocks = Math.round(radius * 3.9f / tileSize);

        for (int i = x - blocks; i <= x + blocks; i++) {
            for (int j = y - blocks; j <= y + blocks; j++) {
                if(i >= 0 && j >= 0) {
                    float tx = i * tileSize;
                    float ty = j * tileSize;
                    IMapCell cell = map.getCell(tx, ty);
                    float fac = 1f;
                    if (cell != null) {
                        fac = -(cell.z() - position.z) / 30;
                        fac = fac == 0 ? 1 : (fac > 0 ? fac * 1.3f : -fac * 0.76f);
                    }
                    if (i >= 0 && i < width && j >= 0 && j < height && aux.set(tx, ty).dst(position.x, position.y) <= radius * fac) {
                        fog[i][j] = F_VISIBLE;
                        lastVisited[i][j] = now;
                    }
                }
            }
        }

    }

    /**
     * Renders the fog of war
     */
    public void render(Camera camera, ShapeRenderer sr, SpriteBatch sb) {
        renderWithSprites(camera, sb);
    }

    /**
     * Renders the fog of war with sprites
     */
    public void renderWithSprites(Camera camera, SpriteBatch sb) {
        long now = System.currentTimeMillis();
        float ts = tileSize * 2f / camera.zoom;
        sb.begin();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                float f = fog[i][j];
                float x = i * tileSize;
                float y = j * tileSize;
                if (f == F_HIDDEN) {
                    if (camera.containsPoint(x, y, ts)) {
                        sb.setColor(1, 1, 1, 1);
                        sb.draw(black, x, y);
                    }
                } else if (f == F_VISIBLE) {
                    long lv = now - lastVisited[i][j];
                    if (lv > LAST_VISITED_0 && lv < LAST_VISITED_1) {
                        float alpha = MathUtilsd.lint(lv, LAST_VISITED_0, LAST_VISITED_1, 0, FOGGY_ALPHA);
                        sb.setColor(1, 1, 1, alpha);
                        sb.draw(black, x, y);
                    } else if (lv >= LAST_VISITED_1) {
                        fog[i][j] = F_FOGGY;
                        sb.setColor(1, 1, 1, FOGGY_ALPHA);
                        sb.draw(black, x, y);
                    }
                } else if (f == F_FOGGY) {
                    if (camera.containsPoint(x, y, ts)) {
                        sb.setColor(1, 1, 1, FOGGY_ALPHA);
                        sb.draw(black, x, y);
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

    public byte getValue(float worldX, float worldY) {
        return fog[(int) (worldX / tileSize)][(int) (worldY / tileSize)];
    }

    public byte getValue(Vector3 worldPos) {
        return getValue(worldPos.x, worldPos.y);
    }

    public boolean isHidden(float worldX, float worldY) {
        return getValue(worldX, worldY) == F_HIDDEN;
    }

    public boolean isHidden(Vector3 worldPos) {
        return isHidden(worldPos.x, worldPos.y);
    }

    public boolean isFoggy(float worldX, float worldY) {
        return getValue(worldX, worldY) == F_FOGGY;
    }

    public boolean isFoggy(Vector3 worldPos) {
        return isFoggy(worldPos.x, worldPos.y);
    }

    public boolean isHiddenOrFoggy(float worldX, float worldY) {
        byte val = getValue(worldX, worldY);
        return val == F_HIDDEN || val == F_FOGGY;
    }

    public boolean isHiddenOrFoggy(Vector3 worldPos) {
        return isHiddenOrFoggy(worldPos.x, worldPos.y);
    }

    public boolean isVisible(float worldX, float worldY) {
        return getValue(worldX, worldY) == F_VISIBLE;
    }

    public boolean isVisible(Vector3 worldPos) {
        return isVisible(worldPos.x, worldPos.y);
    }
}
