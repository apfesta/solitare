package com.andrewfesta.doublesolitare.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@SuppressWarnings("serial")
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class GameNotFoundException extends RuntimeException {

	public GameNotFoundException(String message) {
		super(message);
	}
	
}
