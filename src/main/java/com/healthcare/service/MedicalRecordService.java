package com.healthcare.service;

import com.healthcare.entity.Appointment;
import com.healthcare.entity.Appointment.Status;
import com.healthcare.entity.Doctor;
import com.healthcare.entity.MedicalRecord;
import com.healthcare.entity.Patient;
import com.healthcare.repository.AppointmentRepository;
import com.healthcare.repository.DoctorRepository;
import com.healthcare.repository.MedicalRecordRepository;
import com.healthcare.repository.PatientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service class for {@link MedicalRecord} operations.
 *
 * <p>A doctor creates a medical record for a patient, optionally linking
 * it to the appointment that produced it.
 */
@Service
@Transactional
public class MedicalRecordService {

    private final MedicalRecordRepository medicalRecordRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;

    public MedicalRecordService(MedicalRecordRepository medicalRecordRepository,
                                PatientRepository patientRepository,
                                DoctorRepository doctorRepository,
                                AppointmentRepository appointmentRepository) {
        this.medicalRecordRepository = medicalRecordRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
    }

    // ---------------------------------------------------------------
    // Create
    // ---------------------------------------------------------------

    /**
     * Create a medical record for a patient by a doctor.
     *
     * <p>Business rules:
     * <ul>
     *   <li>Patient and doctor must exist.</li>
     *   <li>If an appointmentId is provided, it must exist and be in COMPLETED status,
     *       and must not already have a linked record (one-to-one).</li>
     * </ul>
     *
     * @param patientId     ID of the patient this record belongs to
     * @param doctorId      ID of the doctor authoring this record
     * @param appointmentId (optional) ID of the completed appointment — pass null to skip linking
     * @param record        a MedicalRecord pre-filled with diagnosis, prescription, etc.
     * @return the saved MedicalRecord
     */
    public MedicalRecord createRecord(Long patientId, Long doctorId,
                                      Long appointmentId, MedicalRecord record) {
        // 1. Validate patient
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Patient not found with id: " + patientId));

        // 2. Validate doctor
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Doctor not found with id: " + doctorId));

        // 3. Optionally link to an appointment
        if (appointmentId != null) {
            Appointment appointment = appointmentRepository.findById(appointmentId)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Appointment not found with id: " + appointmentId));

            // The appointment must be completed before writing a record
            if (appointment.getStatus() != Status.COMPLETED) {
                throw new IllegalStateException(
                        "A medical record can only be created for a COMPLETED appointment. "
                        + "Current status: " + appointment.getStatus());
            }

            // Prevent attaching two records to the same appointment
            if (medicalRecordRepository.findByAppointmentId(appointmentId).isPresent()) {
                throw new IllegalStateException(
                        "A medical record already exists for appointment id: " + appointmentId);
            }

            record.setAppointment(appointment);
        }

        record.setPatient(patient);
        record.setDoctor(doctor);

        return medicalRecordRepository.save(record);
    }

    // ---------------------------------------------------------------
    // Read
    // ---------------------------------------------------------------

    /**
     * Find a medical record by its ID.
     */
    @Transactional(readOnly = true)
    public Optional<MedicalRecord> findById(Long id) {
        return medicalRecordRepository.findById(id);
    }

    /**
     * Get a patient's full medical history, newest record first.
     */
    @Transactional(readOnly = true)
    public List<MedicalRecord> findByPatient(Long patientId) {
        return medicalRecordRepository.findByPatientIdOrderByRecordDateDesc(patientId);
    }

    /**
     * Get all records authored by a doctor, newest first.
     */
    @Transactional(readOnly = true)
    public List<MedicalRecord> findByDoctor(Long doctorId) {
        return medicalRecordRepository.findByDoctorIdOrderByRecordDateDesc(doctorId);
    }

    /**
     * Get the record linked to a specific appointment (if one exists).
     */
    @Transactional(readOnly = true)
    public Optional<MedicalRecord> findByAppointment(Long appointmentId) {
        return medicalRecordRepository.findByAppointmentId(appointmentId);
    }

    /**
     * Get all records shared between one patient and one doctor.
     */
    @Transactional(readOnly = true)
    public List<MedicalRecord> findByPatientAndDoctor(Long patientId, Long doctorId) {
        return medicalRecordRepository.findByPatientIdAndDoctorId(patientId, doctorId);
    }

    // ---------------------------------------------------------------
    // Update
    // ---------------------------------------------------------------

    /**
     * Update the clinical details of an existing medical record.
     * All four fields are updated; pass the existing value to keep a field unchanged.
     */
    public MedicalRecord updateRecord(Long id, String diagnosis, String prescription,
                                      String labResults, String followUpNotes) {
        MedicalRecord record = medicalRecordRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Medical record not found with id: " + id));

        record.setDiagnosis(diagnosis);
        record.setPrescription(prescription);
        record.setLabResults(labResults);
        record.setFollowUpNotes(followUpNotes);

        return medicalRecordRepository.save(record);
    }

    // ---------------------------------------------------------------
    // Delete
    // ---------------------------------------------------------------

    /**
     * Delete a medical record by ID.
     */
    public void deleteRecord(Long id) {
        if (!medicalRecordRepository.existsById(id)) {
            throw new IllegalArgumentException("Medical record not found with id: " + id);
        }
        medicalRecordRepository.deleteById(id);
    }
}
