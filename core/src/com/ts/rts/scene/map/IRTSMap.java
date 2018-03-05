package com.ts.rts.scene.map;

import java.util.List;
import java.util.Set;

import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.math.Rectangle;
import com.ts.rts.datastructure.IMapCell;
import com.ts.rts.datastructure.geom.Vector2;
import com.ts.rts.scene.cam.Camera;
import com.ts.rts.scene.map.MapProperties.TerrainType;
import com.ts.rts.scene.unit.IBoundsObject;

/**
 * A RTS map. This is the actual map of the engine, containing all the necessary information for rendering, updating,
 * etc.
 * 
 * @author Toni Sagrista
 * 
 */
public interface IRTSMap {
    public void renderBase(Camera camera);

    public void renderOverlays(Camera camera);

    public void renderDebug();

    public void renderFogOfWar(Camera camera);

    public int getWidth();

    public int getHeight();

    public void updateFogOfWar(Vector2 position, int radius);

    public List<IMapCell<IBoundsObject>> findPath(Float inix, Float iniy, Float endx, Float endy);

    public void addEntity(IBoundsObject entity);

    public void removeEntity(IBoundsObject entity);

    public void updateEntity(IBoundsObject entity);

    public void reorganize();

    public Set<IMapCell<IBoundsObject>> findLeafNodesWith(IBoundsObject entity);

    public Set<IBoundsObject> getNearbyEntities(Vector2 pos);

    public Set<IMapCell<IBoundsObject>> getNearbyBlockedNodes(Vector2 pos);

    public boolean walkable(Vector2 ini, Vector2 end, IBoundsObject entity);

    /**
     * Checks if the given rectangle overlaps with a blocked node
     * 
     * @param r
     * @return
     */
    public boolean overlapsWithBlocked(Rectangle r);

    public TerrainType getTerrainType(Vector2 point);

    public IMapCell<IBoundsObject> getCell(Vector2 point);

    public IMapCell<IBoundsObject> getCell(float x, float y);

    public void dispose();

    public MapObjects getMapObjects();
}
