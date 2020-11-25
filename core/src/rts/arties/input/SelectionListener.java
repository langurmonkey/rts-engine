package rts.arties.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.InputAdapter;
import rts.arties.datastructure.geom.Vector2;
import rts.arties.scene.cam.Camera;
import rts.arties.scene.map.IRTSMap;
import rts.arties.scene.selection.Selection;

/**
 * Manages the selection events of the mouse.
 *
 * @author Toni Sagrista
 */
public class SelectionListener extends InputAdapter {

    private final Selection selection;
    private final Camera camera;
    private final IRTSMap map;
    private final Vector2 aux;
    boolean leftDown;

    public SelectionListener(Camera camera, IRTSMap map, Selection selection) {
        super();
        this.selection = selection;
        this.camera = camera;
        this.map = map;
        this.aux = new Vector2();
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button == Buttons.LEFT) {
            leftDown = true;
            screenY = (int) (camera.canvasHeight - screenY);
            selection.start.set(screenX, screenY);
            selection.end.set(screenX, screenY);
        }
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (button == Buttons.LEFT) {
            leftDown = false;
            screenY = (int) (camera.canvasHeight - screenY);
            camera.screenToWorld(screenX, screenY, aux);

            if (selection.active && selection.start.x != screenX && selection.start.y != screenY) {
                selection.select();
                selection.active = false;
            } else {
                float z = map.getCell(aux.x, aux.y).z();
                selection.selectOrMove(aux.x, aux.y, z);
                selection.active = false;
            }
            return true;
        } else if (button == Buttons.RIGHT) {
            selection.clearSelection();
            selection.active = false;
            return true;
        }
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (leftDown) {
            selection.active = true;
            screenY = Gdx.graphics.getHeight() - screenY;
            selection.end.set(screenX, screenY);
        }
        return true;

    }

}
