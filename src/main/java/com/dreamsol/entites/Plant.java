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
@AllArgsConstructor
@NoArgsConstructor
public class Plant extends CommonAutoIdEntity {

    @Column(nullable = false, length = 50, unique = true)
    private String plantName;

    @Column(nullable = false, length = 250)
    private String plantBrief;

}
