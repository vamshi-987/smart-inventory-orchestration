package com.vamshi.stockflow_backend.auth.service;

import com.vamshi.stockflow_backend.auth.dto.*;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);
}