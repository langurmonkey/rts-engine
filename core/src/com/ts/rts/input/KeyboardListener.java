package com.ts.rts.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.ts.rts.RTSGame;

/**
 * Keyboard listener.
 *
 * @author Toni Sagrista
 */
public class KeyboardListener extends InputAdapter {

    @Override
    public boolean keyDown(int keycode) {
        // TODO Auto-generated method stub
        return super.keyDown(keycode);
    }

    @Override
    public boolean keyUp(int keycode) {
        if (keycode == Keys.T) {
            RTSGame.debugRender = !RTSGame.debugRender;
        } else if (keycode == Keys.ESCAPE) {
            Gdx.app.exit();
        }
        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

}
