package rts.arties.scene.ecs.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.SortedIteratingSystem;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import rts.arties.scene.ecs.Mapper;
import rts.arties.scene.ecs.component.*;

import java.util.Comparator;

public class BaseRenderSystem extends SortedIteratingSystem {
    private final SpriteBatch batch;
    private final ShaderProgram program;

    public BaseRenderSystem(Family family, Comparator<Entity> comparator, int priority, SpriteBatch batch, ShaderProgram program) {
        super(family, comparator, priority);
        this.batch = batch;
        this.program = program;
    }

    public void update (float deltaTime) {
        batch.begin();
        super.update(deltaTime);
        batch.end();
        forceSort();
    }

    protected void processEntity(Entity entity, float deltaTime) {
        PositionComponent pc = Mapper.position.get(entity);
        RenderableBaseComponent rbc = Mapper.rbase.get(entity);
        MovementComponent mc = Mapper.movement.get(entity);
        RenderableWalkerComponent rwc = Mapper.rwalker.get(entity);
        RenderableShadowComponent rsc = Mapper.rshadow.get(entity);
        VisibilityComponent vc = Mapper.visibility.get(entity);

        if (vc == null || vc.visible) {
            TextureRegion spriteToDraw = rbc.sprite;

            if (rwc != null) {
                StateComponent sc = Mapper.state.get(entity);
                spriteToDraw = getImageToDraw(mc, rbc, rwc, sc);
            }

            float angle = 0f;
            if (mc != null && rbc.rotateImage) {
                angle = mc.heading.angle2();
            }

            // Shadow
            if (rsc != null) {
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
