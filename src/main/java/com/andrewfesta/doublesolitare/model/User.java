package com.andrewfesta.doublesolitare.model;

public class User {
	
	Integer id;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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
