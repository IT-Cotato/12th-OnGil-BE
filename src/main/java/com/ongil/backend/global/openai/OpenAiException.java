package com.ongil.backend.global.openai;

public class OpenAiException extends RuntimeException {

	public OpenAiException(String message, Throwable cause) {
		super(message, cause);
	}
}
