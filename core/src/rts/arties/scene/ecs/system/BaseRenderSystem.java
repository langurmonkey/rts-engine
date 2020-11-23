package rts.arties.scene.ecs.system;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import rts.arties.scene.ecs.Mapper;
import rts.arties.scene.ecs.component.*;
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
        RenderableWalkerComponent rwc = Mapper.rwalker.get(entity);
        RenderableShadowComponent rsc = Mapper.rshadow.get(entity);

        TextureRegion spriteToDraw = rbc.sprite;

        if(rwc != null){
            StateComponent sc = Mapper.state.get(entity);
            spriteToDraw = getImageToDraw(mc, rbc, rwc, sc);
        }

        float angle = 0f;
        if (mc != null && rbc.rotateImage) {
            angle = mc.heading.angle2();
        }

        // Shadow
        if(rsc != null) {
            // Shadow
            spriteToDraw.flip(false, rsc.shadowFlipY);
            batch.setColor(0f, 0f, 0f, 0.2f);
            batch.draw(spriteToDraw, pc.pos.x - spriteToDraw.getRegionWidth() / 2 + rbc.spriteOffsetX, pc.pos.y - spriteToDraw.getRegionHeight() / 2 + rbc.spriteOffsetY - spriteToDraw.getRegionHeight() + rbc.spriteOffsetY + rsc.shadowOffsetY, spriteToDraw.getRegionWidth() / 2 + rbc.spriteOffsetX, spriteToDraw.getRegionHeight() / 2, spriteToDraw.getRegionWidth(), spriteToDraw.getRegionHeight(), rbc.scale, rbc.scale, angle);
            spriteToDraw.flip(false, rsc.shadowFlipY);
            batch.setColor(1, 1, 1, 1);
        }

        // Sprite
        batch.draw(spriteToDraw, pc.pos.x - spriteToDraw.getRegionWidth() / 2 + rbc.spriteOffsetX, pc.pos.y - spriteToDraw.getRegionHeight() / 2 + rbc.spriteOffsetY, spriteToDraw.getRegionWidth() / 2 + rbc.spriteOffsetX, spriteToDraw.getRegionHeight() / 2, spriteToDraw.getRegionWidth(), spriteToDraw.getRegionHeight(), rbc.scale, rbc.scale, angle);

    }

    protected TextureRegion getImageToDraw(MovementComponent mc, RenderableBaseComponent rbc, RenderableWalkerComponent rwc, StateComponent sc) {
        if (!mc.moving) {
            return rbc.sprite;
        } else {
            TextureRegion spriteToDraw;
            if (mc.heading.angle2() > 180) {
                // left
                spriteToDraw = rwc.walkL.getKeyFrame(sc.stateTime, true);
            } else {
                // right
                spriteToDraw = rwc.walkR.getKeyFrame(sc.stateTime, true);
            }
            return spriteToDraw;
        }
    }
}
