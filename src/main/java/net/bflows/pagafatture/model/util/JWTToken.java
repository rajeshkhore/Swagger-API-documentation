package net.bflows.pagafatture.model.util;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import net.bflows.pagafatture.entities.UserEntity;

/**
 * JWTToken
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-09-04T16:53:00.396048+02:00[Europe/Rome]")
@Getter
@Setter
public class JWTToken   {
  @JsonProperty("id_token")
  private String idToken;

  @JsonProperty("user")
  private UserEntity user;


}

