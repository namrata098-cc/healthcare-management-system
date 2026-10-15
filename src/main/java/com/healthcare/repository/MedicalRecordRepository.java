package com.healthcare.repository;

import com.healthcare.entity.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for {@link MedicalRecord} entities.
 *
 * <p>Provides CRUD operations and domain-specific derived queries
 * for retrieving a patient's medical history.
 */
@Repository
public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {

    /**
     * Get the full medical history for a specific patient,
     * ordered from most recent to oldest.
     */
    List<MedicalRecord> findByPatientIdOrderByRecordDateDesc(Long patientId);

    /**
     * Get all records authored by a specific doctor,
     * ordered from most recent to oldest.
     */
    List<MedicalRecord> findByDoctorIdOrderByRecordDateDesc(Long doctorId);

    /**
     * Find the medical record linked to a specific appointment (if one exists).
     * Returns Optional because not every appointment produces a record.
     */
    Optional<MedicalRecord> findByAppointmentId(Long appointmentId);

    /**
     * Get records for a patient written by a specific doctor.
     * Useful for showing the history between one doctor and one patient.
     */
    List<MedicalRecord> findByPatientIdAndDoctorId(Long patientId, Long doctorId);
}
