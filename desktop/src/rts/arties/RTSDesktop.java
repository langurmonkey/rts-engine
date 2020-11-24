package rts.arties;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Window;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3WindowListener;

import java.awt.event.WindowListener;

public class RTSDesktop {
    public static void main(String[] args) {
        RTSGame game = new RTSGame();

        Lwjgl3ApplicationConfiguration cfg = new Lwjgl3ApplicationConfiguration();
        cfg.setTitle("RTS Engine");
        cfg.setWindowedMode(1600, 900);
        cfg.setResizable(true);
        cfg.useVsync(false);
        cfg.setBackBufferConfig(8, 8, 8, 8, 16, 0, 4);
        cfg.useOpenGL3(true, 4, 1);
        cfg.setWindowListener(new RTSWindowListener(game));

        new Lwjgl3Application(game, cfg);
    }

    private static class RTSWindowListener implements Lwjgl3WindowListener {
        private RTSGame game;
        public RTSWindowListener(RTSGame game){
            this.game = game;
        }

        @Override
        public void created(Lwjgl3Window window) {

        }

        @Override
        public void iconified(boolean isIconified) {

        }

        @Override
        public void maximized(boolean isMaximized) {

        }

        @Override
        public void focusLost() {
            game.focusLost();
        }

        @Override
        public void focusGained() {
            game.focusGained();
        }

        @Override
        public boolean closeRequested() {
            return false;
        }

        @Override
        public void filesDropped(String[] files) {

        }

        @Override
        public void refreshRequested() {

        }
    }
}
