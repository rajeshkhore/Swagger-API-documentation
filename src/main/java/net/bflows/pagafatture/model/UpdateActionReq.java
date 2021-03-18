package net.bflows.pagafatture.model;


import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateActionReq {

	private String name;

	private Long actionTypeId;

	private Map<String, Object> actionJson;

	private Integer triggerDays;
}

