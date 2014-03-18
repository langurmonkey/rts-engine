package com.ts.rts.scene.cam;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.ts.rts.datastructure.geom.Vector2;
import com.ts.rts.scene.unit.PositionPhysicalEntity;
import com.ts.rts.util.VectorPool;

/**
 * Handles the camera of the scene.
 * 
 * @author Toni Sagrista
 * 
 */
public class Camera {
    /**
     * Camera velocity in pixels/second
     */
    private static final int MAX_CAM_VEL = 1900;
    private static final int MAX_CAM_ACCEL = 5300;

    /**
     * The current map width
     */
    public float mapWidth;

    /**
     * The current map height
     */
    public float mapHeight;

    /**
     * The canvas (screen) width
     */
    public float canvasWidth;

    /**
     * The canvas (screen) height
     */
    public float canvasHeight;

    /**
     * The position of the camera, from the top-left corner of the canvas square
     */
    public Vector2 pos;

    /**
     * Current velocity of the camera
     */
    public Vector2 vel;

    /**
     * Camera acceleration
     */
    public Vector2 accel;

    private com.badlogic.gdx.graphics.Camera libgdxCamera;

    public static Camera camera;

    public static Camera getInstance() {
	assert camera != null : "Camera not initialized";
	return camera;
    }

    public static Camera initialize(OrthographicCamera ortocamera, float camX, float camY, float mapWidth,
	    float mapHeight, float canvasWidth, float canvasHeight) {
	camera = new Camera(ortocamera, camX, camY, mapWidth, mapHeight, canvasWidth, canvasHeight);
	return camera;
    }

    public Camera(OrthographicCamera camera, float camX, float camY, float mapWidth, float mapHeight,
	    float canvasWidth, float canvasHeight) {
	super();
	this.libgdxCamera = camera;
	pos = VectorPool.getObject(camX, camY);

	vel = VectorPool.getObject();

	accel = VectorPool.getObject();

	this.mapHeight = mapHeight;
	this.mapWidth = mapWidth;

	this.canvasHeight = canvasHeight;
	this.canvasWidth = canvasWidth;

    }

    public void lookAt(Vector2 pos) {
	this.pos.set(pos);
    }

    public void lookAt(float x, float y) {
	pos.set(x, y);
    }

    public void update(float secs) {

	// dv = da*dt
	vel.add(accel.clone().multiply(secs));

	// dx = dv*dt
	pos.add(vel.clone().multiply(secs).truncate(MAX_CAM_VEL));

	/**
	 * canvasWidth/2 <= x <= mapWidth - canvasWidth/2
	 * canvasHeight/2 <= y <= mapHeight - canvasHeight/2
	 */

	// Boundary check, do not allow canvas to go outside map
	if (pos.x > mapWidth - canvasWidth / 2) {
	    pos.x = mapWidth - canvasWidth / 2;
	    stopHorizontal();
	}
	if (pos.x < canvasWidth / 2) {
	    pos.x = canvasWidth / 2;
	    stopHorizontal();
	}
	if (pos.y > mapHeight - canvasHeight / 2) {
	    pos.y = mapHeight - canvasHeight / 2;
	    stopVertical();
	}
	if (pos.y < canvasHeight / 2) {
	    pos.y = canvasHeight / 2;
	    stopVertical();
	}

	// Update libgdx camera
	libgdxCamera.position.set(pos.x, pos.y, 0);
	// Tell the camera to update its matrices
	libgdxCamera.update();
    }

    public void setAccel(Vector2 accel) {
	this.accel.add(accel);
	this.accel.truncate(MAX_CAM_ACCEL);
    }

    public void stop() {
	this.vel.zero();
	this.accel.zero();
    }

    /**
     * Triggers movement of this camera to the right
     */
    public void right() {
	vel.x = MAX_CAM_VEL;
    }

    public void right(float value) {
	vel.x = value;
    }

    public void left() {
	vel.x = -MAX_CAM_VEL;
    }

    public void left(float value) {
	vel.x = -value;
    }

    /**
     * Stops the horizontal movement of this camera
     */
    public void stopHorizontal() {
	vel.x = 0;
	accel.x = 0;
    }

    public void up() {
	vel.y = MAX_CAM_VEL;
    }

    public void up(float value) {
	vel.y = value;
    }

    public void down() {
	vel.y = -MAX_CAM_VEL;
    }

    public void down(float value) {
	vel.y = -value;
    }

    public void stopVertical() {
	vel.y = 0;
	accel.y = 0;
    }

    public com.badlogic.gdx.graphics.Camera getLibgdxCamera() {
	return libgdxCamera;
    }

    /**
     * Checks if the current camera position contains the given entity
     * 
     * @param e
     * @return
     */
    public boolean contains(PositionPhysicalEntity e) {
	// TODO do this
	return true;
    }

    @Override
    public String toString() {
	return "Camera " + pos;
    }

}
