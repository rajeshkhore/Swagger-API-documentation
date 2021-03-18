package net.bflows.pagafatture.model;

import java.time.LocalDateTime;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActionTypeReq {

	private Long id;

	private String name;
	
	private Map<String, Object> json;

	LocalDateTime createdDate;

	LocalDateTime updatedDate;
}
