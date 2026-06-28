package com.vamshi.stockflow_backend.user.service;

import com.vamshi.stockflow_backend.auth.dto.CreateManagerRequest;
import com.vamshi.stockflow_backend.auth.dto.CreateStaffRequest;
import com.vamshi.stockflow_backend.user.dto.*;

import java.util.List;
import java.util.UUID;

public interface UserService {

    UserResponse createManager(
            CreateManagerRequest request);

    UserResponse createStaffByAdmin(
            CreateStaffRequest request);

    UserResponse createStaffByManager(
            CreateStaffRequest request);

    UserResponse updateManager(
            UUID id,
            UserUpdateRequest request);

    UserResponse updateStaff(
            UUID id,
            UserUpdateRequest request);

    List<UserResponse> getAllUsers();

    UserResponse getUser(UUID id);

    List<UserResponse> getAllManagers();

    List<UserResponse> getAllStaff();

    List<UserResponse> getManagersByWarehouse(
            UUID warehouseId);

    List<UserResponse> getStaffByWarehouse(
            UUID warehouseId);

    List<UserResponse> getMyWarehouseStaff();

    void hardDeleteUser(UUID userId);

    void activateUser(UUID id);

    void deactivateUser(UUID id);
}