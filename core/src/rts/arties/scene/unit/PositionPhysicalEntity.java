package rts.arties.scene.unit;

import rts.arties.RTSGame;
import rts.arties.datastructure.geom.Vector3;
import rts.arties.scene.unit.steeringbehaviour.IEntity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;

/**
 * Represents an entity which is physical and has a position.
 *
 * @author Toni Sagrista
 */
public abstract class PositionPhysicalEntity extends PositionEntity implements IEntity, Comparable<PositionPhysicalEntity> {

    /**
     * The default sprite
     */
    public TextureRegion sprite;

    /**
     * Mass [kg]
     **/
    public float mass;
    /**
     * Weight for the map cells
     */
    public float weight = 1;

    /**
     * SIZE
     */
    public float width;
    public float height;

    /**
     * Health points
     **/
    protected float maxHp;
    public float hp;

    /**
     * Bounds for selection - these match the bounds of the sprite
     **/
    public Rectangle imageBounds;

    /**
     * Entity bounds for collisions
     */
    public Rectangle hardRadius;

    /**
     * The soft radius for flocking/steering
     */
    public float softRadius;

    /**
     * Heading
     **/
    public Vector3 heading;

    /**
     * The sprite scale, 1 by default
     **/
    protected float scale = 1f;

    /** Shadow attributes **/
    public boolean shadowFlipY = true;
    public float shadowOffsetY = 0;

    /**
     * Units in which the image rotates, such as tanks
     **/
    protected boolean rotateImage = false;

    /**
     * Sprite offsets from the center, positive down and left
     */
    protected float spriteOffsetX = 0f, spriteOffsetY = 0f;

    public PositionPhysicalEntity() {
        super();
    }

    public PositionPhysicalEntity(float x, float y, float z) {
        super(x, y, z);
    }

    public PositionPhysicalEntity(Vector3 pos) {
        super(pos);
    }

    /**
     * This method is called after all loading has happened. Intitializes
     * sprites and other assets.
     */
    public abstract void initAssets(AssetManager assets);

    /**
     * Initializes the hard radius and the image bounds. The position of the entity must be set, as well as
     * its image initialized.
     *
     * @param height The height of the hard radius, usually the height of the image
     */
    protected void initHardRadius(float height) {
        float w2 = this.width / 2f;
        float h2 = this.height / 2f;
        hardRadius = new Rectangle(pos.x - w2, pos.y - h2 + spriteOffsetY, this.width, height);
        imageBounds = new Rectangle(pos.x - w2 + spriteOffsetX, pos.y - h2 + spriteOffsetY, this.width, this.height);
    }

    public Rectangle bounds() {
        return hardRadius;
    }

    @Override
    public Vector3 pos() {
        return pos;
    }

    public float softRadius() {
        return softRadius;
    }

    public Vector3 heading() {
        return heading;
    }

    @Override
    public float weight() {
        return weight;
    }


    public boolean isDead() {
        return hp <= 0f;
    }

    public boolean isImageColliding(float posx, float posy) {
        return imageBounds.contains(posx, posy);
    }

    /**
     * Returns the image to draw at each frame
     *
     * @return
     */
    protected TextureRegion getImageToDraw() {
        return sprite;
    }

    public abstract void update(float deltaSecs);

    protected void updateVisible() {
        visible = RTSGame.game.isVisible(pos);
    }

    /**
     * Renders the sprite
     */
    public void render(SpriteBatch batch, ShaderProgram program) {
        if (visible)
            renderSprite(batch, program);
    }

    public void renderSprite(SpriteBatch batch, ShaderProgram program) {
        // By default, heading rotates sprite
        positionSpriteAndDraw(batch, program);
    }

    /**
     * Renders the texture region returned by getImageToDraw
     */
    protected void positionSpriteAndDraw(SpriteBatch batch, ShaderProgram shader) {
        TextureRegion spriteToDraw = getImageToDraw();
        float angle = 0f;
        if (rotateImage) {
            angle = heading.angle2();
        }

        if (RTSGame.drawShadows) {
            spriteToDraw.flip(false, shadowFlipY);
            batch.setColor(0f, 0f, 0f, 0.2f);
            batch.draw(spriteToDraw, pos.x - spriteToDraw.getRegionWidth() / 2 + spriteOffsetX, pos.y - spriteToDraw.getRegionHeight() / 2 + spriteOffsetY - spriteToDraw.getRegionHeight() + spriteOffsetY + shadowOffsetY, spriteToDraw.getRegionWidth() / 2 + spriteOffsetX, spriteToDraw.getRegionHeight() / 2, spriteToDraw.getRegionWidth(), spriteToDraw.getRegionHeight(), scale, scale, angle);

            spriteToDraw.flip(false, shadowFlipY);
            batch.setColor(1, 1, 1, 1);
        }
        batch.draw(spriteToDraw, pos.x - spriteToDraw.getRegionWidth() / 2 + spriteOffsetX, pos.y - spriteToDraw.getRegionHeight() / 2 + spriteOffsetY, spriteToDraw.getRegionWidth() / 2 + spriteOffsetX, spriteToDraw.getRegionHeight() / 2, spriteToDraw.getRegionWidth(), spriteToDraw.getRegionHeight(), scale, scale, angle);
    }

    public abstract void renderShapeFilledLayer0(ShapeRenderer sr);

    public abstract void renderShapeLineLayer1(ShapeRenderer sr);

    public abstract void renderShapeFilledLayer2(ShapeRenderer sr);

    public abstract void renderShapeLineLayer3(ShapeRenderer sr);

    @Override
    public int compareTo(PositionPhysicalEntity o) {
        return Float.compare(pos.y, o.pos.y) / -1;
    }

    public void renderDebug(ShapeRenderer sr) {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        sr.begin(ShapeType.Filled);

        // Hard radius
        sr.setColor(new Color(0f, 0f, .4f, .4f));
        sr.rect(hardRadius.x, hardRadius.y, hardRadius.width, hardRadius.height);

        // Soft radius
        sr.setColor(new Color(0f, .4f, 0f, .2f));
        sr.circle(pos.x, pos.y, softRadius);

        // Position
        sr.setColor(new Color(0f, 1f, 0f, 1f));
        sr.circle(pos.x, pos.y, 1);
        sr.end();

        sr.begin(ShapeType.Line);
        // Image bounds
        sr.setColor(new Color(0f, 0f, 0f, 1f));
        sr.rect(imageBounds.x, imageBounds.y, imageBounds.width, imageBounds.height);

        // View distance
        sr.setColor(new Color(0f, 0.5f, 0.2f, 1f));
        sr.circle(pos.x, pos.y, viewDistance);

        // Heading
        if (heading != null) {
            sr.setColor(new Color(1f, 0f, 0f, 1f));
            sr.line(pos.x, pos.y, heading.x * 40 + pos.x, heading.y * 40 + pos.y);
        }

        sr.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    public void setHp(float newHp) {
        if (newHp > maxHp) {
            newHp = maxHp;
        }
        hp = newHp;
    }

}
