package com.andrewfesta.doublesolitare.service.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.andrewfesta.doublesolitare.model.User;

@Component
public class UserService implements UserDetailsService {

	Map<UserDetails, User> users = new HashMap<>();
	Map<String, UserDetails> usersByUsername = new HashMap<>();
	AtomicInteger userIdSequence = new AtomicInteger(0);
	
	User createGuest() {
		User user = new User(userIdSequence.incrementAndGet());
		@SuppressWarnings("deprecation")
		org.springframework.security.core.userdetails.User.UserBuilder userBuilder = 
				org.springframework.security.core.userdetails.User.withDefaultPasswordEncoder();
		UserDetails userDetails = userBuilder
				.username(user.getUsername())
				.password("password")
				.authorities(new SimpleGrantedAuthority("GUEST_ROLE"))
				.build();
		user.setPrincipal(userDetails);
		users.put(userDetails, user);
		usersByUsername.put(user.getUsername(), userDetails);
		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
				userDetails, "password");
		SecurityContextHolder.getContext().setAuthentication(authentication);
		return user;
	}
	
	public User getUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication instanceof AnonymousAuthenticationToken) {
			return createGuest();
		}
		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		User user = users.get(userDetails);
		return user;
	}
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return usersByUsername.get(username);
	}

}
