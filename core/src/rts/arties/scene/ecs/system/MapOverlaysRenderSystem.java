package rts.arties.scene.ecs.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import rts.arties.RTSGame;
import rts.arties.scene.cam.Camera;
import rts.arties.scene.ecs.Mapper;
import rts.arties.scene.ecs.component.MapComponent;
import rts.arties.scene.unit.group.UnitGroupManager;

public class MapOverlaysRenderSystem extends IteratingSystem {

    public MapOverlaysRenderSystem(Family family, int priority) {
        super(family, priority);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        MapComponent mc = Mapper.map.get(entity);
        Camera camera = RTSGame.getCamera();

        mc.map.renderOverlays(camera);
        mc.map.renderFogOfWar(camera);

        Gdx.gl.glEnable(GL20.GL_BLEND);
        // Lines to scale with zoom
        Gdx.gl.glLineWidth(1f * RTSGame.getCamera().zoom);
        UnitGroupManager.getInstance().render();
    }
}
