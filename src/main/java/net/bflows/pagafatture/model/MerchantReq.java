package net.bflows.pagafatture.model;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonView;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;
import net.bflows.pagafatture.model.view.ClientView;

/**
 * Merchant Entity
 */
@ApiModel(description = "Merchant Entity")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-09-04T16:42:43.418892+02:00[Europe/Rome]")
@Getter
@Setter
@JsonView(value= {ClientView.InvoicesView.class})
public class MerchantReq {
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotNull
    @NotEmpty
    private String name;

    private String vatNumber;

    private String directUrl;

    private String addressStreet;

    private String addressCAP;

    private String addressCity;

    private String addressProvince;

    private String addressState;

    private String email;

    private String bankName;

    private String bankCompanyName;

    private String iban;

    private String swift;

    private Boolean integrations;
    
    private String phone;
    
    private String website;

    private Integer defaultPaymentDays;
	
   

	/**
     * Gets or Sets merchantstate
     */
    public enum MerchantstateEnum {
        ACTIVE("ACTIVE"),

        INACTIVE("INACTIVE");

        private String value;

        MerchantstateEnum(String value) {
            this.value = value;
        }

        @JsonValue
        public String getValue() {
            return value;
        }
    }


  
}
