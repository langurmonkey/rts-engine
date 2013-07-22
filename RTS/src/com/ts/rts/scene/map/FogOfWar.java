package com.ts.rts.scene.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.ts.rts.RTSGame;
import com.ts.rts.datastructure.geom.Vector2;
import com.ts.rts.scene.cam.Camera;

/**
 * Fog of war implementation as a matrix of boolean values [visible|hidden].
 * 
 * @author Toni Sagrista
 * 
 */
public class FogOfWar {
	private static final byte F_HIDDEN = 0;
	private static final byte F_VISIBLE = 1;

	private byte[][] fog;
	private int tileSize;
	private int width, height;

	private ShapeRenderer shapeRenderer;

	/**
	 * Creates a new fog of war with the given with and heigth (in tiles) and the tile size
	 * 
	 * @param width
	 * @param height
	 * @param tileSize
	 */
	public FogOfWar(int width, int height, int tileSize) {
		super();
		this.width = width;
		this.height = height;
		this.fog = new byte[width][height];
		this.tileSize = tileSize;
		this.shapeRenderer = RTSGame.getInstance().cameraShapeRenderer;
	}

	/**
	 * Updates the current fog of war with a visibility centered in the given position and with the given radius
	 * 
	 * @param x
	 * @param y
	 * @param radius
	 */
	public void update(Vector2 position, int radius) {
		int x = (int) (position.x / tileSize);
		int y = (int) (position.y / tileSize);
		int blocks = Math.round(radius / tileSize);

		for (int i = x - blocks; i <= x + blocks; i++) {
			for (int j = y - blocks; j <= y + blocks; j++) {
				if (i >= 0 && i < width && j >= 0 && j < height) {
					fog[i][j] = F_VISIBLE;
				}
			}
		}
	}

	/**
	 * Renders the fog of war
	 */
	public void render(Camera camera) {
		Gdx.gl.glEnable(GL20.GL_BLEND);
		shapeRenderer.setProjectionMatrix(camera.getLibgdxCamera().combined);
		shapeRenderer.begin(ShapeType.Filled);

		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				if (fog[i][j] == F_HIDDEN) {
					if (checkSurroundings(i, j, F_VISIBLE)) {
						shapeRenderer.setColor(0f, 0f, 0f, .5f);
					} else {
						shapeRenderer.setColor(0f, 0f, 0f, 1f);
					}
					shapeRenderer.rect(i * tileSize, j * tileSize, tileSize, tileSize);
				}
			}
		}
		shapeRenderer.end();
		Gdx.gl.glDisable(GL20.GL_BLEND);
	}

	public boolean checkSurroundings(int x, int y, byte value) {
		if (x > 0) {
			if (fog[x - 1][y] == value) {
				return true;
			}
			if (y > 0 && fog[x - 1][y - 1] == value) {
				return true;
			}
			if (y < height - 1 && fog[x - 1][y + 1] == value) {
				return true;
			}
		}
		if (x < width - 1) {
			// x == 0
			if (fog[x + 1][y] == value) {
				return true;
			}
			if (y > 0 && fog[x + 1][y - 1] == value) {
				return true;
			}
			if (y < height - 1 && fog[x + 1][y + 1] == value) {
				return true;
			}
		}
		if (y > 0) {
			if (fog[x][y - 1] == value) {
				return true;
			}
		}
		if (y < height - 1) {
			// y == 0
			if (fog[x][y + 1] == value) {
				return true;
			}
		}
		return false;
	}
}
