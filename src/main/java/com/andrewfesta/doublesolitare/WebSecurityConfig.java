package com.andrewfesta.doublesolitare;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import com.andrewfesta.doublesolitare.service.impl.UserService;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig 
	extends WebSecurityConfigurerAdapter {
	
	@Autowired
	UserService userService;
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.anonymous().and()
			.userDetailsService(userService)
			.authorizeRequests()
				.antMatchers("/css/**", "/js/**", "/img/**", "/webjars/**", 
						"/manifest.json","/service-worker.js").permitAll().and()
			.authorizeRequests()
				.antMatchers("/","/api/**").permitAll();
	}

}
