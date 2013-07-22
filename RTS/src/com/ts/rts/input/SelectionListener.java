package com.ts.rts.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.InputAdapter;
import com.ts.rts.RTSGame;
import com.ts.rts.scene.selection.Selection;

/**
 * Manages the selection events of the mouse.
 * 
 * @author Toni Sagrista
 * 
 */
public class SelectionListener extends InputAdapter {

	private Selection selection;
	boolean leftDown;

	public SelectionListener(Selection selection) {
		super();
		this.selection = selection;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if (button == Buttons.LEFT) {
			leftDown = true;
			screenY = Gdx.graphics.getHeight() - screenY;
			selection.start.set(screenX, screenY);
			selection.end.set(screenX, screenY);
		}
		return true;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if (button == Buttons.LEFT) {
			leftDown = false;
			screenY = Gdx.graphics.getHeight() - screenY;
			int mapX = (int) (screenX + RTSGame.getCamera().pos.x - RTSGame.getCamera().canvasWidth / 2);
			int mapY = (int) (screenY + RTSGame.getCamera().pos.y - RTSGame.getCamera().canvasHeight / 2);
			if (selection.active && selection.start.x != screenX && selection.start.y != screenY) {
				selection.select();
				selection.active = false;
			} else {
				selection.selectOrMove(mapX, mapY);
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
