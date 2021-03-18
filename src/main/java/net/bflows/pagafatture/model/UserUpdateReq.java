package net.bflows.pagafatture.model;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class UserUpdateReq {
    
    @NotNull
    @NotEmpty
    private String firstName;

    @NotNull
    @NotEmpty
    private String lastName;
    
    private String phone;

    private Long roleId;
    

}
