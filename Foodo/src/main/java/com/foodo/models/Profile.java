package com.foodo.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Profile extends User {
	
	private String imageUrl;
	private final List<Address> addresses;
	
	public Profile(User user, String imageUrl, Collection<Address> addresses) {
		
		super(user.getFirstName(), user.getLastName(), user.getPhone(), user.getEmail(), user.getPassword());
		this.imageUrl = imageUrl;
		this.addresses = new ArrayList<>();
		if (addresses != null) this.addresses.addAll(addresses);
		
	}
	
	public String getImageUrl() {
		return imageUrl;
	}
	
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	
	public List<Address> getAddresses() {
		return addresses;
	}
	
	public String getFullName() {
		
		if (firstName == null && lastName == null) return null;
		else if (firstName == null) return firstName;
		else if (lastName == null) return lastName;
		else return firstName + " " + lastName;
		
	}
	
}
