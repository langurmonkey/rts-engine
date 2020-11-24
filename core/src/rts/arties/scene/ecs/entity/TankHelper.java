package rts.arties.scene.ecs.entity;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.MathUtils;
import rts.arties.scene.ecs.component.*;
import rts.arties.scene.map.IRTSMap;
import rts.arties.scene.unit.DefaultEntity;
import rts.arties.scene.unit.state.StateManager;
import rts.arties.scene.unit.steeringbehaviour.SteeringBehaviours;

public class TankHelper {

    public static Entity create(Engine engine, IRTSMap map, float x, float y, float hp) {

        Entity e = new Entity();
        DefaultEntity de = new DefaultEntity(e);

        // Body
        BodyComponent bc = engine.createComponent(BodyComponent.class);
        bc.me = de;
        bc.mass = 0.9f;
        bc.weight = 1f;
        bc.softRadius = 15;
        // Health
        HealthComponent hc = engine.createComponent(HealthComponent.class);
        hc.maxHp = 100;
        hc.setHp(MathUtils.clamp(hp, 0, hc.maxHp));
        // Position
        PositionComponent pc = engine.createComponent(PositionComponent.class);
        pc.pos.set(x, y);
        pc.viewingDistance = 160;
        // Movement
        MovementComponent mc = engine.createComponent(MovementComponent.class);
        mc.heading.set(1, 0, 0);
        mc.slowingDistance = 70;
        mc.maxSpeed = 90f; // m/s
        mc.maxForce = 130f; // Kg*m/s^2
        mc.maxTurnRate = (float) (Math.PI); // rad/s
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
        rbc.textureName = "tank-32";
        rbc.rotateImage = true;
        // Shadow
        RenderableShadowComponent rsc = engine.createComponent(RenderableShadowComponent.class);
        rsc.shadowOffsetY = 25f;
        rsc.shadowFlipY = false;
        // Map
        MapComponent mpc = engine.createComponent(MapComponent.class);
        mpc.map = map;
        // Player
        PlayerComponent plc = engine.createComponent(PlayerComponent.class);
        plc.selectionRadius = 17;

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

        return e;
    }
}
