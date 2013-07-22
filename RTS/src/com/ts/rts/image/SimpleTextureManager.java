package com.ts.rts.image;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Loads textures as sprites. Very basic implementation, not in use anymore.
 * 
 * @author Toni Sagrista
 * @deprecated Use the {@link AtlasTextureManager} instead.
 */
public class SimpleTextureManager implements ITextureManager {

	private Map<String, Texture> textures;

	public SimpleTextureManager() {
		textures = new HashMap<String, Texture>();
	}

	/**
	 * Loads all the textures
	 */
	public void loadTextures() {
		/** TANK **/
		textures.put("tank-32", new Texture(Gdx.files.internal("data/img/tank-32.png")));

		/** GOONER **/
		textures.put("goon-blue-stand-left", new Texture(Gdx.files.internal("data/img/goon-blue-stand-left.png")));
		textures.put("goon-blue-shoot1-left", new Texture(Gdx.files.internal("data/img/goon-blue-shoot1-left.png")));
		textures.put("goon-blue-shoot2-left", new Texture(Gdx.files.internal("data/img/goon-blue-shoot2-left.png")));
		textures.put("goon-blue-walk1-left", new Texture(Gdx.files.internal("data/img/goon-blue-walk1-left.png")));
		textures.put("goon-blue-walk2-left", new Texture(Gdx.files.internal("data/img/goon-blue-walk2-left.png")));
		textures.put("goon-blue-walk3-left", new Texture(Gdx.files.internal("data/img/goon-blue-walk3-left.png")));

	}

	/**
	 * Disposes all the loaded textures
	 */
	public void disposeTextures() {
		Set<String> keys = textures.keySet();
		for (String key : keys) {
			textures.get(key).dispose();
		}
	}

	/**
	 * Gets the texture indexed by the given key
	 * 
	 * @param key
	 * @return
	 */
	public TextureRegion getTexture(String texture, String key) {
		return new Sprite(textures.get(key));
	}

}
