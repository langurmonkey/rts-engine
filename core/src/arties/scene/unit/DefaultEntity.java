package arties.scene.unit;

import arties.datastructure.geom.Vector3;
import arties.scene.ecs.Mapper;
import arties.scene.map.IRTSMap;
import arties.scene.unit.steeringbehaviour.IEntity;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Rectangle;

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
    public float softRadius() {
        return Mapper.movement.get(entity).softRadius;
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
}
