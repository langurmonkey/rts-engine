package rts.arties.scene.unit;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import rts.arties.datastructure.geom.Vector3;
import rts.arties.scene.map.IRTSMap;
import rts.arties.scene.unit.group.UnitGroup;
import rts.arties.scene.unit.steeringbehaviour.SteeringBehaviours;

/**
 * This represents a physical, non-abstract map object.
 *
 * @author Toni Sagrista
 */
public class PhysicalObject extends PositionPhysicalEntity {

    private final String textureName;

    public PhysicalObject(float x, float y, float offsetX, float offsetY, String textureName, IRTSMap map) {
        super(x, y, 0);
        this.map = map;
        this.textureName = textureName;
        this.spriteOffsetX = offsetX;
        this.spriteOffsetY = offsetY;

        // Default shadow
        shadowFlipY = true;
        shadowOffsetY = 0;

        // Default soft radius
        softRadius = 10;

    }

    public PhysicalObject(float x, float y, String textureName, IRTSMap map) {
        this(x, y, 0f, 0f, textureName, map);
    }

    @Override
    public void initAssets(AssetManager assets) {
        try {
            TextureAtlas ta = assets.get("data/tex/base-textures.atlas");
            sprite = new Sprite(ta.findRegion(textureName));
            width = sprite.getRegionWidth();
            height = sprite.getRegionHeight();

            initHardRadius(height / 2f);

            // Add to map, just once (these entities do not move)
            // This needs to happen here because we need the width and height
            map.updateEntity(this);
        } catch (Exception e) {
        }
    }

    @Override
    public void update(float deltaSecs) {
        // void
    }

    @Override public void renderShapeFilledLayer0(ShapeRenderer sr) {

    }

    @Override public void renderShapeLineLayer1(ShapeRenderer sr) {

    }

    @Override public void renderShapeFilledLayer2(ShapeRenderer sr) {

    }

    @Override public void renderShapeLineLayer3(ShapeRenderer sr) {

    }

    @Override
    public Vector3 vel() {
        return null;
    }

    @Override
    public Rectangle hardRadius() {
        return hardRadius;
    }

    @Override
    public float slowingDistance() {
        return 0;
    }

    @Override
    public float maxSpeed() {
        return 0;
    }

    @Override
    public float maxForce() {
        return 0;
    }

    @Override
    public void group(UnitGroup group) {
    }

    @Override
    public UnitGroup group() {
        return null;
    }

    @Override
    public boolean selected() {
        return false;
    }

    @Override
    public void toggleSelection() {
    }

    @Override
    public void select() {
    }

    @Override
    public void unselect() {
    }

    @Override
    public SteeringBehaviours steeringBehaviours() {
        return null;
    }
}
