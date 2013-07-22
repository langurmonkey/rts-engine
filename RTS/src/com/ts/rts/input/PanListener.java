package com.ts.rts.input;

import com.badlogic.gdx.Gdx;
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

}
