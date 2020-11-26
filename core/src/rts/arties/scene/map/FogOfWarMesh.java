package rts.arties.scene.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.TimeUtils;
import rts.arties.datastructure.IMapCell;
import rts.arties.scene.cam.Camera;
import rts.arties.util.graphics.RegularGridMesh;
import rts.arties.util.graphics.shader.ExtShaderLoader;
import rts.arties.util.graphics.shader.ExtShaderProgram;
import rts.arties.util.graphics.shader.ShaderLoader;

public class FogOfWarMesh implements IFogOfWar {

    private final IRTSMap map;
    private RegularGridMesh fowMesh;
    private ShaderProgram program;

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

        this.C_VISIBLE = Color.toFloatBits(0f, 0f, 0f, 0f);
        this.C_HIDDEN = Color.toFloatBits(0f, 0f, 0f, 1f);
        this.C_FOGGY = Color.toFloatBits(0f, 0f, 0f, 0.4f);
    }

    @Override
    public void initialize() {
        fowMesh = new RegularGridMesh(0, 0, tileWidth, tileHeight, width, height);
        program = ShaderLoader.fromFile("data/shaders/regulargrid.vert.glsl", "data/shaders/regulargrid.frag.glsl");
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
                        fowMesh.setColor(i, j, C_VISIBLE);
                        //lastVisited[i][j] = now;
                    }
                }
            }
        }

    }

    @Override
    public void render(Camera camera) {
        Gdx.gl.glEnable(GL30.GL_BLEND);
        program.begin();
        program.setUniformMatrix("u_projTrans", camera.combined());
        fowMesh.render(program);
        program.end();
    }

    @Override
    public boolean isHidden(Vector3 worldPos) {
        return false;
    }

    @Override
    public boolean isFoggy(Vector3 worldPos) {
        return false;
    }

    @Override
    public boolean isHiddenOrFoggy(Vector3 worldPos) {
        return false;
    }

    @Override
    public boolean isVisible(Vector3 worldPos) {
        return false;
    }
}
