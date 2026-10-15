package com.healthcare.service;

import com.healthcare.entity.User;
import com.healthcare.entity.User.Role;
import com.healthcare.repository.UserRepository;
 
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service class for {@link User} operations.
 *
 * <p>@Service tells Spring this is a business-logic component.
 * <p>@Transactional ensures database operations are wrapped in a transaction —
 * if anything fails, all changes are rolled back automatically.
 */
@Service
@Transactional
public class UserService {

    // Spring injects the repository automatically via constructor injection.
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

   public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
}

    // ---------------------------------------------------------------
    // Create
    // ---------------------------------------------------------------

    /**
     * Register a new user.
     * Throws an exception if the email address is already taken.
     *
     * NOTE: Password encoding (BCrypt) will be added when Security is wired up.
     * For now the raw password is stored — do NOT use in production as-is.
     */
   public User registerUser(User user) {
    if (userRepository.existsByEmail(user.getEmail())) {
        throw new IllegalArgumentException(
                "Email already registered: " + user.getEmail());
    }

    user.setPassword(passwordEncoder.encode(user.getPassword()));
    return userRepository.save(user);
}

    // ---------------------------------------------------------------
    // Read
    // ---------------------------------------------------------------

    /**
     * Find a user by their database ID.
     * Returns an Optional — the caller decides what to do if not found.
     */
    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * Find a user by email. Used during login lookups.
     */
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Get all users in the system (admin use only).
     */
    @Transactional(readOnly = true)
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Get all users with a specific role (e.g., all doctors or all patients).
     */
    @Transactional(readOnly = true)
    public List<User> findByRole(Role role) {
        return userRepository.findByRole(role);
    }

    /**
     * Check if an email is already taken. Useful for registration form validation.
     */
    @Transactional(readOnly = true)
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    // ---------------------------------------------------------------
    // Update
    // ---------------------------------------------------------------

    /**
     * Update a user's full name and phone number.
     * Only updates the fields that are safe to change.
     *
     * @param id            the ID of the user to update
     * @param newFullName   the updated full name
     * @param newPhone      the updated phone number
     * @return the saved, updated User
     */
    public User updateUserProfile(Long id, String newFullName, String newPhone) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));

        user.setFullName(newFullName);
        user.setPhoneNumber(newPhone);

        // @PreUpdate in the entity sets updatedAt automatically.
        return userRepository.save(user);
    }

    // ---------------------------------------------------------------
    // Delete
    // ---------------------------------------------------------------

    /**
     * Delete a user account by ID.
     * Because of CascadeType.ALL on User → Doctor/Patient, the
     * associated profiles are also deleted.
     */
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }
}
