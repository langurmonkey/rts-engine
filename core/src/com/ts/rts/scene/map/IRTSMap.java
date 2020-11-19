package com.ts.rts.scene.map;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.ts.rts.datastructure.IMapCell;
import com.ts.rts.datastructure.geom.Vector2;
import com.ts.rts.scene.cam.Camera;
import com.ts.rts.scene.map.MapProperties.TerrainType;
import com.ts.rts.scene.unit.IBoundsObject;

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

    void renderFogOfWar(Camera camera);

    int getWidth();

    int getHeight();

    void updateFogOfWar(Vector3 position, int radius);

    List<IMapCell<IBoundsObject>> findPath(Float inix, Float iniy, Float endx, Float endy);

    void addEntity(IBoundsObject entity);

    void removeEntity(IBoundsObject entity);

    void updateEntity(IBoundsObject entity);

    void reorganize();

    Set<IMapCell<IBoundsObject>> findLeafNodesWith(IBoundsObject entity);

    Set<IBoundsObject> getNearbyEntities(Vector2 pos);
    Set<IBoundsObject> getNearbyEntities(Vector3 pos);

    Set<IMapCell<IBoundsObject>> getNearbyBlockedNodes(Vector2 pos);
    Set<IMapCell<IBoundsObject>> getNearbyBlockedNodes(Vector3 pos);

    boolean walkable(Vector2 ini, Vector2 end, IBoundsObject entity);
    boolean walkable(Vector3 ini, Vector3 end, IBoundsObject entity);

    /**
     * Checks if the given rectangle overlaps with a blocked node
     *
     * @param r
     * @return
     */
    boolean overlapsWithBlocked(Rectangle r);

    TerrainType getTerrainType(Vector2 point);
    TerrainType getTerrainType(Vector3 point);

    IMapCell<IBoundsObject> getCell(Vector2 point);
    IMapCell<IBoundsObject> getCell(Vector3 point);

    IMapCell<IBoundsObject> getCell(float x, float y);

    void dispose();

    MapObjects getMapObjects();
}
