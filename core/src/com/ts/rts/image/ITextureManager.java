package com.ts.rts.image;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * A texture manager interface.
 * 
 * @author Toni Sagrista
 * 
 */
public interface ITextureManager {

	public void disposeTextures();

	public TextureRegion getTexture(String texture, String key);

	public void loadTextures();
}
