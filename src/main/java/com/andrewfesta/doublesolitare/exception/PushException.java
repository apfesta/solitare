package com.andrewfesta.doublesolitare.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@SuppressWarnings("serial")
@ResponseStatus(value = HttpStatus.FORBIDDEN)
public class PushException extends RuntimeException {

	public PushException(String message) {
		super(message);
	}

}
