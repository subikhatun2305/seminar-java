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
public class VehicleLicence extends CommonAutoIdEntity{

    @Column(length = 50, nullable = false)
    private String vehicleOwner;

    @Column(length = 20, nullable = false,unique = true)
    private String vehicleNumber;

    @Column(length = 100, nullable = false)
    private String vehicleType;

    @Column(nullable = false)
    private LocalDate insuranceDate;

    @Column(nullable = false)
    private LocalDate pucDate;

    @Column(nullable = false)
    private LocalDate registrationDate;

    @Column(length = 200)
    private String brief;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "puc_attachment_id", referencedColumnName = "id")
    private VehicleLicenceAttachment pucAttachment;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "insurance_attachment_id", referencedColumnName = "id")
    private VehicleLicenceAttachment insuranceAttachment;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "registration_attachment_id", referencedColumnName = "id")
    private VehicleLicenceAttachment registrationAttachment;
}
