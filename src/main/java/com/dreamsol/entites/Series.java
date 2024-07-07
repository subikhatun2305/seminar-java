package com.dreamsol.entites;

import javax.persistence.Column;
import javax.persistence.Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Series extends CommonAutoIdEntity {

    @Column(length = 50, nullable = false)
    private String seriesFor;

    @Column(length = 20, nullable = false)
    private String prefix;

    @Column(length = 50, nullable = false)
    private String subPrefix;

    @Column(length = 100, nullable = false)
    private int numberSeries;
}
