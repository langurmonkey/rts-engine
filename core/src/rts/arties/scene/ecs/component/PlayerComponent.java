package rts.arties.scene.ecs.component;

import com.badlogic.ashley.core.Component;

public class PlayerComponent implements Component {
    // Is it selected?
    public boolean selected;

    // Radius of the selection circle
    public float selectionRadius;

    public float healthBarStartX, healthBarStartY;
    public float healthBarLength;
}
