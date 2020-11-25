package rts.arties.scene.ecs.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import rts.arties.RTSGame;
import rts.arties.datastructure.geom.Vector2;
import rts.arties.scene.cam.Camera;
import rts.arties.scene.ecs.Mapper;
import rts.arties.scene.ecs.component.MapComponent;
import rts.arties.scene.ecs.component.PositionComponent;

public class MapBaseRenderSystem extends IteratingSystem {
    private final Family playerFamily;
    private final ShaderProgram shader;
    private final Camera camera;
    private final Vector2 aux;

    public MapBaseRenderSystem(Family family, int priority, Camera camera, ShaderProgram shader, Family playerFamily) {
        super(family, priority);
        this.playerFamily = playerFamily;
        this.shader = shader;
        this.camera = camera;
        this.aux = new Vector2();
    }

    private final boolean useShaderFog = false;
    private final float[] lights = new float[0];
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        MapComponent mc = Mapper.map.get(entity);
        Camera camera = RTSGame.getCamera();

        // Contains circular light positions and radius
        ImmutableArray<Entity> playerEntities = getEngine().getEntitiesFor(playerFamily);
        // Contains shadow positions and width and height
        int i = 0;
        if(useShaderFog) {
            for (Entity e : playerEntities) {
                PositionComponent pc = Mapper.position.get(e);
                if (pc.viewingDistance > 0) {
                    camera.worldToScreen(pc.pos.x, pc.pos.y, aux);
                    lights[i++] = aux.x;
                    lights[i++] = aux.y;
                    lights[i++] = pc.viewingDistance / camera.zoom;
                }
            }
        }

        shader.bind();
        shader.setUniform3fv("u_lights", lights, 0, i);
        shader.setUniformi("u_light_count", i / 3);
        mc.map.renderBase(camera);

    }
}
