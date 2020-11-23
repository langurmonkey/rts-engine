package rts.arties.scene.unit.steeringbehaviour;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import rts.arties.datastructure.geom.Vector3;
import rts.arties.util.Vector3Pool;

/**
 * This behaviour creates a force that leads the unit to the next waypoint in a path.
 *
 * @author Toni Sagrista
 */
public class BehaviourFollowPath extends AbstractSteeringBehaviour {
    protected float waypointSeekDistSq = 20 * 20;
    protected Path path;

    protected BehaviourCohesion cohesion = null;
    protected BehaviourSeek seek;
    protected BehaviourArrive arrive = null;

    public BehaviourFollowPath(IEntity unit, Path path, IGroup group) {
        super(unit);
        this.path = path;
        if (group != null) {
            cohesion = new BehaviourCohesion(unit, group);
        }
        seek = new BehaviourSeek(unit, Vector3Pool.getObject());
    }

    @Override
    public Vector3 calculate() {
        if (unit.pos().dst2(path.currentWaypoint()) < waypointSeekDistSq) {
            path.nextWaypoint();
        }
        if (!path.finished()) {
            if (cohesion == null) {
                return seek.updateTarget(path.currentWaypoint()).calculate();
            } else {
                return seek.updateTarget(path.currentWaypoint()).calculate().add(cohesion.calculate());
            }
        } else {
            if (arrive == null)
                arrive = new BehaviourArrive(unit, path.currentWaypoint());
            return arrive.calculate();
        }
    }

    @Override
    public boolean isDone() {
        return arrive != null && arrive.isDone();
    }

    private Color col = new Color(0f, .6f, 0f, .7f);

    @Override
    public void renderFilled(ShapeRenderer sr) {
        for (int j = 1; j < path.size(); j++) {
            Vector3 waypoint = path.get(j);
            sr.setColor(col);
            sr.circle(waypoint.x, waypoint.y, 5f);
        }
        if (arrive != null)
            arrive.renderFilled(sr);
        else if (seek != null)
            seek.renderFilled(sr);
    }

    @Override
    public void renderLine(ShapeRenderer sr) {
        if (arrive != null)
            arrive.renderLine(sr);
        else if (seek != null)
            seek.renderLine(sr);
    }

    @Override
    public void dispose() {
        path.dispose();
        seek.dispose();
        if (arrive != null) {
            arrive.dispose();
        }
    }

}
