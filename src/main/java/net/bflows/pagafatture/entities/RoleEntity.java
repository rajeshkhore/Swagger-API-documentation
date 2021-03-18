package net.bflows.pagafatture.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ROLE")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RoleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", unique = true)
    private Long id;

    @Column(name = "ROLE_NAME", length = 30,nullable = false,unique=true)
    private String roleName;

    @Column(name = "ROLE_DESC",length = 120)
    private String roleDesc;

    /**
     * Role name
     */
    public enum RoleEnum {
        ROLE_ADMIN("ROLE_ADMIN"),

        ROLE_CUSTOMER("ROLE_CUSTOMER"),

        ROLE_ANONYMOUS("ROLE_ANONYMOUS"),
        
        ROLE_SUPER_ADMIN("ROLE_SUPER_ADMIN");;

        private String value;

        RoleEnum(String value) {
            this.value = value;
        }

        @JsonValue
        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }
    }

  
    
}
