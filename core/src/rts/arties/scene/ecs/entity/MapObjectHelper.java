package rts.arties.scene.ecs.entity;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.utils.Logger;
import rts.arties.scene.ecs.component.*;
import rts.arties.scene.map.IRTSMap;
import rts.arties.scene.unit.DefaultEntity;
import rts.arties.util.TextUtils;
import rts.arties.util.parse.Parser;

import java.nio.file.Paths;

public class MapObjectHelper {
    private static final Logger logger = new Logger(MapObjectHelper.class.getSimpleName(), Logger.INFO);

    public static Entity create(Engine engine, MapObject mo, IRTSMap map){
        float x = mo.getProperties().get("x", Float.class);
        float y = mo.getProperties().get("y", Float.class);
        float ow = mo.getProperties().get("width", Float.class);

        float shadowOffsetY = 0;
        float weight = 1;
        String textureName;
        if (mo instanceof TiledMapTileMapObject) {
            TiledMapTileMapObject tmtmo = (TiledMapTileMapObject) mo;
            String fileName = Paths.get(tmtmo.getTextureRegion().getTexture().toString()).getFileName().toString();
            textureName = TextUtils.removeExtension(fileName);
            if (tmtmo.getTile().getProperties() != null) {
                try {
                    shadowOffsetY = Parser.parseFloatException(tmtmo.getTile().getProperties().get("shadowOffsetY", "0", String.class));
                } catch (NumberFormatException nfe) {
                    logger.debug("Could not parse 'shadowOffsetY' from tile: " + nfe.getLocalizedMessage());
                }
                try {
                    weight = Parser.parseFloatException(tmtmo.getTile().getProperties().get("weight", "1", String.class));
                } catch (NumberFormatException nfe) {
                    logger.debug("Could not parse 'shadowOffsetY' from tile: " + nfe.getLocalizedMessage());
                }
            }
        } else {
            textureName = mo.getName();
        }

        Entity e = new Entity();
        DefaultEntity de = new DefaultEntity(e);

        // Body
        BodyComponent bc = engine.createComponent(BodyComponent.class);
        bc.me = de;
        bc.weight = weight;
        bc.softRadius = 10;
        // Health
        HealthComponent hc = engine.createComponent(HealthComponent.class);
        hc.maxHp = 100;
        hc.setHp(100);
        // Position
        PositionComponent pc = engine.createComponent(PositionComponent.class);
        pc.pos.set(x + ow / 2f, y);
        pc.viewingDistance = 0;
        // Renderable
        RenderableBaseComponent rbc = engine.createComponent(RenderableBaseComponent.class);
        rbc.textureName = textureName;
        rbc.spriteOffsetX = 0;
        rbc.spriteOffsetY = 25f;
        // Shadow
        RenderableShadowComponent rsc = engine.createComponent(RenderableShadowComponent.class);
        rsc.shadowOffsetY = shadowOffsetY;
        // Map
        MapComponent mpc = engine.createComponent(MapComponent.class);
        mpc.map = map;
        // Visibility
        VisibilityComponent vc = engine.createComponent(VisibilityComponent.class);
        vc.visible = false;

        // Add components
        e.add(pc);
        e.add(bc);
        e.add(rbc);
        e.add(rsc);
        e.add(mpc);
        e.add(hc);
        e.add(vc);

        return e;

    }
}
