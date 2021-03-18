package net.bflows.pagafatture.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "invoices")
@Getter
@Setter
public class Invoices extends BaseEntity {
    


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", unique = true)
    private Long id;

    @Column(name = "AMOUNT_GROSS", nullable = false, precision = 22, scale = 0)
    private BigDecimal amountGross;

    @Column(name = "AMOUNT_NET", precision = 22, scale = 0)
    private BigDecimal amountNet;

    @Column(name = "EXTERNAL_ID", length = 255)
    private String externalId;

    @Column(name = "EXTERNAL_REF", nullable = false,unique=false, length = 255)
    private String externalRef;

    @Column(name = "LINK_DOC", nullable = false, length = 255)
    private String linkDoc;

    @Column(name = "INVOICE_NUMBER", nullable = false,length = 255)
    private String invoiceNumber;

    @Column(name = "CURRENCY",length = 255)
    private String currency;

    @Column(name = "INVOICE_STATE", length = 30, nullable = false)
    private String invoiceState;
    
    @ManyToOne
    @JoinColumn(name = "CLIENT_ID")
    private ClientEntity clientEntity;
    
  
	@Column(name = "EXPECTED_DATE")
	private LocalDateTime expectedDate;
	
	@Column(name = "DUE_DATE")
	private LocalDateTime dueDate;
	
	@Column(name = "EXTERNAL_CREATED_DATE")
	private LocalDateTime externalCreatedDate;


    
     /**
       * Gets or Sets invoiceState
       */
      public enum InvoiceStateEnum {
        DUE("DUE"),
        
        SENT("SENT"),
        
        OVERDUE("OVERDUE"),
        
        PAID("PAID"),
        
        WRITE_OFF("WRITE_OFF"),
        
        DISPUTED("DISPUTED");

        private String value;

        InvoiceStateEnum(String value) {
          this.value = value;
        }

        @JsonValue
        public String getValue() {
          return value;
        }

        @Override
        public String toString() {
          return String.valueOf(value);
        }

        @JsonCreator
        public static InvoiceStateEnum fromValue(String value) {
          for (InvoiceStateEnum b : InvoiceStateEnum.values()) {
            if (b.value.equals(value)) {
              return b;
            }
          }
          throw new IllegalArgumentException("Unexpected value '" + value + "'");
        }
      }


	

      
}
