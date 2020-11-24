package rts.arties.scene.ecs.entity;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.MathUtils;
import rts.arties.scene.ecs.component.*;
import rts.arties.scene.map.IRTSMap;
import rts.arties.scene.unit.DefaultEntity;
import rts.arties.scene.unit.state.StateManager;
import rts.arties.scene.unit.steeringbehaviour.SteeringBehaviours;

public class WalkerHelper {

    public static Entity create(Engine engine, IRTSMap map, float x, float y, float hp) {

        Entity e = new Entity();
        DefaultEntity de = new DefaultEntity(e);

        // Body
        BodyComponent bc = engine.createComponent(BodyComponent.class);
        bc.me = de;
        bc.mass = 0.1f;
        bc.weight = 0.3f;
        bc.softRadius = 13;
        // Health
        HealthComponent hc = engine.createComponent(HealthComponent.class);
        hc.maxHp = 20;
        hc.setHp(MathUtils.clamp(hp, 0, hc.maxHp));
        // Position
        PositionComponent pc = engine.createComponent(PositionComponent.class);
        pc.pos.set(x, y);
        pc.viewingDistance = 220;
        // Movement
        MovementComponent mc = engine.createComponent(MovementComponent.class);
        mc.heading.set(1, 0, 0);
        mc.slowingDistance = 10;
        mc.maxSpeed = 40f; // m/s
        mc.maxForce = 40f; // Kg*m/s^2
        mc.maxTurnRate = (float) (10 * Math.PI); // rad/s
        mc.updateMaxSpeed(hc);
        // Steering
        SteeringComponent sc = engine.createComponent(SteeringComponent.class);
        sc.steeringBehaviours = new SteeringBehaviours(de);
        sc.steeringBehaviours.addSeparation(map);
        sc.steeringBehaviours.addAvoidWalls(map);
        // State
        StateComponent stc = engine.createComponent(StateComponent.class);
        stc.stateManager = new StateManager(de);
        // Group
        UnitGroupComponent ugc = engine.createComponent(UnitGroupComponent.class);
        // Renderable
        RenderableBaseComponent rbc = engine.createComponent(RenderableBaseComponent.class);
        rbc.textureName = "goon-blue-stand-left";
        rbc.spriteOffsetY = 11f;
        rbc.rotateImage = false;
        // Walker
        RenderableWalkerComponent rwc = engine.createComponent(RenderableWalkerComponent.class);
        rwc.walkLeftTextures = new String[4];
        rwc.walkLeftTextures[0] = "goon-blue-walk1-left";
        rwc.walkLeftTextures[1] = "goon-blue-walk2-left";
        rwc.walkLeftTextures[2] = "goon-blue-walk1-left";
        rwc.walkLeftTextures[3] = "goon-blue-walk3-left";
        // Shadow
        RenderableShadowComponent rsc = engine.createComponent(RenderableShadowComponent.class);
        rsc.shadowOffsetY = 0f;
        // Map
        MapComponent mpc = engine.createComponent(MapComponent.class);
        mpc.map = map;
        // Player
        PlayerComponent plc = engine.createComponent(PlayerComponent.class);
        plc.selectionRadius = 18;

        // Add components
        e.add(plc);
        e.add(pc);
        e.add(mc);
        e.add(bc);
        e.add(rbc);
        e.add(rsc);
        e.add(sc);
        e.add(stc);
        e.add(mpc);
        e.add(ugc);
        e.add(hc);
        e.add(rwc);

        return e;
    }
}
