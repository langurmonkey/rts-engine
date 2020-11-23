package rts.arties.scene.unit.steeringbehaviour;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import rts.arties.datastructure.IMapCell;
import rts.arties.datastructure.geom.Vector3;
import rts.arties.util.Vector3Pool;

import java.util.Set;

/**
 * Steers the agent away from walls when a collision is about to occur.
 *
 * @author Toni Sagrista
 */
public class BehaviourAvoidWalls extends AbstractSteeringBehaviour {

    float effectRadius = 50f;

    private Vector3 force;

    public BehaviourAvoidWalls(IEntity unit) {
        super(unit);
    }

    @Override
    public Vector3 calculate() {
        Set<IMapCell<IEntity>> blocked = unit.map().getNearbyBlockedNodes(unit.pos());
        force = Vector3Pool.getObject();
        for (IMapCell<IEntity> node : blocked) {
            Vector3 entityUnit = unit.pos().clone().sub(node.x(), node.y());
            float length = entityUnit.len();
            if (length < (unit.softRadius() + 8 + node.bounds().width / 2)) {
                // Calculate force
                entityUnit.nor().scl((1 / length) * 200);
                force.add(entityUnit);
            }
            Vector3Pool.returnObject(entityUnit);
        }
        return force;
    }

    private Color col = new Color(1f, .5f, 0f, 1f);
    @Override
    public void renderLine(ShapeRenderer sr) {
        if (force != null) {
            sr.setColor(col);
            sr.line(unit.pos().x, unit.pos().y, unit.pos().x + force.x, unit.pos().y + force.y);

        }
    }

}
