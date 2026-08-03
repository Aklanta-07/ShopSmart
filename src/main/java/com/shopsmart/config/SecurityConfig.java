package com.shopsmart.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopsmart.security.CustomUserDetailsService;
import com.shopsmart.security.JwtAuthFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
	
	private final JwtAuthFilter jwtAuthFilter;
	private final CustomUserDetailsService userDetailsService;
	
	@Bean
	public SecurityFilterChain securityFilterChain(
			                                                        HttpSecurity http) throws Exception {
		 	
		// disable csrf? because we are using JWT token for authentication and not using cookies, 
		//so we don't need csrf protection.
		http.csrf(csrf -> csrf.disable())
		.authorizeHttpRequests(auth -> auth // define which endpoints are public and which are protected
				.requestMatchers("/api/auth/**",
				        "/swagger-ui/**",
				        "/swagger-ui.html",
				        "/swagger-ui/index.html",
				        "/v3/api-docs/**",
				        "/v3/api-docs",
				        "/swagger-resources/**",
				        "/webjars/**",
				        "/*.html",          
				        "/favicon.ico"
						).permitAll()
				.anyRequest().authenticated())
		
		.exceptionHandling(ex -> ex
	            .authenticationEntryPoint(
	                authenticationEntryPoint())
	            .accessDeniedHandler(
	                accessDeniedHandler())
	        )
		
		.sessionManagement(session -> session
				.sessionCreationPolicy(
						SessionCreationPolicy.STATELESS))
		
		.authenticationProvider(authenticationProvider())
		.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
		
		return http.build();
	}
	
	@Bean
	public AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
		provider.setPasswordEncoder(passwordEncoder());
		return provider;
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}
	
	@Bean
	public AuthenticationEntryPoint authenticationEntryPoint() {
	    return (request, response, authException) -> {
	        response.setStatus(HttpStatus.UNAUTHORIZED.value());
	        response.setContentType("application/json");
	        response.getWriter().write(
	            new ObjectMapper().writeValueAsString(
	                java.util.Map.of(
	                    "status", 401,
	                    "error", "Unauthorized",
	                    "message", "Access denied - please login first",
	                    "path", request.getRequestURI(),
	                    "timestamp", java.time.LocalDateTime
	                                    .now().toString()
	                )
	            )
	        );
	    };
	}
	
	@Bean
	public AccessDeniedHandler accessDeniedHandler() {
	    return (request, response, accessDeniedException) -> {
	        response.setStatus(HttpStatus.FORBIDDEN.value());
	        response.setContentType("application/json");
	        response.getWriter().write(
	            new ObjectMapper().writeValueAsString(
	                java.util.Map.of(
	                    "status", 403,
	                    "error", "Forbidden",
	                    "message", "You don't have permission to access this resource",
	                    "path", request.getRequestURI(),
	                    "timestamp", java.time.LocalDateTime
	                                    .now().toString()
	                )
	            )
	        );
	    };
	}

}
