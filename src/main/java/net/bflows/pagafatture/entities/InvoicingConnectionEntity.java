package net.bflows.pagafatture.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "invoicing_connection")
@Setter
@Getter
public class InvoicingConnectionEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", unique = true)
    private Long id;
    
    @Column(name = "TYPE_ID",length = 255)
    @JsonIgnore
    private Long typeId;

    @Column(name = "API_KEY",length = 255)
    private String apiKey;
    
    @Column(name = "API_UID",length = 255)
    private String apiUid;
    
    @OneToOne
    @JoinColumn(name = "MERCHANT_ID")
    private MerchantEntity merchantEntity;
    
    @OneToOne
    @JoinColumn(name = "INVOICING_ID")
    private InvoicingEntity invoicing;

    
}
