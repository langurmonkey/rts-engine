package com.ts.rts.scene.unit.steeringbehaviour;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.ts.rts.datastructure.geom.Vector2;
import com.ts.rts.scene.map.IRTSMap;
import com.ts.rts.scene.unit.MovingEntity;
import com.ts.rts.scene.unit.group.UnitGroup;
import com.ts.rts.util.VectorPool;

/**
 * First experimental implementation of flocking for a group of units. Uses alignment, separation and cohesion
 * behaviours.
 * 
 * @author Toni Sagrista
 * 
 */
public class BehaviourFlockingPath extends AbstractSteeringBehaviour {

	protected float waypointSeekDistSq = 20 * 20;
	protected Path path;
	protected boolean finished;
	protected UnitGroup group;
	protected IRTSMap map;

	BehaviourAlignment alignment;
	BehaviourCohesion cohesion;
	BehaviourArriveGroup arrive;

	public BehaviourFlockingPath(MovingEntity unit, Path path, UnitGroup group, IRTSMap map) {
		super(unit);
		this.path = path;
		this.group = group;
		this.map = map;
		alignment = new BehaviourAlignment(unit, group, path.currentWaypoint());
		cohesion = new BehaviourCohesion(unit, group);
	}

	@Override
	public Vector2 calculate() {
		if (group.pos.distanceSq(path.currentWaypoint()) < waypointSeekDistSq) {
			path.nextWaypoint();
		}
		if (!path.finished()) {
			alignment.updateTarget(path.currentWaypoint());
			Vector2 force = VectorPool.getObject();
			force.add(alignment.calculate()).add(cohesion.calculate());
			return force;
		} else {
			if (arrive == null) {
				// Here we give the group position as checker
				arrive = new BehaviourArriveGroup(unit, path.currentWaypoint(), group);
			}
			return arrive.calculate().add(cohesion.calculate());
		}
	}

	@Override
	public boolean isDone() {
		return arrive != null && arrive.isDone();
	}

	@Override
	public void renderBehaviour() {
		for (int j = 1; j < path.size(); j++) {
			Vector2 waypoint = path.get(j);
			shapeRenderer.begin(ShapeType.Filled);
			shapeRenderer.setColor(new Color(0f, .6f, 0f, .7f));
			shapeRenderer.circle(waypoint.x, waypoint.y, 5f);
			shapeRenderer.end();
		}
		if (arrive != null)
			arrive.renderBehaviour();
		cohesion.renderBehaviour();
		alignment.renderBehaviour();
	}

	@Override
	public void dispose() {
		path.dispose();
	}

}
