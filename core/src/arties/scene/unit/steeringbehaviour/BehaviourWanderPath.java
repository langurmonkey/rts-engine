package arties.scene.unit.steeringbehaviour;

import arties.datastructure.IMapCell;
import arties.datastructure.geom.Vector3;
import arties.scene.map.IRTSMap;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

import java.util.List;
import java.util.Random;

/**
 * Wanders around to random positions inside a wander radius.
 *
 * @author Toni Sagrista
 */
public class BehaviourWanderPath extends AbstractSteeringBehaviour {

    private final IRTSMap map;
    private final Random rand;

    private final Vector3 referencePosition;
    private final int wanderRadius;
    private BehaviourFollowPath pathFollowing;
    private float timeToNextPath;

    private long timer;

    public BehaviourWanderPath(IEntity unit, int wanderRadius) {
        super(unit);
        this.map = unit.map();
        this.wanderRadius = wanderRadius;
        this.referencePosition = unit.pos().clone();
        this.rand = new Random(System.currentTimeMillis());
        this.timer = 0l;
        this.timeToNextPath = -1;
    }

    @Override
    public Vector3 calculate() {
        long offset = System.currentTimeMillis() - timer;
        // Update path every 12 seconds
        if (offset > timeToNextPath) {
            // Update path
            float[] xy;
            xy = newXY();
            List<IMapCell<IEntity>> nodeList;
            while ((nodeList = map.findPath(unit.pos().x, unit.pos().y, xy[0], xy[1])) == null) {
                xy = newXY();
            }

            Path path = new Path(nodeList, unit.pos(), xy[0], xy[1]);
            path.smooth(unit);
            // We have a path here!
            pathFollowing = new BehaviourFollowPath(unit, path, null);

            // Update timer
            timer = System.currentTimeMillis();

            // Time to next path is the time the unit would take to get there in a straight line at max speed
            float dist = unit.pos().dst(xy[0], xy[1], 0);
            timeToNextPath = (dist / unit.maxSpeed()) * 1000;
        }

        return pathFollowing.calculate();
    }

    /**
     * Gets a new random x and y inside the wander radius
     *
     * @return
     */
    private float[] newXY() {
        float x = nextCoord();
        float y = nextCoord();

        while (Math.sqrt(x * x + y * y) > wanderRadius) {
            x = nextCoord();
            y = nextCoord();
        }

        x += referencePosition.x;
        y += referencePosition.y;

        // Boundaries
        if (x <= 0)
            x = 5;
        if (y <= 0)
            y = 5;

        if (x >= map.getWidth())
            x = map.getWidth() - 5;
        if (y >= map.getHeight())
            y = map.getHeight() - 5;

        return new float[] { x, y };
    }

    private float nextCoord() {
        return rand.nextInt(wanderRadius * 2) - wanderRadius;
    }

    @Override
    public void renderBehaviour() {
        if (pathFollowing != null)
            pathFollowing.renderBehaviour();

        // Draw wander radius
        shapeRenderer.begin(ShapeType.Line);
        shapeRenderer.setColor(new Color(1f, 1f, 0f, 1f));
        shapeRenderer.circle(referencePosition.x, referencePosition.y, wanderRadius);
        shapeRenderer.end();

    }

}
