package com.ts.rts.scene.unit;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.ts.rts.datastructure.geom.Vector3;

/**
 * Interface for an object with rectangle bounds.
 *
 * @author Toni Sagrista
 */
public interface IBoundsObject {

    Rectangle bounds();

    Rectangle getBounds();

    Circle softRadius();

    Vector3 pos();
}
