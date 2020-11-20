package arties.datastructure.geom;

import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Some basic geometry utilities lacking in the underlying libs.
 *
 * @author Toni Sagrista
 */
public class GeomUtil {

    /**
     * Checks if the shape contains the point
     *
     * @param bounds
     * @param p
     * @return
     */
    public static boolean contains(Polygon bounds, Vector2 p) {
        return bounds.contains(p.x, p.y);
    }

    /**
     * Checks if the shape contains the rectangle
     *
     * @param shape
     * @param rect
     * @return
     */
    public static boolean contains(Polygon shape, Rectangle rect) {
        return shape.contains(rect.x + rect.width, rect.y + rect.height) && shape.contains(rect.x, rect.y);
    }

    /**
     * Checks if the rectangle contains the shape
     *
     * @param rect
     * @param shape
     * @return
     */
    public static boolean contains(Rectangle rect, Polygon shape) {
        return rect.contains(shape.getBoundingRectangle());
    }
}
