package net.bflows.pagafatture.entities;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.Email;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "USER")
@Getter
@Setter
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", unique = true)
    private Long id;

    @Column(name = "FIRST_NAME", length = 50,nullable = false)
    private String firstName;

    @Column(name = "LAST_NAME", length = 50,nullable = false)
    private String lastName;

    @Email
    @Column(name = "EMAIL", length = 255, unique = true, nullable = false)
    private String email;

    @Column(name = "PASSWORD", length = 60,nullable = false)
    @JsonIgnore
    private String password;

    @Column(name = "PHONE", length = 15)
    private String phone;

    @Column(name = "USER_STATUS", length = 30,nullable = false)
    private String userStatus;
    
    @Column(name = "TEMP_TOKEN", length = 255)
    private String tempToken;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "MERCHANT_ID")
    private MerchantEntity merchant;

    @OneToOne
    @JoinColumn(name = "ROLE_ID")
    private RoleEntity role;
    
    @Column(name = "IS_EMAIL_VERIFIED")
    private Boolean isEmailVerified;
    
    @Column(name = "EMAIL_TOKEN")
    private String emailToken;
    
}
