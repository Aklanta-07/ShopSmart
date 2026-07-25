package com.shopsmart.service;

import com.shopsmart.dto.request.LoginRequest;
import com.shopsmart.dto.request.RegisterRequest;
import com.shopsmart.dto.response.AuthResponse;

public interface AuthService {
	
	AuthResponse register(RegisterRequest request);
	
	AuthResponse login(LoginRequest request);

}
