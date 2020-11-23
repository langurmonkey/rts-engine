package rts.arties.scene.ecs.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class RenderableWalkerComponent implements Component {
    public float lastAngle;
    public String[] walkLeftTextures;
    public Animation<TextureRegion> walkL;
    public Animation<TextureRegion> walkR;
}
