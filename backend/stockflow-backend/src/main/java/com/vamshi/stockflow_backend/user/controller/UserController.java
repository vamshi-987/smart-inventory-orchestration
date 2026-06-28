package com.vamshi.stockflow_backend.user.controller;

import com.vamshi.stockflow_backend.auth.dto.CreateManagerRequest;
import com.vamshi.stockflow_backend.auth.dto.CreateStaffRequest;
import com.vamshi.stockflow_backend.user.dto.UserUpdateRequest;
import com.vamshi.stockflow_backend.user.dto.UserResponse;
import com.vamshi.stockflow_backend.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/managers")
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse createManager(
            @Valid @RequestBody CreateManagerRequest request) {

        return userService.createManager(request);
    }

    @PostMapping("/staff")
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse createStaff(
            @Valid @RequestBody CreateStaffRequest request) {

        return userService.createStaffByAdmin(
                request);
    }

    @PostMapping("/my-warehouse/staff")
    @PreAuthorize(
            "hasRole('WAREHOUSE_MANAGER')")
    public UserResponse createStaffByManager(
            @Valid @RequestBody CreateStaffRequest request) {

        return userService.createStaffByManager(
                request);
    }

    @PutMapping("/managers/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse updateManager(
            @PathVariable UUID id,
            @Valid @RequestBody UserUpdateRequest request) {

        return userService.updateManager(
                id,
                request);
    }

    @PutMapping("/staff/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse updateStaff(
            @PathVariable UUID id,
            @Valid @RequestBody UserUpdateRequest request) {

        return userService.updateStaff(
                id,
                request);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getAllUsers() {

        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse getUser(
            @PathVariable UUID id) {

        return userService.getUser(id);
    }

    @GetMapping("/managers")
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getManagers() {

        return userService.getAllManagers();
    }

    @GetMapping("/staff")
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getStaff() {

        return userService.getAllStaff();
    }

    @GetMapping("/warehouses/{warehouseId}/managers")
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getManagersByWarehouse(
            @PathVariable UUID warehouseId) {

        return userService.getManagersByWarehouse(
                warehouseId);
    }

    @GetMapping("/warehouses/{warehouseId}/staff")
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getStaffByWarehouse(
            @PathVariable UUID warehouseId) {

        return userService.getStaffByWarehouse(
                warehouseId);
    }

    @GetMapping("/my-warehouse/staff")
    @PreAuthorize("hasRole('WAREHOUSE_MANAGER')")
    public List<UserResponse> getMyWarehouseStaff() {

        return userService.getMyWarehouseStaff();
    }

    @PutMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public void activateUser(
            @PathVariable UUID id) {

        userService.activateUser(id);
    }

    @PutMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public void deactivateUser(
            @PathVariable UUID id) {

        userService.deactivateUser(id);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(
            @PathVariable UUID id) {

        userService.hardDeleteUser(id);
    }
}