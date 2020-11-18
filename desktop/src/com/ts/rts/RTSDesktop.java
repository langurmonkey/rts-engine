package com.ts.rts;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

public class RTSDesktop {
    public static void main(String[] args) {
	Lwjgl3ApplicationConfiguration cfg = new Lwjgl3ApplicationConfiguration();
	cfg.setTitle("RTS Engine");
	cfg.setWindowedMode(1024, 768);
	cfg.setResizable(true);
	cfg.useVsync(true);

	new Lwjgl3Application(new RTSGame(), cfg);
    }
}
