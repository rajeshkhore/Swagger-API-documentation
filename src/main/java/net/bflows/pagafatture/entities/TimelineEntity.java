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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "timeline")
@Getter
@Setter
public class TimelineEntity	 extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", unique = true)
	private Long id;
	
	@Column(name = "SKIP")
	private Boolean skip;
	
	@Column(name = "RESCHEDULE")
	private Boolean reschedule;
	
	@Column(name = "STATUS")
	private String status;
	
	@Column(name = "MESSAGE")
	private String message;
	
	@ManyToOne
	@JoinColumn(name = "CLIENT_ID")
	private ClientEntity clientEntity;
	
	@ManyToOne
	@JoinColumn(name = "INVOICE_ID")
	private Invoices invoice;
	
	@OneToOne
	@JoinColumn(name ="ACTION_ID")
	private ActionEntity actionEntity;
	
	
	@Column(name = "TYPE")
	private String type;
	
	@Column(name = "ACTION_JSON", nullable = false)
	private String actionJson;
	

	public enum TimelineEntityTypeEnum {
		ACTION("ACTION"),
		
		INVOICE_STATE_CHANGED("INVOICE_STATE_CHANGED"),
		
		TRANSACTION("TRANSACTION");

		private String value;

		TimelineEntityTypeEnum(String value) {
			this.value = value;
		}

		@JsonValue
		public String getValue() {
			return value;
		}

		@JsonCreator
		public static TimelineEntityTypeEnum fromValue(String value) {
			for (TimelineEntityTypeEnum b : TimelineEntityTypeEnum.values()) {
				if (b.value.equals(value)) {
					return b;
				}
			}
			throw new IllegalArgumentException("Unexpected value '" + value + "'");
		}
	}

}
