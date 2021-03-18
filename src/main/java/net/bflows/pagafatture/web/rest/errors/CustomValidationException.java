package net.bflows.pagafatture.web.rest.errors;


public class CustomValidationException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    private final Status status;
    
    public CustomValidationException(Status status,String message) {
		super(message);
		this.status = status;
	}

	public Status getStatus() {
		return status;
	}
}

