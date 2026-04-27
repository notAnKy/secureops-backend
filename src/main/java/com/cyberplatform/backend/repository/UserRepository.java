package com.cyberplatform.backend.repository;

import com.cyberplatform.backend.entity.User;
import com.cyberplatform.backend.entity.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    Optional<User> findByCode(String code);
    long countByRole(Role role);

    // Non-paginated — kept for internal use (stats, validation)
    List<User> findAllByRole(Role role);

    // Paginated — used by ManageUsers page
    Page<User> findAll(Pageable pageable);
    Page<User> findAllByRole(Role role, Pageable pageable);

    // Password reset — find user by their reset token
    Optional<User> findByResetToken(String resetToken);
}