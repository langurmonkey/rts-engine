package rts.arties.scene.selection;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import rts.arties.RTSGame;
import rts.arties.datastructure.geom.Vector2;
import rts.arties.scene.cam.Camera;
import rts.arties.scene.unit.group.UnitGroup;
import rts.arties.scene.unit.group.UnitGroupManager;
import rts.arties.scene.unit.steeringbehaviour.IEntity;
import rts.arties.util.Vector2Pool;

import java.util.Set;

/**
 * Manages the selection of entities.
 *
 * @author Toni Sagrista
 */
public class Selection {

    /**
     * The game
     */
    private final RTSGame game;

    /**
     * The camera
     */
    private final Camera camera;

    /**
     * Is there an active selection in process?
     */
    public boolean active = false;

    /**
     * Selection start point
     */
    public Vector2 start;

    /**
     * Selection end point
     */
    public Vector2 end;

    /**
     * Selected units
     */
    public UnitGroup selected;

    private final ShapeRenderer shapeRenderer;

    private final Rectangle sel;

    public Selection(RTSGame game) {
        this.game = game;
        this.camera = game.camera();
        start = Vector2Pool.getObject();
        end = Vector2Pool.getObject();
        // The selection is in screen coordinates
        shapeRenderer = RTSGame.game.screenShapeRenderer;
        this.selected = null;
        sel = new Rectangle();
    }

    public void render() {
        if (active) {
            Rectangle r = getRectangle();
            shapeRenderer.begin(ShapeType.Line);
            shapeRenderer.setColor(new Color(1f, 1f, 0f, 1f));
            shapeRenderer.rect(r.x, r.y, r.width, r.height);
            shapeRenderer.end();
        }
    }

    /**
     * Triggers the selection of the units inside the selection bounds.
     */
    public void select() {
        if (active) {
            camera.screenToWorld(start.x, start.y, start);
            camera.screenToWorld(end.x, end.y, end);

            selectUnitsIn(getRectangle());
        }
    }

    /**
     * Selects the units inside the given rectangle
     *
     * @param r The rectangle in canvas coordinates
     */
    public void selectUnitsIn(Rectangle r) {
        // Translate rectangle to map coordinates, use camera
        r.setX(r.getX());
        r.setY(r.getY());

        selected = UnitGroupManager.getInstance().getSelectionUnitGroup();
        Set<IEntity> inside = game.getInsideUnits(r);
        if (!inside.isEmpty()) {
            clearSelection();
            for (IEntity u : inside) {
                selected.add(u);
                u.select();
            }
        }
        selected.sortByPosition();
    }

    /**
     * Clears the current selection, unselecting all units
     */
    public void clearSelection() {
        if (selected != null) {
            for (IEntity u : selected) {
                u.unselect();
            }
            selected.clear();
        }
    }

    /**
     * Selects the unit colliding with the given coordinates
     *
     * @param x The x in canvas coordinates
     * @param y The y in canvas coordinates
     * @param z The z
     */
    public void selectOrMove(float x, float y, float z) {
        IEntity entity = game.getCollidingUnitImage((int) x, (int) y, (int) z);
        if (entity != null) {
            // Remove current selection
            clearSelection();

            entity.toggleSelection();

            selected = UnitGroupManager.getInstance().getSelectionUnitGroup();

            selected.add(entity);
            selected.sortByPosition();
        } else {
            if (selected != null && !selected.isEmpty()) {
                game.moveUnits(selected, (int) x, (int) y, (int) z);
            }
        }
    }

    /**
     * Gets the rectangle bounds of the current selection
     *
     * @return
     */
    private Rectangle getRectangle() {
        float x1, x2, y1, y2;

        if (start.x < end.x) {
            x1 = start.x;
            x2 = end.x;
        } else {
            x1 = end.x;
            x2 = start.x;
        }
        if (start.y < end.y) {
            y1 = start.y;
            y2 = end.y;
        } else {
            y1 = end.y;
            y2 = start.y;
        }

        sel.set(x1, y1, x2 - x1, y2 - y1);
        return sel;
    }
}
