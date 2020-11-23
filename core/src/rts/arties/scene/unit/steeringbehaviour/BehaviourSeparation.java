package rts.arties.scene.unit.steeringbehaviour;

import rts.arties.datastructure.geom.Vector3;
import rts.arties.util.Vector3Pool;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

import java.util.Set;

/**
 * This behavior keeps the vehicles a certain distance away from others. This keeps the vehicles from colliding and
 * crowding to close together.
 *
 * @author Toni Sagrista
 */
public class BehaviourSeparation extends AbstractSteeringBehaviour {

    private Vector3 force = null;

    /**
     * Creates a separation behaviour based on the units in the map
     *
     * @param unit
     */
    public BehaviourSeparation(IEntity unit) {
        super(unit);
    }

    @Override
    public Vector3 calculate() {
        Set<IEntity> entities = unit.map().getNearbyEntities(unit.pos());
        force = Vector3Pool.getObject();
        for (IEntity entity : entities) {
            if (!unit.equals(entity)) {
                Vector3 entityUnit = unit.pos().clone().sub(entity.pos());
                float length = entityUnit.len();
                if (length < (unit.softRadius() + entity.softRadius())) {
                    // Calculate force
                    entityUnit.nor().scl((1 / length) * 500);
                    force.add(entityUnit);
                }
                Vector3Pool.returnObject(entityUnit);
            }
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
