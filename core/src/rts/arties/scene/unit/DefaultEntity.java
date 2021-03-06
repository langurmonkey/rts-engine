package rts.arties.scene.unit;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Rectangle;
import rts.arties.datastructure.geom.Vector3;
import rts.arties.scene.ecs.Mapper;
import rts.arties.scene.ecs.component.HealthComponent;
import rts.arties.scene.ecs.component.PlayerComponent;
import rts.arties.scene.map.IRTSMap;
import rts.arties.scene.unit.group.UnitGroup;
import rts.arties.scene.unit.steeringbehaviour.IEntity;
import rts.arties.scene.unit.steeringbehaviour.SteeringBehaviours;

public class DefaultEntity implements IEntity {

    public Entity entity;

    public DefaultEntity(Entity entity) {
        super();
        this.entity = entity;
    }

    @Override
    public Vector3 pos() {
        return Mapper.position.get(entity).pos;
    }

    @Override
    public Vector3 vel() {
        return Mapper.movement.get(entity).vel;
    }

    @Override
    public Vector3 heading() {
        return Mapper.movement.get(entity).heading;
    }

    @Override
    public float viewingDistance() {
        return Mapper.position.get(entity).viewingDistance;
    }

    @Override
    public float softRadius() {
        return Mapper.body.get(entity).softRadius;
    }

    @Override
    public Rectangle hardRadius() {
        return Mapper.body.get(entity).hardRadius;
    }

    @Override
    public float weight() {
        return Mapper.body.get(entity).weight;
    }

    @Override
    public boolean isDead() {
        HealthComponent hc = Mapper.health.get(entity);
        return hc != null && hc.hp <= 0;
    }

    @Override
    public float slowingDistance() {
        return Mapper.movement.get(entity).slowingDistance;
    }

    @Override
    public float maxSpeed() {
        return Mapper.movement.get(entity).maxSpeed;
    }

    @Override
    public float maxForce() {
        return Mapper.movement.get(entity).maxForce;
    }

    @Override
    public IRTSMap map() {
        return Mapper.map.get(entity).map;
    }

    @Override
    public Rectangle bounds() {
        return Mapper.rbase.get(entity).imageBounds;
    }

    @Override
    public void group(UnitGroup group) {
        Mapper.group.get(entity).group = group;
    }

    @Override
    public UnitGroup group() {
        return Mapper.group.get(entity).group;
    }

    @Override
    public boolean selected() {
        return Mapper.player.get(entity).selected;
    }

    @Override
    public void toggleSelection() {
        PlayerComponent pc = Mapper.player.get(entity);
        pc.selected = !pc.selected;
    }

    @Override
    public void select() {
        Mapper.player.get(entity).selected = true;
    }

    @Override
    public void unselect() {
        Mapper.player.get(entity).selected = false;
    }

    @Override
    public SteeringBehaviours steeringBehaviours() {
        return Mapper.steering.get(entity).steeringBehaviours;
    }
}
