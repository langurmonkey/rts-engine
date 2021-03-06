package rts.arties.datastructure.grid.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import rts.arties.RTSGame;
import rts.arties.datastructure.IMap;
import rts.arties.datastructure.IMapDebugRenderer;
import rts.arties.datastructure.grid.GridCell;
import rts.arties.datastructure.grid.GridMap;
import rts.arties.scene.cam.Camera;

/**
 * Renders debug information for grid maps.
 *
 * @author Toni Sagrista
 */
public class GridDebugRenderer implements IMapDebugRenderer {

    private BitmapFont font9;
    private Camera camera;
    private ShapeRenderer shapeRenderer;
    private SpriteBatch spriteBatch;
    private final Color colBlocked;
    private final Color colHasobjs;
    private final Color colLine;

    public GridDebugRenderer(Camera camera, ShapeRenderer sr, SpriteBatch sb) {

        colBlocked = new Color(0f, 0f, 1f, .4f);
        colHasobjs = new Color(1f, 1f, 0f, .2f);
        colLine = new Color(1f, 1f, 1f, .8f);

        // load font from a .ttf file
        try {
            FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("data/font/jgfont.ttf"));
            FreeTypeFontParameter fp = new FreeTypeFontParameter();
            fp.size = 9;
            font9 = generator.generateFont(fp);
            generator.dispose();

            this.camera = camera;
            this.shapeRenderer = sr;
            this.spriteBatch = sb;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void drawMap(IMap t) {
        GridMap map = (GridMap) t;

        drawCellsFilled(map);
        drawCellsOutline(map);

        drawCellsText(map);

    }

    public void drawCellsFilled(GridMap map) {
        float tileSize = map.cellHeight;
        float ts2 = tileSize / 2f;
        shapeRenderer.begin(ShapeType.Filled);
        for (int i = 0; i < map.columns; i++) {
            for (int j = 0; j < map.rows; j++) {
                float x = i * tileSize;
                float y = j * tileSize;
                if (RTSGame.getCamera().containsPoint(x + ts2, y + ts2, tileSize)) {
                    GridCell cell = map.cells[i][j];
                    if (cell.isBlocked() || cell.hasObjects()) {
                        if (cell.isBlocked())
                            shapeRenderer.setColor(colBlocked);
                        else if (cell.hasObjects())
                            shapeRenderer.setColor(colHasobjs);

                        shapeRenderer.rect(cell.bounds.x, cell.bounds.y, cell.bounds.width, cell.bounds.height);
                    }
                }
            }
        }
        shapeRenderer.end();
    }

    public void drawCellsOutline(GridMap map) {
        // Lines to scale with zoom
        Gdx.gl.glLineWidth(1f);

        shapeRenderer.begin(ShapeType.Line);
        shapeRenderer.setColor(colLine);
        for (int i = 0; i < map.columns; i++) {
            for (int j = 0; j < map.rows; j++) {
                GridCell cell = map.cells[i][j];
                shapeRenderer.rect(cell.bounds.x, cell.bounds.y, cell.bounds.width, cell.bounds.height);
            }
        }
        shapeRenderer.end();

    }

    public void drawCellsText(GridMap map) {
        spriteBatch.begin();
        font9.setColor(1f, .3f, .3f, 1f);
        for (int i = 0; i < map.columns; i++) {
            for (int j = 0; j < map.rows; j++) {
                GridCell cell = map.cells[i][j];
                if (cell.hasObjects()) {
                    // Render number of objects
                    font9.setColor(.3f, .3f, .3f, 1f);
                    font9.draw(spriteBatch, Integer.toString(cell.objects.size()), cell.bounds.getX() + 2f, cell.bounds.getY() + 10f);
                    font9.setColor(1f, .3f, .3f, 1f);
                }
                if (cell.z > 0)
                    font9.draw(spriteBatch, Integer.toString((int) cell.z), cell.bounds.getX() + 2f, cell.bounds.getY() + 20f);
            }
        }
        spriteBatch.flush();
        spriteBatch.end();
    }

}
