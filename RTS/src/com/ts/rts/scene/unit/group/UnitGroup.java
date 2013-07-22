package com.ts.rts.scene.unit.group;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.ts.rts.scene.unit.PositionEntity;
import com.ts.rts.scene.unit.Unit;
import com.ts.rts.util.VectorPool;

/**
 * Represents a unit group.
 * 
 * @author Toni Sagrista
 * 
 */
public class UnitGroup extends PositionEntity implements List<Unit> {

	private List<Unit> group;

	private ShapeRenderer shapeRenderer;
	private static final UnitComparatorByPosition comp = new UnitComparatorByPosition();

	public UnitGroup(ShapeRenderer shapeRenderer) {
		super();
		this.group = new LinkedList<Unit>();
		this.shapeRenderer = shapeRenderer;
		this.pos = VectorPool.getObject();
	}

	public void update() {
		pos.zero();
		for (Unit unit : group) {
			pos.add(unit.pos);
		}
		pos.div(group.size());
	}

	public void render() {
		if (!pos.isZeroVector()) {
			shapeRenderer.circle(pos.x, pos.y, 3);
		}
	}

	public void sortByPosition() {
		Collections.sort(group, comp);
	}

	@Override
	public boolean add(Unit e) {
		e.group = this;
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
	public Iterator<Unit> iterator() {
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
		return remove((Unit) o);
	}

	public boolean remove(Unit u) {
		u.group = null;
		return group.remove(u);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return group.containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends Unit> c) {
		for (Unit u : c) {
			u.group = this;
		}
		return group.addAll(c);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		for (Object o : c) {
			((Unit) o).group = null;
		}
		return group.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return group.retainAll(c);
	}

	@Override
	public void clear() {
		for (Unit u : group) {
			u.group = null;
		}
		group.clear();
	}

	@Override
	public boolean addAll(int index, Collection<? extends Unit> c) {
		for (Unit u : c) {
			u.group = this;
		}
		return group.addAll(index, c);
	}

	@Override
	public Unit get(int index) {
		return group.get(index);
	}

	@Override
	public Unit set(int index, Unit element) {
		element.group = this;
		return group.set(index, element);
	}

	@Override
	public void add(int index, Unit element) {
		element.group = this;
		group.add(index, element);

	}

	@Override
	public Unit remove(int index) {
		assert index >= 0 && index < group.size() : "Index out of bounds: " + index;
		group.get(index).group = null;
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
	public ListIterator<Unit> listIterator() {
		return group.listIterator();
	}

	@Override
	public ListIterator<Unit> listIterator(int index) {
		return group.listIterator(index);
	}

	@Override
	public List<Unit> subList(int fromIndex, int toIndex) {
		return group.subList(fromIndex, toIndex);
	}

	private static class UnitComparatorByPosition implements Comparator<Unit> {

		@Override
		public int compare(Unit o1, Unit o2) {
			int comp = Float.compare(o1.pos.y, o2.pos.y);
			if (comp == 0) {
				comp = Float.compare(o1.pos.x, o2.pos.x);
			}
			return comp;
		}
	}

}
