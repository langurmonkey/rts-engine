package com.ts.rts.scene.unit.steeringbehaviour;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.ts.rts.datastructure.geom.Vector2;
import com.ts.rts.scene.map.IRTSMap;
import com.ts.rts.scene.unit.Unit;
import com.ts.rts.scene.unit.group.UnitGroup;
import com.ts.rts.util.VectorPool;

/**
 * Class which holds steering behaviours for a unit.
 * 
 * @author Toni Sagrista
 * 
 */
public class SteeringBehaviours {
	enum BehaviourClass {
		M_SEEK, M_FLEE, M_ARRIVE, M_PURSUIT, M_EVADE, M_WANDER, M_WANDERPATH, M_FOLLOWPATH, M_AVOIDWALLS, M_SEPARATION, M_FLOCKING, M_COHESION;
	}

	private Unit unit;
	private Map<BehaviourClass, ISteeringBehaviour> sbMap;

	public SteeringBehaviours(Unit unit) {
		super();
		this.sbMap = new HashMap<BehaviourClass, ISteeringBehaviour>();
		this.unit = unit;
	}

	public Vector2 calculate() {
		Vector2 sum = VectorPool.getObject();
		Set<BehaviourClass> bhs = sbMap.keySet();
		for (BehaviourClass bh : bhs) {
			ISteeringBehaviour sb = sbMap.get(bh);
			Vector2 partialForce = sb.calculate();
			sum.add(partialForce);
			VectorPool.returnObject(partialForce);
		}
		return sum;
	}

	public void render() {
		Set<BehaviourClass> classes = sbMap.keySet();
		for (BehaviourClass clazz : classes) {
			ISteeringBehaviour sb = sbMap.get(clazz);
			sb.render();
		}
	}

	public Set<ISteeringBehaviour> getBehaviours() {
		Set<BehaviourClass> classes = sbMap.keySet();
		Set<ISteeringBehaviour> set = new HashSet<ISteeringBehaviour>();
		for (BehaviourClass clazz : classes) {
			set.add(sbMap.get(clazz));
		}
		return set;
	}

	public void removeDoneBehaviours() {
		Iterator<BehaviourClass> it = sbMap.keySet().iterator();
		while (it.hasNext()) {
			BehaviourClass bc = it.next();
			if (sbMap.get(bc).isDone()) {
				it.remove();
			}
		}
	}

	public void addSeek(Vector2 targetPosition) {
		if (sbMap.containsKey(BehaviourClass.M_SEEK)) {
			sbMap.remove(BehaviourClass.M_SEEK);
		}
		sbMap.put(BehaviourClass.M_SEEK, new BehaviourSeek(unit, targetPosition));
	}

	public void addFlee(Vector2 targetPosition) {
		if (sbMap.containsKey(BehaviourClass.M_FLEE)) {
			sbMap.remove(BehaviourClass.M_FLEE);
		}
		sbMap.put(BehaviourClass.M_FLEE, new BehaviourFlee(unit, targetPosition));
	}

	public void addArrive(Vector2 targetPosition) {
		if (sbMap.containsKey(BehaviourClass.M_ARRIVE)) {
			sbMap.remove(BehaviourClass.M_ARRIVE);
		}
		sbMap.put(BehaviourClass.M_ARRIVE, new BehaviourArrive(unit, targetPosition));
	}

	public void addWander() {
		if (sbMap.containsKey(BehaviourClass.M_WANDER)) {
			sbMap.remove(BehaviourClass.M_WANDER);
		}
		sbMap.put(BehaviourClass.M_WANDER, new BehaviourWander(unit));
	}

	public void addWanderPath(IRTSMap map, int wanderRadius) {
		if (sbMap.containsKey(BehaviourClass.M_WANDERPATH)) {
			sbMap.remove(BehaviourClass.M_WANDERPATH);
		}
		sbMap.put(BehaviourClass.M_WANDERPATH, new BehaviourWanderPath(unit, map, wanderRadius));
	}

	public void addSeparation(IRTSMap map) {
		if (sbMap.containsKey(BehaviourClass.M_SEPARATION)) {
			sbMap.remove(BehaviourClass.M_SEPARATION);
		}
		sbMap.put(BehaviourClass.M_SEPARATION, new BehaviourSeparation(unit, map));
	}

	public void addPursuit(Unit evader) {
		if (sbMap.containsKey(BehaviourClass.M_PURSUIT)) {
			sbMap.remove(BehaviourClass.M_PURSUIT);
		}
		sbMap.put(BehaviourClass.M_PURSUIT, new BehaviourPursuit(unit, evader));
	}

	public void addFollowPath(Path path) {
		if (sbMap.containsKey(BehaviourClass.M_FOLLOWPATH)) {
			sbMap.remove(BehaviourClass.M_FOLLOWPATH);
		}
		sbMap.put(BehaviourClass.M_FOLLOWPATH, new BehaviourFollowPath(unit, path, null));
	}

	public void addFollowPathCohesion(Path path, UnitGroup group) {
		if (sbMap.containsKey(BehaviourClass.M_FOLLOWPATH)) {
			sbMap.remove(BehaviourClass.M_FOLLOWPATH);
		}
		sbMap.put(BehaviourClass.M_FOLLOWPATH, new BehaviourFollowPath(unit, path, group));
	}

	public void addAvoidWalls(IRTSMap map) {
		if (sbMap.containsKey(BehaviourClass.M_AVOIDWALLS)) {
			sbMap.remove(BehaviourClass.M_AVOIDWALLS);
		}
		sbMap.put(BehaviourClass.M_AVOIDWALLS, new BehaviourAvoidWalls(unit, map));
	}

	public void addFlocking(Path path, UnitGroup group, IRTSMap map) {
		if (sbMap.containsKey(BehaviourClass.M_FLOCKING)) {
			sbMap.remove(BehaviourClass.M_FLOCKING);
		}
		sbMap.put(BehaviourClass.M_FLOCKING, new BehaviourFlockingPath(unit, path, group, map));
	}

	public void addCohesion(UnitGroup group) {
		if (sbMap.containsKey(BehaviourClass.M_COHESION)) {
			sbMap.remove(BehaviourClass.M_COHESION);
		}
		sbMap.put(BehaviourClass.M_COHESION, new BehaviourCohesion(unit, group));
	}

}
