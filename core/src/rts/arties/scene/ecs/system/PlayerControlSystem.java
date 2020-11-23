package rts.arties.scene.ecs.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

public class PlayerControlSystem extends IteratingSystem {

    public PlayerControlSystem(Family family) {
        super(family);
    }

    public PlayerControlSystem(Family family, int priority){
        super(family, priority);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {

    }
}
