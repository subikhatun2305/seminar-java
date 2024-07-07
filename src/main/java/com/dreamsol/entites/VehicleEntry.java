package com.dreamsol.entites;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VehicleEntry extends CommonAutoIdEntity {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "vehicle_id")
    private VehicleLicence vehicleLicence;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "driver_id")
    private DrivingLicence drivingLicence;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "plant_id")
    private Plant plant;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "purpose_id")
    private Purpose purpose;

    @Column(length = 200)
    private String locationFrom;

    @Column(length = 30)
    private String tripId;

    @Column(length = 100)
    private String invoiceNo;

    @Column(length = 200)
    private String materialDescription;

    private Long quantity;

    private Long numberOfBill;

    @Column(length = 200)
    private String destinationTo;
}
