package rts.arties.scene.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.TimeUtils;
import rts.arties.datastructure.IMapCell;
import rts.arties.scene.cam.Camera;
import rts.arties.util.MathUtilsd;
import rts.arties.util.graphics.RegularGridMesh;
import rts.arties.util.graphics.shader.ExtShaderLoader;
import rts.arties.util.graphics.shader.ExtShaderProgram;

import java.util.Arrays;

public class FogOfWarMesh implements IFogOfWar {

    private final IRTSMap map;
    private RegularGridMesh fowMesh;
    private ExtShaderProgram program;

    private final byte[][] fog;
    private final long[][] lastVisited;
    private int width, height, tileWidth, tileHeight;
    private float C_VISIBLE, C_HIDDEN, C_FOGGY;

    private final Vector2 aux;

    public FogOfWarMesh(IRTSMap map, int width, int height, int tileWidth, int tileHeight) {
        this.map = map;
        this.width = width;
        this.height = height;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.aux = new Vector2();
        this.fog = new byte[width][height];
        this.lastVisited = new long[width][height];

        this.C_VISIBLE = Color.toFloatBits(0f, 0f, 0f, 0f);
        this.C_HIDDEN = Color.toFloatBits(0f, 0f, 0f, 1f);
        this.C_FOGGY = Color.toFloatBits(0f, 0f, 0f, FOGGY_ALPHA);

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                this.fog[i][j] = F_HIDDEN;
                this.lastVisited[i][j] = Long.MIN_VALUE;
            }
        }
    }

    @Override
    public void initialize() {
        fowMesh = new RegularGridMesh(0, 0, tileWidth, tileHeight, width, height);
        program = ExtShaderLoader.fromFile("data/shaders/regulargrid.vert.glsl", "data/shaders/regulargrid.frag.glsl");
    }

    @Override
    public void doneLoading(AssetManager assts) {
    }

    @Override
    public void update(Vector3 position, int radiusPixels) {
        // Update the colors in the mesh
        long now = TimeUtils.millis();
        int x = (int) (position.x / tileWidth);
        int y = (int) (position.y / tileHeight);
        int blocks = Math.round(radiusPixels * 3.9f / tileWidth);
        float rad2 = radiusPixels * radiusPixels;

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
                        if(fog[i][j] != F_VISIBLE) {
                            fog[i][j] = F_VISIBLE;
                            fowMesh.setColor(i, j, C_VISIBLE);
                        }
                        lastVisited[i][j] = now;
                    }
                }
            }
        }
    }

    @Override
    public void render(Camera camera) {
        long now = TimeUtils.millis();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                float f = fog[i][j];
                if (f == F_VISIBLE || f == F_FOGGY) {
                    long lv = now - lastVisited[i][j];
                    if (lv > LAST_VISITED_0 && lv < LAST_VISITED_1) {
                        fog[i][j] = F_FOGGY;
                        float alpha = MathUtilsd.lint(lv, LAST_VISITED_0, LAST_VISITED_1, 0, FOGGY_ALPHA);
                        fowMesh.setColor(i, j, Color.toFloatBits(0, 0, 0, alpha));
                    }
                }
            }
        }
        Gdx.gl.glEnable(GL30.GL_BLEND);
        program.begin();
        program.setUniformMatrix("u_projTrans", camera.combined());
        fowMesh.render(program);
        program.end();
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
