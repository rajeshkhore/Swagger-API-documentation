package net.bflows.pagafatture.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.Map;

import net.bflows.pagafatture.entities.TimelineEntity.TimelineEntityTypeEnum;

@Getter
@Setter
public class TimelineReq {

	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private Long id;
	
	private Boolean skip;
	
	private Boolean reschedule;
	
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private String status;
	
	private String message;
	
	private Long clientId;
	
	private Long invoiceId;
	
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private Long actionId;
	
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private LocalDateTime createdDate;
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private LocalDateTime updatedDate;
	
	private Map<String, Object> actionJson;
	
	private TimelineEntityTypeEnum type;
}
