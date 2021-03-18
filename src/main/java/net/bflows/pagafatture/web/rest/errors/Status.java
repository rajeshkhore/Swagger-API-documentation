package net.bflows.pagafatture.web.rest.errors;

public enum Status {
	SUCCESS("1"), VALIDATION_ERROR("2"), EXCEPTION_ERROR("3") , NOT_FOUND("4") , BAD_REQUEST("5") , UNAUTHORIZED("6");

    private String action;

    public String getAction() {
        return this.action;
    }

    private Status(String action) {
        this.action = action;
    }
}
