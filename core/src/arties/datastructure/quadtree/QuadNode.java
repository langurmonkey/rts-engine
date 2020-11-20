package arties.datastructure.quadtree;

import arties.datastructure.IMapCell;
import arties.datastructure.Pair;
import arties.datastructure.astar.AStar;
import arties.datastructure.astar.AStarNode;
import arties.scene.map.MapProperties.TerrainType;
import arties.scene.unit.steeringbehaviour.IEntity;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Quadnode of a quadtree implementing {@link IMapCell} and with A* pathfinding capabilities. Each node maintains a list
 * of all its adjacent nodes with a weight, so that pathfinding is even faster.
 *
 * @param <T>
 * @author Toni Sagrista
 */
public class QuadNode<T extends IEntity> extends AStarNode<T> implements IMapCell<T> {

    public Rectangle bounds;

    /**
     * The objects in this cell
     */
    public Set<T> objects;

    public QuadTree<T> owner;

    public QuadNode<T> northEast;
    public QuadNode<T> northWest;
    public QuadNode<T> southEast;
    public QuadNode<T> southWest;

    /**
     * Start A* fields
     */
    private TerrainType type = TerrainType.UNDEFINED;
    public List<Pair<QuadNode<T>, Float>> adjacentNodes = new ArrayList<>();

    public float x;
    public float y;
    public float z;

    /**
     * Creates a new node with the given bounds, the given owner and the given parent
     *
     * @param bounds
     * @param owner
     * @param parent
     */
    public QuadNode(Rectangle bounds, QuadTree<T> owner, QuadNode<T> parent) {
        this.bounds = bounds;
        this.owner = owner;
        this.parent = parent;
        this.x = bounds.getX() + bounds.getWidth() / 2;
        this.y = bounds.getY() + bounds.getHeight() / 2;
        this.objects = new HashSet<>();
    }

    public boolean isLeaf() {
        return northWest == null && northEast == null && southWest == null && southEast == null;
    }

    public boolean hasObjects() {
        return !objects.isEmpty();
    }

    public boolean hasObjectsDifferentThan(T o) {
        return !objects.contains(o);
    }

    /**
     * Adds the object to this node.
     *
     * @param o
     * @return True if the object has been added, false if it already existed.
     */
    public boolean addObject(T o) {
        return objects.add(o);
    }

    /**
     * Adds all the objects to the node
     *
     * @param objs
     */
    public void addObjects(Set<T> objs) {
        objects.addAll(objs);
    }

    public void addObjectsRec(Set<T> objs) {
        for (T obj : objs) {
            add(obj);
        }
    }

    public void clearObjects() {
        objects = new HashSet<>();
    }

    /**
     * Removes the given object in the node and its descendants
     *
     * @param o
     */
    public void remove(T o) {
        if (!isLeaf()) {
            northEast.remove(o);
            northWest.remove(o);
            southEast.remove(o);
            southWest.remove(o);
        }

        if (isEmpty()) {
            join();
        }
        removeObject(o);

    }

    /**
     * Removes the given object in this node, if it is here
     *
     * @param o
     */
    @Override
    public boolean removeObject(T o) {
        return objects.remove(o);
    }

    public void add(T o) {
        // Do we have to split?
        if (isLeaf() && hasObjects() && bounds.getWidth() > owner.minSize && bounds.getHeight() > owner.minSize) {
            split();
        }

        if (isLeaf() || o.bounds().contains(bounds)) {
            // We set the object if we are
            // a leaf, or if the bounds of the object contain our bounds.
            addObject(o);
        } else {
            if (northEast.bounds.overlaps(o.bounds())) {
                northEast.add(o);
            }
            if (northWest.bounds.overlaps(o.bounds())) {
                northWest.add(o);
            }
            if (southEast.bounds.overlaps(o.bounds())) {
                southEast.add(o);
            }
            if (southWest.bounds.overlaps(o.bounds())) {
                southWest.add(o);
            }
        }
    }

