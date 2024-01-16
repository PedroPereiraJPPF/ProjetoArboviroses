package com.arboviroses.conectaDengue.Entities.Notification;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "notifications")
@Entity(name = "notifications")
public class Notification {
    @Id
    private long idNotification;
    private String idAgravo;
    private String dataNotification;
    private String dataNascimento;
    private String classificacao;
    private String sexo;
    private String nomeBairro;
    private String evolucao;  
}
