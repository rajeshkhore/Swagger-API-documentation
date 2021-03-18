package net.bflows.pagafatture.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

/**
 * connection information for merchant  
 */
@ApiModel(description = "connection information for merchant  ")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-09-04T16:42:43.418892+02:00[Europe/Rome]")
@Getter
@Setter
public class InvoicingConnectionReq   {
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private Long id;

  @JsonProperty("typeId")
  private Long typeId;

  @JsonProperty("apiKey")
  private String apiKey;


  @JsonProperty("apiUid")
  private String apiUid;

}

