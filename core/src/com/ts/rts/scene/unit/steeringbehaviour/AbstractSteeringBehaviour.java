package com.ts.rts.scene.unit.steeringbehaviour;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.ts.rts.RTSGame;
import com.ts.rts.scene.unit.MovingEntity;

/**
 * An abstract steering behaviour. Most of the behaviours implemented have been extracted from the paper
 * "Steering behaviors for autonomous characters" by Craig Reynolds ({@link http://www.red3d.com/cwr/steer/}).
 * 
 * @author Toni Sagrista
 * 
 */
public abstract class AbstractSteeringBehaviour implements ISteeringBehaviour {

    protected MovingEntity unit;
    protected boolean done;
    protected ShapeRenderer shapeRenderer;

    public AbstractSteeringBehaviour(MovingEntity unit) {
	super();
	this.unit = unit;
	done = false;
	shapeRenderer = RTSGame.game.cameraShapeRenderer;
    }

    /**
     * @return the done
     */
    public boolean isDone() {
	return done;
    }

    @Override
    public void render() {
	shapeRenderer.setProjectionMatrix(RTSGame.getGdxCamera().combined);
	renderBehaviour();
    }

    public void renderBehaviour() {
	// Empty because some behaviours do not have render
    }

    @Override
    public void dispose() {
    }

}
