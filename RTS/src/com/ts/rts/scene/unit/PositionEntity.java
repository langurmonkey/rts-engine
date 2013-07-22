package com.ts.rts.scene.unit;

import com.ts.rts.datastructure.geom.Vector2;
import com.ts.rts.scene.map.IRTSMap;
import com.ts.rts.util.VectorPool;

/**
 * A physical or abstract entity that has a position.
 * 
 * @author Toni Sagrista
 * 
 */
public abstract class PositionEntity {
	private static long uniqueIdSeq = 0;
	protected final long uniqueId;

	/** Reference to the map **/
	protected IRTSMap map;

	/** Position of this entity **/
	public Vector2 pos;
	public float z;

	/**
	 * The viewing distance in pixels, for the fog of war
	 */
	public int viewingDistance;

	public PositionEntity() {
		this.uniqueId = getUniqueId();
	}

	public PositionEntity(Vector2 pos) {
		this();
		this.pos = pos;
	}

	public PositionEntity(float x, float y) {
		this();
		this.pos = VectorPool.getObject(x, y);
	}

	private static synchronized long getUniqueId() {
		return uniqueIdSeq++;
	}

	public void dispose() {
		VectorPool.returnObject(pos);
	}
}
