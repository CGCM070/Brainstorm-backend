package org.brainstorm.exception;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

public class EntityNotFoundException extends ResponseStatusException {
  public EntityNotFoundException(String message) {
    super(HttpStatus.NOT_FOUND, message);
  }
}