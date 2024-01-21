package com.arboviroses.conectaDengue.Entities.Notification;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "notifications")
@Entity(name = "Notification")
public class Notification {
    @Id
    private long idNotification;
    private String idAgravo;
    private Date dataNotification;
    private Date dataNascimento;
    private String classificacao;
    private String sexo;
    private String nomeBairro;
    private String evolucao;  
}
