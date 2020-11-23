package rts.arties.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import rts.arties.RTSGame;

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
            RTSGame.toggleDebug();
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
