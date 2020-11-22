package arties.scene.ecs;

import arties.scene.ecs.component.*;
import com.badlogic.ashley.core.ComponentMapper;

public class Mapper {
    public static final ComponentMapper<BodyComponent> body = ComponentMapper.getFor(BodyComponent.class);
    public static final ComponentMapper<HealthComponent> health = ComponentMapper.getFor(HealthComponent.class);
    public static final ComponentMapper<MapComponent> map = ComponentMapper.getFor(MapComponent.class);
    public static final ComponentMapper<MovementComponent> movement = ComponentMapper.getFor(MovementComponent.class);
    public static final ComponentMapper<ParticleEffectComponent> particle = ComponentMapper.getFor(ParticleEffectComponent.class);
    public static final ComponentMapper<PlayerComponent> player = ComponentMapper.getFor(PlayerComponent.class);
    public static final ComponentMapper<PositionComponent> position = ComponentMapper.getFor(PositionComponent.class);
    public static final ComponentMapper<RenderableBaseComponent> rbase = ComponentMapper.getFor(RenderableBaseComponent.class);
    public static final ComponentMapper<RenderableShadowComponent> rshadow = ComponentMapper.getFor(RenderableShadowComponent.class);
    public static final ComponentMapper<RenderableWalkerComponent> rwalker = ComponentMapper.getFor(RenderableWalkerComponent.class);
    public static final ComponentMapper<SteeringComponent> steering = ComponentMapper.getFor(SteeringComponent.class);
    public static final ComponentMapper<StateComponent> state = ComponentMapper.getFor(StateComponent.class);
}
