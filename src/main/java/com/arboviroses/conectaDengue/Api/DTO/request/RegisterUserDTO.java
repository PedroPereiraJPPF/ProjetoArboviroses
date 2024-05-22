package com.arboviroses.conectaDengue.Api.DTO.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterUserDTO {
    private String cpf;
    
    private String password;
    
    private String fullName;

    public RegisterUserDTO setCpf(String cpf)
    {
        this.cpf = cpf.replaceAll("\\D", "");

        return this;
    }
}