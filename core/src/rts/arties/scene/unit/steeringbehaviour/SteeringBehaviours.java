package rts.arties.scene.unit.steeringbehaviour;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import rts.arties.datastructure.geom.Vector3;
import rts.arties.scene.map.IRTSMap;
import rts.arties.scene.unit.group.UnitGroup;
import rts.arties.util.Vector3Pool;

import java.util.*;

/**
 * Class which holds steering behaviours for a unit.
 *
 * @author Toni Sagrista
 */
public class SteeringBehaviours {
    enum BehaviourClass {
        M_SEEK, M_FLEE, M_ARRIVE, M_PURSUIT, M_EVADE, M_WANDER, M_WANDERPATH, M_FOLLOWPATH, M_AVOIDWALLS, M_SEPARATION, M_FLOCKING, M_COHESION
    }


    private final IEntity unit;
    private final Map<BehaviourClass, ISteeringBehaviour> sbMap;

    public SteeringBehaviours(IEntity unit) {
        super();
        this.sbMap = new HashMap<>();
        this.unit = unit;
    }

    public Vector3 calculate() {
        Vector3 sum = Vector3Pool.getObject();
        Set<BehaviourClass> bhs = sbMap.keySet();
        for (BehaviourClass bh : bhs) {
            ISteeringBehaviour sb = sbMap.get(bh);
            Vector3 partialForce = sb.calculate();
            sum.add(partialForce);
            Vector3Pool.returnObject(partialForce);
        }
        return sum;
    }

    public void renderLine(ShapeRenderer sr) {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Set<BehaviourClass> classes = sbMap.keySet();
        for (BehaviourClass clazz : classes) {
            ISteeringBehaviour sb = sbMap.get(clazz);
            sb.renderLine(sr);
        }
    }

    public void renderFilled(ShapeRenderer sr) {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Set<BehaviourClass> classes = sbMap.keySet();
        for (BehaviourClass clazz : classes) {
            ISteeringBehaviour sb = sbMap.get(clazz);
            sb.renderFilled(sr);
        }
    }

    public Set<ISteeringBehaviour> getBehaviours() {
        Set<BehaviourClass> classes = sbMap.keySet();
        Set<ISteeringBehaviour> set = new HashSet<>();
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

    public void addSeek(Vector3 targetPosition) {
        sbMap.remove(BehaviourClass.M_SEEK);
        sbMap.put(BehaviourClass.M_SEEK, new BehaviourSeek(unit, targetPosition));
    }

    public void addFlee(Vector3 targetPosition) {
        sbMap.remove(BehaviourClass.M_FLEE);
        sbMap.put(BehaviourClass.M_FLEE, new BehaviourFlee(unit, targetPosition));
    }

    public void addArrive(Vector3 targetPosition) {
        sbMap.remove(BehaviourClass.M_ARRIVE);
        sbMap.put(BehaviourClass.M_ARRIVE, new BehaviourArrive(unit, targetPosition));
    }

    public void addWander() {
        sbMap.remove(BehaviourClass.M_WANDER);
        sbMap.put(BehaviourClass.M_WANDER, new BehaviourWander(unit));
    }

    public void addWanderPath(IRTSMap map, int wanderRadius) {
        sbMap.remove(BehaviourClass.M_WANDERPATH);
        sbMap.put(BehaviourClass.M_WANDERPATH, new BehaviourWanderPath(unit, wanderRadius));
    }

    public void addSeparation(IRTSMap map) {
        sbMap.remove(BehaviourClass.M_SEPARATION);
        sbMap.put(BehaviourClass.M_SEPARATION, new BehaviourSeparation(unit));
    }

    public void addPursuit(IEntity evader) {
        sbMap.remove(BehaviourClass.M_PURSUIT);
        sbMap.put(BehaviourClass.M_PURSUIT, new BehaviourPursuit(unit, evader));
    }

    public void addFollowPath(Path path) {
        if (!path.finished()) {
            sbMap.remove(BehaviourClass.M_FOLLOWPATH);
            sbMap.put(BehaviourClass.M_FOLLOWPATH, new BehaviourFollowPath(unit, path, null));
        }
    }

    public void addFollowPathCohesion(Path path, UnitGroup group) {
        sbMap.remove(BehaviourClass.M_FOLLOWPATH);
        sbMap.put(BehaviourClass.M_FOLLOWPATH, new BehaviourFollowPath(unit, path, group));
    }

    public void addAvoidWalls(IRTSMap map) {
        sbMap.remove(BehaviourClass.M_AVOIDWALLS);
        sbMap.put(BehaviourClass.M_AVOIDWALLS, new BehaviourAvoidWalls(unit));
    }

    public void addFlocking(Path path, UnitGroup group, IRTSMap map) {
        sbMap.remove(BehaviourClass.M_FLOCKING);
        sbMap.put(BehaviourClass.M_FLOCKING, new BehaviourFlockingPath(unit, path, group));
    }

    public void addCohesion(UnitGroup group) {
        sbMap.remove(BehaviourClass.M_COHESION);
        sbMap.put(BehaviourClass.M_COHESION, new BehaviourCohesion(unit, group));
    }

}
