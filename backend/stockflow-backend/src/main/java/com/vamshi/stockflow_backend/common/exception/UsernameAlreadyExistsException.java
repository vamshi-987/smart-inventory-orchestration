package com.vamshi.stockflow_backend.common.exception;

import org.springframework.http.HttpStatus;

public class UsernameAlreadyExistsException extends AppException {

    public UsernameAlreadyExistsException(String username) {
        super(HttpStatus.CONFLICT, ErrorCode.USERNAME_ALREADY_EXISTS, "Username already exists: " + username);
    }
}