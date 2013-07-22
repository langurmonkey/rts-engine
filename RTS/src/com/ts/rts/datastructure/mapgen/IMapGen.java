package com.ts.rts.datastructure.mapgen;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.ts.rts.datastructure.IMap;
import com.ts.rts.scene.unit.IBoundsObject;

/**
 * A map generator interface which is intended to generate a new {@link IMap} from a given {@link TiledMap}.
 * 
 * @author Toni Sagrista
 * 
 * @param <T>
 */
public interface IMapGen<T extends IBoundsObject> {

	/** Terrain type property name **/
	static final String PROPERTY_TYPE = "type";
	/** Shadow property name **/
	static final String PROPERTY_SHADOW = "shadow";
	/** Sum to height value sweeping map horizontally from left to right **/
	static final String PROPERTY_SLOPEH = "slopeh";
	/** Sum to height value sweeping map vertically from top to bottom **/
	static final String PROPERTY_SLOPEV = "slopev";

	/**
	 * Generates a new map from the given tiled map
	 * 
	 * @param map
	 * @return
	 */
	public IMap<T> generateMap(TiledMap map);

}
