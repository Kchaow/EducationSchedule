package org.letunov.controller;

import org.letunov.exceptions.AccessDeniedException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.NoSuchElementException;

@ControllerAdvice
public class ExceptionController
{
    @ExceptionHandler({AccessDeniedException.class})
    public ResponseEntity<String> accessDeniedExceptionHandler(RuntimeException exception)
    {
        return new ResponseEntity<String>(exception.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler({NoSuchElementException.class})
    public ResponseEntity<String> noSuchElementExceptionHandler(RuntimeException exception)
    {
        return new ResponseEntity<String>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }
}