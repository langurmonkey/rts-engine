package rts.arties.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import rts.arties.datastructure.geom.Vector2;
import rts.arties.scene.cam.Camera;

/**
 * Listens to mouse scroll events and +/- keys to adjust the scene zoom.
 */
public class ZoomListener extends InputAdapter {

    private final Camera camera;
    private final Vector2 aux;

    public ZoomListener(Camera camera) {
        this.camera = camera;
        this.aux = new Vector2();
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        // Let's get the mouse location in world coordinates
        camera.screenToWorld(camera.zoom, Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY(), aux);
        aux.sub(camera.pos);

        float amount = camera.zoom;
        camera.zoom(amountY * 0.1f);
        amount /= camera.zoom;
        camera.pos.sub(aux).add(aux.scl(amount));
        return super.scrolled(amountX, amountY);
    }

}
