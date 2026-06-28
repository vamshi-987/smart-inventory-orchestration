package com.vamshi.stockflow_backend.user.repository;

import com.vamshi.stockflow_backend.user.domain.Role;
import com.vamshi.stockflow_backend.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository
        extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    List<User> findByRole(Role role);

    List<User> findByWarehouseId(UUID warehouseId);


    List<User> findByRoleAndWarehouseId(
            Role role,
            UUID warehouseId
    );

}