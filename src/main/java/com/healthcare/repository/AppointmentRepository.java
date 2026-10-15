package com.healthcare.repository;

import com.healthcare.entity.Appointment;
import com.healthcare.entity.Appointment.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for {@link Appointment} entities.
 *
 * <p>Provides CRUD operations and domain-specific derived queries
 * for managing appointments between patients and doctors.
 */
@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    /**
     * Get all appointments booked by a specific patient.
     */
    List<Appointment> findByPatientId(Long patientId);

    /**
     * Get all appointments assigned to a specific doctor.
     */
    List<Appointment> findByDoctorId(Long doctorId);

    /**
     * Get all appointments with a specific status (e.g., PENDING, CONFIRMED).
     */
    List<Appointment> findByStatus(Status status);

    /**
     * Get all appointments for a patient filtered by status.
     * Example use: show a patient their upcoming (CONFIRMED) appointments.
     */
    List<Appointment> findByPatientIdAndStatus(Long patientId, Status status);

    /**
     * Get all appointments for a doctor filtered by status.
     * Example use: show a doctor their pending requests.
     */
    List<Appointment> findByDoctorIdAndStatus(Long doctorId, Status status);

    /**
     * Find all appointments scheduled within a date/time range.
     * Useful for checking scheduling conflicts or generating daily reports.
     */
    List<Appointment> findByAppointmentDateTimeBetween(LocalDateTime start, LocalDateTime end);
    long countByStatus(Status status);


}
