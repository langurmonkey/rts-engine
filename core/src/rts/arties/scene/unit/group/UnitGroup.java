package rts.arties.scene.unit.group;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import rts.arties.datastructure.geom.Vector3;
import rts.arties.scene.unit.steeringbehaviour.IEntity;
import rts.arties.scene.unit.steeringbehaviour.IGroup;
import rts.arties.util.Vector3Pool;

import java.util.*;

/**
 * Represents a unit group.
 *
 * @author Toni Sagrista
 */
public class UnitGroup implements IGroup, List<IEntity> {

    private final List<IEntity> group;
    private static final UnitComparatorByPosition comp = new UnitComparatorByPosition();

    private Vector3 pos;

    public UnitGroup() {
        super();
        this.group = new LinkedList<>();
        this.pos = Vector3Pool.getObject();
    }

    @Override
    public Vector3 pos() {
        return pos;
    }

    public void update() {
        pos.setZero();
        for (IEntity unit : group) {
            pos.add(unit.pos());
        }
        pos.scl(1f / group.size());
    }

    public void render(ShapeRenderer renderer) {
        if (!pos.isZero()) {
            // Render center
            renderer.setColor(0f, .3f, 1f, 1f);
            renderer.circle(pos.x, pos.y, 3);
            // Render lines to all units
            renderer.setColor(0f, 1f, .3f, 1f);
            for (IEntity unit : group) {
                renderer.line(pos.x, pos.y, unit.pos().x, unit.pos().y);
            }
        }
    }

    public void sortByPosition() {
        Collections.sort(group, comp);
    }

    @Override
    public boolean add(IEntity e) {
        e.group(this);
        return group.add(e);
    }

    @Override
    public int size() {
        return group.size();
    }

    @Override
    public boolean isEmpty() {
        return group.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return group.contains(o);
    }

    @Override
    public Iterator<IEntity> iterator() {
        return group.iterator();
    }

    @Override
    public Object[] toArray() {
        return group.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return group.toArray(a);
    }

    @Override
    public boolean remove(Object o) {
        return remove((IEntity) o);
    }

    public boolean remove(IEntity u) {
        u.group(null);
        return group.remove(u);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return group.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends IEntity> c) {
        for (IEntity u : c) {
            u.group(this);
        }
        return group.addAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        for (Object o : c) {
            ((IEntity) o).group(null);
        }
        return group.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return group.retainAll(c);
    }

    @Override
    public void clear() {
        for (IEntity u : group) {
            u.group(null);
        }
        group.clear();
    }

    @Override
    public boolean addAll(int index, Collection<? extends IEntity> c) {
        for (IEntity u : c) {
            u.group(this);
        }
        return group.addAll(index, c);
    }

    @Override
    public IEntity get(int index) {
        return group.get(index);
    }

    @Override
    public IEntity set(int index, IEntity element) {
        element.group(this);
        return group.set(index, element);
    }

    @Override
    public void add(int index, IEntity element) {
        element.group(this);
        group.add(index, element);

    }

    @Override
    public IEntity remove(int index) {
        assert index >= 0 && index < group.size() : "Index out of bounds: " + index;
        group.get(index).group(null);
        return group.remove(index);

    }

    @Override
    public int indexOf(Object o) {
        return group.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return group.lastIndexOf(o);
    }

    @Override
    public ListIterator<IEntity> listIterator() {
        return group.listIterator();
    }

    @Override
    public ListIterator<IEntity> listIterator(int index) {
        return group.listIterator(index);
    }

    @Override
    public List<IEntity> subList(int fromIndex, int toIndex) {
        return group.subList(fromIndex, toIndex);
    }

    @Override
    public Spliterator<IEntity> spliterator() {
        return null;
    }

    private static class UnitComparatorByPosition implements Comparator<IEntity> {

        @Override
        public int compare(IEntity o1, IEntity o2) {
            int comp = Float.compare(o1.pos().y, o2.pos().y);
            if (comp == 0) {
                comp = Float.compare(o1.pos().x, o2.pos().x);
            }
            return comp;
        }
    }

}
