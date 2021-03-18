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
@Table(name = "WORKFLOW")
@Getter
@Setter
public class WorkflowEntity extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", unique = true)
	private Long id;

	@Column(name = "NAME", nullable = false, length = 255)
	private String name;

	@ManyToOne
	@JoinColumn(name = "MERCHANT_ID")
	private MerchantEntity merchantEntity;

	@Column(name = "IS_DELETED")
	private Boolean isDeleted;

	@Column(name = "EMAIL", nullable = false, length = 255)
	private String email;

	@Column(name = "DEFAULT_WORKFLOW")
	private Boolean defaultWorkflow;

	@Column(name = "MINIMUM_CONTACT_DELAY")
	private Integer minimumContactDelay;

}
