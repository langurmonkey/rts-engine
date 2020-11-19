package com.ts.rts.scene.unit;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Circle;
import com.ts.rts.RTSGame;
import com.ts.rts.scene.map.IRTSMap;
import com.ts.rts.util.VectorPool;

/**
 * Experimental implementation of a tank.
 * TODO All config must be read from files, as in Gunner.
 *
 * @author Toni Sagrista
 */
public class Tank extends Unit {
    private ParticleEffect malfunction;

    public Tank(float x, float y, IRTSMap map) {
        super(x, y, map);

        /** Physical parameters **/

        mass = .9f;

        // m/s
        maxSpeed = 90f;

        // Kg*m/s^2
        maxForce = 130f;

        // rad/s
        maxTurnRate = (float) (Math.PI / 2);

        // 70 units
        slowingDistance = 70f;

        // Scale
        scale = 1f;

        // Max health points
        maxHp = 100;
        hp = maxHp;

        vel = VectorPool.getObject(0f, 0f);
        heading = VectorPool.getObject(0, 1);

        softRadius = new Circle(x, y, 15);
        selectionRadius = 17;

        shadowA = 15 * scale;
        shadowB = 15 * scale;

        viewingDistance = 160;

        rotateImage = true;

    }

    @Override
    public void initAssets(AssetManager assets) {
        try {
            TextureAtlas ta = assets.get("data/img/textures/textures.pack");
            sprite = new Sprite(ta.findRegion( "units/tank-32"));
            width = sprite.getRegionWidth() * scale - 5;
            height = sprite.getRegionHeight() * scale - 5;

            initHardRadius(height);
        } catch (Exception e) {
        }
    }

    boolean loading = false;

    @Override
    public void update(float deltaSecs) {
        super.update(deltaSecs);

        // If health below 1/3, emit particles
        if (hp / maxHp < 0.333f) {
            if (malfunction == null && !loading) {
                loading = true;
                Gdx.app.postRunnable(() -> {
                    malfunction = new ParticleEffect();
                    malfunction.load(Gdx.files.internal("data/effects/malfunction.p"), Gdx.files.internal("data"));
                    loading = false;
                });
            }
            if (malfunction != null)
                malfunction.update(deltaSecs);
        } else {
            if (malfunction != null && !loading) {
                malfunction.dispose();
                malfunction = null;
            }
        }
    }

    @Override
    public void render() {
        super.render();

        if (malfunction != null) {
            malfunction.setPosition(pos.x, pos.y);
            malfunction.draw(RTSGame.getSpriteBatch());
        }
    }

    @Override
    public String getName() {
        return "Tank";
    }

}
