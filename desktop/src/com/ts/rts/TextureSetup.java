package com.ts.rts;

import com.badlogic.gdx.tools.texturepacker.TexturePacker;

public class TextureSetup {
    public static void main(String[] args) {
	TexturePacker.process("/home/tsagrista/Dropbox/Development/RTS/Sprites/Mine/gamesprites", "../RTS-android/assets/data/img/textures/", "textures.pack");
    }
}
