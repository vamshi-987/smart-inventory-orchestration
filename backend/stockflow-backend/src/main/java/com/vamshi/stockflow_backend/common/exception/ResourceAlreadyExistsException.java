package com.vamshi.stockflow_backend.common.exception;

import org.springframework.http.HttpStatus;

public class ResourceAlreadyExistsException extends AppException {

    public ResourceAlreadyExistsException(String resourceName, String identifier) {
        super(
                HttpStatus.CONFLICT,
                ErrorCode.RESOURCE_ALREADY_EXISTS,
                resourceName + " already exists: " + identifier
        );
    }
}