package arties.scene.ecs.component;

import com.badlogic.ashley.core.Component;

public class HealthComponent implements Component {
    /**
     * Health points
     **/
    public float maxHp;
    public float hp;

    public void setHp(float newHp) {
        if (newHp > maxHp) {
            newHp = maxHp;
        }
        hp = newHp;
    }
}
