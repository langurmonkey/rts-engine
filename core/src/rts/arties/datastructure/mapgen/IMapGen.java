package rts.arties.datastructure.mapgen;

import com.badlogic.gdx.maps.tiled.TiledMap;
import rts.arties.datastructure.IMap;
import rts.arties.scene.unit.steeringbehaviour.IEntity;

/**
 * A map generator interface which is intended to generate a new {@link IMap} from a given {@link TiledMap}.
 *
 * @author Toni Sagrista
 */
public interface IMapGen<T extends IEntity> {

    /**
     * Terrain type property name
     **/
    String PROPERTY_TYPE = "type";
    /**
     * Shadow property name
     **/
    String PROPERTY_SHADOW = "shadow";
    /**
     * Sum to height value sweeping map horizontally from left to right
     **/
    String PROPERTY_SLOPEH = "slopeh";
    /**
     * Sum to height value sweeping map vertically from top to bottom
     **/
    String PROPERTY_SLOPEV = "slopev";
    /**
     * Slowdown from default speed
     **/
    String PROPERTY_SLOWDOWN = "slowdown";

    /**
     * Generates a new map from the given tiled map
     *
     * @param map
     * @return
     */
    IMap<T> generateMap(TiledMap map);

}
