package rts.arties.util;

import rts.arties.datastructure.geom.Vector2;
import rts.arties.datastructure.geom.Vector3;
import com.badlogic.gdx.utils.Pool;

import java.util.Collection;

/**
 * Implementation of Pool for vectors. Should bust performance in hand-held devices, minimizing GC time.
 *
 * @author Toni Sagrista
 */
public class Vector3Pool extends Pool<Vector3> {
    private static int created = 0;
    private static int requested = 0;

    private static Vector3Pool pool;

    public static void initialize(int capacity) {
        pool = new Vector3Pool(capacity);
    }

    public static Vector3 getObject() {
        requested++;
        return pool.obtain();
    }

    public static Vector3 getObject(float x, float y, float z) {
        return getObject().set(x, y, z);
    }

    public static Vector3 getObject(float x, float y) {
        return getObject().set(x, y, 0);
    }

    public static Vector3 getObject(Vector3 vec) {
        return getObject().set(vec.x, vec.y, vec.z);
    }

    public static Vector3 getObject(Vector2 vec) {
        return getObject().set(vec.x, vec.y, 0);
    }

    public static Vector3 getObject(Vector2 vec, float z) {
        return getObject().set(vec.x, vec.y, z);
    }

    public static void returnObject(Vector3 vector) {
        pool.free(vector);
    }

    public static void returnObjects(Vector3... vectors) {
        for (Vector3 vector : vectors)
            pool.free(vector);
    }

    public static void returnObjects(Collection<Vector3> vectors) {
        for (Vector3 vector : vectors)
            pool.free(vector);
    }

    protected Vector3Pool(int capacity) {
        super(capacity);
    }

    @Override
    protected Vector3 newObject() {
        created++;
        return new Vector3();
    }

    public static String getStats() {
        return "Created: " + created + ", requested: " + requested + ", pool hits: " + (requested - created);
    }
}
