package rts.arties.scene.unit;

import rts.arties.datastructure.geom.Vector3;
import rts.arties.scene.map.IRTSMap;
import rts.arties.util.Vector3Pool;

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
    public int viewingDistance = 0;

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

    public Vector3 pos(){
        return pos;
    }

    public IRTSMap map() {
        return this.map;
    }

    private static synchronized long getUniqueId() {
        return uniqueIdSeq++;
    }

    public void dispose() {
        Vector3Pool.returnObject(pos);
    }

    public float viewingDistance(){
        return viewingDistance;
    }

}
