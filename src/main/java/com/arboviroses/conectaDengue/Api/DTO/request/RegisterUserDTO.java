package com.arboviroses.conectaDengue.Api.DTO.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterUserDTO {
    private String cpf;
    
    private String password;
    
    private String fullName;
}