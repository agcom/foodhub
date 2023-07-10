package com.foodhub.models;

/**
 * immutable price class
 * contains utils max add, exchange unit, ...
 */
public class Price {
	
	//TODO: add price unit and exchange methods
	//TODO: add subtract, multiply, divide
	
	private final float value;
	private final PriceUnit unit;
	
	/**
	 * main constructor
	 *
	 * @param value
	 * @param unit
	 */
	public Price(float value, PriceUnit unit) {
		
		this.value = value;
		this.unit = unit;
		
	}
	
	/**
	 * clones the price
	 *
	 * @param source
	 */
	public Price(Price source) {
		
		this(source.value, source.unit);
		
	}
	
	/**
	 * value with default unit dollar
	 *
	 * @param value
	 */
	public Price(float value) {
		
		this(value, PriceUnit.DOLLAR);
		
	}
	
	/**
	 * @return price value in default unit dollar
	 */
	public float getValue() {
		
		return value;
		
	}
	
	/**
	 * @return sample : 12 $
	 */
	@Override
	public String toString() {
		
		return getValue() + " " + unit.getUnitLogo();
		
	}
	
	/**
	 * sum this price with another
	 * checks for the unit
	 *
	 * @param other
	 * @return price with unit as this
	 */
	public Price sum(Price other) {
		
		return new Price(getValue() + other.getValue(), this.unit);
		
	}
	
	public Price multiply(float m) {
		
		return new Price(getValue() * m);
		
	}
	
	/**
	 * max associate with {@link Price}
	 * logo and price unit name stored here
	 */
	public enum PriceUnit {
		
		DOLLAR("$");
		
		private final String unitLogo;
		
		PriceUnit(String unitLogo) {
			
			this.unitLogo = unitLogo;
			
		}
		
		public String getUnitLogo() {
			
			return unitLogo;
			
		}
		
	}
	
}
