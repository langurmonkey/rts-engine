package rts.arties.scene.ecs;

import com.badlogic.ashley.core.Entity;
import rts.arties.scene.ecs.component.PositionComponent;

import java.util.Comparator;

/**
 * Compares entities by Y position. If the Y is equal, compares them by X.
 */
public class EntityPositionComparator implements Comparator<Entity> {
    @Override
    public int compare(Entity e1, Entity e2) {
        PositionComponent p1 = Mapper.position.get(e1);
        PositionComponent p2 = Mapper.position.get(e2);
        int comp = Float.compare(p1.pos.y, p2.pos.y);
        if (comp == 0) {
            comp = Float.compare(p1.pos.x, p2.pos.x);
        }
        return -comp;
    }
}
