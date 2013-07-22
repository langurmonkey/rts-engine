package com.ts.rts.image;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Manages the texture maps of the application.
 * 
 * @author Toni Sagrista
 * 
 */
public class AtlasTextureManager implements ITextureManager {

	private Map<String, TextureAtlas> atlases;

	public AtlasTextureManager() {
		atlases = new HashMap<String, TextureAtlas>();
	}

	@Override
	public void disposeTextures() {
		Set<String> names = atlases.keySet();
		for (String name : names) {
			atlases.get(name).dispose();
		}
		atlases.clear();
	}

	@Override
	public TextureRegion getTexture(String texture, String key) {
		return atlases.get(texture).findRegion(key);
	}

	@Override
	public void loadTextures() {
		TextureAtlas textures = new TextureAtlas(Gdx.files.internal("data/img/textures/textures.pack"));
		TextureAtlas objects = new TextureAtlas(Gdx.files.internal("data/img/textures/objects.pack"));
		atlases.put("textures", textures);
		atlases.put("objects", objects);
	}

}
