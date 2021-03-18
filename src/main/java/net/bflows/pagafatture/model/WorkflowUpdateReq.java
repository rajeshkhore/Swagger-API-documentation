package net.bflows.pagafatture.model;

import javax.validation.constraints.Email;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WorkflowUpdateReq {

	private String name;
	@Email
	private String email;
	private Integer minimumContactDelay;
}
