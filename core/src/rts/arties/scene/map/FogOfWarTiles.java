package rts.arties.scene.map;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.TimeUtils;
import rts.arties.datastructure.IMapCell;
import rts.arties.scene.cam.Camera;
import rts.arties.util.MathUtilsd;

/**
 * Tile-based fog of war implementation
 *
 * @author Toni Sagrista
 */
public class FogOfWarTiles implements IFogOfWar {


    private final IRTSMap map;
    private final byte[][] fog;
    private final short[][] bits;
    private final long[][] lastVisited;
    private final Sprite[] lookup;
    private Sprite sprite;
    private final int tileWidth, tileHeight;
    private final int width;
    private final int height;

    private final Vector2 aux;

    private SpriteBatch sb;

    private boolean useImageTiles = false;

    /**
     * Creates a new fog of war with the given with and height (in tiles) and the tile size
     */
    public FogOfWarTiles(IRTSMap map, int width, int height, int tileWidth, int tileHeight, SpriteBatch sb) {
        super();
        this.map = map;
        this.width = width;
        this.height = height;
        this.fog = new byte[width][height];
        this.bits = new short[width][height];
        this.lastVisited = new long[width][height];
        this.lookup = new Sprite[16];
        this.sprite = null;
        this.sb = sb;

        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.aux = new Vector2();

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                this.fog[i][j] = F_HIDDEN;
                this.bits[i][j] = -1;
                this.lastVisited[i][j] = Long.MIN_VALUE;
            }
        }
    }

    @Override
    public void initialize() {

    }

    public void doneLoading(AssetManager assets) {
        TextureAtlas ta = assets.get("data/tex/base-textures.atlas");
        if (useImageTiles) {
            // one
            lookup[0x01] = new Sprite(ta.findRegion("0"));
            lookup[0x02] = new Sprite(ta.findRegion("1"));
            lookup[0x04] = new Sprite(ta.findRegion("2"));
            lookup[0x08] = new Sprite(ta.findRegion("3"));

            // two
            lookup[0x03] = new Sprite(ta.findRegion("01"));
            lookup[0x05] = new Sprite(ta.findRegion("02"));
            lookup[0x06] = new Sprite(ta.findRegion("12"));
            lookup[0x09] = new Sprite(ta.findRegion("03"));
            lookup[0x0A] = new Sprite(ta.findRegion("13"));
            lookup[0x0C] = new Sprite(ta.findRegion("23"));

            // three
            lookup[0x07] = new Sprite(ta.findRegion("012"));
            lookup[0x0B] = new Sprite(ta.findRegion("013"));
            lookup[0x0D] = new Sprite(ta.findRegion("023"));
            lookup[0x0E] = new Sprite(ta.findRegion("123"));

            // four
            lookup[0x0F] = new Sprite(ta.findRegion("0123"));
        } else {
            sprite = new Sprite(ta.findRegion("0123"));
        }
    }

    /**
     * Updates the current fog of war with a visibility centered in the given position and with the given radius
     *
     * @param position
     * @param radius
     */
    public void update(Vector3 position, int radius) {
        long now = TimeUtils.millis();
        int x = (int) (position.x / tileWidth);
        int y = (int) (position.y / tileHeight);
        int blocks = Math.round(radius * 3.9f / tileWidth);
        float rad2 = radius * radius;

        for (int i = x - blocks; i <= x + blocks; i++) {
            for (int j = y - blocks; j <= y + blocks; j++) {
                if (i >= 0 && j >= 0) {
                    float tx = i * tileWidth;
                    float ty = j * tileHeight;
                    IMapCell cell = map.getCell(tx, ty);
                    float fac = 1f;
                    if (cell != null) {
                        fac = -(cell.z() - position.z) / 30;
                        fac = fac == 0 ? 1 : (fac > 0 ? fac * 1.2f : -fac * 0.7f);
                        fac = Math.signum(fac) * (fac * fac);
                    }
                    if (i >= 0 && i < width && j >= 0 && j < height && aux.set(tx, ty).dst2(position.x, position.y) <= rad2 * fac) {
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
    public void render(Camera camera) {
        renderWithSprites(camera, sb);
    }

    /**
     * Renders the fog of war with sprites
     */
    public void renderWithSprites(Camera camera, SpriteBatch sb) {
        long now = TimeUtils.millis();
        float ts = tileWidth * 2f / camera.zoom;
        sb.begin();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                float f = fog[i][j];
                float x = i * tileWidth;
                float y = j * tileHeight;
                if (f == F_HIDDEN) {
                    if (camera.containsPoint(x, y, ts)) {
                        sb.setColor(1, 1, 1, 1);
                        if (useImageTiles)
                            drawTile(sb, i, j, x, y);
                        else
                            sb.draw(sprite, x, y);
                    }
                } else if (f == F_VISIBLE) {
                    long lv = now - lastVisited[i][j];
                    if (lv > LAST_VISITED_0 && lv < LAST_VISITED_1) {
                        float alpha = MathUtilsd.lint(lv, LAST_VISITED_0, LAST_VISITED_1, 0, FOGGY_ALPHA);
                        sb.setColor(1, 1, 1, alpha);
                        if(!useImageTiles)
                            sb.draw(sprite, x, y);
                    } else if (lv >= LAST_VISITED_1) {
                        fog[i][j] = F_FOGGY;
                        sb.setColor(1, 1, 1, FOGGY_ALPHA);
                        if(!useImageTiles)
                            sb.draw(sprite, x, y);
                    }
                } else if (f == F_FOGGY) {
                    if (camera.containsPoint(x, y, ts)) {
                        sb.setColor(1, 1, 1, FOGGY_ALPHA);
                        if(!useImageTiles)
                            sb.draw(sprite, x, y);
                    }

                }
            }
        } sb.end();
    }

    private void drawTile(SpriteBatch sb, int i, int j, float x, float y) {
        boolean nw, n, ne, w, e, sw, s, se;
        nw = j >= height - 1 || i <= 0 || fog[i - 1][j + 1] == F_HIDDEN;
        n = j >= height - 1 || fog[i][j + 1] == F_HIDDEN;
        ne = j >= height - 1 || i >= width - 1 || fog[i + 1][j + 1] == F_HIDDEN;
        w = i <= 0 || fog[i - 1][j] == F_HIDDEN;
        e = i >= width - 1 || fog[i + 1][j] == F_HIDDEN;
        sw = j <= 0 || i <= 0 || fog[i - 1][j - 1] == F_HIDDEN;
        s = j <= 0 || fog[i][j - 1] == F_HIDDEN;
        se = j <= 0 || i >= width - 1 || fog[i + 1][j - 1] == F_HIDDEN;

        //short c = 0;
        //c = setBit(c, (short) 0, (short) (nw ? 1 : 0));
        //c = setBit(c, (short) 1, (short) (n ? 1 : 0));
        //c = setBit(c, (short) 2, (short) (ne ? 1 : 0));
        //c = setBit(c, (short) 3, (short) (w ? 1 : 0));
        //c = setBit(c, (short) 4, (short) (e ? 1 : 0));
        //c = setBit(c, (short) 5, (short) (sw ? 1 : 0));
        //c = setBit(c, (short) 6, (short) (s ? 1 : 0));
        //c = setBit(c, (short) 7, (short) (se ? 1 : 0));

        //if (lookup[c] != null)
        //    sb.draw(lookup[c], x, y);
        short spr = sprite(nw, n, ne, w, e, sw, s, se);
        //spr = 0x0F;
        if (spr > 0)
            sb.draw(lookup[spr], x, y);
    }

    // Returns modified n.
    public short setBit(short n, short p, short b) {
        short mask = (short) (1 << p);
        return (short) ((n & ~mask) | ((b << p) & mask));
    }

    private short sprite(boolean nw, boolean n, boolean ne, boolean w, boolean e, boolean sw, boolean s, boolean se) {
        // FOUR
        if (n && s && e && w && nw && ne && se && sw)
            return 0x0F;
        // THREE
        if (n && s && e && w && ne && !se)
            return 0x07;
        if (n && s && e && w && nw && !sw)
            return 0x0B;
        if (n && s && e && w && se && !ne)
            return 0x0D;
        if (n && s && e && w && sw && !nw)
            return 0x0E;
        // TWO
        if (e && w && n && nw && ne && !s)
            return 0x03;
        if (n && s && w && sw && nw && !e)
            return 0x05;
        if (n && s && e && ne && se)
            return 0x0A;
        if (e && w && s && se && sw && !n)
            return 0x0C;
        // ONE
        if (nw && n && w)
            return 0x01;
        if (n && ne && e && !s)
            return 0x02;
        if (w && sw && s)
            return 0x04;
        if (e && se && s && !w)
            return 0x08;

        return -1;
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
        return fog[(int) (worldX / tileWidth)][(int) (worldY / tileHeight)];
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
