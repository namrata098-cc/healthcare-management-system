package com.healthcare.service;

import com.healthcare.entity.Doctor;
import com.healthcare.entity.User;
import com.healthcare.repository.DoctorRepository;
import com.healthcare.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service class for {@link Doctor} profile operations.
 *
 * <p>Handles creating, reading, updating, and deleting doctor profiles.
 * A Doctor profile is always linked to an existing User account.
 */
@Service
@Transactional
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;

    public DoctorService(DoctorRepository doctorRepository, UserRepository userRepository) {
        this.doctorRepository = doctorRepository;
        this.userRepository = userRepository;
    }

    // ---------------------------------------------------------------
    // Create
    // ---------------------------------------------------------------

    /**
     * Create a doctor profile for an existing user.
     *
     * @param userId the ID of the User account this doctor profile belongs to
     * @param doctor a Doctor object pre-filled with specialization, qualification, etc.
     * @return the saved Doctor
     */
    public Doctor createDoctorProfile(Long userId, Doctor doctor) {
        // 1. Make sure the user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "User not found with id: " + userId));

        // 2. Make sure this user doesn't already have a doctor profile
        if (doctorRepository.findByUserId(userId).isPresent()) {
            throw new IllegalStateException(
                    "A doctor profile already exists for user id: " + userId);
        }

        // 3. Link and save
        doctor.setUser(user);
        return doctorRepository.save(doctor);
    }

    // ---------------------------------------------------------------
    // Read
    // ---------------------------------------------------------------

    /**
     * Get a doctor by their profile ID.
     */
    @Transactional(readOnly = true)
    public Optional<Doctor> findById(Long id) {
        return doctorRepository.findById(id);
    }

    /**
     * Get a doctor profile by the linked user's ID.
     * Useful after login — you have the user ID and need their doctor profile.
     */
    @Transactional(readOnly = true)
    public Optional<Doctor> findByUserId(Long userId) {
        return doctorRepository.findByUserId(userId);
    }

    /**
     * Get all doctors in the system.
     */
    @Transactional(readOnly = true)
    public List<Doctor> findAllDoctors() {
        return doctorRepository.findAll();
    }

    /**
     * Get all doctors who are currently marked as available.
     */
    @Transactional(readOnly = true)
    public List<Doctor> findAvailableDoctors() {
        return doctorRepository.findByAvailableTrue();
    }

    /**
     * Find doctors by specialization (e.g., "Cardiology").
     */
    @Transactional(readOnly = true)
    public List<Doctor> findBySpecialization(String specialization) {
        return doctorRepository.findBySpecialization(specialization);
    }

    /**
     * Find available doctors filtered by specialization.
     */
    @Transactional(readOnly = true)
    public List<Doctor> findAvailableBySpecialization(String specialization) {
        return doctorRepository.findBySpecializationAndAvailableTrue(specialization);
    }

    // ---------------------------------------------------------------
    // Update
    // ---------------------------------------------------------------

    /**
     * Update a doctor's professional details.
     *
     * @param id               the doctor profile ID to update
     * @param specialization   updated specialization
     * @param qualification    updated qualification
     * @param experienceYears  updated years of experience
     * @param consultationFee  updated consultation fee
     * @return the updated Doctor
     */
    public Doctor updateDoctorProfile(Long id, String specialization, String qualification,
                                      Integer experienceYears, Double consultationFee) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Doctor not found with id: " + id));

        doctor.setSpecialization(specialization);
        doctor.setQualification(qualification);
        doctor.setExperienceYears(experienceYears);
        doctor.setConsultationFee(consultationFee);

        return doctorRepository.save(doctor);
    }

    /**
     * Toggle the doctor's availability on or off.
     * Useful when a doctor goes on leave or returns to practice.
     */
    public Doctor setAvailability(Long id, boolean available) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Doctor not found with id: " + id));

        doctor.setAvailable(available);
        return doctorRepository.save(doctor);
    }

    // ---------------------------------------------------------------
    // Delete
    // ---------------------------------------------------------------

    /**
     * Delete a doctor profile by ID.
     */
    public void deleteDoctorProfile(Long id) {
        if (!doctorRepository.existsById(id)) {
            throw new IllegalArgumentException("Doctor not found with id: " + id);
        }
        doctorRepository.deleteById(id);
    }
}
