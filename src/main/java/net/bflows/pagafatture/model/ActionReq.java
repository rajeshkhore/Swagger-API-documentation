package net.bflows.pagafatture.model;

import java.time.LocalDateTime;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Getter;
import lombok.Setter;
import net.bflows.pagafatture.model.view.ActionView;
import net.bflows.pagafatture.model.view.WorkflowView;

@Getter
@Setter
@JsonView(value = { WorkflowView.WorkflowWithAction.class,ActionView.ActionWithoutTriggerDays.class})
public class ActionReq {

	@JsonView(value = { ActionView.ActionWithoutTriggerDays.class,WorkflowView.WorkflowWithAction.class})
	private Long id;

	@JsonView(value = { ActionView.ActionWithoutTriggerDays.class,WorkflowView.WorkflowWithAction.class})
	private String name;

	@JsonView(value = { ActionView.ActionWithoutTriggerDays.class,WorkflowView.WorkflowWithAction.class})
	private Long actionTypeId;

	@JsonView(value = { ActionView.ActionWithoutTriggerDays.class,WorkflowView.WorkflowWithAction.class})
	private Map<String, Object> actionJson;

	@JsonView(value = { WorkflowView.WorkflowWithAction.class})
	private Integer triggerDays;
	
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	@JsonView(value = { ActionView.ActionWithoutTriggerDays.class,WorkflowView.WorkflowWithAction.class})
	private String actionTypeName;

	@JsonIgnore
	private Boolean isDeleted;
	@JsonIgnore
	private LocalDateTime createdDate;
	@JsonIgnore
	private LocalDateTime updatedDate;
}
