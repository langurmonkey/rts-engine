package rts.arties.scene.cam;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import rts.arties.datastructure.geom.Vector2;

/**
 * Handles the camera of the scene.
 *
 * @author Toni Sagrista
 */
public class Camera {
    /**
     * Camera velocity in pixels/second
     */
    private static final int MAX_CAM_VEL = 1900;
    private static final int MAX_CAM_ACCEL = 5300;

    private static final float MIN_ZOOM = 0.3f;
    private static final float MAX_ZOOM = 2.0f;

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
     * The position of the middle of the viewport of the camera in world coordinates
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

    /**
     * The zoom level
     */
    public float zoom = 1;

    private final OrthographicCamera orthoCamera;
    public static Camera camera;

    private Vector2 aux;

    public static Camera getInstance() {
        assert camera != null : "Camera not initialized";
        return camera;
    }

    public static Camera initialize(float camX, float camY, float mapWidth, float mapHeight, float canvasWidth, float canvasHeight) {
        OrthographicCamera orthoCamera = new OrthographicCamera(canvasWidth, canvasHeight);
        camera = new Camera(orthoCamera, camX, camY, mapWidth, mapHeight, canvasWidth, canvasHeight);
        return camera;
    }

    public Camera(OrthographicCamera camera, float camX, float camY, float mapWidth, float mapHeight, float canvasWidth, float canvasHeight) {
        super();
        this.orthoCamera = camera;
        pos = new Vector2(camX, camY);
        vel = new Vector2();
        accel = new Vector2();
        aux = new Vector2();

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
        pos.add(vel.clone().multiply(secs).truncate(MAX_CAM_VEL * zoom));

        /**
         * canvasWidth/2 <= x <= mapWidth - canvasWidth/2
         * canvasHeight/2 <= y <= mapHeight - canvasHeight/2
         */

        // Boundary check, do not allow canvas to go outside map
        if (pos.x > mapWidth - canvasWidth * zoom / 2f) {
            pos.x = mapWidth - canvasWidth * zoom / 2f;
            stopHorizontal();
        }
        if (pos.x < canvasWidth * zoom / 2f) {
            pos.x = canvasWidth * zoom / 2f;
            stopHorizontal();
        }
        if (pos.y > mapHeight - canvasHeight * zoom / 2f) {
            pos.y = mapHeight - canvasHeight * zoom / 2f;
            stopVertical();
        }
        if (pos.y < canvasHeight * zoom / 2f) {
            pos.y = canvasHeight * zoom / 2f;
            stopVertical();
        }

        // Update libgdx camera
        orthoCamera.position.set(pos.x, pos.y, 0);
        orthoCamera.zoom = zoom;
        // Tell the camera to update its matrices
        orthoCamera.update();
    }

    public void zoom(float zoomAddition, float dx, float dy) {
        float newZoom = MathUtils.clamp(zoom + zoomAddition, MIN_ZOOM, MAX_ZOOM);
        if (newZoom != zoom) {
            zoom = newZoom;
            pos.add(dx, dy);
        }
    }

    /**
     * Convert from screen coordinates (pixels from canvas bottom-left) to world coordinates (pixels from map bottom-left)
     *
     * @param screenX x screen coordinate
     * @param screenY y screen coordinate
     * @param out     Vector for the result
     * @return The resulting world coordinates
     */
    public Vector2 screenToWorld(float screenX, float screenY, Vector2 out) {
        // Coordinates accounting for zoom
        float zX = screenX * zoom;
        float zY = screenY * zoom;

        // Position of bottom-left corner of canvas (our view)
        float canvasX = pos.x - canvasWidth * zoom / 2f;
        float canvasY = pos.y - canvasHeight * zoom / 2f;

        return out.set(canvasX + zX, canvasY + zY);
    }

    public Vector2 screenToWorld(float zoom, float screenX, float screenY, Vector2 out) {
        // Coordinates accounting for zoom
        float zX = screenX * zoom;
        float zY = screenY * zoom;

        // Position of bottom-left corner of canvas (our view)
        float canvasX = pos.x - canvasWidth * zoom / 2f;
        float canvasY = pos.y - canvasHeight * zoom / 2f;

        return out.set(canvasX + zX, canvasY + zY);
    }

    /**
     * Convert from world coordinates (pixels from map bottom-left) to screen coordinates (pixels from canvas bottom-left)
     *
     * @param worldX x world coordinate
     * @param worldY y world coordinate
     * @param out    Vector for the result
     * @return The resulting screen coordinates
     */
    public Vector2 worldToScreen(float worldX, float worldY, Vector2 out) {
        return out.set((2f * worldX - 2f * pos.x + canvasWidth * zoom) / (2f * zoom), (2f * worldY - 2f * pos.y + canvasHeight * zoom) / (2f * zoom));
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

    public OrthographicCamera getOrthoCamera() {
        return orthoCamera;
    }

    /**
     * Check if the current viewport contains this point in world space
     *
     * @param worldX X in world space
     * @param worldY Y in world space
     * @return Whether the camera contains the point
     */
    public boolean containsPoint(float worldX, float worldY) {
        worldToScreen(worldX, worldY, aux);
        return aux.x >= 0 && aux.y >= 0 && aux.x <= canvasWidth && aux.y <= canvasHeight;
    }

    /**
     * Check if the current viewport contains this point in world space
     *
     * @param worldX X in world space
     * @param worldY Y in world space
     * @param size   The size of the point
     * @return Whether the camera contains the point
     */
    public boolean containsPoint(float worldX, float worldY, float size) {
        worldToScreen(worldX, worldY, aux);
        return aux.x + size >= 0 && aux.y + size >= 0 && aux.x - size <= canvasWidth && aux.y - size <= canvasHeight;
    }

    @Override
    public String toString() {
        return "Camera " + pos;
    }

    public float getCameraDisplacementX() {
        return pos.x - canvasWidth / 2f;
    }

    public float getCameraDisplacementY() {
        return pos.y - canvasHeight / 2f;
    }

    public void resize(int width, int height) {
        this.canvasWidth = width;
        this.canvasHeight = height;
        this.orthoCamera.setToOrtho(false, width, height);
    }

    public Matrix4 combined() {
        return orthoCamera.combined;
    }
}
