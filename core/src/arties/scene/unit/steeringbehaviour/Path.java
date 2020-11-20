package arties.scene.unit.steeringbehaviour;

import arties.datastructure.IMapCell;
import arties.datastructure.geom.Vector3;
import arties.scene.map.IRTSMap;
import arties.scene.unit.IBoundsObject;
import arties.scene.unit.PositionPhysicalEntity;
import arties.util.Vector3Pool;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * This class represents a path composed of a sequence of waypoints, which in fact are {@link IMapCell} objects.
 *
 * @author Toni Sagrista
 */
public class Path {

    private List<Vector3> waypoints;
    private Iterator<Vector3> it;
    private Vector3 current;

    public Path(List<IMapCell<IBoundsObject>> nodes, float x, float y, float finalX, float finalY) {
        this(nodes, Vector3Pool.getObject(x, y), finalX, finalY);
    }

    /**
     * Creates a path using a list of nodes, an entity and a target position
     *
     * @param nodes
     */
    public Path(List<IMapCell<IBoundsObject>> nodes, Vector3 pos, float finalX, float finalY) {
        waypoints = new LinkedList<>();

        if (nodes != null) {
            if (nodes.size() > 1) {
                // At least two waypoints
                for (int i = nodes.size() - 1; i >= 0; i--) {
                    IMapCell<IBoundsObject> node = nodes.get(i);
                    Vector3 point;

                    if (i == nodes.size() - 1) {
                        point = pos;
                    } else if (i == 0) {
                        point = Vector3Pool.getObject(finalX, finalY);
                    } else {
                        point = Vector3Pool.getObject(node.x(), node.y());
                    }

                    waypoints.add(point);

                }
            } else {
                // Only one waypoint, we're moving in the same node
                waypoints.add(Vector3Pool.getObject(finalX, finalY));
            }

            resetPath();
        }

    }

    /**
     * Creates a path with the given list of waypoints
     */
    private Path(List<Vector3> waypoints) {
        this.waypoints = waypoints;
        resetPath();
    }

    /**
     * Sets the current waypoint to the start
     */
    public void resetPath() {
        it = waypoints.iterator();
        nextWaypoint();
    }

    /**
     * Has the path reached the final waypoint?
     *
     * @return True if finished
     */
    public boolean finished() {
        return !it.hasNext();
    }

    /**
     * Moves to the next waypoint, if any
     */
    public void nextWaypoint() {
        if (it.hasNext())
            current = it.next();
    }

    /**
     * Returns the current waypoint
     *
     * @return
     */
    public Vector3 currentWaypoint() {
        return current;
    }

    /**
     * Gets the size of this path
     *
     * @return
     */
    public int size() {
        return waypoints.size();
    }

    /**
     * Gets the waypoint at the given position
     *
     * @param i The waypoint index
     * @return
     */
    public Vector3 get(int i) {
        assert i >= 0 && i < size() : "Index out of bounds";
        return waypoints.get(i);
    }

    /**
     * This smooths the path for the given entity. It also resets the current waypoint.
     *
     */
    public void smooth(PositionPhysicalEntity entity, IRTSMap map) {
        List<Vector3> smoothed = new ArrayList<>();
        smoothed.addAll(waypoints);

        int checkPoint = 0;
        int currentPoint = 1;
        while (currentPoint + 1 < waypoints.size()) {
            if (map.walkable(waypoints.get(checkPoint), waypoints.get(currentPoint + 1), entity)) {
                Vector3 v = waypoints.get(currentPoint);
                smoothed.remove(v);
                Vector3Pool.returnObject(v);
                currentPoint++;
            } else {
                checkPoint = currentPoint;
                currentPoint++;
            }
        }

        waypoints = smoothed;
        resetPath();

    }

    /**
     * Clones the path
     */
    public Path clone() {
        return new Path(waypoints);
    }

    public void dispose() {
        Vector3Pool.returnObjects(waypoints);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Vector3 waypoint : waypoints) {
            sb.append(waypoint.toString());
        }
        return sb.toString();
    }
}
