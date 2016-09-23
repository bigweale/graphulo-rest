package edu.mit.ll.graphulo.rest;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public final class ResourceNotFoundException extends RuntimeException {

	private final String message;
	
	public ResourceNotFoundException() {
		this.message = "No message available";
	}
	
	public ResourceNotFoundException(String message) {
		this.message = message;
	}
	
	public String getMessage() {
		return message;
	}
}