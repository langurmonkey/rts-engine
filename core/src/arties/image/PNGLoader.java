package arties.image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Very basic PNG image loader.
 *
 * @author Toni Sagrista
 */
public class PNGLoader {

    public static BufferedImage loadImage(String path) {
        try {
            BufferedImage image = ImageIO.read(new File(path));
            return image;
        } catch (Exception ex) {
        }
        return null;
    }

}
