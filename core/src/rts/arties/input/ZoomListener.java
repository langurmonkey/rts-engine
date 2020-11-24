package rts.arties.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import rts.arties.datastructure.geom.Vector2;
import rts.arties.scene.cam.Camera;

/**
 * Listens to mouse scroll events and +/- keys to adjust the scene zoom.
 */
public class ZoomListener extends InputAdapter {

    private Camera camera;
    private Vector2 aux;

    public ZoomListener(Camera camera) {
        this.camera = camera;
        this.aux = new Vector2();
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        // New coordinates of camera (center of viewport from bottom left of map)
        camera.screenToWorld(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY(), aux);
        camera.zoom(amountY * 0.1f, aux.x, aux.y);
        return super.scrolled(amountX, amountY);
    }

}
