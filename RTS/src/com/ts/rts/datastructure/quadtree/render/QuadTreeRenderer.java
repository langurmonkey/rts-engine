package com.ts.rts.datastructure.quadtree.render;

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
import com.ts.rts.datastructure.quadtree.QuadNode;
import com.ts.rts.datastructure.quadtree.QuadTree;

/**
 * A renderer for a {@link QuadTree}. It renders the cell borders, the position of each node and the number of objects.
 * 
 * @author Toni Sagrista
 * 
 */
public class QuadTreeRenderer implements IMapRenderer {

    BitmapFont font9;
    ShapeRenderer shapeRenderer;
    SpriteBatch fontBatch;

    public QuadTreeRenderer() {

	// load font from a .ttf file
	try {
	    FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("data/font/jgfont.ttf"));
	    font9 = generator.generateFont(9);
	    generator.dispose();

	    shapeRenderer = RTSGame.game.cameraShapeRenderer;
	    fontBatch = new SpriteBatch();
	} catch (Exception e) {
	    e.printStackTrace();
	}

    }

    public void drawMap(IMap t) {
	QuadTree tree = (QuadTree) t;
	fontBatch.setProjectionMatrix(RTSGame.game.orthoCamera.combined);
	drawNode(tree.root);
    }

    public void drawNode(QuadNode node) {
	drawNodeActually(node);
	if (!node.isLeaf()) {
	    // Draw children
	    drawNode(node.northEast);
	    drawNode(node.northWest);
	    drawNode(node.southEast);
	    drawNode(node.southWest);
	}
    }

    public void drawNodeActually(QuadNode node) {
	Gdx.gl.glEnable(GL20.GL_BLEND);
	Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
	drawNodeShapes(node);
	Gdx.gl.glDisable(GL20.GL_BLEND);
	drawNodeText(node);
    }

    public void drawNodeShapes(QuadNode node) {
	if (node.isBlocked()) {
	    shapeRenderer.begin(ShapeType.Filled);
	    shapeRenderer.setColor(new Color(0f, 0f, 1f, .4f));
	    shapeRenderer.rect(node.bounds.x, node.bounds.y, node.bounds.width, node.bounds.height);
	    shapeRenderer.end();
	} else if (node.isLeaf()) {
	    // Render coordinates and adjacent nodes
	    if (node.hasObjects()) {
		shapeRenderer.begin(ShapeType.Filled);
		shapeRenderer.setColor(new Color(1f, 1f, 0f, .2f));
		shapeRenderer.rect(node.bounds.x, node.bounds.y, node.bounds.width, node.bounds.height);
		shapeRenderer.end();
	    }
	}
	shapeRenderer.begin(ShapeType.Line);
	shapeRenderer.setColor(new Color(1f, 1f, 1f, .8f));
	shapeRenderer.rect(node.bounds.x, node.bounds.y, node.bounds.width, node.bounds.height);
	shapeRenderer.end();
    }

    public void drawNodeText(QuadNode node) {
	if (node.isLeaf()) {
	    // Render coordinates and adjacent nodes
	    fontBatch.begin();
	    font9.setColor(1f, 1f, 1f, 1f);
	    font9.draw(fontBatch, "(" + node.x + "," + node.y + ")", (float) node.x - 30f, (float) node.y - 10);

	    font9.setColor(0f, .5f, .8f, 1f);
	    font9.draw(fontBatch, node.adjacentNodes.size() + "", (float) node.x - 5f, (float) node.y);

	    if (node.hasObjects()) {
		// Render number of objects
		font9.setColor(.3f, .3f, .3f, 1f);
		font9.draw(fontBatch, node.objects.size() + "", (float) node.bounds.getX() + 2f,
			(float) node.bounds.getY() + 10f);

	    }
	    fontBatch.flush();
	    fontBatch.end();
	}
    }

}
