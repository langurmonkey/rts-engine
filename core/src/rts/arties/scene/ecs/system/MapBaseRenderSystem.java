package rts.arties.scene.ecs.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import rts.arties.RTSGame;
import rts.arties.scene.cam.Camera;
import rts.arties.scene.ecs.Mapper;
import rts.arties.scene.ecs.component.MapComponent;
import rts.arties.scene.ecs.component.PositionComponent;

public class MapBaseRenderSystem extends IteratingSystem {
    private Family playerFamily;
    private ShaderProgram shader;

    public MapBaseRenderSystem(Family family, int priority, ShaderProgram shader, Family playerFamily) {
        super(family, priority);
        this.playerFamily = playerFamily;
        this.shader = shader;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        MapComponent mc = Mapper.map.get(entity);
        Camera camera = RTSGame.getCamera();

        // Contains circular light positions and radius
        ImmutableArray<Entity> playerEntities = getEngine().getEntitiesFor(playerFamily);
        float[] lights = new float[playerEntities.size() * 3];
        // Contains shadow positions and width and height
        int i = 0;
        for(Entity e : playerEntities){
            PositionComponent pc = Mapper.position.get(e);
            if(pc.viewingDistance > 0){
                lights[i++] = pc.pos.x;
                lights[i++] = pc.pos.y;
                lights[i++] = pc.viewingDistance;
            }
        }

        shader.bind();
        shader.setUniform3fv("u_lights", lights, 0, i);
        shader.setUniformi("u_light_count", i / 3);
        shader.setUniformf("u_camera_offset", camera.pos.x - Gdx.graphics.getWidth() / 2f, camera.pos.y - Gdx.graphics.getHeight() / 2f);
        mc.map.renderBase(camera);

    }
}
