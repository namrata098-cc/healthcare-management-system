package com.healthcare.service;

import com.healthcare.entity.Patient;
import com.healthcare.entity.User;
import com.healthcare.repository.PatientRepository;
import com.healthcare.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service class for {@link Patient} profile operations.
 *
 * <p>Handles creating, reading, updating, and deleting patient profiles.
 * A Patient profile is always linked to an existing User account.
 */
@Service
@Transactional
public class PatientService {

    private final PatientRepository patientRepository;
    private final UserRepository userRepository;

    public PatientService(PatientRepository patientRepository, UserRepository userRepository) {
        this.patientRepository = patientRepository;
        this.userRepository = userRepository;
    }

    // ---------------------------------------------------------------
    // Create
    // ---------------------------------------------------------------

    /**
     * Create a patient profile for an existing user.
     *
     * @param userId  the ID of the User account this patient profile belongs to
     * @param patient a Patient object pre-filled with date of birth, gender, etc.
     * @return the saved Patient
     */
    public Patient createPatientProfile(Long userId, Patient patient) {
        // 1. Make sure the user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "User not found with id: " + userId));

        // 2. Prevent creating a second patient profile for the same user
        if (patientRepository.existsByUserId(userId)) {
            throw new IllegalStateException(
                    "A patient profile already exists for user id: " + userId);
        }

        // 3. Link and save
        patient.setUser(user);
        return patientRepository.save(patient);
    }

    // ---------------------------------------------------------------
    // Read
    // ---------------------------------------------------------------

    /**
     * Find a patient by their profile ID.
     */
    @Transactional(readOnly = true)
    public Optional<Patient> findById(Long id) {
        return patientRepository.findById(id);
    }

    /**
     * Find a patient profile by the linked user's ID.
     * Useful after login — you have the user ID and need their patient profile.
     */
    @Transactional(readOnly = true)
    public Optional<Patient> findByUserId(Long userId) {
        return patientRepository.findByUserId(userId);
    }

    /**
     * Get all patients in the system (admin / doctor use).
     */
    @Transactional(readOnly = true)
    public List<Patient> findAllPatients() {
        return patientRepository.findAll();
    }

    // ---------------------------------------------------------------
    // Update
    // ---------------------------------------------------------------

    /**
     * Update a patient's personal health details.
     *
     * @param id             the patient profile ID to update
     * @param address        updated home address
     * @param medicalHistory updated summary of past medical history
     * @param allergies      updated allergies description
     * @return the updated Patient
     */
    public Patient updatePatientProfile(Long id, String address,
                                        String medicalHistory, String allergies) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Patient not found with id: " + id));

        patient.setAddress(address);
        patient.setMedicalHistory(medicalHistory);
        patient.setAllergies(allergies);

        return patientRepository.save(patient);
    }

    // ---------------------------------------------------------------
    // Delete
    // ---------------------------------------------------------------

    /**
     * Delete a patient profile by ID.
     */
    public void deletePatientProfile(Long id) {
        if (!patientRepository.existsById(id)) {
            throw new IllegalArgumentException("Patient not found with id: " + id);
        }
        patientRepository.deleteById(id);
    }
}
