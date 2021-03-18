package net.bflows.pagafatture.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "ACTION")
@Getter
@Setter
public class ActionEntity extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", unique = true)
	private Long id;

	@Column(name = "NAME", nullable = false, length = 255)
	private String name;

	@ManyToOne
	@JoinColumn(name = "ACTION_TYPE_ID")
	private ActionTypeEntity actionType;

	@Column(name = "ACTION_JSON", nullable = false)
	private String actionJson;

	@Column(name = "TRIGGER_DAYS")
	private Integer triggerDays;

	@ManyToOne
	@JoinColumn(name = "WORKFLOW_ID")
	private WorkflowEntity workflowEntity;

	@Column(name = "IS_DELETED")
	private Boolean isDeleted;
}
