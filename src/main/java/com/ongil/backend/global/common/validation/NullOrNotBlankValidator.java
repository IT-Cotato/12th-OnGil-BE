package com.ongil.backend.global.common.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NullOrNotBlankValidator implements ConstraintValidator<NullOrNotBlank, String> {

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		// null은 허용, 값이 있으면 blank가 아니어야 함
		return value == null || !value.isBlank();
	}
}