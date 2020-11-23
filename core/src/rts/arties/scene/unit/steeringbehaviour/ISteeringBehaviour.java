package rts.arties.scene.unit.steeringbehaviour;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import rts.arties.datastructure.geom.Vector3;

/**
 * Interface for a steering behaviour.
 *
 * @author Toni Sagrista
 */
public interface ISteeringBehaviour {

    Vector3 calculate();

    boolean isDone();

    void renderLine(ShapeRenderer sr);
    void renderFilled(ShapeRenderer sr);

    void dispose();
}
