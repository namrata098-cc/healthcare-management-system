package com.healthcare.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * A clinical record created by a Doctor for a Patient, optionally tied to an Appointment.
 * Contains diagnosis, prescription, and any lab/test notes.
 */
@Entity
@Table(name = "medical_records")
public class MedicalRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Diagnosis is required")
    @Size(max = 1000, message = "Diagnosis must not exceed 1000 characters")
    @Column(name = "diagnosis", nullable = false, length = 1000)
    private String diagnosis;

    @Size(max = 1000, message = "Prescription must not exceed 1000 characters")
    @Column(name = "prescription", length = 1000)
    private String prescription;

    @Size(max = 1000, message = "Lab results notes must not exceed 1000 characters")
    @Column(name = "lab_results", length = 1000)
    private String labResults;

    @Size(max = 1000, message = "Follow-up notes must not exceed 1000 characters")
    @Column(name = "follow_up_notes", length = 1000)
    private String followUpNotes;

    @Column(name = "record_date", nullable = false, updatable = false)
    private LocalDateTime recordDate;

    // ---------------------------------------------------------------
    // Relationships
    // ---------------------------------------------------------------

    /**
     * Many MedicalRecords belong to one Patient.
     * FK column "patient_id" lives in the medical_records table.
     */
    @NotNull(message = "Medical record must be associated with a patient")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    /**
     * Many MedicalRecords are authored by one Doctor.
     * FK column "doctor_id" lives in the medical_records table.
     */
    @NotNull(message = "Medical record must be associated with a doctor")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    /**
     * Optionally, the record is linked to the Appointment that produced it.
     * FK column "appointment_id" lives in the medical_records table.
     * This is the owning side of the Appointment ↔ MedicalRecord relationship.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id", unique = true)
    private Appointment appointment;

    // ---------------------------------------------------------------
    // Lifecycle callbacks
    // ---------------------------------------------------------------

    @PrePersist
    protected void onCreate() {
        this.recordDate = LocalDateTime.now();
    }

    // ---------------------------------------------------------------
    // Constructors
    // ---------------------------------------------------------------

    public MedicalRecord() {
    }

    public MedicalRecord(String diagnosis, Patient patient, Doctor doctor) {
        this.diagnosis = diagnosis;
        this.patient = patient;
        this.doctor = doctor;
    }

    // ---------------------------------------------------------------
    // Getters and Setters
    // ---------------------------------------------------------------

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDiagnosis() { return diagnosis; }
    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }

    public String getPrescription() { return prescription; }
    public void setPrescription(String prescription) { this.prescription = prescription; }

    public String getLabResults() { return labResults; }
    public void setLabResults(String labResults) { this.labResults = labResults; }

    public String getFollowUpNotes() { return followUpNotes; }
    public void setFollowUpNotes(String followUpNotes) { this.followUpNotes = followUpNotes; }

    public LocalDateTime getRecordDate() { return recordDate; }

    public Patient getPatient() { return patient; }
    public void setPatient(Patient patient) { this.patient = patient; }

    public Doctor getDoctor() { return doctor; }
    public void setDoctor(Doctor doctor) { this.doctor = doctor; }

    public Appointment getAppointment() { return appointment; }
    public void setAppointment(Appointment appointment) { this.appointment = appointment; }
}
