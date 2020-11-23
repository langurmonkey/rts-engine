package rts.arties.scene.ecs.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import rts.arties.scene.unit.group.UnitGroup;
import rts.arties.scene.unit.steeringbehaviour.IEntity;

import java.util.Comparator;

/**
 * Component that represents a group of units or entities
 */
public class UnitGroupComponent implements Component {
    private static final UnitComparatorByPosition comp = new UnitComparatorByPosition();

    private static class UnitComparatorByPosition implements Comparator<Entity> {

        @Override
        public int compare(Entity o1, Entity o2) {
            PositionComponent pc1 = o1.getComponent(PositionComponent.class);
            PositionComponent pc2 = o2.getComponent(PositionComponent.class);
            if (pc1 != null && pc2 != null) {
                int comp = Float.compare(pc1.pos.y, pc2.pos.y);
                if (comp == 0) {
                    comp = Float.compare(pc1.pos.x, pc2.pos.x);
                }
                return comp;
            } else {
                return 0;
            }
        }
    }

    public UnitGroup group;
}
