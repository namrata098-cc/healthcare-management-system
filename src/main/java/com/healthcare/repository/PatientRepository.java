package com.healthcare.repository;

import com.healthcare.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for {@link Patient} entities.
 *
 * <p>Provides CRUD operations and domain-specific derived queries
 * for looking up patient profiles.
 */
@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    /**
     * Find a patient by the linked User's ID.
     * Useful when you have the logged-in user's ID and need their patient profile.
     */
    Optional<Patient> findByUserId(Long userId);

    /**
     * Check whether a patient profile already exists for a given user.
     * Prevents creating duplicate patient profiles.
     */
    boolean existsByUserId(Long userId);
}
