package com.arboviroses.conectaDengue.Entities.DTO.response;

import com.arboviroses.conectaDengue.Entities.Notification.Notification;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DataNotificationResponseDTO {
    private long id_notification;
    private String id_agravo;
    private String data_notification;
    private String data_nascimento;
    private String classificacao;
    private String sexo;
    private String nome_bairro;
    private String evolucao;  
}
