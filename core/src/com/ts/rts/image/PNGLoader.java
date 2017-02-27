package com.ts.rts.image;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

/**
 * Very basic PNG image loader.
 * 
 * @author Toni Sagrista
 * 
 */
public class PNGLoader {

	public static BufferedImage loadImage(String path) {
		try {
			BufferedImage image = (BufferedImage) ImageIO.read(new File(path));
			return image;
		} catch (Exception ex) {
		}
		return null;
	}

}
