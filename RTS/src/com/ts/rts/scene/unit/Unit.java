package com.ts.rts.scene.unit;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.ts.rts.RTSGame;
import com.ts.rts.datastructure.IMapCell;
import com.ts.rts.datastructure.geom.Vector2;
import com.ts.rts.scene.map.IRTSMap;
import com.ts.rts.scene.map.MapProperties;
import com.ts.rts.scene.unit.group.UnitGroup;
import com.ts.rts.scene.unit.state.IState;
import com.ts.rts.scene.unit.state.StateManager;
import com.ts.rts.scene.unit.steeringbehaviour.SteeringBehaviours;
import com.ts.rts.util.VectorPool;

/**
 * A moving entity.
 * Rough conversion: 10px = 1m
 * 
 * @author Toni Sagrista
 * 
 */
public abstract class Unit extends MovingEntity {

    public static final float steeringForceTweaker = 200;
    public static final float slopeVelMult = .9f;

    /** Metre to pixel conversion **/
    public static final float M_TO_PX = 5;

    /** Pixel to metre conversion **/
    public static final float PX_TO_M = 1 / M_TO_PX;

    /** Steering behaviours **/
    public SteeringBehaviours steeringBehaviours;

    public Vector2 targetHeading;

    protected boolean turning = false;

    /** The unit group it belongs to, if any **/
    public UnitGroup group;

    /** The cell we're in **/
    private IMapCell<IBoundsObject> cell;

    /**
     * Is it selected?
     */
    protected boolean selected;

    /** The radius of the selection circumference **/
    protected float selectionRadius;

    public StateManager stateManager;

    /** State time in seconds **/
    protected float stateTime = 0f;

    /**
     * Creates a new unit with the given position in the map
     * 
     * @param x
     * @param y
     * @param map
     */
    public Unit(float x, float y, IRTSMap map) {
	super(x, y);
	this.map = map;
	this.steeringBehaviours = new SteeringBehaviours(this);
	this.shapeRenderer = RTSGame.game.cameraShapeRenderer;
	this.stateManager = new StateManager(this);
	lastUpdateX = Float.MAX_VALUE;
	lastUpdateY = Float.MAX_VALUE;

	steeringBehaviours.addSeparation(map);
	steeringBehaviours.addAvoidWalls(map);
    }

    public abstract String getName();

    public boolean isSelected() {
	return selected;
    }

    public void toggleSelection() {
	selected = !selected;
    }

    public void select() {
	selected = true;
    }

    public void unselect() {
	selected = false;
    }

    /**
     * The default behaviour for a unit is to only update the position
     */
    @Override
    public void update(float deltaSecs) {
	stateTime += deltaSecs;
	updateStates(deltaSecs);
	updatePosition(deltaSecs);
    }

    public void updateStates(float deltaSecs) {
	IState state = stateManager.getCurrentState();
	if (state != null) {
	    if (!state.isDone()) {
		state.process();
	    } else {
		stateManager.moveToNextState();
	    }
	}
    }

    public void updatePosition(float deltaSecs) {
	/** PHYSICAL MOVEMENT IMPLEMENTATION **/
	// First, get terrain type
	cell = map.getCell(pos);

	if (!turning) {
	    Vector2 steeringForce = steeringBehaviours.calculate().truncate(maxForce);
	    if (!steeringForce.isZeroVector()) {
		moving = true;

		Vector2 acceleration = steeringForce.divide(mass);
		vel.add(acceleration.multiply(deltaSecs)).truncate(maxSpeed);

		if (cell != null && MapProperties.isSlope(cell.getTerrain())) {
		    vel.multiply(slopeVelMult);
		}

		pos.add(vel.multiplyValues(deltaSecs));

		// Update heading if the vehicle has a velocity greater than a very small value
		if (vel.lengthSquared() > 0.00000001f) {
		    targetHeading = vel.clone().normalise();

		    float currentTurn = targetHeading.angleRad(heading);
		    float maxTurn = maxTurnRate * deltaSecs;

		    if (currentTurn > maxTurn) {
			// Approach heading to targetHeading
			turning = true;
		    } else {
			VectorPool.returnObject(heading);
			heading = targetHeading;
		    }
		}
	    } else {
		vel.zero();
		moving = false;
	    }
	    VectorPool.returnObject(steeringForce);
	}

	if (turning) {
	    // We're turning, update heading
	    updateHeading(deltaSecs);
	}

	// Update height
	z = cell.z(pos.x, pos.y);

	updateBounds(pos.x, pos.y);

	// Update entity in map if distance to last update is bigger than the unit's width
	if (Math.sqrt(Math.pow(pos.x - lastUpdateX, 2) + Math.pow(pos.y - lastUpdateY, 2)) > width / 4f) {
	    map.updateEntity(this);
	    touchLastUpdate();
	}

	// Update behaviour list
	steeringBehaviours.removeDoneBehaviours();
    }

