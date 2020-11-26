package rts.arties.scene.map;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.Vector3;
import rts.arties.scene.cam.Camera;

/**
 * Interface for fog of war implementations
 */
public interface IFogOfWar {
    // Tile values
    byte F_HIDDEN = 0;
    byte F_VISIBLE = 1;
    byte F_FOGGY = 2;

    // Last visited lower bound [ms]
    long LAST_VISITED_0 = 3000;
    // Last visited upper bound [ms]
    long LAST_VISITED_1 = 6000;

    // Alpha value for foggy tiles
    float FOGGY_ALPHA = 0.75f;

    void initialize();
    void doneLoading(AssetManager assts);
    void update(Vector3 position, int radiusPixels);
    void render(Camera camera);
    boolean isHidden(Vector3 worldPos);
    boolean isFoggy(Vector3 worldPos);
    boolean isHiddenOrFoggy(Vector3 worldPos);
    boolean isVisible(Vector3 worldPos);

}
