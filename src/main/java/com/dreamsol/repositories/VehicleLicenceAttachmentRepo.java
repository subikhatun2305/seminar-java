package com.dreamsol.repositories;

import com.dreamsol.entites.VehicleLicenceAttachment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VehicleLicenceAttachmentRepo extends JpaRepository<VehicleLicenceAttachment, Long> {

    Optional<VehicleLicenceAttachment> findByGeneratedFileName(String generatedFileName);
}
