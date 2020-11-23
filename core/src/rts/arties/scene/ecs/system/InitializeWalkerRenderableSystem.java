package rts.arties.scene.ecs.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import rts.arties.scene.ecs.Mapper;
import rts.arties.scene.ecs.component.MovementComponent;
import rts.arties.scene.ecs.component.RenderableBaseComponent;
import rts.arties.scene.ecs.component.RenderableWalkerComponent;

public class InitializeWalkerRenderableSystem extends IteratingSystem {

    private AssetManager assets;
    public InitializeWalkerRenderableSystem(Family family, int priority, AssetManager assets) {
        super(family, priority);
        this.assets = assets;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        RenderableBaseComponent rbc = Mapper.rbase.get(entity);
        RenderableWalkerComponent rwc = Mapper.rwalker.get(entity);
        MovementComponent mc = Mapper.movement.get(entity);

        TextureAtlas ta = assets.get("data/tex/base-textures.atlas");

        String[] wlt = rwc.walkLeftTextures;
        TextureRegion[] walkLeftFrames = new TextureRegion[wlt.length];
        for(int i = 0; i < wlt.length; i++){
            walkLeftFrames[i] = ta.findRegion(wlt[i]);
        }

        TextureRegion[] walkRightFrames = new TextureRegion[walkLeftFrames.length];
        for(int i = 0; i < walkLeftFrames.length; i++){
            walkRightFrames[i] = new TextureRegion(walkLeftFrames[i]);
            walkRightFrames[i].flip(true, false);
        }

        rwc.walkL = new Animation<>(.1f, walkLeftFrames);
        rwc.walkR = new Animation<>(.1f, walkRightFrames);

        rwc.lastAngle = mc.heading.angle2();
        if (rwc.lastAngle <= 180) {
            rbc.sprite.flip(true, false);
        }
    }
}
