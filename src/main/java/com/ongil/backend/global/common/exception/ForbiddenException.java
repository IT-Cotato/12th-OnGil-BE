package com.ongil.backend.global.common.exception;

// 403 Forbidden
public class ForbiddenException extends AppException {
	public ForbiddenException(ErrorCode errorCode) {
		super(errorCode);
	}
}
