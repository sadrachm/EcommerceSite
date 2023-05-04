package net.revature.ecommerce.exceptions;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;

@ControllerAdvice
public class EcommerceExceptionAdvice{
    @ExceptionHandler(value = {UserNotFoundException.class})
    public ResponseEntity<Object> handleUserException(UserNotFoundException ex, WebRequest webRequest) {
        ErrorMessage err = new ErrorMessage(new Date(), ex.getMessage());
        return new ResponseEntity<>(err, new HttpHeaders(), HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(value = {InvalidInputException.class})
    public ResponseEntity<Object> handleInputException(UserNotFoundException ex, WebRequest webRequest) {
        ErrorMessage err = new ErrorMessage(new Date(), ex.getMessage());
        return new ResponseEntity<>(err, new HttpHeaders(), HttpStatus.NOT_ACCEPTABLE);
    }
}
