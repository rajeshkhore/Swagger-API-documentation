package net.bflows.pagafatture.entities;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "client")
@Getter
@Setter
public class ClientEntity extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", unique = true)
	private Long id;

	@Column(name = "vatNumber", length = 15)
	private String vatNumber;

	@Column(name = "NAME", length = 255)
	private String name;
	@Column(name = "EX_ID_CLIENT", length = 255)
	private String exIdClient;
	@Column(name = "CF", length = 255)
	private String cf;
	@Column(name = "ADDRESS", length = 255)
	private String address;
	@Column(name = "ZIP_CODE", length = 255)
	private String zipCode;
	@Column(name = "CITY", length = 255)
	private String city;
	@Column(name = "PROVINCE", length = 255)
	private String province;
	@Column(name = "ADDRESS_EXTRA", length = 255)
	private String addressExtra;
	@Column(name = "PA_CODE", length = 255)
	private String paCode;
	@Column(name = "PEC", length = 255)
	private String pec;
	@Column(name = "EMAIL", length = 255)
	private String email;
	@Column(name = "COUNTRY", length = 255)
	private String country;

	@Column(name = "DELETED")
	private Boolean deleted;
	   
	@ManyToOne
	@JoinColumn(name = "MERCHANT_ID")
	@JsonIgnore
	private MerchantEntity merchantEntity;
	
	@Column(name = "DEFAULT_PAYMENT_DAYS")
	private Integer defaultPaymentDays;
	
	
	@Column(name = "TELEPHONE", length = 255)
	private String telephone;
	
	@OneToOne
	@JoinColumn(name = "WORKFLOW_ID")
	private WorkflowEntity workflowEntity;

	
	
	
	
}
