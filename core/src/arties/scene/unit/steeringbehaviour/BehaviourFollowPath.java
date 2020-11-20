package arties.scene.unit.steeringbehaviour;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import arties.datastructure.geom.Vector3;
import arties.scene.unit.MovingEntity;
import arties.scene.unit.group.UnitGroup;
import arties.util.Vector3Pool;

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

    public BehaviourFollowPath(MovingEntity unit, Path path, UnitGroup group) {
        super(unit);
        this.path = path;
        if (group != null) {
            cohesion = new BehaviourCohesion(unit, group);
        }
        seek = new BehaviourSeek(unit, Vector3Pool.getObject());
    }

    @Override
    public Vector3 calculate() {
        if (unit.pos.dst2(path.currentWaypoint()) < waypointSeekDistSq) {
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

    @Override
    public void renderBehaviour() {
        for (int j = 1; j < path.size(); j++) {
            Vector3 waypoint = path.get(j);
            shapeRenderer.begin(ShapeType.Filled);
            shapeRenderer.setColor(new Color(0f, .6f, 0f, .7f));
            shapeRenderer.circle(waypoint.x, waypoint.y, 5f);
            shapeRenderer.end();
        }
        if (arrive != null)
            arrive.renderBehaviour();
        else if (seek != null)
            seek.renderBehaviour();
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
