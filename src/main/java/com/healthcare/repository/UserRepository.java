package com.healthcare.repository;

import com.healthcare.entity.User;
import com.healthcare.entity.User.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for {@link User} entities.
 *
 * <p>JpaRepository<User, Long> provides built-in methods like:
 * save(), findById(), findAll(), deleteById(), count(), etc.
 *
 * <p>The method names below follow Spring Data's naming convention so
 * the framework generates the SQL automatically — no @Query needed.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find a user by their email address.
     * Used for login / duplicate-email checks.
     */
    Optional<User> findByEmail(String email);

    /**
     * Check whether an email address is already registered.
     */
    boolean existsByEmail(String email);

    /**
     * Find all users that have a specific role (ADMIN, DOCTOR, PATIENT).
     */
    List<User> findByRole(Role role);
}
