package rts.arties.scene.unit;

import rts.arties.RTSGame;
import rts.arties.scene.map.IRTSMap;
import rts.arties.util.Vector3Pool;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.ParticleEffectLoader.ParticleEffectParameter;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

/**
 * Experimental implementation of a tank.
 * TODO All config must be read from files, as in Gunner.
 *
 * @author Toni Sagrista
 */
public class Tank extends Unit {
    private ParticleEffect malfunction;

    public Tank(float x, float y, IRTSMap map) {
        super(x, y, 0, map);

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

        vel = Vector3Pool.getObject(0f, 0f);
        heading = Vector3Pool.getObject(0, 1);

        softRadius = 15;
        selectionRadius = 17;

        shadowFlipY = false;
        shadowOffsetY = 25;

        viewDistance = 260;

        rotateImage = true;

    }

    @Override
    public void initAssets(AssetManager assets) {
        try {
            TextureAtlas ta = assets.get("data/tex/base-textures.atlas");
            sprite = new Sprite(ta.findRegion("tank-32"));
            width = sprite.getRegionWidth() * scale;
            height = sprite.getRegionHeight() * scale;

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
            if (malfunction == null  && !RTSGame.assets().contains("data/effects/malfunction.p")) {
                ParticleEffectParameter param = new ParticleEffectParameter();
                param.imagesDir = Gdx.files.internal("data/effects/");
                RTSGame.assets().load("data/effects/malfunction.p", ParticleEffect.class, param);
            } else if (malfunction == null && RTSGame.assets().isLoaded("data/effects/malfunction.p")) {
                malfunction = RTSGame.assets().get("data/effects/malfunction.p", ParticleEffect.class);
            } else if (malfunction != null) {
                malfunction.update(deltaSecs);
            }
        } else {
            if (malfunction != null) {
                malfunction.dispose();
                malfunction = null;
            }
        }
    }

    @Override
    public void render(SpriteBatch batch, ShaderProgram program) {
        super.render(batch, program);

        if (malfunction != null) {
            malfunction.setPosition(pos.x, pos.y);
            malfunction.draw(batch);
        }
    }

    @Override
    public String getName() {
        return "Tank";
    }

}
