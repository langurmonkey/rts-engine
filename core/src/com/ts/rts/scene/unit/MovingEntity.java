package com.ts.rts.scene.unit;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.ts.rts.datastructure.geom.Vector3;

/**
 * An entity that moves.
 *
 * @author Toni Sagrista
 */
public abstract class MovingEntity extends PositionPhysicalEntity {
    /**
     * Velocity [m/s]
     **/
    public Vector3 vel;

    /**
     * Max speed [m/s]
     **/
    public float maxSpeed;

    /**
     * Max force [Kg*m/s^2]
     **/
    public float maxForce;

    /**
     * Maximum turn rate [rad/s]
     **/
    public float maxTurnRate;

    /**
     * This tells us if we're moving
     **/
    public boolean moving;

    /**
     * The distance from the target at which the unit starts to slow, for the arrive behaviour
     **/
    public float slowingDistance;

    float lastUpdateX, lastUpdateY;

    public abstract void updatePosition(float secs);

    public MovingEntity() {
        super();
    }

    public MovingEntity(float x, float y, float z) {
        super(x, y, z);
    }

    public MovingEntity(Vector3 pos) {
        super(pos);
    }

    @Override
    public void renderDebug(ShapeRenderer sr) {
        super.renderDebug(sr);

        sr.begin(ShapeType.Line);
        if (vel != null) {
            sr.setColor(new Color(0f, .6f, 0f, 1f));
            sr.line(pos.x, pos.y, pos.x + vel.x, pos.y + vel.y);
        }
        sr.end();
    }

    protected void updateCurrentMaxSpeed() {
        maxSpeed = maxSpeed * Math.max(hp / maxHp, 0.2f);
    }

    @Override
    public void setHp(float newHp) {
        super.setHp(newHp);
        updateCurrentMaxSpeed();
    }

}
