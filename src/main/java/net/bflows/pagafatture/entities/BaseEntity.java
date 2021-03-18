package net.bflows.pagafatture.entities;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter
public class BaseEntity {

    @Column(name = "CREATED_DATE")
    LocalDateTime createdDate;
    
    @Column(name = "UPDATED_DATE")
    LocalDateTime updatedDate;

}
