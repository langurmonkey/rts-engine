package rts.arties.scene.ecs.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalIteratingSystem;
import rts.arties.scene.ecs.Mapper;
import rts.arties.scene.ecs.component.MapComponent;
import rts.arties.scene.ecs.component.PositionComponent;
import rts.arties.scene.ecs.component.VisibilityComponent;

public class ObjectUpdateSystem extends IntervalIteratingSystem {

    public ObjectUpdateSystem(Family family, float interval, int priority) {
        super(family, interval, priority);
    }

    @Override
    protected void processEntity(Entity entity) {
        PositionComponent pc = Mapper.position.get(entity);
        VisibilityComponent vc = Mapper.visibility.get(entity);
        MapComponent mpc = Mapper.map.get(entity);
        if(vc != null && mpc != null) {
            vc.visible = mpc.map.isVisible(pc.pos);
        }
        // Ground in map
        //mpc.map.updateEntity(bc.me);
    }
}
