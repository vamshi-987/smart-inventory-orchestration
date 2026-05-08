package com.vamshi.stockflow_backend.category.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryUpdateRequest {

    @NotBlank
    private String name;

    private boolean deleted;
}