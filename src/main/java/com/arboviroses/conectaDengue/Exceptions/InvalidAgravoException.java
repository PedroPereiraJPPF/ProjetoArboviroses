package com.arboviroses.conectaDengue.Exceptions;

import lombok.Getter;

@Getter
public class InvalidAgravoException extends Exception {
    private int code = 404;
    public InvalidAgravoException(String message) {
        super(message);
    }
}
