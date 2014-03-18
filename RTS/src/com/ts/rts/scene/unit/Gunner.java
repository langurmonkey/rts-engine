package com.ts.rts.scene.unit;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.ts.rts.image.TextureManager;
import com.ts.rts.scene.map.IRTSMap;
import com.ts.rts.scene.unit.steeringbehaviour.SteeringBehaviours;
import com.ts.rts.util.VectorPool;

/**
 * Experimental gunner implementation of {@link Unit}.
 * TODO All the configuration must be read from config files.
 * 
 * @author Toni Sagrista
 * 
 */
public class Gunner extends Unit {

    float lastAngle;
    Animation walkL;
    Animation walkR;

    public Gunner(float x, float y, IRTSMap map) {
	super(x, y, map);

	/** Physical parameters **/

	mass = .1f;

	// m/s
	maxSpeed = 30f;

	// Kg*m/s^2
	maxForce = 40f;

	// rad/s
	maxTurnRate = (float) (Math.PI);

	// 20 units
	slowingDistance = 20f;

	// The gunner scale, 0.6f
	scale = 0.7f;

	// Max health points
	maxHp = 20;
	hp = maxHp;

	vel = VectorPool.getObject(0f, 0f);
	heading = VectorPool.getObject(0, -1);

	initGraphics();

	hardRadius = new Rectangle(x - width / 2f, y - height / 4f, width, height / 2f);
	imageBounds = new Rectangle(x - width / 2f + spriteOffsetX, y - height / 2f + spriteOffsetY, width, height);

	softRadius = new Circle(x, y, 9);
	selectionRadius = 13;

	shadowWidth = 20;
	shadowHeight = 4;

	viewingDistance = 60;

	steeringBehaviours = new SteeringBehaviours(this);

	lastAngle = heading.angle();
	if (lastAngle <= 180) {
	    sprite.flip(true, false);
	}
    }

    @Override
    public void initGraphics() {
	try {
	    sprite = new Sprite(TextureManager.getTexture("textures", "goon-blue-stand-left"));

	    TextureRegion[] walkLeftFrames = new TextureRegion[4];

	    walkLeftFrames[0] = TextureManager.getTexture("textures", "goon-blue-walk1-left");
	    walkLeftFrames[1] = TextureManager.getTexture("textures", "goon-blue-walk2-left");
	    walkLeftFrames[2] = TextureManager.getTexture("textures", "goon-blue-walk1-left");
	    walkLeftFrames[3] = TextureManager.getTexture("textures", "goon-blue-walk3-left");

	    TextureRegion[] walkRightFrames = new TextureRegion[4];

	    walkRightFrames[0] = new TextureRegion(walkLeftFrames[0]);
	    walkRightFrames[0].flip(true, false);
	    walkRightFrames[1] = new TextureRegion(walkLeftFrames[1]);
	    walkRightFrames[1].flip(true, false);
	    walkRightFrames[2] = new TextureRegion(walkLeftFrames[2]);
	    walkRightFrames[2].flip(true, false);
	    walkRightFrames[3] = new TextureRegion(walkLeftFrames[3]);
	    walkRightFrames[3].flip(true, false);

	    walkL = new Animation(.1f, walkLeftFrames);
	    walkR = new Animation(.1f, walkRightFrames);

	    width = sprite.getRegionWidth() * scale;
	    height = sprite.getRegionHeight() * scale;

	    spriteOffsetY = 8f;

	    rotateImage = false;
	} catch (Exception e) {
	}
    }

    public void renderEntity() {
	// Rotate the sprite
	float angle = heading.angle();
	if ((lastAngle > 180 && angle <= 180) || (lastAngle <= 180 && angle > 180)) {
	    sprite.flip(true, false);
	}
	lastAngle = angle;
	positionSpriteAndDraw();
    }

    protected TextureRegion getImageToDraw() {
	if (!moving) {
	    return sprite;
	} else {
	    TextureRegion spriteToDraw;
	    if (heading.angle() > 180) {
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
