package rts.arties.scene.ecs.component;

import com.badlogic.ashley.core.Component;
import rts.arties.scene.unit.state.StateManager;

public class StateComponent implements Component {
    public StateManager stateManager;

    // State time in seconds
    public float stateTime = 0f;
}
