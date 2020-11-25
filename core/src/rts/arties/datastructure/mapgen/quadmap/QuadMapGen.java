package rts.arties.datastructure.mapgen.quadmap;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.math.Rectangle;
import rts.arties.datastructure.IMap;
import rts.arties.datastructure.mapgen.IMapGen;
import rts.arties.datastructure.quadtree.QuadNode;
import rts.arties.datastructure.quadtree.QuadTree;
import rts.arties.scene.map.MapProperties;
import rts.arties.scene.unit.steeringbehaviour.IEntity;

/**
 * Quadtree generator, which generates a {@link QuadTree} from a TiledMap.
 *
 * @author Toni Sagrista
 */
public class QuadMapGen<T extends IEntity> implements IMapGen<T> {
    private TiledMap tm;
    private TiledMapTileLayer mainLayer;

    private int pixelwidth;
    private int pixelheight;

    @Override
    public IMap<T> generateMap(TiledMap map) {
        tm = map;
        mainLayer = (TiledMapTileLayer) tm.getLayers().get(0);
        pixelwidth = mainLayer.getWidth() * mainLayer.getTileWidth();
        pixelheight = mainLayer.getHeight() * mainLayer.getTileHeight();
        int resolution = mainLayer.getTileWidth();

        // generate the quad tree

        QuadTree<T> tree = new QuadTree<>(resolution);
        QuadNode<T> headNode = generateSquare(0, 0, pixelwidth, pixelheight, tree, null);
        tree.root = headNode;

        headNode.genVisibilityGraph();

        return tree;
    }

    public QuadNode<T> generateSquare(int x, int y, int width, int height, QuadTree<T> root, QuadNode<T> parent) {
        int curX, curY;
        int free = 0, blocked = 0;
        QuadNode<T> newNode = new QuadNode<>(new Rectangle(x, y, width, height), root, parent);

        // check if this quad is completely blocked, or completely unblocked.
        // if it's either of them, it's a complete Quad, not need for further
        // drill down.
        boolean divided = false;
        for (curX = x; curX < x + width; curX += mainLayer.getTileWidth()) {
            for (curY = y; curY < y + width; curY += mainLayer.getTileHeight()) {
                try {
                    Cell cell = mainLayer.getCell(curX / mainLayer.getTileWidth(),
                        curY / mainLayer.getTileHeight());
                    String type = cell.getTile().getProperties().get("type", "undefined", String.class);

                    if (MapProperties.isMapBlocked(type)) {
                        blocked++;
                    } else {
                        free++;
                    }

                    // We've found free and blocked pixels
                    if (blocked > 0 && free > 0) {
                        // Can we divide? If so, divide and go on. Otherwise,
                        // keep recording pixel data
                        if (width >= root.minSize && height >= root.minSize) {

                            int centerX = width / 2;
                            int centerY = height / 2;

                            int newWidthL = centerX;
                            int newHeightT = centerY;

                            // Add 1 unit if width or height is odd
                            int widthAdd = (width % 2 != 0 ? 1 : 0);
                            int heightAdd = (height % 2 != 0 ? 1 : 0);

                            newNode.northWest = generateSquare(x, y, newWidthL, newHeightT, root, newNode);
                            newNode.northEast = generateSquare(x + centerX, y, newWidthL + widthAdd, newHeightT, root,
                                newNode);
                            newNode.southWest = generateSquare(x, y + centerY, newWidthL, newHeightT + heightAdd, root,
                                newNode);
                            newNode.southEast = generateSquare(x + centerX, y + centerY, newWidthL + widthAdd,
                                newHeightT + heightAdd, root, newNode);
                            divided = true;
                            break;
                        } else {
                            // Go on
                        }
                    }

                } catch (ArrayIndexOutOfBoundsException e) {
                    break;
                }

            }

            if (divided)
                break;

        }

        if (!divided) {
            if (free > 0) {
                if (blocked > 0) {
                    int total = blocked + free;
                    // We've got free and blocked, see ratio
                    // We block it only if 75% or more of its pixels are blocked
                    float ratio = (float) blocked / (float) total;
                    if (ratio >= 0.5f) {
                        newNode.block();
                    }
                }
            } else if (free == 0) {
                if (blocked > 0)
                    newNode.block();
            }
        }

        return newNode;
    }

}
