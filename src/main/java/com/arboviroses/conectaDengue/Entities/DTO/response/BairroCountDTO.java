package com.arboviroses.conectaDengue.Entities.DTO.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BairroCountDTO
{
    private String nomeBairro;
    private long casosReportados;
}
