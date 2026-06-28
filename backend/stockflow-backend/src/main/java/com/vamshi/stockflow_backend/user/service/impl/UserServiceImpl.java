package com.vamshi.stockflow_backend.user.service.impl;

import com.vamshi.stockflow_backend.auth.dto.CreateManagerRequest;
import com.vamshi.stockflow_backend.auth.dto.CreateStaffRequest;
import com.vamshi.stockflow_backend.common.exception.ResourceAlreadyExistsException;
import com.vamshi.stockflow_backend.common.exception.ResourceNotFoundException;
import com.vamshi.stockflow_backend.user.domain.Role;
import com.vamshi.stockflow_backend.user.domain.User;
import com.vamshi.stockflow_backend.user.dto.UserUpdateRequest;
import com.vamshi.stockflow_backend.user.dto.UserResponse;
import com.vamshi.stockflow_backend.user.repository.UserRepository;
import com.vamshi.stockflow_backend.user.service.UserService;
import com.vamshi.stockflow_backend.warehouse.domain.Warehouse;
import com.vamshi.stockflow_backend.warehouse.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final WarehouseRepository warehouseRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse createManager(
            CreateManagerRequest request) {

        validateUserCreation(
                request.getUsername(),
                request.getEmail());

        Warehouse warehouse =
                warehouseRepository.findById(
                                request.getWarehouseId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Warehouse",
                                        request.getWarehouseId().toString()));

        User manager = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(
                        passwordEncoder.encode(
                                request.getPassword()))
                .role(Role.WAREHOUSE_MANAGER)
                .warehouse(warehouse)
                .active(true)
                .build();

        userRepository.save(manager);

        return map(manager);
    }

    @Override
    public UserResponse createStaffByAdmin(
            CreateStaffRequest request) {

        validateUserCreation(
                request.getUsername(),
                request.getEmail());

        Warehouse warehouse =
                warehouseRepository.findById(
                                request.getWarehouseId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Warehouse",
                                        request.getWarehouseId().toString()));

        User staff = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(
                        passwordEncoder.encode(
                                request.getPassword()))
                .role(Role.WAREHOUSE_STAFF)
                .warehouse(warehouse)
                .active(true)
                .build();

        userRepository.save(staff);

        return map(staff);
    }

    @Override
    public UserResponse createStaffByManager(
            CreateStaffRequest request) {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        String email =
                authentication.getName();

        User manager =
                userRepository.findByEmail(email)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Manager", email));

        if (manager.getWarehouse() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Manager is not assigned to a warehouse");
        }

        validateUserCreation(
                request.getUsername(),
                request.getEmail());

        User staff = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(
                        passwordEncoder.encode(
                                request.getPassword()))
                .role(Role.WAREHOUSE_STAFF)
                .warehouse(manager.getWarehouse())
                .active(true)
                .build();

        userRepository.save(staff);

        return map(staff);
    }

    @Override
    public UserResponse updateManager(
            UUID id,
            UserUpdateRequest request) {

        User manager =
                userRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Manager", id.toString()));

        if (manager.getRole() !=
                Role.WAREHOUSE_MANAGER) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "User is not a manager");
        }

        validateUserUpdate(
                manager,
                request.getUsername(),
                request.getEmail());

        Warehouse warehouse =
                warehouseRepository.findById(
                                request.getWarehouseId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Warehouse",
                                        request.getWarehouseId().toString()));

        manager.setUsername(
                request.getUsername());

        manager.setEmail(
                request.getEmail());

        manager.setWarehouse(
                warehouse);

        userRepository.save(manager);

        return map(manager);
    }

    @Override
    public UserResponse updateStaff(
            UUID id,
            UserUpdateRequest request) {

        User staff =
                userRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Staff", id.toString()));

        if (staff.getRole() !=
                Role.WAREHOUSE_STAFF) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "User is not staff");
        }

        validateUserUpdate(
                staff,
                request.getUsername(),
                request.getEmail());

        Warehouse warehouse =
                warehouseRepository.findById(
                                request.getWarehouseId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Warehouse",
                                        request.getWarehouseId().toString()));

        staff.setUsername(
                request.getUsername());

        staff.setEmail(
                request.getEmail());

        staff.setWarehouse(
                warehouse);

        userRepository.save(staff);

        return map(staff);
    }

    @Override
    public List<UserResponse> getAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(this::map)
                .toList();
    }

    @Override
    public UserResponse getUser(UUID id) {

        return map(
                userRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User not found"))
        );
    }

    @Override
    public List<UserResponse> getAllManagers() {

        return userRepository
                .findByRole(Role.WAREHOUSE_MANAGER)
                .stream()
                .map(this::map)
                .toList();
    }

    @Override
    public List<UserResponse> getManagersByWarehouse(
            UUID warehouseId) {

        return userRepository
                .findByRoleAndWarehouseId(
                        Role.WAREHOUSE_MANAGER,
                        warehouseId
                )
                .stream()
                .map(this::map)
                .toList();
    }

    @Override
    public List<UserResponse> getStaffByWarehouse(
            UUID warehouseId) {

        return userRepository
                .findByRoleAndWarehouseId(
                        Role.WAREHOUSE_STAFF,
                        warehouseId
                )
                .stream()
                .map(this::map)
                .toList();
    }

    @Override
    public List<UserResponse> getMyWarehouseStaff() {

        String email =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getName();

        User manager =
                userRepository.findByEmail(email)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Manager", email));

        if (manager.getWarehouse() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Manager is not assigned to a warehouse");
        }

        UUID warehouseId =
                manager.getWarehouse().getId();

        return userRepository
                .findByRoleAndWarehouseId(
                        Role.WAREHOUSE_STAFF,
                        warehouseId
                )
                .stream()
                .map(this::map)
                .toList();
    }


    @Override
    public List<UserResponse> getAllStaff() {

        return userRepository
                .findByRole(Role.WAREHOUSE_STAFF)
                .stream()
                .map(this::map)
                .toList();
    }

    @Override
    public void activateUser(UUID id) {

        User user =
                userRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "User", id.toString()));

        user.setActive(true);

        userRepository.save(user);
    }

    @Override
    public void deactivateUser(UUID id) {

        User user =
                userRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "User", id.toString()));

        user.setActive(false);

        userRepository.save(user);
    }

    private void validateUserCreation(
            String username,
            String email) {

        if (userRepository.existsByUsername(
                username)) {

            throw new ResourceAlreadyExistsException(
                    "Username", username);
        }

        if (userRepository.existsByEmail(
                email)) {

            throw new ResourceAlreadyExistsException(
                    "Email", email);
        }
    }

    private void validateUserUpdate(
            User user,
            String username,
            String email) {

        if (username != null
                && !username.equals(user.getUsername())
                && userRepository.existsByUsername(username)) {

            throw new ResourceAlreadyExistsException(
                    "Username", username);
        }

        if (email != null
                && !email.equals(user.getEmail())
                && userRepository.existsByEmail(email)) {

            throw new ResourceAlreadyExistsException(
                    "Email", email);
        }
    }

    private UserResponse map(User user) {

        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .active(user.isActive())
                .warehouseId(
                        user.getWarehouse() != null
                                ? user.getWarehouse().getId()
                                : null
                )
                .build();
    }

    @Override
    public void hardDeleteUser(UUID userId) {

        User user =
                userRepository.findById(userId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "User", userId.toString()));

        userRepository.delete(user);
    }
}