package rts.arties.scene.unit.steeringbehaviour;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import rts.arties.datastructure.IMapCell;
import rts.arties.datastructure.geom.Vector3;
import rts.arties.scene.map.IRTSMap;
import rts.arties.util.color.ColorUtils;

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
            float[] xyz;
            xyz = newXYZ();
            List<IMapCell<IEntity>> nodeList;
            while ((nodeList = map.findPath(unit.pos().x, unit.pos().y, xyz[0], xyz[1])) == null) {
                xyz = newXYZ();
            }

            Path path = new Path(nodeList, unit.pos(), xyz[0], xyz[1], xyz[2]);
            path.smooth(unit);
            // We have a path here!
            pathFollowing = new BehaviourFollowPath(unit, path, null);

            // Update timer
            timer = System.currentTimeMillis();

            // Time to next path is the time the unit would take to get there in a straight line at max speed
            float dist = unit.pos().dst(xyz[0], xyz[1], 0);
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

    private float[] newXYZ() {
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

        return new float[] { x, y, map.getCell(x, y).z() };
    }

    private float nextCoord() {
        return rand.nextInt(wanderRadius * 2) - wanderRadius;
    }

    @Override
    public void renderLine(ShapeRenderer sr) {
        if (pathFollowing != null)
            pathFollowing.renderLine(sr);

        // Draw wander radius
        sr.setColor(ColorUtils.gYellowC);
        sr.circle(referencePosition.x, referencePosition.y, wanderRadius);

    }

}
