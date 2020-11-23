package rts.arties.scene.ecs.component;

import rts.arties.scene.unit.state.StateManager;
import com.badlogic.ashley.core.Component;

public class StateComponent implements Component {
    public StateManager stateManager;

    // State time in seconds
    public float stateTime = 0f;
}
