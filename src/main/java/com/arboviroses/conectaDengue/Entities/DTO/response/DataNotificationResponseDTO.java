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
    private long idNotification;
    private String idAgravo;
    private String dataNotification;
    private String dataNascimento;
    private String classificacao;
    private String sexo;
    private String nomeBairro;
    private String evolucao;  

    public DataNotificationResponseDTO(Notification notification)
    {
        this.idNotification = notification.getIdNotification();
        this.idAgravo = notification.getIdAgravo();
        this.dataNotification = notification.getDataNotification();
        this.dataNascimento = notification.getDataNascimento();
        this.classificacao = notification.getClassificacao();
        this.sexo = notification.getSexo();
        this.nomeBairro = notification.getNomeBairro();
        this.evolucao = notification.getEvolucao();
    }
}
