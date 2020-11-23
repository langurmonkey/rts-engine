package rts.arties.scene.ecs.entity;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import rts.arties.scene.ecs.component.*;
import rts.arties.scene.map.IRTSMap;
import rts.arties.scene.unit.DefaultEntity;
import rts.arties.scene.unit.state.StateManager;
import rts.arties.scene.unit.steeringbehaviour.SteeringBehaviours;

public class TankHelper {

    public static Entity createTank(Engine engine, IRTSMap map, float x, float y, float hp) {

        Entity tank = new Entity();
        DefaultEntity tankEntity = new DefaultEntity(tank);

        // Body
        BodyComponent bc = engine.createComponent(BodyComponent.class);
        bc.me = tankEntity;
        bc.mass = 0.9f;
        bc.weight = 1f;
        // Health
        HealthComponent hc = engine.createComponent(HealthComponent.class);
        hc.maxHp = 100;
        hc.setHp(hp);
        // Position
        PositionComponent pc = engine.createComponent(PositionComponent.class);
        pc.pos.set(x, y);
        pc.viewingDistance = 160;
        // Movement
        MovementComponent mc = engine.createComponent(MovementComponent.class);
        mc.heading.set(1, 0, 0);
        mc.softRadius = 15;
        mc.slowingDistance = 70;
        mc.maxSpeed = 90f; // m/s
        mc.maxForce = 130f; // Kg*m/s^2
        mc.maxTurnRate = (float) (Math.PI / 2.0); // rad/s
        mc.updateMaxSpeed(hc);
        // Steering
        SteeringComponent sc = engine.createComponent(SteeringComponent.class);
        sc.steeringBehaviours = new SteeringBehaviours(tankEntity);
        sc.steeringBehaviours.addSeparation(map);
        sc.steeringBehaviours.addAvoidWalls(map);
        // State
        StateComponent stc = engine.createComponent(StateComponent.class);
        stc.stateManager = new StateManager(tankEntity);
        // Renderable
        RenderableBaseComponent rvc = engine.createComponent(RenderableBaseComponent.class);
        rvc.textureName = "tank-32";
        rvc.rotateImage = true;
        // Shadow
        RenderableShadowComponent rsc = engine.createComponent(RenderableShadowComponent.class);
        rsc.shadowOffsetY = 25f;
        // Map
        MapComponent mpc = engine.createComponent(MapComponent.class);
        mpc.map = map;
        // Player
        PlayerComponent prc = engine.createComponent(PlayerComponent.class);

        // Add components
        tank.add(prc);
        tank.add(pc);
        tank.add(mc);
        tank.add(bc);
        tank.add(rvc);
        tank.add(rsc);
        tank.add(sc);
        tank.add(stc);
        tank.add(mpc);

        return tank;
    }
}
