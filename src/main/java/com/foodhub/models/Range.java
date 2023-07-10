package com.foodhub.models;

/**
 * simple immutable class; contains a min and a max
 *
 * @param <T>
 */
public class Range<T> {
	
	protected final T min, max;
	
	public Range(T min, T max) {
		
		this.min = min;
		this.max = max;
		
	}
	
	public T max() {
		
		return max;
		
	}
	
	public T min() {
		
		return min;
		
	}
	
}
