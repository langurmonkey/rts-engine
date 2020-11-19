package com.ts.rts.scene.unit;

import com.ts.rts.datastructure.geom.Vector3;
import com.ts.rts.scene.map.IRTSMap;
import com.ts.rts.util.Vector3Pool;

/**
 * A physical or abstract entity that has a position.
 *
 * @author Toni Sagrista
 */
public abstract class PositionEntity {
    private static long uniqueIdSeq = 0;
    protected final long uniqueId;

    /**
     * Reference to the map
     **/
    protected IRTSMap map;

    /**
     * Position of this entity
     **/
    public Vector3 pos;

    boolean visible = true;

    /**
     * The viewing distance in pixels, for the fog of war
     */
    public int viewDistance = 0;

    public PositionEntity() {
        this.uniqueId = getUniqueId();
    }

    public PositionEntity(Vector3 pos) {
        this();
        this.pos = pos;
    }

    public PositionEntity(float x, float y, float z) {
        this();
        this.pos = Vector3Pool.getObject(x, y, z);
    }

    private static synchronized long getUniqueId() {
        return uniqueIdSeq++;
    }

    public void dispose() {
        Vector3Pool.returnObject(pos);
    }
}
