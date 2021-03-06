package rts.arties.scene.ecs.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import rts.arties.scene.ecs.Mapper;
import rts.arties.scene.ecs.component.BodyComponent;
import rts.arties.scene.ecs.component.PositionComponent;
import rts.arties.scene.ecs.component.RenderableBaseComponent;

/**
 * Initializes renderable entities
 */
public class InitializeBaseRenderableSystem extends IteratingSystem {

    private final AssetManager assets;
    public InitializeBaseRenderableSystem(Family family, int priority, AssetManager assets) {
        super(family, priority);
        this.assets = assets;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        RenderableBaseComponent rbc = Mapper.rbase.get(entity);
        BodyComponent bc = Mapper.body.get(entity);
        PositionComponent pc = Mapper.position.get(entity);

        TextureAtlas ta = assets.get("data/tex/base-textures.atlas");
        rbc.sprite = new Sprite(ta.findRegion(rbc.textureName));
        bc.width = rbc.sprite.getRegionWidth() * rbc.scale;
        bc.height = rbc.sprite.getRegionHeight() * rbc.scale;

        // Initialize hard radius and image bounds
        float w2 = bc.width / 2f;
        float h2 = bc.height / 2f;
        bc.hardRadius = new Rectangle(pc.pos.x - w2, pc.pos.y - h2 + rbc.spriteOffsetY, bc.width, bc.height);
        rbc.imageBounds = new Rectangle(pc.pos.x - w2 + rbc.spriteOffsetX, pc.pos.y - h2 + rbc.spriteOffsetY, bc.width, bc.height);

    }

}
