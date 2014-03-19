package com.ts.rts;

import com.badlogic.gdx.tools.imagepacker.TexturePacker2;

public class TextureSetup {
    public static void main(String[] args) {
	TexturePacker2.process("/home/tsagrista/Dropbox/Development/RTS/Sprites/Mine/gamesprites", "../RTS-android/assets/data/img/textures/", "textures.pack");
    }
}
