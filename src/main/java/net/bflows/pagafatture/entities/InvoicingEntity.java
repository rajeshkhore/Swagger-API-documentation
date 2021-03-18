package net.bflows.pagafatture.entities;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import net.bflows.pagafatture.converter.IntegrationtypeEnumConverter;
import net.bflows.pagafatture.enums.IntegrationtypeEnum;

@Entity
@Table(name="invoicing")
@Setter
@Getter
public class InvoicingEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column
	private String name;

	@Column
	private String link;

	@Column
	private String helptext;
	
	@Convert(converter = IntegrationtypeEnumConverter.class)
	@Column
	private IntegrationtypeEnum integrationtype;

}