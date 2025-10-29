package com.quora.quora_backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice//this annotation allows us to handle exceptions globally
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<String>handleResourceAlreadyExistException(
        ResourceAlreadyExistsException ex, WebRequest request){
            return new ResponseEntity<>(ex.getMessage(),HttpStatus.CONFLICT);
        }

}
/*@ControllerAdvice: Tells Spring to use this class to handle exceptions across all controllers.

@ExceptionHandler(...): Specifies which exception this method should catch.

ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT):
 Creates a response with the exception's message in the body and 
 sets the HTTP status code to 409 Conflict
 , which is the standard code for duplicate resources. */