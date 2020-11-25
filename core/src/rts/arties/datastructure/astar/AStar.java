package rts.arties.datastructure.astar;

import com.badlogic.gdx.math.Vector2;
import rts.arties.datastructure.IMap;
import rts.arties.datastructure.IMapCell;
import rts.arties.scene.unit.steeringbehaviour.IEntity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A* algorithm implementation. Both manhattan and euclidean distances are implemented as heuristics.
 *
 * @param <T>
 * @author Toni Sagrista
 */
public class AStar<T extends IEntity> implements IAStar<T> {

    private final Set<IMapCell<T>> open = new HashSet<>();
    private final Set<IMapCell<T>> closed = new HashSet<>();

    private List<IMapCell<T>> path;

    private final IMap<T> map;

    public AStar(IMap<T> map) {
        this.map = map;
    }

    @Override
    public void clear() {
        open.clear();
        closed.clear();
    }

    @Override
    public List<IMapCell<T>> findPath(Vector2 ini, Vector2 end) {
        IMapCell<T> iniN = map.getCell(ini);
        IMapCell<T> endN = map.getCell(end);

        // Did we reach the end node?
        /**
         * -1 - unreachable 0 - searching 1 - found it!
         */
        int state = 0;

        // Start with iniN, put it in open
        open.add(iniN);
        while (state == 0) {
            state = aStarAlgorithm(endN);
        }

        if (state < 0)
            return null;
        else {
            // Make path!
            path = new ArrayList<>();
            IMapCell<T> current = endN;
            while (current != iniN) {
                path.add(current);
                current = current.parent();
            }
            path.add(iniN);
            return path;
        }

    }

    public int aStarAlgorithm(IMapCell<T> endN) {
        if (open.isEmpty())
            return -1;

        // Search for lowest f (g+h)
        IMapCell<T> lowest = null;
        for (IMapCell<T> current : open) {
            if (lowest == null || lowest.f() > current.f())
                lowest = current;
        }
        // Move from open to closed
        open.remove(lowest);
        closed.add(lowest);

        Set<IMapCell<T>> adjacents = lowest.findAdjacentCells();

        for (IMapCell<T> adjacent : adjacents) {
            if (!adjacent.isBlocked()) {
                float dist = distance(lowest.x(), lowest.y(), adjacent.x(), adjacent.y());
                if (adjacent == endN) {
                    adjacent.setParent(lowest);
                    return 1;
                } else if (!adjacent.isBlocked() && !closed.contains(adjacent)) {
                    float g = lowest.g() + dist;
                    if (open.contains(adjacent)) {
                        if (adjacent.g() > g) {
                            adjacent.setParent(lowest);
                            adjacent.setG(g);
                            adjacent.setH(heuristicManhattan(adjacent, endN));
                        }
                    } else {
                        open.add(adjacent);
                        adjacent.setParent(lowest);
                        adjacent.setG(g);
                        adjacent.setH(heuristicManhattan(adjacent, endN));
                    }
                }
            }
        }
        return 0;

    }

    public float distance(float x1, float y1, float x2, float y2) {
        return (float) Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

    public static float heuristicEuclidian(IMapCell n1, IMapCell n2) {
        return heuristicEuclidian(n1.x(), n1.y(), n2.x(), n2.y());
    }

    public static float heuristicManhattan(IMapCell n1, IMapCell n2) {
        return heuristicManhattan(n1.x(), n1.y(), n2.x(), n2.y());
    }

    public static float heuristicEuclidian(float x1, float y1, float x2, float y2) {
        return (float) Math.sqrt(Math.pow((x1 - x2), 2) + Math.pow((y1 - y2), 2));
    }

    public static float heuristicManhattan(float x1, float y1, float x2, float y2) {
        return Math.abs(x2 - x1) + Math.abs(y2 - y1);
    }

}
