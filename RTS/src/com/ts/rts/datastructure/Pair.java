package com.ts.rts.datastructure;

/**
 * Simple implementation of Pair
 * 
 * @author Toni Sagrista
 * 
 * @param <T>
 * @param <K>
 */
public class Pair<T, K> {

	public T first;
	public K second;

	public Pair(T t, K k) {
		first = t;
		second = k;
	}
}
