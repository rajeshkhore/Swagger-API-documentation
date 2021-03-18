package net.bflows.pagafatture.web.rest.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class UnauthorizedAccessException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final Status status;

	public UnauthorizedAccessException(Status status, String message) {
		super(message);
		this.status = status;
	}

	public Status getStatus() {
		return status;
	}
}
