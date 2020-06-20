package com.andrewfesta.doublesolitare.model;

import java.util.Collections;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class User extends org.springframework.security.core.userdetails.User {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1571362421820222821L;
	
	Integer id;

	public User(Integer id, String username) {
		super(username, "", Collections.singletonList(new SimpleGrantedAuthority("GUEST_ROLE")));
		this.id = id;
	}
	
	public User(Integer id) {
		this(id, "Guest "+id);
	}

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
