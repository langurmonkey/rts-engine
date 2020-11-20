package arties.scene.ecs.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class RenderableWalkerComponent implements Component {
    float lastAngle;
    Animation<TextureRegion> walkL;
    Animation<TextureRegion> walkR;
}
