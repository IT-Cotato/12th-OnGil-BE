package com.ongil.backend.common.exception;

// 비즈니스 로직 검증 실패 (400 Bad Request, 409 Conflict)
public class ValidationException extends AppException {
	public ValidationException(ErrorCode errorCode) {
		super(errorCode);
	}
}