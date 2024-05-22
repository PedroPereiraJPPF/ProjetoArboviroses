package com.arboviroses.conectaDengue.Api.DTO.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginUserDTO {
    private String cpf;
    
    private String password;

    public LoginUserDTO setCpf(String cpf)
    {
        this.cpf = cpf.replaceAll("\\D", "");

        return this;
    }
}
