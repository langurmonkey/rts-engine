package com.ts.rts.scene.unit.steeringbehaviour;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.ts.rts.datastructure.IMapCell;
import com.ts.rts.datastructure.geom.Vector2;
import com.ts.rts.scene.map.IRTSMap;
import com.ts.rts.scene.unit.IBoundsObject;
import com.ts.rts.scene.unit.MovingEntity;
import com.ts.rts.util.VectorPool;

import java.util.Set;

/**
 * Steers the agent away from walls when a collision is about to occur.
 *
 * @author Toni Sagrista
 */
public class BehaviourAvoidWalls extends AbstractSteeringBehaviour {

    private final IRTSMap map;
    float effectRadius = 50f;

    private Vector2 force;

    public BehaviourAvoidWalls(MovingEntity unit, IRTSMap map) {
        super(unit);
        this.map = map;
    }

    @Override
    public Vector2 calculate() {
        Set<IMapCell<IBoundsObject>> blocked = map.getNearbyBlockedNodes(unit.pos);
        force = VectorPool.getObject();
        for (IMapCell<IBoundsObject> node : blocked) {
            Vector2 entityUnit = unit.pos.clone().subtract(node.x(), node.y());
            float length = entityUnit.len();
            if (length < (unit.softRadius.radius + 8 + node.bounds().width / 2)) {
                // Calculate force
                entityUnit.normalise().multiply((1 / length) * 200);
                force.add(entityUnit);
            }
            VectorPool.returnObject(entityUnit);
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
