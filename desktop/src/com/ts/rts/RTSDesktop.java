package com.ts.rts;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class RTSDesktop {
    public static void main(String[] args) {
	LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
	cfg.title = "RTS";
	cfg.width = 1024;
	cfg.height = 768;
	cfg.fullscreen = false;
	cfg.backgroundFPS = -1;
	cfg.resizable = false;
	cfg.samples = 2;
	cfg.vSyncEnabled = true;

	new LwjglApplication(new RTSGame(), cfg);
    }
}
