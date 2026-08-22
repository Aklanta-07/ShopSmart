package com.shopsmart.exception;

import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.shopsmart.dto.response.ErrorResponse;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpServletRequest;

@Hidden
@RestControllerAdvice
public class GlobalExceptioHandler {

	@ExceptionHandler(EmailAlreadyExistsException.class)
	public ResponseEntity<ErrorResponse> handleEmailExists(EmailAlreadyExistsException ex, HttpServletRequest request) {
		return buildError(HttpStatus.CONFLICT, "Email already exists", ex.getMessage(), request.getRequestURI());
	}

	@ExceptionHandler(InvalidCredentialsException.class)
	public ResponseEntity<ErrorResponse> handleInvalidCredentials(InvalidCredentialsException ex, HttpServletRequest request) {
		return buildError(HttpStatus.UNAUTHORIZED, "Unauthorized", ex.getMessage(), request.getRequestURI());
	}

	@ExceptionHandler(CategoryNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleCategoryNotFound(CategoryNotFoundException ex, HttpServletRequest request) {
		return buildError(HttpStatus.NOT_FOUND, "Not Found", ex.getMessage(), request.getRequestURI());
	}

	@ExceptionHandler(ProductNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleProductNotFound(ProductNotFoundException ex, HttpServletRequest request) {
		return buildError(HttpStatus.NOT_FOUND, "Not Found", ex.getMessage(), request.getRequestURI());
	}

	@ExceptionHandler(InventoryNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleInventoryNotFound(InventoryNotFoundException ex, HttpServletRequest request) {
		return buildError(HttpStatus.NOT_FOUND, "Not Found", ex.getMessage(), request.getRequestURI());
	}

	@ExceptionHandler(DuplicateSkuException.class)
	public ResponseEntity<ErrorResponse> handleDuplicateSku(DuplicateSkuException ex, HttpServletRequest request) {
		return buildError(HttpStatus.CONFLICT, "Conflict", ex.getMessage(), request.getRequestURI());
	}

	@ExceptionHandler(DuplicateCategoryNameException.class)
	public ResponseEntity<ErrorResponse> handleDuplicateCategoryName(DuplicateCategoryNameException ex, HttpServletRequest request) {
		return buildError(HttpStatus.CONFLICT, "Conflict", ex.getMessage(), request.getRequestURI());
	}

	@ExceptionHandler(InsufficientStockException.class)
	public ResponseEntity<ErrorResponse> handleInsufficientStock(InsufficientStockException ex, HttpServletRequest request) {
		return buildError(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage(), request.getRequestURI());
	}

	@ExceptionHandler(InvalidStockOperationException.class)
	public ResponseEntity<ErrorResponse> handleInvalidStockOperation(InvalidStockOperationException ex, HttpServletRequest request) {
		return buildError(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage(), request.getRequestURI());
	}

	@ExceptionHandler(CustomerNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleCustomerNotFound(CustomerNotFoundException ex, HttpServletRequest request) {
		return buildError(HttpStatus.NOT_FOUND, "Not Found", ex.getMessage(), request.getRequestURI());
	}

	@ExceptionHandler(CustomerGroupNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleCustomerGroupNotFound(CustomerGroupNotFoundException ex, HttpServletRequest request) {
		return buildError(HttpStatus.NOT_FOUND, "Not Found", ex.getMessage(), request.getRequestURI());
	}

	@ExceptionHandler(DuplicatePhoneException.class)
	public ResponseEntity<ErrorResponse> handleDuplicatePhone(DuplicatePhoneException ex, HttpServletRequest request) {
		return buildError(HttpStatus.CONFLICT, "Conflict", ex.getMessage(), request.getRequestURI());
	}

	@ExceptionHandler(DuplicateEmailException.class)
	public ResponseEntity<ErrorResponse> handleDuplicateEmail(DuplicateEmailException ex, HttpServletRequest request) {
		return buildError(HttpStatus.CONFLICT, "Conflict", ex.getMessage(), request.getRequestURI());
	}

	@ExceptionHandler(DuplicateCustomerGroupNameException.class)
	public ResponseEntity<ErrorResponse> handleDuplicateCustomerGroupName(DuplicateCustomerGroupNameException ex, HttpServletRequest request) {
		return buildError(HttpStatus.CONFLICT, "Conflict", ex.getMessage(), request.getRequestURI());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> MethodArgumentNotValidException(MethodArgumentNotValidException ex,
			HttpServletRequest request) {
		String errorMessage = ex.getBindingResult().getFieldErrors().stream()
				.map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
		return buildError(HttpStatus.BAD_REQUEST, "Bad Request", errorMessage, request.getRequestURI());
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleGeneral(Exception ex, HttpServletRequest request) {
		return buildError(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error",
				"Something went wrong. Please try again.", request.getRequestURI());
	}

	private ResponseEntity<ErrorResponse> buildError(HttpStatus status, String error,
			                                                       String message, String path) {
		ErrorResponse errorResponse = ErrorResponse.builder()
				.status(status.value())
				.error(error)
				.message(message)
				.path(path)
				.timestamp(java.time.LocalDateTime.now())
				.build();
		return ResponseEntity.status(status).body(errorResponse);
	}

}
