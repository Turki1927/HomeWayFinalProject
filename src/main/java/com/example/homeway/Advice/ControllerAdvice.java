package com.example.homeway.Advice;

import com.example.homeway.API.APIException;
import com.example.homeway.API.APIResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.sql.SQLIntegrityConstraintViolationException;

@RestControllerAdvice
public class ControllerAdvice {
    @ExceptionHandler(value = APIException.class)
    public ResponseEntity<?> APIException(APIException apiExecption) {
        String message = apiExecption.getMessage();
        return ResponseEntity.status(400).body(new APIResponse(message));
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<?> ValidException(MethodArgumentNotValidException e){
        String error = e.getFieldError().getDefaultMessage();
        return ResponseEntity.status(400).body(new APIResponse(error));
    }

    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    public ResponseEntity<?> HttpMessageNotReadable(){
        return ResponseEntity.status(400).body(new APIResponse("your input not readable"));
    }

    @ExceptionHandler(value = NoResourceFoundException.class)
    public ResponseEntity<?> urlError(){
        return ResponseEntity.status(404).body(new APIResponse("wrong Url"));
    }

    @ExceptionHandler(value = SQLIntegrityConstraintViolationException.class)

    public ResponseEntity<?> SQLError(APIException apiException){
        return ResponseEntity.status(400).body(new APIResponse(apiException.getMessage()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handleDataIntegrityViolation(DataIntegrityViolationException duplicateEntry) {
        return ResponseEntity.status(400).body(new APIResponse(duplicateEntry.getMostSpecificCause().getMessage()));
    }

    @ExceptionHandler(value = MethodArgumentTypeMismatchException.class)
    public ResponseEntity<?> TypeMismatchError(MethodArgumentTypeMismatchException mismatchError){
        return ResponseEntity.status(400).body(new APIResponse(("wrong value type entered did you do a word in place of a number?")));
    }

    @ExceptionHandler(value = ConstraintViolationException.class)
    public ResponseEntity<?> ConstraintViolationException(ConstraintViolationException e){
        String error = e.getConstraintViolations().iterator().next().getMessage();
        return ResponseEntity.status(400).body(new APIResponse(error));
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<?> DuplicateKeyException(DuplicateKeyException duplicateEntry) {
        return ResponseEntity.status(400).body(new APIResponse("the email or username is already taken please choose another"));
    }

    @ExceptionHandler(value = NullPointerException.class)
    public ResponseEntity<?> NullPointerException(NullPointerException e){
        return ResponseEntity.status(400).body(new APIResponse(e.getMessage()));
    }
}
