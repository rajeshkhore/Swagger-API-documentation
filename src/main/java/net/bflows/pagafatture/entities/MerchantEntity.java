package net.bflows.pagafatture.entities;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.Email;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="merchant")
@Setter
@Getter
public class MerchantEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "NAME", length = 255,nullable = false)
	private String name;

	@Column(name = "VAT_NUMBER", length = 255)
	private String vatNumber;

	@Column(name = "DIRECT_URL", length = 255)
	private String directUrl;

	@Email
	@Column(name = "EMAIL", length = 255)
	private String email;

	@Column(name = "ADDRESS_STREET", length = 255)
	private String addressStreet;

	@Column(name = "ADDRESS_CAP", length = 255)
	private String addressCAP;

	@Column(name = "ADDRESS_CITY", length = 255)
	private String addressCity;

	@Column(name = "ADDRESS_PROVINCE", length = 255)
	private String addressProvince;

	@Column(name = "ADDRESS_STATE", length = 255)
	private String addressState;

	@Column(name = "BANK_NAME", length = 255)
	private String bankName;

	@Column(name = "BANK_COMPANY_NAME", length = 255)
	private String bankCompanyName;

	@Column(name = "IBAN", length = 255)
	private String iban;

	@Column(name = "SWIFT", length = 255)
	private String swift;  

	@Column(name = "MERCHANT_STATE", length = 255)
	private String merchantstate;
	
	@OneToOne(mappedBy = "merchant", cascade = CascadeType.ALL)
	@JsonIgnore
    private UserEntity userEntity;

	@Column(name = "integrations")
	private Boolean integrations;
	
	@Column(name = "phone")
	private String phone;
	
	
	@Column(name = "DEFAULT_PAYMENT_DAYS")
	private Integer defaultPaymentDays;
	
	@Column(name="WEBSITE")
	private String website;
    
}
