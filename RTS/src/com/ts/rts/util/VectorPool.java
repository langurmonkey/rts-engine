package com.ts.rts.util;

import java.util.Collection;

import com.badlogic.gdx.utils.Pool;
import com.ts.rts.datastructure.geom.Vector2;

/**
 * Implementation of Pool for vectors. Should bust performance in hand-held devices, minimizing GC time.
 * 
 * @author Toni Sagrista
 * 
 */
public class VectorPool extends Pool<Vector2> {
	private static int created = 0;
	private static int requested = 0;

	private static VectorPool pool;

	public static void initialize(int capacity) {
		pool = new VectorPool(capacity);
	}

	public static Vector2 getObject() {
		requested++;
		return pool.obtain();
	}

	public static Vector2 getObject(float x, float y) {
		return getObject().set(x, y);
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

	protected VectorPool(int capacity) {
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
