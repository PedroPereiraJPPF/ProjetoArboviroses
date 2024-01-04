package com.arboviroses.conectaDengue.Entities.Notification;

import java.text.ParseException;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
@Entity
@Table(name = "notifications")
public class Notification {
    @Id
    private long id_notification;
    private String id_agravo;
    private String data_notification;
    private String data_nascimento;
    private String classificacao;
    private String sexo;
    private String nome_bairro;
    private String evolucao;
}
