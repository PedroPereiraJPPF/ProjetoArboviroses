package com.arboviroses.conectaDengue.Api.Exceptions;

import java.io.IOException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.text.ParseException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import com.opencsv.exceptions.CsvException;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.ServletException;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> HandleExceptions(Exception exception, WebRequest request)
    {
        ApiExceptionResponse exceptionResponse = new ApiExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
        return new ResponseEntity<>(exceptionResponse, new HttpHeaders(), exceptionResponse.getHttpStatus());
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<Object> HandleExceptions(IOException exception, WebRequest request)
    {
        ApiExceptionResponse exceptionResponse = new ApiExceptionResponse(HttpStatus.BAD_REQUEST, exception.getMessage());
        return new ResponseEntity<>(exceptionResponse, new HttpHeaders(), exceptionResponse.getHttpStatus());
    }

    @ExceptionHandler(CsvException.class)
    public ResponseEntity<Object> HandleExceptions(CsvException exception, WebRequest request)
    {
        ApiExceptionResponse exceptionResponse = new ApiExceptionResponse(HttpStatus.BAD_REQUEST, exception.getMessage());
        return new ResponseEntity<>(exceptionResponse, new HttpHeaders(), exceptionResponse.getHttpStatus());
    }

    @ExceptionHandler(NumberFormatException.class)
    public ResponseEntity<Object> HandleExceptions(NumberFormatException exception, WebRequest request)
    {
        ApiExceptionResponse exceptionResponse = new ApiExceptionResponse(HttpStatus.BAD_REQUEST, exception.getMessage());
        return new ResponseEntity<>(exceptionResponse, new HttpHeaders(), exceptionResponse.getHttpStatus());
    }

    @ExceptionHandler(ParseException.class)
    public ResponseEntity<Object> HandleExceptions(ParseException exception, WebRequest request)
    {
        ApiExceptionResponse exceptionResponse = new ApiExceptionResponse(HttpStatus.BAD_REQUEST, exception.getMessage());
        return new ResponseEntity<>(exceptionResponse, new HttpHeaders(), exceptionResponse.getHttpStatus());
    }

    @ExceptionHandler(InvalidAgravoException.class)
    public ResponseEntity<Object> HandleExceptions(InvalidAgravoException exception, WebRequest request)
    {
        ApiExceptionResponse exceptionResponse = new ApiExceptionResponse(HttpStatus.BAD_REQUEST, exception.getMessage());
        return new ResponseEntity<>(exceptionResponse, new HttpHeaders(), exceptionResponse.getHttpStatus());
    }

    @ExceptionHandler(InvalidDateStringException.class)
    public ResponseEntity<Object> HandleExceptions(InvalidDateStringException exception, WebRequest request)
    {
        ApiExceptionResponse exceptionResponse = new ApiExceptionResponse(HttpStatus.BAD_REQUEST, exception.getMessage());
        return new ResponseEntity<>(exceptionResponse, new HttpHeaders(), exceptionResponse.getHttpStatus());
    }

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ResponseEntity<Object> HandleExceptions(SQLIntegrityConstraintViolationException exception, WebRequest request)
    {
        ApiExceptionResponse exceptionResponse = new ApiExceptionResponse(HttpStatus.BAD_REQUEST, "Os dados desse csv já existem na base de dados");
        return new ResponseEntity<>(exceptionResponse, new HttpHeaders(), exceptionResponse.getHttpStatus());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> HandleExceptions(AccessDeniedException exception, WebRequest request)
    {
        ApiExceptionResponse exceptionResponse = new ApiExceptionResponse(HttpStatus.BAD_REQUEST, "Acesso negado");
        return new ResponseEntity<>(exceptionResponse, new HttpHeaders(), 401);
    }

    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<Object> HandleExceptions(SignatureException exception, WebRequest request)
    {
        ApiExceptionResponse exceptionResponse = new ApiExceptionResponse(HttpStatus.BAD_REQUEST, "Você não pode acessar esse recurso");
        return new ResponseEntity<>(exceptionResponse, new HttpHeaders(), 401);
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<Object> HandleExceptions(ExpiredJwtException exception, WebRequest request)
    {
        ApiExceptionResponse exceptionResponse = new ApiExceptionResponse(HttpStatus.BAD_REQUEST, "Seu token foi inválidado");
        return new ResponseEntity<>(exceptionResponse, new HttpHeaders(), 401);
    }

    @ExceptionHandler(AccountStatusException.class)
    public ResponseEntity<Object> HandleExceptions(AccountStatusException exception, WebRequest request)
    {
        ApiExceptionResponse exceptionResponse = new ApiExceptionResponse(HttpStatus.BAD_REQUEST, "Você não pode acessar esse recurso");
        return new ResponseEntity<>(exceptionResponse, new HttpHeaders(), 401);
    }

    @ExceptionHandler(ServletException.class)
    public ResponseEntity<Object> HandleExceptions(ServletException exception, WebRequest request)
    {
        ApiExceptionResponse exceptionResponse = new ApiExceptionResponse(HttpStatus.BAD_REQUEST, "Você não pode acessar esse recurso");
        return new ResponseEntity<>(exceptionResponse, new HttpHeaders(), 500);
    }
}
