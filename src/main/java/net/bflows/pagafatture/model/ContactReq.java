package net.bflows.pagafatture.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import net.bflows.pagafatture.entities.ContactsEntity.ContactsRoleEnum;

@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-09-04T16:42:43.418892+02:00[Europe/Rome]")
@Getter
@Setter
public class ContactReq {
@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private Long id;
    
    private String name;
    private String email;
    private String phone;
    private ContactsRoleEnum role;
    private String lastName;

}