    protected void updateHeading(float deltaSecs) {
	// We're turning, update heading

	if (maxTurnRate >= Math.PI * 2) {
	    // Optimisation, instant rotation
	    VectorPool.returnObject(heading);
	    heading = targetHeading;
	    turning = false;
	    return;
	}

	float angle = heading.angle();
	float targetAngle = targetHeading.angle();
	float maxTurn = (float) (Math.toDegrees(maxTurnRate) * deltaSecs);

	// Find out which side we're going
	float distNeg1 = 360 - targetAngle + angle;
	float distNeg2 = angle - targetAngle;
	distNeg1 = (distNeg1 < 0) ? Float.MAX_VALUE : distNeg1;
	distNeg2 = (distNeg2 < 0) ? Float.MAX_VALUE : distNeg2;
	float distNeg = Math.min(distNeg1, distNeg2);

	float distPos1 = targetAngle - angle;
	float distPos2 = 360 - angle + targetAngle;
	distPos1 = (distPos1 < 0) ? Float.MAX_VALUE : distPos1;
	distPos2 = (distPos2 < 0) ? Float.MAX_VALUE : distPos2;
	float distPos = Math.min(distPos1, distPos2);

	if (distNeg < distPos) {
	    // Turning counter-clockwise
	    maxTurn = -maxTurn;
	} else {
	    // Turning clockwise
	}

	float futureAngle = angle + maxTurn;
	if (futureAngle < 0) {
	    angle = 360f + angle;
	    futureAngle = 360 + futureAngle;
	} else if (futureAngle > 360) {
	    angle = 360 - angle;
	    futureAngle = futureAngle - 360;
	}

	if ((futureAngle % 360 > targetAngle && angle < targetAngle)
		|| (futureAngle % 360 < targetAngle & angle > targetAngle)) {
	    // Finish
	    VectorPool.returnObject(heading);
	    heading = targetHeading;
	    turning = false;
	} else {
	    // Advance step
	    heading.rotate((float) Math.toRadians(maxTurn));
	}

    }

    public void renderSelection() {
	Gdx.gl.glEnable(GL20.GL_BLEND);
	Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
	if (selected) {
	    /** SELECTION BOX **/
	    shapeRenderer.begin(ShapeType.Line);
	    shapeRenderer.setColor(new Color(0f, 0f, 0f, .9f));
	    shapeRenderer.circle(pos.x + spriteOffsetX, pos.y + spriteOffsetY, selectionRadius + 1);
	    shapeRenderer.setColor(new Color(1f, 1f, 1f, .8f));
	    shapeRenderer.circle(pos.x + spriteOffsetX, pos.y + spriteOffsetY, selectionRadius);
	    shapeRenderer.setColor(new Color(1f, 1f, 1f, .5f));
	    shapeRenderer.circle(pos.x + spriteOffsetX, pos.y + spriteOffsetY, selectionRadius - 1);
	    shapeRenderer.end();

	    /** HEALTH BAR **/
	    int startx = Math.round(pos.x - selectionRadius + spriteOffsetX);
	    int starty = Math.round(pos.y - selectionRadius + spriteOffsetY);
	    float healthLength = getHealthLength(selectionRadius * 2f);

	    shapeRenderer.begin(ShapeType.Filled);
	    shapeRenderer.setColor(new Color(0f, 0f, 0f, 1f));
	    shapeRenderer.rect(startx - 2f, starty - 1f, 3f, selectionRadius * 2f + 1f);
	    shapeRenderer.end();

	    float[] color = getHealthColor();
	    shapeRenderer.begin(ShapeType.Line);
	    shapeRenderer.setColor(new Color(color[0], color[1], color[2], 1f));
	    shapeRenderer.line(startx, starty, startx, starty + healthLength - 1f);
	    shapeRenderer.end();

	}
	Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    public void renderDebug() {
	super.renderDebug();
	Gdx.gl.glEnable(GL20.GL_BLEND);
	Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

	steeringBehaviours.render();
	Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    private void touchLastUpdate() {
	lastUpdateX = pos.x;
	lastUpdateY = pos.y;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
	if (obj.getClass().equals(this.getClass())) {
	    PositionEntity e = (PositionEntity) obj;
	    return e.uniqueId == uniqueId;
	}
	return false;
    }

    /**
     * Updates the bounds position with the given position and the angle given the offset
     * 
     * @param pos
     *            The new position
     * @param offsetTheta
     *            The offset angle
     */
    public void updateBounds(float x, float y) {
	// Update hard and soft radius and image bounds
	hardRadius.setX(x - hardRadius.width / 2);
	hardRadius.setY(y - hardRadius.height / 2);
	softRadius.set(x, y, softRadius.radius);
	imageBounds.setX(x - imageBounds.width / 2 + spriteOffsetX);
	imageBounds.setY(y - imageBounds.height / 2 + spriteOffsetY);
    }

    /**
     * Gets the length of the health bar normalized to max
     * 
     * @param max
     * @return
     */
    protected float getHealthLength(float max) {
	return hp * max / maxHp;
    }

    protected float[] getHealthColor() {
	float norm = hp / maxHp;
	float[] color;
	if (norm > (2f / 3f)) {
	    // Green
	    color = new float[] { 0f, 1f, 0f };
	} else if (norm < (2f / 3f) && norm > (1f / 3f)) {
	    // Yellow
	    color = new float[] { 1f, 1f, 0f };
	} else {
	    // Red
	    color = new float[] { 1f, 0f, 0f };
	}
	return color;
    }

    @Override
    public String toString() {
	return getName() + " " + pos + " id" + uniqueId;
    }

    @Override
    public void dispose() {
	VectorPool.returnObjects(vel, heading);
	super.dispose();
    }

}
