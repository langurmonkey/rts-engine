package com.ts.rts.scene.unit.group;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.ts.rts.RTSGame;

/**
 * Manages all the groups of units.
 * 
 * @author Toni Sagrista
 * 
 */
public class UnitGroupManager {

    private static final int SELECTION_CODE = -1;

    private static UnitGroupManager instance;
    /** Map of <key code, group> **/
    private Map<Integer, UnitGroup> groups;
    private ShapeRenderer shapeRenderer;

    /**
     * Initializes the manager
     */
    public static void initialize() {
	instance = new UnitGroupManager();
    }

    public static UnitGroupManager getInstance() {
	return instance;
    }

    private UnitGroupManager() {
	groups = new HashMap<>();
	shapeRenderer = RTSGame.game.cameraShapeRenderer;
    }

    /**
     * Gets the unit group associated with the given key code. If there is none it returns a new group and associates it
     * 
     * @param keyCode
     * @return
     */
    public UnitGroup getUnitGroup(Integer keyCode) {
	UnitGroup ug;
	if (!groups.containsKey(keyCode)) {
	    ug = new UnitGroup(shapeRenderer);
	    groups.put(keyCode, ug);
	} else {
	    ug = groups.get(keyCode);
	}
	return ug;
    }

    /**
     * Returns a new selection unit group
     * 
     * @return
     */
    public UnitGroup getSelectionUnitGroup() {
	if (!groups.containsKey(SELECTION_CODE)) {
	    groups.put(SELECTION_CODE, new UnitGroup(shapeRenderer));
	}
	return groups.get(SELECTION_CODE);
    }

    public void clearSelectionUnitGroup() {
	groups.remove(SELECTION_CODE);
    }

    public void update() {
	// for (Integer keyCode : groups.keySet()) {
	// if (groups.get(keyCode) != null) {
	// groups.get(keyCode).update();
	// }
	// }
    }

    public void render() {
	shapeRenderer.begin(ShapeType.Line);
	shapeRenderer.setColor(0f, .3f, 1f, 1f);
	for (Integer keyCode : groups.keySet()) {
	    if (groups.get(keyCode) != null) {
		groups.get(keyCode).render();
	    }
	}
	shapeRenderer.end();
    }

}
