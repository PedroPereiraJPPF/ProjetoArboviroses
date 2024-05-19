package com.arboviroses.conectaDengue.Api.DTO.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private long expirationTime;
    private String fullName;

    public LoginResponse setToken(String token) {
        this.token = token;
        return this;
    }

    public LoginResponse setExpirationTime(long time) {
        this.expirationTime = time;
        return this;
    }

    public LoginResponse setFullName(String fullName) {
        this.fullName = fullName;
        return this;
    }
}
