package com.property.management.repository;

import java.util.Optional;

import com.property.management.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByUsername(String username);

  Boolean existsByUsername(String username);

  Boolean existsByEmail(String email);

  @Query("SELECT u FROM User u JOIN FETCH u.roles WHERE u.id = :userId")
  Optional<User> findUserWithRolesById(@Param("userId") Long userId);
}
