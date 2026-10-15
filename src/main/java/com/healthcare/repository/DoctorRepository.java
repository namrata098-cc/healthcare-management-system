package com.healthcare.repository;

import com.healthcare.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for {@link Doctor} entities.
 *
 * <p>Provides CRUD operations and domain-specific derived queries
 * for looking up doctors by their profile details.
 */
@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    /**
     * Find all doctors with a given specialization (e.g., "Cardiology").
     */
    List<Doctor> findBySpecialization(String specialization);

    /**
     * Find all doctors who are currently available for appointments.
     */
    List<Doctor> findByAvailableTrue();

    /**
     * Find a doctor by the linked User's ID.
     * Useful when you have the logged-in user's ID and need their doctor profile.
     */
    Optional<Doctor> findByUserId(Long userId);

    /**
     * Find all doctors with a given specialization who are also available.
     */
    List<Doctor> findBySpecializationAndAvailableTrue(String specialization);
}
