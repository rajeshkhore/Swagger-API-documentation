package net.bflows.pagafatture.model;

import java.util.List;

import javax.validation.constraints.Email;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.annotation.JsonIgnore;

import net.bflows.pagafatture.model.view.WorkflowView;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;
@ApiModel(description = "Workflow Entity")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-10-01T16:42:43.418892+02:00[Europe/Rome]")
@Getter
@Setter
@JsonView(value= {WorkflowView.WorkflowWithoutAction.class})
public class WorkflowReq {

	@JsonView(value = { WorkflowView.WorkflowWithoutAction.class})
	private Long id;
	
	@JsonView(value = { WorkflowView.WorkflowWithoutAction.class})
	private String name;
	
	@JsonIgnore
	private Boolean isDeleted;
	
	@Email
	@JsonView(value = { WorkflowView.WorkflowWithoutAction.class})
	private String email;

	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	@JsonView(value = { WorkflowView.WorkflowWithoutAction.class})
	private Boolean defaultWorkflow;
	
	@JsonView(value = { WorkflowView.WorkflowWithoutAction.class})
	private Integer minimumContactDelay;

	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	@JsonView(value = { WorkflowView.WorkflowWithoutAction.class})
	private LocalDateTime createdDate;

	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	@JsonView(value = { WorkflowView.WorkflowWithoutAction.class})
	private LocalDateTime updatedDate;
	
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	@JsonView(value = { WorkflowView.WorkflowWithAction.class})
	private List<ActionReq> actions;
	
}
