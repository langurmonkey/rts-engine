package arties;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

public class RTSDesktop {
    public static void main(String[] args) {
	Lwjgl3ApplicationConfiguration cfg = new Lwjgl3ApplicationConfiguration();
	cfg.setTitle("RTS Engine");
	cfg.setWindowedMode(1920, 1080);
	cfg.setResizable(true);
	cfg.useVsync(false);

	new Lwjgl3Application(new RTSGame(), cfg);
    }
}
