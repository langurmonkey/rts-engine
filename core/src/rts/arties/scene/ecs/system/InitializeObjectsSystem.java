package rts.arties.scene.ecs.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import rts.arties.scene.ecs.Mapper;
import rts.arties.scene.ecs.component.BodyComponent;
import rts.arties.scene.ecs.component.MapComponent;

public class InitializeObjectsSystem extends IteratingSystem {
    public InitializeObjectsSystem(Family family, int priority) {
        super(family, priority);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        MapComponent mpc = Mapper.map.get(entity);
        BodyComponent bc = Mapper.body.get(entity);
        mpc.map.updateEntity(bc.me);

    }
}
