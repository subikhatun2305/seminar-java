package com.dreamsol.entites;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class UserType extends CommonAutoIdEntity
{
    @Column(length = 50, nullable = false, unique = true)
    private String userTypeName;

    @Column(length = 50,nullable = false, unique = true)
    private String userTypeCode;
}
