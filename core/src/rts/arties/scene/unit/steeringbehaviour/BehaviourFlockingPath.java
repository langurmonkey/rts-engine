package rts.arties.scene.unit.steeringbehaviour;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import rts.arties.datastructure.geom.Vector3;
import rts.arties.scene.map.IRTSMap;
import rts.arties.util.Vector3Pool;

/**
 * First experimental implementation of flocking for a group of units. Uses alignment, separation and cohesion
 * behaviours.
 *
 * @author Toni Sagrista
 */
public class BehaviourFlockingPath extends AbstractSteeringBehaviour {

    protected float waypointSeekDistSq = 20 * 20;
    protected Path path;
    protected boolean finished;
    protected IGroup group;
    protected IRTSMap map;

    BehaviourAlignment alignment;
    BehaviourCohesion cohesion;
    BehaviourArriveGroup arrive;

    public BehaviourFlockingPath(IEntity unit, Path path, IGroup group) {
        super(unit);
        this.path = path;
        this.group = group;
        this.map = unit.map();
        alignment = new BehaviourAlignment(unit, group, path.currentWaypoint());
        cohesion = new BehaviourCohesion(unit, group);
    }

    @Override
    public Vector3 calculate() {
        if (group.pos().dst2(path.currentWaypoint()) < waypointSeekDistSq) {
            path.nextWaypoint();
        }
        if (!path.finished()) {
            alignment.updateTarget(path.currentWaypoint());
            Vector3 force = Vector3Pool.getObject();
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

    private final Color col = new Color(0f, .6f, 0f, .7f);

    @Override
    public void renderFilled(ShapeRenderer sr) {
        for (int j = 1; j < path.size(); j++) {
            Vector3 waypoint = path.get(j);
            sr.setColor(col);
            sr.circle(waypoint.x, waypoint.y, 5f);
        }
        if (arrive != null)
            arrive.renderFilled(sr);
        cohesion.renderFilled(sr);
        alignment.renderFilled(sr);
    }

    @Override
    public void renderLine(ShapeRenderer sr) {
        if (arrive != null)
            arrive.renderLine(sr);
        cohesion.renderLine(sr);
        alignment.renderLine(sr);

    }

    @Override
    public void dispose() {
        path.dispose();
    }

}
