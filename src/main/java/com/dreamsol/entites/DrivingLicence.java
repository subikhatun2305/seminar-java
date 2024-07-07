package com.dreamsol.entites;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DrivingLicence extends  CommonAutoIdEntity{

    @Column(length = 50, nullable = false)
    private String driverName;

    @Column(length = 10, nullable = false,unique = true)
    private Long driverMobile;

    @Column(length = 100, nullable = false, unique = true)
    private String licence;

    @Column(nullable = false)
    private LocalDate expDate;

    @Column(length = 200)
    private String brief;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "fileId")
    private DrivingLicenceAttachment file;

}
