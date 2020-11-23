package rts.arties.util;

import rts.arties.datastructure.geom.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Pool;

import java.util.Collection;

/**
 * Implementation of Pool for vectors. Should bust performance in hand-held devices, minimizing GC time.
 *
 * @author Toni Sagrista
 */
public class Vector2Pool extends Pool<Vector2> {
    private static int created = 0;
    private static int requested = 0;

    private static Vector2Pool pool;

    public static void initialize(int capacity) {
        pool = new Vector2Pool(capacity);
    }

    public static Vector2 getObject() {
        requested++;
        return pool.obtain();
    }

    public static Vector2 getObject(float x, float y) {
        return getObject().set(x, y);
    }

    public static Vector2 getObject(Vector2 other) {
        return getObject().set(other.x, other.y);
    }

    public static Vector2 getObject(Vector3 other) {
        return getObject().set(other.x, other.y);
    }

    public static void returnObject(Vector2 vector) {
        pool.free(vector);
    }

    public static void returnObjects(Vector2... vectors) {
        for (Vector2 vector : vectors)
            pool.free(vector);
    }

    public static void returnObjects(Collection<Vector2> vectors) {
        for (Vector2 vector : vectors)
            pool.free(vector);
    }

    protected Vector2Pool(int capacity) {
        super(capacity);
    }

    @Override
    protected Vector2 newObject() {
        created++;
        return new Vector2();
    }

    public static String getStats() {
        return "Created: " + created + ", requested: " + requested + ", pool hits: " + (requested - created);
    }
}
