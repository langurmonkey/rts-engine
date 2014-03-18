package com.ts.rts.input;

import java.util.HashSet;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Rectangle;
import com.ts.rts.datastructure.geom.Vector2;
import com.ts.rts.scene.cam.Camera;
import com.ts.rts.scene.selection.Selection;
import com.ts.rts.util.VectorPool;

/**
 * Pan listener which manages camera pan mouse moves.
 * 
 * @author Toni Sagrista
 * 
 */
public class PanListener extends InputAdapter {
    /**
     * Pan zone padding
     */
    private static final int PADDING = 15;

    private Camera camera;
    private Selection selection;
    private Rectangle activeZone;
    private Vector2 canvasCenter;

    // Definition of movement keys
    private static final int KEY_UP = Keys.W;
    private static final int KEY_DOWN = Keys.S;
    private static final int KEY_LEFT = Keys.A;
    private static final int KEY_RIGHT = Keys.D;

    private static final float KEYBOARD_MOVEMENT_MULTIPLIER = 500f;

    private static Set<Integer> movementKeys;
    static {
	movementKeys = new HashSet<Integer>();
	movementKeys.add(KEY_UP);
	movementKeys.add(KEY_DOWN);
	movementKeys.add(KEY_LEFT);
	movementKeys.add(KEY_RIGHT);
    }

    public PanListener(Camera camera, Selection selection) {
	super();
	this.camera = camera;
	this.selection = selection;
	this.activeZone = new Rectangle(PADDING, PADDING, camera.canvasWidth - PADDING * 2, camera.canvasHeight
		- PADDING * 2);
	this.canvasCenter = VectorPool.getObject(camera.canvasWidth / 2f, camera.canvasHeight / 2f);
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
	screenY = Gdx.graphics.getHeight() - screenY;
	if (!selection.active && !activeZone.contains(screenX, screenY)) {
	    Vector2 movement = VectorPool.getObject(screenX, screenY);
	    movement.subtract(canvasCenter);
	    camera.setAccel(movement);
	    VectorPool.returnObject(movement);
	} else {
	    camera.stop();
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

}
