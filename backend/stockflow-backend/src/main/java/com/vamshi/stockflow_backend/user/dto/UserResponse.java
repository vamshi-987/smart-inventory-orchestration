package com.vamshi.stockflow_backend.user.dto;

import com.vamshi.stockflow_backend.user.domain.Role;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class UserResponse {

    private UUID id;

    private String username;

    private String email;

    private Role role;

    private Boolean active;

    private UUID warehouseId;
}