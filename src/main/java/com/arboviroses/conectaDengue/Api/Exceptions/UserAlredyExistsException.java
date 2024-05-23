package com.arboviroses.conectaDengue.Api.Exceptions;

public class UserAlredyExistsException extends Exception {
    private int code = 404;
    
    public UserAlredyExistsException(String message) {
        super(message);
    }
}
