package com.ts.rts.datastructure.grid.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.ts.rts.RTSGame;
import com.ts.rts.datastructure.IMap;
import com.ts.rts.datastructure.IMapRenderer;
import com.ts.rts.datastructure.grid.GridCell;
import com.ts.rts.datastructure.grid.GridMap;

/**
 * Renderer for grid maps.
 * 
 * @author Toni Sagrista
 * 
 */
public class GridMapRenderer implements IMapRenderer {

	BitmapFont font9;
	ShapeRenderer shapeRenderer;
	SpriteBatch fontBatch;

	public GridMapRenderer() {

		// load font from a .ttf file
		try {
			FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("data/font/jgfont.ttf"));
			font9 = generator.generateFont(9);
			generator.dispose();

			shapeRenderer = RTSGame.getInstance().cameraShapeRenderer;
			fontBatch = new SpriteBatch();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void drawMap(IMap t) {
		GridMap map = (GridMap) t;

		fontBatch.setProjectionMatrix(RTSGame.getInstance().orthoCamera.combined);

		for (int i = 0; i < map.columns; i++) {
			for (int j = 0; j < map.rows; j++) {
				drawCell(map.cells[i][j]);
			}
		}

	}

	public void drawCell(GridCell cell) {
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		// Draw lines
		if (cell.isBlocked()) {
			shapeRenderer.begin(ShapeType.Filled);
			shapeRenderer.setColor(new Color(0f, 0f, 1f, .4f));
			shapeRenderer.rect(cell.bounds.x, cell.bounds.y, cell.bounds.width, cell.bounds.height);
			shapeRenderer.end();
		} else if (cell.hasObjects()) {
			shapeRenderer.begin(ShapeType.Filled);
			shapeRenderer.setColor(new Color(1f, 1f, 0f, .2f));
			shapeRenderer.rect(cell.bounds.x, cell.bounds.y, cell.bounds.width, cell.bounds.height);
			shapeRenderer.end();
		}
		shapeRenderer.begin(ShapeType.Line);
		shapeRenderer.setColor(new Color(1f, 1f, 1f, .8f));
		shapeRenderer.rect(cell.bounds.x, cell.bounds.y, cell.bounds.width, cell.bounds.height);
		shapeRenderer.end();

		Gdx.gl.glDisable(GL20.GL_BLEND);

		// Draw text
		drawCellText(cell);
	}

	public void drawCellText(GridCell cell) {
		// Render coordinates and adjacent nodes
		fontBatch.begin();
		// font9.setColor(1f, 1f, 1f, 1f);
		// font9.draw(fontBatch, "(" + cell.x + "," + cell.y + ")", (float) cell.x - 30f, (float) cell.y - 10);

		if (cell.hasObjects()) {
			// Render number of objects
			font9.setColor(.3f, .3f, .3f, 1f);
			font9.draw(fontBatch, cell.objects.size() + "", (float) cell.bounds.getX() + 2f,
					(float) cell.bounds.getY() + 10f);
		}

		font9.setColor(1f, .3f, .3f, 1f);
		font9.draw(fontBatch, cell.z + "", (float) cell.bounds.getX() + 2f, (float) cell.bounds.getY() + 20f);

		fontBatch.flush();
		fontBatch.end();
	}

}
