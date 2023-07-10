package com.foodhub.models;

/**
 * immutable class Address max associate with {@link Restaurant} and {@link Profile}
 */
public class Address {
	
	private final String address;
	
	/**
	 * @param address which uses ',' as primary separator
	 */
	public Address(String address) {
		
		this.address = address;
		
	}
	
	/**
	 * @return address string which uses ',' as primary separator
	 */
	public String getAddress() {
		
		return address;
		
	}
	
	/**
	 * @return array of string that is made by splitting address with '<space>,<space>' regex
	 */
	public String[] separate() {
		
		return address.split("\\s*,\\s*");
		
	}
	
}
