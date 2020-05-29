package com.andrewfesta.doublesolitare.model;

public class User {
	
	Integer id;
	String username;

	public User() {
		super();
	}

	public User(Integer id) {
		super();
		this.id = id;
		this.username = "User "+id;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof User) {
			User user = (User)obj;
			return id.equals(user.id);
		}
		return false;
	}
	
	
}
