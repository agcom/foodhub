package com.foodhub.models;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.Objects;

/**
 * BPServer's user data
 */
public class UserData {
	
	protected String firstName, lastName, email, phone, password; //email unique
	
	public UserData(String firstName, String lastName, String email, String phone, String password) {
		
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.phone = phone;
		this.password = password;
		
	}
	
	public UserData(String firstName, String lastName, String email, String phone) {
		
		this(firstName, lastName, email, phone, null);
		
	}
	
	public UserData(String email, String password) {
		
		this(null, null, email, null, password);
		
	}
	
	public UserData(JSONObject json) {
		
		this(json.getString("first_name"), json.getString("last_name"), json.getString("email"), json.getString("phone"));
		
	}
	
	/**
	 * @return firstName lastName; ignores null fields
	 */
	public String getFullName() {
		
		String fullName = (firstName != null ? firstName + " " : "") + (lastName != null ? lastName : "");
		
		return !fullName.isEmpty() ? fullName : null;
		
	}
	
	public String getFirstName() {
		return firstName;
	}
	
	public String getLastName() {
		return lastName;
	}
	
	public String getEmail() {
		return email;
	}
	
	public String getPhone() {
		return phone;
	}
	
	public String getPassword() {
		return password;
	}
	
	@Override
	public String toString() {
		
		String[] elements = {firstName, lastName, email, phone, password};
		
		return Arrays.stream(elements).filter(Objects::nonNull).reduce((s1, s2) -> s1 + ", " + s2).orElse("Null");
		
	}
	
}
