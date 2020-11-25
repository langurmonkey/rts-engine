package rts.arties.datastructure.quadtree.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import rts.arties.datastructure.IMap;
import rts.arties.datastructure.IMapRenderer;
import rts.arties.datastructure.quadtree.QuadNode;
import rts.arties.datastructure.quadtree.QuadTree;
import rts.arties.scene.cam.Camera;

/**
 * A renderer for a {@link QuadTree}. It renders the cell borders, the position of each node and the number of objects.
 *
 * @author Toni Sagrista
 */
public class QuadTreeRenderer implements IMapRenderer {

    private BitmapFont font9;
    private Camera camera;
    private ShapeRenderer shapeRenderer;
    private SpriteBatch spriteBatch;

    public QuadTreeRenderer(Camera camera, ShapeRenderer sr, SpriteBatch sb) {

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

    public void drawMap(IMap t) {
        QuadTree tree = (QuadTree) t;
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
        drawNodeShapes(node);
        Gdx.gl.glDisable(GL20.GL_BLEND);
        drawNodeText(node);
        Gdx.gl.glEnable(GL20.GL_BLEND);
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
            spriteBatch.begin();
            font9.setColor(1f, 1f, 1f, 1f);
            font9.draw(spriteBatch, "(" + node.x + "," + node.y + ")", node.x - 30f, node.y - 10);

            font9.setColor(0f, .5f, .8f, 1f);
            font9.draw(spriteBatch, node.adjacentNodes.size() + "", node.x - 5f, node.y);

            if (node.hasObjects()) {
                // Render number of objects
                font9.setColor(.3f, .3f, .3f, 1f);
                font9.draw(spriteBatch, node.objects.size() + "", node.bounds.getX() + 2f, node.bounds.getY() + 10f);

            }
            spriteBatch.end();
        }
    }

}
