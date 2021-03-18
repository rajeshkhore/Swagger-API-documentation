package net.bflows.pagafatture.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "contact")
@Getter
@Setter
public class ContactsEntity extends BaseEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", unique = true)
    private Long id;
    
    @Column(name = "NAME", nullable = false , length = 255)
    private String name;
    
    @Column(name = "EMAIL", nullable = false , length = 255)
    private String email;
    
    @Column(name = "PHONE", nullable = true , length = 255)
    private String phone;
    
    @Column(name = "ROLE", nullable = true , length = 255)
    private String role;

    @Column(name = "DELETED")
	private Boolean deleted;
    
    @ManyToOne
    @JoinColumn(name = "CLIENT_ID")
    @JsonIgnore
    private ClientEntity clientEntity;
    
    
    @Column(name = "LAST_NAME", nullable = true , length = 255)
    private String lastName;
    
    
    /**
	 * Gets or Sets Method Type
	 */
	public enum ContactsRoleEnum {
		SALES("SALES"),

		PAYER("PAYER"),

		ACCOUNTING("ACCOUNTING"),
		
		PURCHASER("PURCHASER"),
		
		ADMIN("ADMIN");

		private String value;

		ContactsRoleEnum(String value) {
			this.value = value;
		}

		@JsonValue
		public String getValue() {
			return value;
		}

		@JsonCreator
		public static ContactsRoleEnum fromValue(String value) {
			for (ContactsRoleEnum b : ContactsRoleEnum.values()) {
				if (b.value.equals(value)) {
					return b;
				}
			}
			throw new IllegalArgumentException("Unexpected value '" + value + "'");
		}
	}
    
}
