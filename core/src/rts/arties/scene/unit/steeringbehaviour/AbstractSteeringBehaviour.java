package rts.arties.scene.unit.steeringbehaviour;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * An abstract steering behaviour. Most of the behaviours implemented have been extracted from the paper
 * "Steering behaviors for autonomous characters" by Craig Reynolds).
 *
 * @author Toni Sagrista
 */
public abstract class AbstractSteeringBehaviour implements ISteeringBehaviour {

    protected IEntity unit;
    protected boolean done;

    public AbstractSteeringBehaviour(IEntity unit) {
        super();
        this.unit = unit;
        done = false;
    }

    /**
     * @return the done
     */
    public boolean isDone() {
        return done;
    }

    @Override
    public void renderLine(ShapeRenderer sr) {
    }

    @Override
    public void renderFilled(ShapeRenderer sr) {
    }

    @Override
    public void dispose() {
    }

}
