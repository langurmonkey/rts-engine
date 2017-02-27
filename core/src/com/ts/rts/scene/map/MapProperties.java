package com.ts.rts.scene.map;

/**
 * This class contains the terrain types and some utilities.
 * 
 * @author Toni Sagrista
 * 
 */
public class MapProperties {

	/**
	 * Terrain types
	 * 
	 * @author Toni Sagrista
	 * 
	 */
	public static enum TerrainType {
		UNDEFINED, BLOCKED, GRASS, VINE, JUNGLE, WATER, SAND, DESERT, SNOW, ROCK, SHORE, CLIFF, TREE, MUD, CRATER, LAVA, ICE, SLOPE_TOP, SLOPE_BOTTOM, SLOPE_RIGHT, SLOPE_LEFT, ROAD, ROCKY, CACTUS
	}

	public static TerrainType getTerrainType(String typeString) {
		return TerrainType.valueOf(typeString.toUpperCase());
	}

	public static boolean isMapBlocked(TerrainType type) {
		return type.equals(TerrainType.BLOCKED) || type.equals(TerrainType.ROCK) || type.equals(TerrainType.WATER)
				|| type.equals(TerrainType.CLIFF) || type.equals(TerrainType.SHORE) || type.equals(TerrainType.CRATER)
				|| type.equals(TerrainType.TREE);
	}

	public static boolean isMapBlocked(String type) {
		return isMapBlocked(getTerrainType(type));
	}

	public static boolean isSlope(TerrainType type) {
		return type.toString().startsWith("SLOPE_");
	}

	public static boolean isShadowSlope(TerrainType type) {
		return type.equals(TerrainType.SLOPE_BOTTOM) || type.equals(TerrainType.SLOPE_RIGHT);
	}

	public static boolean isLightSlope(TerrainType type) {
		return type.equals(TerrainType.SLOPE_TOP) || type.equals(TerrainType.SLOPE_LEFT);
	}

}
