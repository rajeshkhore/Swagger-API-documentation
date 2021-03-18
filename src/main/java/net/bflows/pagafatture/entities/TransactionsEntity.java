package net.bflows.pagafatture.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "transactions")
@Getter
@Setter
public class TransactionsEntity extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", unique = true)
	private Long id;

	@Column(name = "AMOUNT", nullable = false, precision = 22, scale = 0)
	private BigDecimal amount;

	@Column(name = "METHOD_TYPE", length = 255)
	private String methodType;

	@Column(name = "PAYMENT_DATE")
	private LocalDateTime paymentDate;
	
	@Column(name = "CURRENCY",length = 10)
	private String currency;

	@OneToOne
	@JoinColumn(name = "INVOICES_ID")
	private Invoices invoices;

	

	/**
	 * Gets or Sets Method Type
	 */
	public enum TransactionsMethodTypeEnum {
		BANK("BANK"),

		CREDIT_CARD("CREDIT_CARD"),

		CASH("CASH"),
		
		PAYPAL("PAYPAL");

		private String value;

		TransactionsMethodTypeEnum(String value) {
			this.value = value;
		}

		@JsonValue
		public String getValue() {
			return value;
		}

		@JsonCreator
		public static TransactionsMethodTypeEnum fromValue(String value) {
			for (TransactionsMethodTypeEnum b : TransactionsMethodTypeEnum.values()) {
				if (b.value.equals(value)) {
					return b;
				}
			}
			throw new IllegalArgumentException("Unexpected value '" + value + "'");
		}
	}
}
