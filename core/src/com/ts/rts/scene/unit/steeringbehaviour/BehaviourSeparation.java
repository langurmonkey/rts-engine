package com.ts.rts.scene.unit.steeringbehaviour;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.ts.rts.datastructure.geom.Vector2;
import com.ts.rts.datastructure.geom.Vector3;
import com.ts.rts.scene.map.IRTSMap;
import com.ts.rts.scene.unit.IBoundsObject;
import com.ts.rts.scene.unit.MovingEntity;
import com.ts.rts.util.Vector2Pool;
import com.ts.rts.util.Vector3Pool;

import java.util.Set;

/**
 * This behavior keeps the vehicles a certain distance away from others. This keeps the vehicles from colliding and
 * crowding to close together.
 *
 * @author Toni Sagrista
 */
public class BehaviourSeparation extends AbstractSteeringBehaviour {

    protected IRTSMap map;
    private Vector3 force = null;

    /**
     * Creates a separation behaviour based on the units in the map
     *
     * @param unit
     * @param map
     */
    public BehaviourSeparation(MovingEntity unit, IRTSMap map) {
        super(unit);
        this.map = map;
    }

    @Override
    public Vector3 calculate() {
        Set<IBoundsObject> entities = map.getNearbyEntities(unit.pos);
        force = Vector3Pool.getObject();
        for (IBoundsObject entity : entities) {
            if (!unit.equals(entity)) {
                Vector3 entityUnit = unit.pos.clone().sub(entity.pos());
                float length = entityUnit.len();
                if (entity.softRadius() != null && length < (unit.softRadius.radius + entity.softRadius().radius)) {
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
            shapeRenderer.line(unit.pos.x, unit.pos.y, unit.pos.x + force.x, unit.pos.y + force.y);
            shapeRenderer.end();

        }
    }

}
