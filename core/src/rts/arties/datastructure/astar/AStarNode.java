package rts.arties.datastructure.astar;

import rts.arties.datastructure.IMapCell;
import rts.arties.scene.unit.steeringbehaviour.IEntity;

/**
 * A node in the A* pathfinding algorithm. Contains some metadata useful for nodes that can be part of a path.
 *
 * @param <T>
 * @author Toni Sagrista
 */
public abstract class AStarNode<T extends IEntity> {

    // Cost, depth and parent for pathfinding
    protected float g = 0f;
    protected float h = 0f;
    protected IMapCell<T> parent;

    public float f() {
        return g + h;
    }

    public float g() {
        return g;
    }

    public float h() {
        return h;
    }

    public void setG(float g) {
        this.g = g;
    }

    public void setH(float h) {
        this.h = h;
    }

    public IMapCell<T> parent() {
        return parent;
    }

    public void setParent(IMapCell<T> parent) {
        this.parent = parent;
    }

}
