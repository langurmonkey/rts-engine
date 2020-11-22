package arties.scene.unit.steeringbehaviour;

import arties.datastructure.IMapCell;
import arties.datastructure.geom.Vector3;
import arties.scene.map.IRTSMap;
import arties.util.Vector3Pool;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

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

    @Override
    public void renderBehaviour() {
        if (force != null) {
            shapeRenderer.begin(ShapeType.Line);
            shapeRenderer.setColor(new Color(1f, .5f, 0f, 1f));
            shapeRenderer.line(unit.pos().x, unit.pos().y, unit.pos().x + force.x, unit.pos().y + force.y);
            shapeRenderer.end();

        }
    }

}