    public void split() {
        if (isLeaf()) {
            float hw = bounds.getWidth() / 2f;
            float hh = bounds.getHeight() / 2f;
            northWest = new QuadNode<>(new Rectangle(bounds.getX(), bounds.getY(), hw, hh), this.owner, this);
            northEast = new QuadNode<>(new Rectangle(bounds.getX() + hw, bounds.getY(), bounds.getWidth() - hw, hh),
                this.owner, this);
            southWest = new QuadNode<>(new Rectangle(bounds.getX(), bounds.getY() + hh, hw, bounds.getHeight() - hh),
                this.owner, this);
            southEast = new QuadNode<>(new Rectangle(bounds.getX() + hw, bounds.getY() + hh, bounds.getWidth() - hw,
                bounds.getHeight() - hh), this.owner, this);

            owner.root.genVisibilityGraph();

            Set<T> temp = objects;
            clearObjects();

            addObjectsRec(temp);

        }
    }

    public void join() {
        if (!isLeaf()) {
            northWest.join();
            northEast.join();
            southWest.join();
            southEast.join();

            // What do we do with the objects?
            if (northWest.hasObjects()) {
                addObjects(northWest.objects);
            } else if (northEast.hasObjects()) {
                addObjects(northEast.objects);
            } else if (southWest.hasObjects()) {
                addObjects(southWest.objects);
            } else if (southEast.hasObjects()) {
                addObjects(southEast.objects);
            }

            northWest = null;
            northEast = null;
            southWest = null;
            southEast = null;
        }
    }

    public void findContains(Vector2 p, HashSet<T> result) {
        if (contains(p)) {
            for (T object : objects) {
                Rectangle objectBounds = object.bounds();
                if (objectBounds.contains(p.x, p.y))
                    result.add(object);
                if (!isLeaf()) {
                    northWest.findContains(p, result);
                    northEast.findContains(p, result);
                    southWest.findContains(p, result);
                    southEast.findContains(p, result);
                }
            }
        }
    }

    public void findIntersects(Rectangle r, HashSet<T> result) {
        if (bounds.contains(r) || bounds.overlaps(r)) {
            for (T object : objects) {
                Rectangle objectBounds = object.bounds();
                if (objectBounds.overlaps(r))
                    result.add(object);
                if (!isLeaf()) {
                    northWest.findIntersects(r, result);
                    northEast.findIntersects(r, result);
                    southWest.findIntersects(r, result);
                    southEast.findIntersects(r, result);
                }
            }
        }
    }

    public void findInside(Rectangle r, HashSet<T> result) {
        if (bounds.contains(r) || bounds.overlaps(r)) {
            for (T object : objects) {
                Rectangle objectBounds = object.bounds();
                if (hasObjects() && objectBounds.contains(r))
                    result.add(object);
                if (!isLeaf()) {
                    northWest.findInside(r, result);
                    northEast.findInside(r, result);
                    southWest.findInside(r, result);
                    southEast.findInside(r, result);
                }
            }
        }
    }

    public void findNodesInside(Rectangle r, HashSet<QuadNode<T>> result) {
        if (bounds.overlaps(r)) {
            for (T object : objects) {
                Rectangle objectBounds = object.bounds();
                if (hasObjects() && objectBounds.contains(r))
                    result.add(this);
                if (!isLeaf()) {
                    northWest.findNodesInside(r, result);
                    northEast.findNodesInside(r, result);
                    southWest.findNodesInside(r, result);
                    southEast.findNodesInside(r, result);
                }
            }
        }
    }

    public void getNodes(Rectangle rect, List<QuadNode<T>> nodes) {
        if (northWest != null)
            northWest.getNodes(rect, nodes);

        if (northEast != null)
            northEast.getNodes(rect, nodes);

        if (southWest != null)
            southWest.getNodes(rect, nodes);

        if (southEast != null)
            southEast.getNodes(rect, nodes);

        if (isLeaf()) {
            if (rect.overlaps(this.bounds))
                nodes.add(this);
        }

    }

    public QuadNode<T> getNode(Vector2 point) {
        if (this.contains(point)) {
            if (isLeaf())
                return this;
            else {
                if (northEast.contains(point))
                    return northEast.getNode(point);
                else if (northWest.contains(point))
                    return northWest.getNode(point);
                else if (southEast.contains(point))
                    return southEast.getNode(point);
                else if (southWest.contains(point))
                    return southWest.getNode(point);
                else
                    return null;
            }

        } else {
            return null;
        }
    }

