package com.ongil.backend.global.common.exception;

// 404 Not Found
public class EntityNotFoundException extends AppException {
	public EntityNotFoundException(ErrorCode errorCode) {
		super(errorCode);
	}
}
