package com.baseapplication.core.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ExcResponse> handleGenericException(Exception ex) {
		ExcResponse excResponse = new ExcResponse("INTERNAL_SERVER_ERROR", ex.getMessage());
		return new ResponseEntity<>(excResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(InternalException.class)
	public ResponseEntity<ExcResponse> handleInternalException(InternalException ex) {
		ExcResponse excResponse = new ExcResponse("INTERNAL_SERVER_ERROR", ex.getMessage());
		return new ResponseEntity<>(excResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ExcResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
		ExcResponse excResponse = new ExcResponse("NOT_FOUND", ex.getMessage());
		return new ResponseEntity<>(excResponse, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(ConflictException.class)
	public ResponseEntity<ExcResponse> handleConflictException(ConflictException ex) {
		ExcResponse excResponse = new ExcResponse("CONFLICT", ex.getMessage());
		return new ResponseEntity<>(excResponse, HttpStatus.CONFLICT);
	}

	@ExceptionHandler(RestrictionException.class)
	public ResponseEntity<ExcResponse> handleRestrictionException(RestrictionException ex) {
		ExcResponse excResponse = new ExcResponse("FORBIDDEN", ex.getMessage());
		return new ResponseEntity<>(excResponse, HttpStatus.FORBIDDEN);
	}
	
	@ExceptionHandler(InvalidParamException.class)
	public ResponseEntity<ExcResponse> handleInvalidParamException(InvalidParamException ex) {
		ExcResponse excResponse = new ExcResponse("BAD_REQUEST", ex.getMessage());
		return new ResponseEntity<>(excResponse, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(NoContentException.class)
	public ResponseEntity<ExcResponse> handleRNoContentException(NoContentException ex) {
		ExcResponse excResponse = new ExcResponse("NO_CONTENT", ex.getMessage());
		return new ResponseEntity<>(excResponse, HttpStatus.NO_CONTENT);
	}
}
