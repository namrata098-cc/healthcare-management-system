package com.healthcare.service;


import com.healthcare.entity.Appointment;
import com.healthcare.entity.Appointment.Status;
import com.healthcare.entity.Doctor;
import com.healthcare.entity.Patient;
import com.healthcare.repository.AppointmentRepository;
import com.healthcare.repository.DoctorRepository;
import com.healthcare.repository.PatientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service class for {@link Appointment} operations.
 *
 * <p>Handles the full lifecycle of an appointment:
 * booking → confirming → completing, or cancelling at any stage.
 */
@Service
@Transactional
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    public AppointmentService(AppointmentRepository appointmentRepository,
                              PatientRepository patientRepository,
                              DoctorRepository doctorRepository) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
    }

    // ---------------------------------------------------------------
    // Create — Book an appointment
    // ---------------------------------------------------------------

    /**
     * Book a new appointment for a patient with a doctor.
     *
     * <p>Business rules enforced here:
     * <ul>
     *   <li>The patient must exist.</li>
     *   <li>The doctor must exist and be available.</li>
     *   <li>The date/time must be in the future.</li>
     * </ul>
     *
     * @param patientId           the ID of the patient profile
     * @param doctorId            the ID of the doctor profile
     * @param appointmentDateTime when the appointment is scheduled
     * @param reason              why the patient is visiting (optional)
     * @return the saved Appointment with status PENDING
     */
    public Appointment bookAppointment(Long patientId, Long doctorId,
                                       LocalDateTime appointmentDateTime, String reason) {
        // 1. Validate patient exists
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Patient not found with id: " + patientId));

        // 2. Validate doctor exists and is available
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Doctor not found with id: " + doctorId));

        if (!doctor.isAvailable()) {
            throw new IllegalStateException(
                    "Doctor with id " + doctorId + " is not available for appointments.");
        }

        // 3. Appointment must be in the future
        if (appointmentDateTime.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException(
                    "Appointment date and time must be in the future.");
        }

        // 4. Build and save — status defaults to PENDING inside the entity
        Appointment appointment = new Appointment(appointmentDateTime, patient, doctor);
        appointment.setReason(reason);

        return appointmentRepository.save(appointment);
    }

    // ---------------------------------------------------------------
    // Read
    // ---------------------------------------------------------------

    /**
     * Find an appointment by its ID.
     */
    @Transactional(readOnly = true)
    public Optional<Appointment> findById(Long id) {
        return appointmentRepository.findById(id);
    }

    /**
     * Get all appointments for a specific patient.
     */
    @Transactional(readOnly = true)
    public List<Appointment> findByPatient(Long patientId) {
        return appointmentRepository.findByPatientId(patientId);
    }

    /**
     * Get all appointments assigned to a specific doctor.
     */
    @Transactional(readOnly = true)
    public List<Appointment> findByDoctor(Long doctorId) {
        return appointmentRepository.findByDoctorId(doctorId);
    }

    /**
     * Get a patient's appointments filtered by status.
     * Example: show only upcoming CONFIRMED appointments to the patient.
     */
    @Transactional(readOnly = true)
    public List<Appointment> findByPatientAndStatus(Long patientId, Status status) {
        return appointmentRepository.findByPatientIdAndStatus(patientId, status);
    }

    /**
     * Get a doctor's appointments filtered by status.
     * Example: show PENDING requests to the doctor for confirmation.
     */
    @Transactional(readOnly = true)
    public List<Appointment> findByDoctorAndStatus(Long doctorId, Status status) {
        return appointmentRepository.findByDoctorIdAndStatus(doctorId, status);
    }

    /**
     * Get all appointments within a date/time window (e.g., today's schedule).
     */
    @Transactional(readOnly = true)
    public List<Appointment> findByDateRange(LocalDateTime start, LocalDateTime end) {
        return appointmentRepository.findByAppointmentDateTimeBetween(start, end);
    }

    // ---------------------------------------------------------------
    // Update — Change status
    // ---------------------------------------------------------------

    /**
     * Confirm a PENDING appointment (done by the doctor or admin).
     * Only PENDING appointments can be confirmed.
     */
    public Appointment confirmAppointment(Long id) {
        Appointment appointment = getAppointmentOrThrow(id);

        if (appointment.getStatus() != Status.PENDING) {
            throw new IllegalStateException(
                    "Only PENDING appointments can be confirmed. Current status: "
                    + appointment.getStatus());
        }

        appointment.setStatus(Status.CONFIRMED);
        return appointmentRepository.save(appointment);
    }

    /**
     * Mark a CONFIRMED appointment as COMPLETED (done after the visit).
     * Only CONFIRMED appointments can be completed.
     */
    public Appointment completeAppointment(Long id) {
        Appointment appointment = getAppointmentOrThrow(id);

        if (appointment.getStatus() != Status.CONFIRMED) {
            throw new IllegalStateException(
                    "Only CONFIRMED appointments can be completed. Current status: "
                    + appointment.getStatus());
        }

        appointment.setStatus(Status.COMPLETED);
        return appointmentRepository.save(appointment);
    }

    /**
     * Cancel an appointment.
     * PENDING or CONFIRMED appointments can be cancelled.
     * COMPLETED appointments cannot be cancelled.
     */
    public Appointment cancelAppointment(Long id) {
        Appointment appointment = getAppointmentOrThrow(id);

        if (appointment.getStatus() == Status.COMPLETED) {
            throw new IllegalStateException(
                    "A COMPLETED appointment cannot be cancelled.");
        }

        if (appointment.getStatus() == Status.CANCELLED) {
            throw new IllegalStateException(
                    "This appointment is already cancelled.");
        }

        appointment.setStatus(Status.CANCELLED);
        return appointmentRepository.save(appointment);
    }

    /**
     * Update the date/time and notes of a PENDING appointment.
     * Only PENDING appointments can be rescheduled.
     */
    public Appointment rescheduleAppointment(Long id, LocalDateTime newDateTime, String notes) {
        Appointment appointment = getAppointmentOrThrow(id);

        if (appointment.getStatus() != Status.PENDING) {
            throw new IllegalStateException(
                    "Only PENDING appointments can be rescheduled. Current status: "
                    + appointment.getStatus());
        }

        if (newDateTime.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException(
                    "New appointment date and time must be in the future.");
        }

        appointment.setAppointmentDateTime(newDateTime);
        appointment.setNotes(notes);
        return appointmentRepository.save(appointment);
    }

    // ---------------------------------------------------------------
    // Delete
    // ---------------------------------------------------------------

    /**
     * Permanently delete an appointment record.
     * Prefer cancelling over deleting to keep audit history intact.
     */
    public void deleteAppointment(Long id) {
        if (!appointmentRepository.existsById(id)) {
            throw new IllegalArgumentException("Appointment not found with id: " + id);
        }
        appointmentRepository.deleteById(id);
    }

    // ---------------------------------------------------------------
    // Private helper
    // ---------------------------------------------------------------

    /**
     * Fetch an appointment or throw a clear error if it doesn't exist.
     * Keeps the status-change methods DRY (Don't Repeat Yourself).
     */
    private Appointment getAppointmentOrThrow(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Appointment not found with id: " + id));
    }
    public long countAppointments() {
    return appointmentRepository.count();
}

public long countPendingAppointments() {
    return appointmentRepository.countByStatus(Appointment.Status.PENDING);
}
}
