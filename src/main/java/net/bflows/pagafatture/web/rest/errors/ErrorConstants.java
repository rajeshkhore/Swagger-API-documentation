package net.bflows.pagafatture.web.rest.errors;

import java.net.URI;

public final class ErrorConstants {

    public static final String ERR_CONCURRENCY_FAILURE = "error.concurrencyFailure";
    public static final String ERR_VALIDATION = "error.validation";
    public static final String PROBLEM_BASE_URL = "https://www.jhipster.tech/problem";
    public static final URI DEFAULT_TYPE = URI.create(PROBLEM_BASE_URL + "/problem-with-message");
    public static final URI CONSTRAINT_VIOLATION_TYPE = URI.create(PROBLEM_BASE_URL + "/constraint-violation");
    public static final URI INVALID_PASSWORD_TYPE = URI.create(PROBLEM_BASE_URL + "/invalid-password");
    public static final URI EMAIL_ALREADY_USED_TYPE = URI.create(PROBLEM_BASE_URL + "/email-already-used");
    public static final URI LOGIN_ALREADY_USED_TYPE = URI.create(PROBLEM_BASE_URL + "/login-already-used");
    
    public static final String BADREQUEST_ERROR_EXAMPLE="{\"data\": null,"
    		+"\"status\": \"VALIDATION_ERROR\",\"message\": \"string\""
			+ "}";
    public static final String NOTFOUND_ERROR_EXAMPLE="{\"data\": null,"
    		+"\"status\": \"NOT_FOUND\",\"message\": \"string\""
			+ "}";
    public static final String UNAUTHORIZED_ERROR_EXAMPLE="{\"data\": null,"
    		+"\"status\": \"UNAUTHORIZED\",\"message\": \"string\""
			+ "}";

    private ErrorConstants() {
    }
}
