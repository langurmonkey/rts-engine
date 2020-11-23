package rts.arties.scene.ecs.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

/**
 * Contains a reference to the entity which represents a unit group
 */
public class UnitGroupMemberComponent implements Component {
    public Entity unitGroup;
}
