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
import rts.arties.scene.ecs.component.*;
import rts.arties.scene.unit.steeringbehaviour.SteeringBehaviours;
import rts.arties.util.color.ColorUtils;

public class DebugRenderSystem extends EntitySystem {
    private Family family;
    private ImmutableArray<Entity> entities;
    private ShapeRenderer sr;
    private SpriteBatch sb;

    private Color hardRadiusColor, softRadiusColor, posColor, imageBoundsColor, viewDistColor, headingColor;

    public DebugRenderSystem(Family family, ShapeRenderer sr, SpriteBatch sb) {
        this(family, 0, sr, sb);
    }

    public DebugRenderSystem(Family family, int priority, ShapeRenderer sr, SpriteBatch sb) {
        super(priority);

        this.family = family;
        this.sr = sr;
        this.sb = sb;

        // Additional colors
        hardRadiusColor = new Color(0f, 0f, .4f, .4f);
        softRadiusColor = new Color(0f, .4f, 0f, .2f);
        posColor = ColorUtils.gGreenC;
        imageBoundsColor = new Color(0f, 0f, 0f, 1f);
        viewDistColor = new Color(0f, 0.5f, 0.2f, 1f);
        headingColor = ColorUtils.gRedC;
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
        // filled
        sr.begin(ShapeType.Filled);
        entities.forEach(e -> renderDebugFilled(e));
        sr.end();
        // Layer 1 - line
        sr.begin(ShapeType.Line);
        entities.forEach(e -> renderDebugLine(e));
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

    private void renderDebugFilled(Entity e) {
        PositionComponent pc = Mapper.position.get(e);
        MovementComponent mc = Mapper.movement.get(e);
        BodyComponent bc = Mapper.body.get(e);
        SteeringComponent sc = Mapper.steering.get(e);

        // Hard radius
        sr.setColor(hardRadiusColor);
        sr.rect(bc.hardRadius.x, bc.hardRadius.y, bc.hardRadius.width, bc.hardRadius.height);

        // Soft radius
        sr.setColor(softRadiusColor);
        sr.circle(pc.pos.x, pc.pos.y, mc.softRadius);

        // Position
        sr.setColor(posColor);
        sr.circle(pc.pos.x, pc.pos.y, 1);

        // Behaviours
        if(sc != null){
            sc.steeringBehaviours.renderFilled(sr);
        }
    }

    private void renderDebugLine(Entity e) {
        PositionComponent pc = Mapper.position.get(e);
        MovementComponent mc = Mapper.movement.get(e);
        RenderableBaseComponent rbs = Mapper.rbase.get(e);
        SteeringComponent sc = Mapper.steering.get(e);

        // Image bounds
        sr.setColor(imageBoundsColor);
        sr.rect(rbs.imageBounds.x, rbs.imageBounds.y, rbs.imageBounds.width, rbs.imageBounds.height);

        // View distance
        sr.setColor(viewDistColor);
        sr.circle(pc.pos.x, pc.pos.y, pc.viewingDistance);

        // Heading
        if (mc.heading != null) {
            sr.setColor(headingColor);
            sr.line(pc.pos.x, pc.pos.y, mc.heading.x * 40 + pc.pos.x, mc.heading.y * 40 + pc.pos.y);
        }

        // Behaviours
        if(sc != null){
            sc.steeringBehaviours.renderLine(sr);
        }
    }

}
