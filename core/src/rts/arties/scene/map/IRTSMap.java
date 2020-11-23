package rts.arties.scene.map;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import rts.arties.datastructure.IMapCell;
import rts.arties.datastructure.geom.Vector2;
import rts.arties.scene.cam.Camera;
import rts.arties.scene.unit.steeringbehaviour.IEntity;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

import java.util.List;
import java.util.Set;

/**
 * A RTS map. This is the actual map of the engine, containing all the necessary information for rendering, updating,
 * etc.
 *
 * @author Toni Sagrista
 */
public interface IRTSMap {

    void doneLoading(AssetManager assets);

    void renderBase(Camera camera);

    void renderOverlays(Camera camera);

    void renderDebug();

    void renderFogOfWar(Camera camera, ShapeRenderer sr, SpriteBatch sb);

    int getWidth();

    int getHeight();

    void updateFogOfWar(Vector3 position, int radius);

    List<IMapCell<IEntity>> findPath(Float inix, Float iniy, Float endx, Float endy);

    void addEntity(IEntity entity);

    void removeEntity(IEntity entity);

    void updateEntity(IEntity entity);

    void reorganize();

    Set<IMapCell<IEntity>> findLeafNodesWith(IEntity entity);

    Set<IEntity> getNearbyEntities(Vector2 pos);
    Set<IEntity> getNearbyEntities(Vector3 pos);

    Set<IMapCell<IEntity>> getNearbyBlockedNodes(Vector2 pos);
    Set<IMapCell<IEntity>> getNearbyBlockedNodes(Vector3 pos);

    boolean walkable(Vector2 ini, Vector2 end, IEntity entity);
    boolean walkable(Vector3 ini, Vector3 end, IEntity entity);

    /**
     * Checks if the given rectangle overlaps with a blocked node
     *
     * @param r
     * @return
     */
    boolean overlapsWithBlocked(Rectangle r);

    MapProperties.TerrainType getTerrainType(Vector2 point);
    MapProperties.TerrainType getTerrainType(Vector3 point);

    IMapCell<IEntity> getCell(Vector2 point);
    IMapCell<IEntity> getCell(Vector3 point);

    IMapCell<IEntity> getCell(float x, float y);

    void dispose();

    MapObjects getMapObjects();
}
