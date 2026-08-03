package com.shopsmart.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
	private int status;
	private String message;
	private String error;
	private String path;
	private LocalDateTime timestamp;
}
