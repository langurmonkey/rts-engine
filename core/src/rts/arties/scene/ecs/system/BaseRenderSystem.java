package rts.arties.scene.ecs.system;

import rts.arties.scene.ecs.Mapper;
import rts.arties.scene.ecs.component.MovementComponent;
import rts.arties.scene.ecs.component.PositionComponent;
import rts.arties.scene.ecs.component.RenderableBaseComponent;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class BaseRenderSystem extends IteratingSystem {
    private SpriteBatch batch;

    public BaseRenderSystem(Family family, int priority, SpriteBatch batch) {
        super(family, priority);
        this.batch = batch;
    }

    public void update (float deltaTime) {
        batch.begin();
        super.update(deltaTime);
        batch.end();
    }
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PositionComponent pc = Mapper.position.get(entity);
        RenderableBaseComponent rbc = Mapper.rbase.get(entity);
        MovementComponent mc = Mapper.movement.get(entity);

        float angle = 0f;
        if (mc != null && rbc.rotateImage) {
            angle = mc.heading.angle2();
        }
        batch.draw(rbc.sprite, pc.pos.x - rbc.sprite.getRegionWidth() / 2 + rbc.spriteOffsetX, pc.pos.y - rbc.sprite.getRegionHeight() / 2 + rbc.spriteOffsetY, rbc.sprite.getRegionWidth() / 2 + rbc.spriteOffsetX, rbc.sprite.getRegionHeight() / 2, rbc.sprite.getRegionWidth(), rbc.sprite.getRegionHeight(), rbc.scale, rbc.scale, angle);

    }
}
