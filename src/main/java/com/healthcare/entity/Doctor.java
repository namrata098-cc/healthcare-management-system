package com.healthcare.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Doctor's professional profile.
 * Every Doctor is linked to exactly one User account.
 */
@Entity
@Table(name = "doctors")
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Specialization is required")
    @Size(max = 100, message = "Specialization must not exceed 100 characters")
    @Column(name = "specialization", nullable = false, length = 100)
    private String specialization;

    @NotBlank(message = "Qualification is required")
    @Size(max = 150, message = "Qualification must not exceed 150 characters")
    @Column(name = "qualification", nullable = false, length = 150)
    private String qualification;

    @Column(name = "experience_years")
    private Integer experienceYears;

    @Positive(message = "Consultation fee must be positive")
    @Column(name = "consultation_fee")
    private Double consultationFee;

    @Column(name = "available", nullable = false)
    private boolean available = true;

    // ---------------------------------------------------------------
    // Relationships
    // ---------------------------------------------------------------

    /**
     * Each Doctor profile belongs to exactly one User.
     * The FK column "user_id" lives in the doctors table.
     */
    @NotNull(message = "Doctor must be linked to a user account")
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    /**
     * One Doctor can have many Appointments.
     */
    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Appointment> appointments = new ArrayList<>();

    /**
     * One Doctor can have many MedicalRecords that they authored.
     */
    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MedicalRecord> medicalRecords = new ArrayList<>();

    /**
     * Feedback received for this doctor.
     */
    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Feedback> feedbackList = new ArrayList<>();

    // ---------------------------------------------------------------
    // Constructors
    // ---------------------------------------------------------------

    public Doctor() {
    }

    public Doctor(String specialization, String qualification, User user) {
        this.specialization = specialization;
        this.qualification = qualification;
        this.user = user;
    }

    // ---------------------------------------------------------------
    // Getters and Setters
    // ---------------------------------------------------------------

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public String getQualification() { return qualification; }
    public void setQualification(String qualification) { this.qualification = qualification; }

    public Integer getExperienceYears() { return experienceYears; }
    public void setExperienceYears(Integer experienceYears) { this.experienceYears = experienceYears; }

    public Double getConsultationFee() { return consultationFee; }
    public void setConsultationFee(Double consultationFee) { this.consultationFee = consultationFee; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public List<Appointment> getAppointments() { return appointments; }
    public void setAppointments(List<Appointment> appointments) { this.appointments = appointments; }

    public List<MedicalRecord> getMedicalRecords() { return medicalRecords; }
    public void setMedicalRecords(List<MedicalRecord> medicalRecords) { this.medicalRecords = medicalRecords; }

    public List<Feedback> getFeedbackList() { return feedbackList; }
    public void setFeedbackList(List<Feedback> feedbackList) { this.feedbackList = feedbackList; }
}
