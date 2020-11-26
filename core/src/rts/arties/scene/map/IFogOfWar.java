package rts.arties.scene.map;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import rts.arties.scene.cam.Camera;

/**
 * Interface for fog of war implementations
 */
public interface IFogOfWar {

    void initialize();
    void doneLoading(AssetManager assts);
    void update(Vector3 position, int radiusPixels);
    void render(Camera camera);
    boolean isHidden(Vector3 worldPos);
    boolean isFoggy(Vector3 worldPos);
    boolean isHiddenOrFoggy(Vector3 worldPos);
    boolean isVisible(Vector3 worldPos);

}
