package com.dreamsol.entites;

import javax.persistence.Column;
import javax.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Department extends CommonAutoIdEntity {

    @Column(nullable = false, length = 50)
    private String departmentName;

    @Column(nullable = false, length = 50, unique = true)
    private String departmentCode;

}
