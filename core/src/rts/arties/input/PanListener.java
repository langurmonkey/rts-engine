package rts.arties.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.Rectangle;
import rts.arties.RTSGame;
import rts.arties.datastructure.IMapCell;
import rts.arties.datastructure.geom.Vector2;
import rts.arties.scene.cam.Camera;
import rts.arties.scene.selection.Selection;
import rts.arties.scene.unit.steeringbehaviour.IEntity;
import rts.arties.util.Vector2Pool;

import java.util.HashSet;
import java.util.Set;

/**
 * Pan listener which manages camera pan mouse moves.
 *
 * @author Toni Sagrista
 */
public class PanListener extends InputAdapter {
    /**
     * Pan zone padding
     */
    private static final int PADDING = 20;

    private final Camera camera;
    private final Selection selection;
    private final Rectangle activeZone;
    private final Vector2 canvasCenter;

    // Definition of movement keys
    private static final int KEY_UP = Keys.W;
    private static final int KEY_DOWN = Keys.S;
    private static final int KEY_LEFT = Keys.A;
    private static final int KEY_RIGHT = Keys.D;

    // Mouse pixmaps
    private Pixmap m_normal, m_arrow, m_forbidden;
    private Cursor c_normal, c_forbidden;
    private Cursor[] c_arrow;

    private Vector2 aux;

    private static final float KEYBOARD_MOVEMENT_MULTIPLIER = 500f;

    private static final Set<Integer> movementKeys;

    static {
        movementKeys = new HashSet<>();
        movementKeys.add(KEY_UP);
        movementKeys.add(KEY_DOWN);
        movementKeys.add(KEY_LEFT);
        movementKeys.add(KEY_RIGHT);
    }

    public PanListener(Camera camera, Selection selection) {
        super();
        m_normal = new Pixmap(Gdx.files.internal("data/img/cursor.png"));
        m_arrow = new Pixmap(Gdx.files.internal("data/img/right-cursor.png"));
        m_forbidden = new Pixmap(Gdx.files.internal("data/img/forbidden-cursor.png"));

        // Create cursors
        c_normal = Gdx.graphics.newCursor(m_normal, 0, 0);
        c_forbidden = Gdx.graphics.newCursor(m_forbidden, 0, 0);
        c_arrow = new Cursor[360];
        for (int i = 0; i < 360; i++) {
            c_arrow[i] = Gdx.graphics.newCursor(rotatePixmap(m_arrow, 90 - i), 0, 0);
        }

        // Normal image
        Gdx.graphics.setCursor(c_normal);

        this.camera = camera;
        this.selection = selection;
        // Zone of the screen where the mouse can work without panning
        this.activeZone = new Rectangle(PADDING, PADDING, camera.canvasWidth - PADDING * 2, camera.canvasHeight - PADDING * 2);
        this.canvasCenter = new Vector2(camera.canvasWidth / 2f, camera.canvasHeight / 2f);
        this.aux = new Vector2();
    }

    public Pixmap rotatePixmap(Pixmap src, float angle) {
        final int width = src.getWidth();
        final int height = src.getHeight();
        Pixmap rotated = new Pixmap(width, height, src.getFormat());

        final double radians = Math.toRadians(angle), cos = Math.cos(radians), sin = Math.sin(radians);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                final int centerx = width / 2, centery = height / 2, m = x - centerx, n = y - centery, j = ((int) (m * cos + n * sin)) + centerx, k = ((int) (n * cos - m * sin)) + centery;
                if (j >= 0 && j < width && k >= 0 && k < height) {
                    rotated.drawPixel(x, y, src.getPixel(j, k));
                }
            }
        }
        return rotated;

    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        screenY = Gdx.graphics.getHeight() - screenY;

        if (!selection.active && !activeZone.contains(screenX, screenY)) {
            Vector2 movement = Vector2Pool.getObject(screenX, screenY);
            movement.subtract(canvasCenter).scl(camera.zoom);
            camera.setAccel(movement);
            float angle = movement.angle();
            Vector2Pool.returnObject(movement);

            Gdx.graphics.setCursor(c_arrow[(int) angle]);

        } else {
            camera.stop();
            camera.screenToWorld(screenX, screenY, aux);
            IMapCell<IEntity> cell = RTSGame.game.getMap().getCell(aux);
            if (cell != null && cell.isBlocked()) {
                Gdx.graphics.setCursor(c_forbidden);
            } else {
                Gdx.graphics.setCursor(c_normal);
            }

        }
        return true;
    }

    @Override
    public boolean keyDown(int keycode) {
        if (movementKeys.contains(Integer.valueOf(keycode))) {
            switch (keycode) {
            case KEY_UP:
                camera.up(KEYBOARD_MOVEMENT_MULTIPLIER);
                break;
            case KEY_DOWN:
                camera.down(KEYBOARD_MOVEMENT_MULTIPLIER);
                break;
            case KEY_LEFT:
                camera.left(KEYBOARD_MOVEMENT_MULTIPLIER);
                break;
            case KEY_RIGHT:
                camera.right(KEYBOARD_MOVEMENT_MULTIPLIER);
                break;
            }
            return true;
        }
        return super.keyDown(keycode);
    }

    @Override
    public boolean keyUp(int keycode) {
        if (movementKeys.contains(Integer.valueOf(keycode))) {
            switch (keycode) {
            case KEY_UP:
                camera.stopVertical();
                break;
            case KEY_DOWN:
                camera.stopVertical();
                break;
            case KEY_LEFT:
                camera.stopHorizontal();
                break;
            case KEY_RIGHT:
                camera.stopHorizontal();
                break;
            }
            return true;
        }
        return super.keyUp(keycode);
    }

    public void resize(int w, int h) {
        this.activeZone.set(PADDING, PADDING, w - PADDING * 2, h - PADDING * 2);
        this.canvasCenter.set(w / 2f, h / 2f);
    }

}
