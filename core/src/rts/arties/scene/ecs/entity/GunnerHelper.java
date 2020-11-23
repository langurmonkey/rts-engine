package rts.arties.scene.ecs.entity;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import rts.arties.scene.ecs.component.*;
import rts.arties.scene.map.IRTSMap;
import rts.arties.scene.unit.DefaultEntity;
import rts.arties.scene.unit.state.StateManager;
import rts.arties.scene.unit.steeringbehaviour.SteeringBehaviours;

public class GunnerHelper {

    public static Entity create(Engine engine, IRTSMap map, float x, float y, float hp) {

        Entity gunner = new Entity();
        DefaultEntity gunnerEntity = new DefaultEntity(gunner);

        // Body
        BodyComponent bc = engine.createComponent(BodyComponent.class);
        bc.me = gunnerEntity;
        bc.mass = 0.1f;
        bc.weight = 0.3f;
        // Health
        HealthComponent hc = engine.createComponent(HealthComponent.class);
        hc.maxHp = 20;
        hc.setHp(hp);
        // Position
        PositionComponent pc = engine.createComponent(PositionComponent.class);
        pc.pos.set(x, y);
        pc.viewingDistance = 220;
        // Movement
        MovementComponent mc = engine.createComponent(MovementComponent.class);
        mc.heading.set(1, 0, 0);
        mc.softRadius = 13;
        mc.slowingDistance = 10;
        mc.maxSpeed = 40f; // m/s
        mc.maxForce = 40f; // Kg*m/s^2
        mc.maxTurnRate = (float) (10 * Math.PI); // rad/s
        mc.updateMaxSpeed(hc);
        // Steering
        SteeringComponent sc = engine.createComponent(SteeringComponent.class);
        sc.steeringBehaviours = new SteeringBehaviours(gunnerEntity);
        sc.steeringBehaviours.addSeparation(map);
        sc.steeringBehaviours.addAvoidWalls(map);
        // State
        StateComponent stc = engine.createComponent(StateComponent.class);
        stc.stateManager = new StateManager(gunnerEntity);
        // Group
        UnitGroupComponent ugc = engine.createComponent(UnitGroupComponent.class);
        // Renderable
        RenderableBaseComponent rbc = engine.createComponent(RenderableBaseComponent.class);
        rbc.textureName = "goon-blue-stand-left";
        rbc.spriteOffsetY = 11f;
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
        gunner.add(plc);
        gunner.add(pc);
        gunner.add(mc);
        gunner.add(bc);
        gunner.add(rbc);
        gunner.add(rsc);
        gunner.add(sc);
        gunner.add(stc);
        gunner.add(mpc);
        gunner.add(ugc);
        gunner.add(hc);
        gunner.add(rwc);

        return gunner;
    }
}
