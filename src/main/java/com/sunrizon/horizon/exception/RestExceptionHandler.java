package com.sunrizon.horizon.exception;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;

import com.sunrizon.horizon.utils.ResultResponse;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class RestExceptionHandler {

  @ExceptionHandler(EntityNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ResultResponse<String> handleEntityNotFoundException(EntityNotFoundException e) {
    return ResultResponse.error(e.getMessage());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResultResponse<String> handleValidationException(MethodArgumentNotValidException exception) {

    log.error(exception.getDetailMessageCode());

    String errors = exception.getBindingResult().getFieldErrors()
        .stream()
        .map(e -> e.getField() + ": " + e.getDefaultMessage())
        .collect(Collectors.joining("; "));

    return ResultResponse.error(errors);
  }

}
