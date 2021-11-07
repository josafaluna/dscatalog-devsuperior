package br.com.jluna.dscatalog.resources.exceptions;

import java.time.Instant;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import br.com.jluna.dscatalog.services.exceptions.CategoryNotFoundException;
import br.com.jluna.dscatalog.services.exceptions.DatabaseException;

@ControllerAdvice
public class ResourceExceptionHandler {

	@ExceptionHandler(CategoryNotFoundException.class)
	public ResponseEntity<StandardError> resourceEntityNotFoundException(CategoryNotFoundException e,
			HttpServletRequest request) {

		var status = HttpStatus.NOT_FOUND;

		StandardError err = new StandardError();
		err.setTimestamp(Instant.now());
		err.setStatus(status.value());
		err.setError("Resource not found.");
		err.setMessage(e.getMessage());
		err.setPath(request.getRequestURI());

		return ResponseEntity.status(status).body(err);
	}

	@ExceptionHandler(DatabaseException.class)
	public ResponseEntity<StandardError> resourceDatabaseException(DatabaseException e, HttpServletRequest request) {

		var status = HttpStatus.BAD_REQUEST;

		StandardError err = new StandardError();
		err.setTimestamp(Instant.now());
		err.setStatus(status.value());
		err.setError("Database Exception.");
		err.setMessage(e.getMessage());
		err.setPath(request.getRequestURI());

		return ResponseEntity.status(status).body(err);
	}

}
