package com.dreamsol.entites;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Purpose extends CommonAutoIdEntity {
    @Column(length = 50, nullable = false)
    private String purposeFor;

    @Column(length = 250, nullable = false)
    private String purposeBrief;

    private boolean alert;

    private LocalTime alertTime;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = true)
    private User user;
    @ManyToOne
    @JoinColumn(name = "department_id", nullable = true)
    private Department department;
}
