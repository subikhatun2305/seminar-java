package com.dreamsol.repositories;

import com.dreamsol.entites.DrivingLicenceAttachment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DrivingLicenceAttachmentRepo extends JpaRepository<DrivingLicenceAttachment,Long> {

    public Optional<DrivingLicenceAttachment> findByGeneratedFileName(String fileName);
}
