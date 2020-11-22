package arties.scene.ecs.system;

import arties.RTSGame;
import arties.scene.ecs.Mapper;
import arties.scene.ecs.component.BodyComponent;
import arties.scene.ecs.component.PositionComponent;
import arties.scene.ecs.component.RenderableBaseComponent;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;

import static arties.scene.ecs.Mapper.position;

/**
 * Initializes renderable entities
 */
public class InitializeBaseRenderableSystem extends IteratingSystem {

    public InitializeBaseRenderableSystem(Family family) {
        super(family);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        RenderableBaseComponent rbc = Mapper.rbase.get(entity);
        BodyComponent bc = Mapper.body.get(entity);
        PositionComponent pc = position.get(entity);

        TextureAtlas ta = RTSGame.assets().get("data/img/textures/textures.pack");
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
