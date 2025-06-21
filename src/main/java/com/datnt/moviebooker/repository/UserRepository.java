package com.datnt.moviebooker.repository;

import com.datnt.moviebooker.constant.Role;
import com.datnt.moviebooker.entity.User;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(@NotNull(message = "Username can not null") @Size(max = 50, min = 5, message = "Username must be between 5 and 50 characters") String username);
    Page<User> findUsersByRole(@NotNull(message = "Role can not null") Role role, Pageable pageable);
    Optional<User> findByEmail(@NotNull(message = "Email can not null") @Size(max = 100, min = 5, message = "Email must be between 5 and 100 characters") String email);
}
