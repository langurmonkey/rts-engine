package arties.scene.ecs.component;

import com.badlogic.ashley.core.Component;

public class SelectionComponent implements Component {
    // Is it selected?
    protected boolean selected;

    // Radius of the selection circle
    protected float selectionRadius;
}