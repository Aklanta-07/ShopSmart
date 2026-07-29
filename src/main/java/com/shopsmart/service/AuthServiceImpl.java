package com.shopsmart.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.shopsmart.dto.request.LoginRequest;
import com.shopsmart.dto.request.RegisterRequest;
import com.shopsmart.dto.response.AuthResponse;
import com.shopsmart.entity.User;
import com.shopsmart.exception.EmailAlreadyExistsException;
import com.shopsmart.exception.InvalidCredentialsException;
import com.shopsmart.repository.UserRepository;
import com.shopsmart.security.JwtUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
	
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;

	@Override
	public AuthResponse register(RegisterRequest request) {
		
		if(userRepository.existsByEmail(request.getEmail())) {
			throw new EmailAlreadyExistsException("Email already exists " + request.getEmail());
		}
		
		User user = User.builder() 
				.name(request.getName()) 
				.email(request.getEmail())
				.password(passwordEncoder.encode(request.getPassword()))
				.role(request.getRole())
				.build();
		
		userRepository.save(user);
		
		String token = jwtUtil.generateToken(user);
		
		return AuthResponse.builder()
				.token(token)
				.name(user.getName())
				.email(user.getEmail())
				.role(user.getRole().name())
				.message("Registered successfully")
				.build();
	}

	@Override
	public AuthResponse login(LoginRequest request) {
		User user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException(
                    "Invalid email or password"
                ));
		
		if(!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
			throw new InvalidCredentialsException("Invalid email or password");
		}
		
		String token = jwtUtil.generateToken(user);
		
		return AuthResponse.builder()
				.token(token)
				.name(user.getName())
				.email(user.getEmail())
				.role(user.getRole().name())
				.message("Logged in successfully")
				.build();

	}

}
