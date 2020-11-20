package arties.scene.ecs.component;

import com.badlogic.ashley.core.Component;

public class RenderableShadowComponent implements Component {
    /** Shadow attributes **/
    public boolean shadowFlipY = true;
    public float shadowOffsetY = 0;
}
