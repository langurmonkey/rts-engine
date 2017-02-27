package com.ts.rts.scene.unit.steeringbehaviour;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.ts.rts.datastructure.IMapCell;
import com.ts.rts.datastructure.geom.Vector2;
import com.ts.rts.scene.map.IRTSMap;
import com.ts.rts.scene.unit.IBoundsObject;
import com.ts.rts.scene.unit.PositionPhysicalEntity;
import com.ts.rts.util.VectorPool;

/**
 * This class represents a path composed of a sequence of waypoints, which in fact are {@link IMapCell} objects.
 * 
 * @author Toni Sagrista
 * 
 */
public class Path {

    private List<Vector2> waypoints;
    private Iterator<Vector2> it;
    private Vector2 current;

    /**
     * Creates a path using a list of nodes, an entity and a target position
     * 
     * @param nodes
     */
    public Path(List<IMapCell<IBoundsObject>> nodes, Vector2 pos, float finalX, float finalY) {
	waypoints = new LinkedList<Vector2>();

	if (nodes != null) {
	    if (nodes.size() > 1) {
		// At least two waypoints
		for (int i = nodes.size() - 1; i >= 0; i--) {
		    IMapCell<IBoundsObject> node = nodes.get(i);
		    Vector2 point;

		    if (i == nodes.size() - 1) {
			point = pos;
		    } else if (i == 0) {
			point = VectorPool.getObject(finalX, finalY);
		    } else {
			point = VectorPool.getObject(node.x(), node.y());
		    }

		    waypoints.add(point);

		}
	    } else {
		// Only one waypoint, we're moving in the same node
		waypoints.add(VectorPool.getObject(finalX, finalY));
	    }

	    resetPath();
	}

    }

    /**
     * Creates a path with the given list of waypoints
     */
    private Path(List<Vector2> waypoints) {
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
    public Vector2 currentWaypoint() {
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
     * @param i
     *            The waypoint index
     * @return
     */
    public Vector2 get(int i) {
	assert i >= 0 && i < size() : "Index out of bounds";
	return waypoints.get(i);
    }

    /**
     * This smooths the path for the given entity. It also resets the current waypoint.
     * 
     * @param unitBounds
     */
    public void smooth(PositionPhysicalEntity entity, IRTSMap map) {
	List<Vector2> smoothed = new ArrayList<Vector2>();
	smoothed.addAll(waypoints);

	int checkPoint = 0;
	int currentPoint = 1;
	while (currentPoint + 1 < waypoints.size()) {
	    if (map.walkable(waypoints.get(checkPoint), waypoints.get(currentPoint + 1), entity)) {
		Vector2 v = waypoints.get(currentPoint);
		smoothed.remove(v);
		VectorPool.returnObject(v);
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
	VectorPool.returnObjects(waypoints);
    }

    @Override
    public String toString() {
	StringBuilder sb = new StringBuilder();
	for (Vector2 waypoint : waypoints) {
	    sb.append(waypoint.toString());
	}
	return sb.toString();
    }
}
