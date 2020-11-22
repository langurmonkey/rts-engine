package arties.scene.unit;

import arties.scene.map.IRTSMap;
import arties.util.Vector3Pool;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

/**
 * Experimental gunner implementation of {@link Unit}.
 * TODO All the configuration must be read from config files.
 *
 * @author Toni Sagrista
 */
public class Gunner extends Unit {

    float lastAngle;
    Animation<TextureRegion> walkL;
    Animation<TextureRegion> walkR;

    public Gunner(float x, float y, IRTSMap map) {
        super(x, y, 0, map);

        /** Physical parameters **/

        mass = .1f;

        // m/s
        maxSpeed = 60f;

        // Kg*m/s^2
        maxForce = 150f;

        // rad/s
        maxTurnRate = (float) (10 * Math.PI);

        // 20 units
        slowingDistance = 10f;

        // The gunner scale, 0.6f
        scale = .9f;

        // Max health points
        maxHp = 20;
        hp = maxHp;

        vel = Vector3Pool.getObject(0f, 0f);
        heading = Vector3Pool.getObject(0, -1);

        softRadius = 13;
        selectionRadius = 18;

        shadowFlipY = true;
        shadowOffsetY = 0;

        viewDistance = 220;
    }

    @Override
    public void initAssets(AssetManager assets) {
        try {
            TextureAtlas ta = assets.get("data/img/textures/textures.pack");
            sprite = new Sprite(ta.findRegion("units/goon-blue-stand-left"));

            TextureRegion[] walkLeftFrames = new TextureRegion[4];

            walkLeftFrames[0] = ta.findRegion( "units/goon-blue-walk1-left");
            walkLeftFrames[1] = ta.findRegion( "units/goon-blue-walk2-left");
            walkLeftFrames[2] = ta.findRegion( "units/goon-blue-walk1-left");
            walkLeftFrames[3] = ta.findRegion( "units/goon-blue-walk3-left");

            TextureRegion[] walkRightFrames = new TextureRegion[4];

            walkRightFrames[0] = new TextureRegion(walkLeftFrames[0]);
            walkRightFrames[0].flip(true, false);
            walkRightFrames[1] = new TextureRegion(walkLeftFrames[1]);
            walkRightFrames[1].flip(true, false);
            walkRightFrames[2] = new TextureRegion(walkLeftFrames[2]);
            walkRightFrames[2].flip(true, false);
            walkRightFrames[3] = new TextureRegion(walkLeftFrames[3]);
            walkRightFrames[3].flip(true, false);

            walkL = new Animation<>(.1f, walkLeftFrames);
            walkR = new Animation<>(.1f, walkRightFrames);

            width = sprite.getRegionWidth() * scale;
            height = sprite.getRegionHeight() * scale;

            spriteOffsetY = 11f;

            initHardRadius(height / 2f);

            lastAngle = heading.angle2();
            if (lastAngle <= 180) {
                sprite.flip(true, false);
            }
        } catch (Exception e) {
        }
    }

    public void renderSprite(SpriteBatch batch, ShaderProgram program) {
        // Flip if necessary the sprite
        float angle = heading.angle2();
        if ((lastAngle > 180 && angle <= 180) || (lastAngle <= 180 && angle > 180)) {
            sprite.flip(true, false);
        }
        lastAngle = angle;
        positionSpriteAndDraw(batch, program);
    }

    protected TextureRegion getImageToDraw() {
        if (!moving) {
            return sprite;
        } else {
            TextureRegion spriteToDraw;
            if (heading.angle2() > 180) {
                // left
                spriteToDraw = walkL.getKeyFrame(stateTime, true);
            } else {
                // right
                spriteToDraw = walkR.getKeyFrame(stateTime, true);
            }
            return spriteToDraw;
        }
    }

    @Override
    public String getName() {
        return "Gunner";
    }
}
