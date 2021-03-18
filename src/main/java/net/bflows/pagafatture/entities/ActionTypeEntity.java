package net.bflows.pagafatture.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "ACTION_TYPE")
@Getter
@Setter
public class ActionTypeEntity extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", unique = true)
	private Long id;

	@Column(name = "NAME", nullable = false, length = 255)
	private String name;

	@Column(name = "JSON")
	private String json;

}
