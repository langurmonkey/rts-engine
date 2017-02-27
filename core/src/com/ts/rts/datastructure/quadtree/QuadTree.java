/*
 * @(#)QuadTree.java Copyright (c) 1996-2010 by the original authors of JHotDraw and all its contributors. All rights reserved. The copyright of this software is owned by the authors and contributors of the JHotDraw project ("the copyright holders"). You may not use, copy or modify this software,
 * except in accordance with the license agreement you entered into with the copyright holders. For details see accompanying license terms.
 */

package com.ts.rts.datastructure.quadtree;

import java.util.HashSet;
import java.util.Set;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.ts.rts.datastructure.IMap;
import com.ts.rts.datastructure.IMapCell;
import com.ts.rts.datastructure.Pair;
import com.ts.rts.scene.unit.IBoundsObject;

/**
 * Quadtree implementation as an {@link IMap}. A Quadtree allows to quickly find an object on a two-dimensional space.
 * It also makes pathfinding super-fast. However, when objects change cells the tree must be re-balanced, and it may
 * become a costly operation.
 * 
 * @author Toni Sagrista
 */
public class QuadTree<T extends IBoundsObject> implements IMap<T> {

	public QuadNode<T> root;

	/*
	 * Minimum node size
	 */
	public int minSize = 20;

	public QuadTree(int minSize) {
		this.minSize = minSize;
	}

	public QuadTree(QuadNode<T> root) {
		this.root = root;
	}

	public void add(T o) {
		if (root.bounds.contains(o.bounds())) {
			root.add(o);
		} else {
			// Not in area!
		}
	}

	public void reorganize() {
		root.join();
	}

	public void remove(T o) {
		root.remove(o);
	}

	public QuadNode<T> getCell(float x, float y) {
		return getCell(new Vector2(x, y));
	}

	public QuadNode<T> getCell(Vector2 p) {
		return root.getNode(p);
	}

	public void clearPath() {
		root.clearPath();
	}

	/**
	 * Returns a set of all the leaf nodes which contain the given object
	 * 
	 * @param o
	 * @return
	 */
	public Set<IMapCell<T>> findNodesWith(T o) {
		return root.findNodesWith(o);
	}

	/**
	 * Returns a set of all the nearby objects of this given point. Gets objects of the node the point is in and its
	 * adjacent nodes
	 * 
	 * @param p
	 * @return
	 */
	public Set<T> findNearbyObjects(Vector2 p) {
		Set<T> set = new HashSet<T>();
		QuadNode<T> n = getCell(p);
		set.addAll(n.objects);

		for (Pair<QuadNode<T>, Float> pair : n.adjacentNodes) {
			QuadNode<T> node = pair.first;
			set.addAll(node.objects);
		}

		return set;

	}

	/**
	 * Finds all nearby blocked nodes to the given position
	 * 
	 * @param p
	 * @return
	 */
	public Set<IMapCell<T>> findNearbyBlockedNodes(Vector2 p) {
		Set<IMapCell<T>> blocked = new HashSet<IMapCell<T>>();
		QuadNode<T> n = getCell(p);
		if (n.isBlocked()) {
			blocked.add(n);
		}
		for (Pair<QuadNode<T>, Float> pair : n.adjacentNodes) {
			QuadNode<T> node = pair.first;
			if (node.isBlocked())
				blocked.add(node);
		}
		return blocked;
	}

	/**
	 * Checks if the given point is in a blocked node
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean isInBlocked(float x, float y) {
		QuadNode<T> n = getCell(x, y);
		return n != null && n.isBlocked();
	}

	/**
	 * Gets the blocked node this point is in, if it is in a blocked node
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public IMapCell<T> getBlockedNode(float x, float y) {
		QuadNode<T> n = getCell(x, y);
		return (n != null && n.isBlocked()) ? n : null;
	}

	/**
	 * Checks if the given rectangle overlaps with a blocked node
	 * 
	 * @param r
	 * @return
	 */
	public boolean overlapsWithBlocked(Rectangle r) {
		return isInBlocked(r.x, r.y) || isInBlocked(r.x + r.width, r.y) || isInBlocked(r.x + r.width, r.y + r.height)
				|| isInBlocked(r.x, r.y + r.height);
	}

}