    public boolean contains(Vector2 point) {
        return bounds.contains(point.x, point.y);
    }

    public void genVisibilityGraph() {
        genVisibilityGraphForNode(this.parent != null ? (QuadNode<T>) this.parent : this, this);
    }

    /**
     * Create adjacent nodes
     */
    public void genVisibilityGraphForNode(QuadNode<T> headNode, QuadNode<T> node) {

        if (node == null)
            return;

        if (node.isLeaf()) {

            Rectangle rect = new Rectangle(node.bounds.getX() - 1, node.bounds.getY() - 1, node.bounds.getWidth() + 2,
                node.bounds.getHeight() + 2);
            List<QuadNode<T>> nodes = new ArrayList<>();
            headNode.getNodes(rect, nodes);

            // Clear adjacent nodes
            node.adjacentNodes.clear();

            for (QuadNode<T> foundNode : nodes) {
                if (foundNode != node) {
                    if (!foundNode.isBlocked()) {
                        // Work out Manhattan distance between found and node
                        Float dist = AStar.heuristicManhattan(node, foundNode);
                        node.adjacentNodes.add(new Pair<>(foundNode, dist));
                    } else {
                        // Maximum cost for blocked nodes
                        node.adjacentNodes.add(new Pair<>(foundNode, Float.MAX_VALUE));
                    }
                }
            }
        }

        genVisibilityGraphForNode(headNode, node.northWest);
        genVisibilityGraphForNode(headNode, node.northEast);
        genVisibilityGraphForNode(headNode, node.southWest);
        genVisibilityGraphForNode(headNode, node.southEast);

    }

    /**
     * Checks if this quad node and its descendants are empty of objects and blocked nodes
     *
     * @return
     */
    public boolean isEmpty() {
        if (isLeaf()) {
            // Leaf
            return isEmptyNode();
        } else {
            // Not leaf
            return isEmptyNode() && northEast.isEmpty() && northWest.isEmpty() && southEast.isEmpty()
                && southWest.isEmpty();
        }
    }

    /**
     * Checks if this quad node (only) is empty of objects and is not blocked
     *
     * @return
     */
    public boolean isEmptyNode() {
        return !isBlocked() && objects.isEmpty();
    }

    @Override
    public boolean isBlocked() {
        return type.equals(TerrainType.BLOCKED);
    }

    @Override
    public void setTerrainType(TerrainType type) {
        this.type = type;
    }

    @Override
    public void block() {
        type = TerrainType.BLOCKED;
    }

    @Override
    public String toString() {
        return "[" + x + ", " + y + "]";
    }

    public void clearPath() {
        this.parent = null;
        if (!isLeaf()) {
            northWest.clearPath();
            northEast.clearPath();
            southWest.clearPath();
            southEast.clearPath();
        }
    }

    /**
     * Returns a set of all the leaf nodes which contain the given object
     * TODO Improve efficiency
     *
     * @param o
     * @return
     */
    public Set<IMapCell<T>> findNodesWith(T o) {
        Set<IMapCell<T>> set = new HashSet<>();
        if (isLeaf() && objects.contains(o)) {
            set.add(this);
        } else if (!isLeaf()) {
            set.addAll(northWest.findNodesWith(o));
            set.addAll(northEast.findNodesWith(o));
            set.addAll(southWest.findNodesWith(o));
            set.addAll(southEast.findNodesWith(o));
        }
        return set;
    }

    @Override
    public float x() {
        return x;
    }

    @Override
    public float y() {
        return y;
    }

    @Override
    public float z(float x, float y) {
        return z;
    }

    @Override
    public Rectangle bounds() {
        return bounds;
    }

    @Override
    public boolean containsObject(T o) {
        return objects.contains(o);
    }

    @Override
    public Set<IMapCell<T>> findAdjacentCells() {
        Set<IMapCell<T>> adjacent = new HashSet<>();
        for (Pair<QuadNode<T>, Float> adj : adjacentNodes) {
            adjacent.add(adj.first);
        }
        return adjacent;
    }

    @Override
    public Set<T> getObjects() {
        return objects;
    }

    @Override
    public TerrainType getTerrain() {
        return type;
    }

    @Override
    public boolean isShadow() {
        return false;
    }

    @Override public float getSlowdown() {
        return 0;
    }

}
