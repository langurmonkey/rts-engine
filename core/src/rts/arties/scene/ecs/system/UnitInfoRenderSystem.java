package rts.arties.scene.ecs.system;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import rts.arties.scene.ecs.Mapper;
import rts.arties.scene.ecs.component.HealthComponent;
import rts.arties.scene.ecs.component.PlayerComponent;
import rts.arties.scene.ecs.component.PositionComponent;
import rts.arties.scene.ecs.component.RenderableBaseComponent;
import rts.arties.util.color.ColorUtils;

public class UnitInfoRenderSystem extends EntitySystem {
    private Family family;
    private ImmutableArray<Entity> entities;
    private ShapeRenderer sr;
    private SpriteBatch sb;

    private float[] color0, color1, color2, color3;
    // Selection box colors
    private Color sbColor0, sbColor1, sbColor2;

    public UnitInfoRenderSystem(Family family, ShapeRenderer sr, SpriteBatch sb) {
        this(family, 0, sr, sb);
    }

    public UnitInfoRenderSystem(Family family, int priority, ShapeRenderer sr, SpriteBatch sb) {
        super(priority);

        this.family = family;
        this.sr = sr;
        this.sb = sb;

        // Health bar colors
        color0 = ColorUtils.gGreen;
        color1 = ColorUtils.gYellow;
        color2 = ColorUtils.aOrange;
        color3 = ColorUtils.gRed;

        // Additional colors
        sbColor0 = new Color(0f, 0f, 0f, .9f);
        sbColor1 = new Color(1f, 1f, 1f, .8f);
        sbColor2 = new Color(1f, 1f, 1f, .5f);
    }

    @Override
    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(family);
    }

    @Override
    public void removedFromEngine(Engine engine) {
        entities = null;
    }

    @Override
    public void update(float deltaTime) {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        // Render shape renderer layers
        // Layer 0 - filled
        sr.begin(ShapeType.Filled);
        entities.forEach(e -> renderShapeFilledLayer0(e));
        sr.end();
        // Layer 1 - line
        sr.begin(ShapeType.Line);
        entities.forEach(e -> renderShapeLineLayer1(e));
        sr.end();
        // Layer 2 - filled
        sr.begin(ShapeType.Filled);
        entities.forEach(e -> renderShapeFilledLayer2(e));
        sr.end();
        // Layer 3 - line
        sr.begin(ShapeType.Line);
        entities.forEach(e -> renderShapeLineLayer3(e));
        sr.end();
    }

    /**
     * @return set of entities processed by the system
     */
    public ImmutableArray<Entity> getEntities() {
        return entities;
    }

    /**
     * @return the Family used when the system was created
     */
    public Family getFamily() {
        return family;
    }

    private float getHealthLength(float max, HealthComponent hc) {
        return hc.hp * max / hc.maxHp;
    }

    protected float[] getHealthColor(HealthComponent hc) {
        float norm = hc.hp / hc.maxHp;
        float[] color;
        if (norm >= (3f / 4f)) {
            color = color0;
        } else if (norm >= (2f / 4f)) {
            color = color1;
        } else if (norm >= (1f / 4f)) {
            color = color2;
        } else {
            color = color3;
        }
        return color;
    }

    private void renderShapeFilledLayer0(Entity e) {
    }

    private void renderShapeLineLayer1(Entity e) {
        PlayerComponent plc = Mapper.player.get(e);
        PositionComponent pc = Mapper.position.get(e);
        RenderableBaseComponent rbs = Mapper.rbase.get(e);
        if (plc.selected) {
            // Selection box
            sr.setColor(sbColor0);
            sr.circle(pc.pos.x + rbs.spriteOffsetX, pc.pos.y + rbs.spriteOffsetY, plc.selectionRadius + 1);
            sr.setColor(sbColor1);
            sr.circle(pc.pos.x + rbs.spriteOffsetX, pc.pos.y + rbs.spriteOffsetY, plc.selectionRadius);
            sr.setColor(sbColor2);
            sr.circle(pc.pos.x + rbs.spriteOffsetX, pc.pos.y + rbs.spriteOffsetY, plc.selectionRadius - 1);
        }
    }

    private void renderShapeFilledLayer2(Entity e) {
        PlayerComponent plc = Mapper.player.get(e);
        PositionComponent pc = Mapper.position.get(e);
        RenderableBaseComponent rbs = Mapper.rbase.get(e);
        // We may not have a health component
        HealthComponent hc = Mapper.health.get(e);
        if (plc.selected && hc != null) {
            // Health bar
            plc.healthBarStartX = Math.round(pc.pos.x - plc.selectionRadius + rbs.spriteOffsetX);
            plc.healthBarStartY = Math.round(pc.pos.y - plc.selectionRadius + rbs.spriteOffsetY);
            plc.healthBarLength = getHealthLength(plc.selectionRadius * 2f, hc);

            sr.setColor(ColorUtils.blackC);
            sr.rect(plc.healthBarStartX - 2f, plc.healthBarStartY - 1f, 3f, plc.selectionRadius * 2f + 1f);
        }
    }

    private void renderShapeLineLayer3(Entity e) {
        PlayerComponent plc = Mapper.player.get(e);
        RenderableBaseComponent rbs = Mapper.rbase.get(e);
        // We may not have a health component
        HealthComponent hc = Mapper.health.get(e);
        if (plc.selected && hc != null) {
            // Health outline
            float[] color = getHealthColor(hc);
            sr.setColor(new Color(color[0], color[1], color[2], 1f));
            sr.line(plc.healthBarStartX, plc.healthBarStartY, plc.healthBarStartX, plc.healthBarStartY + plc.healthBarLength - 1f);
        }
    }
}
