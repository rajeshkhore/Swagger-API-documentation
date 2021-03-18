package net.bflows.pagafatture.model;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class UserReq {
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotNull
    @NotEmpty
    private String firstName;

    @NotNull
    @NotEmpty
    private String lastName;

    @NotNull
    @Email
    private String email;

    @NotNull
    @NotEmpty
    private String password;

    @NotNull
    @NotEmpty
    private String confPassword;

    private String phone;

    private Long roleId;

    @NotNull
    @NotEmpty
    private String companyName;

      
    /**
     * User Status
     */
    public enum UserStatusEnum {
        ACTIVE("ACTIVE"),

        TOBECONFIRMED("TOBECONFIRMED"),

        INACTIVE("INACTIVE");

        private String value;

        UserStatusEnum(String value) {
            this.value = value;
        }

        @JsonValue
        public String getValue() {
            return value;
        }
    }
}
