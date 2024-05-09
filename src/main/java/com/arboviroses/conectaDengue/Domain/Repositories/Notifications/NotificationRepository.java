package com.arboviroses.conectaDengue.Domain.Repositories.Notifications;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.arboviroses.conectaDengue.Api.DTO.AgeGroupCountDTO;
import com.arboviroses.conectaDengue.Api.DTO.response.AgravoCountBySemanaEpidemiologica;
import com.arboviroses.conectaDengue.Api.DTO.response.BairroCountDTO;
import com.arboviroses.conectaDengue.Domain.Entities.Notification.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> 
{
    Page<Notification> findByIdAgravo(Pageable pageable, String idAgravo);

    long countBySexo(String sexo);

    long countByIdAgravoAndSexo(String idAgravo, String sexo);

    long countByIdAgravoAndDataNotification(String idAgravo, Date date);

    @Query("""
        SELECT count(n.idNotification) FROM Notification as n where n.sexo = :sexo and YEAR(n.dataNotification) = :year 
    """)
    long countBySexoAndYearNotification(String sexo, long year);

    @Query("""
        SELECT count(n.idNotification) FROM Notification as n where n.idAgravo = :idAgravo and n.sexo = :sexo and YEAR(n.dataNotification) = :year
    """)
    long countByIdAgravoAndSexoAndYearNotification(String idAgravo, String sexo, long year);

    long countByIdAgravo(String idAgravo);

    long countByIdAgravoAndEvolucao(String idAgravo, String evolucao);

    long countByEvolucao(String evolucao);

    long countByidAgravoAndNomeBairro(String idAgravo, String nomeBairro);

    long countByNomeBairro(String nomeBairro);

    long countByDataNascimentoBetween(Date dataInicio, Date dataFinal);

    @Query("""
            Select new com.arboviroses.conectaDengue.Api.DTO.response.AgravoCountBySemanaEpidemiologica(n.semanaEpidemiologica, count(n.idNotification), n.idAgravo) 
            from Notification as n
            group by n.semanaEpidemiologica, n.idAgravo
            ORDER BY n.semanaEpidemiologica
            """)
    List<AgravoCountBySemanaEpidemiologica> listarContagemPorSemanaEpidemiologica();

    @Query("""
            Select new com.arboviroses.conectaDengue.Api.DTO.response.AgravoCountBySemanaEpidemiologica(n.semanaEpidemiologica, count(n.idNotification), n.idAgravo) 
            from Notification as n where YEAR(n.dataNotification) = :year 
            group by n.semanaEpidemiologica, n.idAgravo
            ORDER BY n.semanaEpidemiologica
            """)
    List<AgravoCountBySemanaEpidemiologica> listarContagemPorSemanaEpidemiologica(int year);

    @Query("""
            Select new com.arboviroses.conectaDengue.Api.DTO.response.AgravoCountBySemanaEpidemiologica(n.semanaEpidemiologica, count(n.idNotification), n.idAgravo) 
            from Notification as n where YEAR(n.dataNotification) = :year and n.idAgravo = :agravo
            group by n.semanaEpidemiologica, n.idAgravo
            ORDER BY n.semanaEpidemiologica
            """)
    List<AgravoCountBySemanaEpidemiologica> listarContagemPorSemanaEpidemiologica(String agravo, int year);

    @Query("""
        SELECT new com.arboviroses.conectaDengue.Api.DTO.response.BairroCountDTO(n.nomeBairro, count(n.nomeBairro)) FROM Notification as n
        group by n.nomeBairro
        order by count(n.nomeBairro) desc
        """)
    List<BairroCountDTO> listarBairrosMaisAfetados();

    @Query("""
        SELECT new com.arboviroses.conectaDengue.Api.DTO.response.BairroCountDTO(n.nomeBairro, count(n.nomeBairro)) FROM Notification as n
        where YEAR(n.dataNotification) = :year
        group by n.nomeBairro
        order by count(n.nomeBairro) desc
        """)
    List<BairroCountDTO> listarBairrosMaisAfetadosByYear(int year);

    @Query("""
        SELECT new com.arboviroses.conectaDengue.Api.DTO.response.BairroCountDTO(n.nomeBairro, count(n.nomeBairro)) FROM Notification as n
        where n.idAgravo = :idAgravo
        and YEAR(n.dataNotification) = :year
        group by n.nomeBairro
        order by count(n.nomeBairro) desc
        """)
    List<BairroCountDTO> listarBairrosMaisAfetadosByIdAgravoAndYear(String idAgravo, int year);

    @Query("""
        SELECT
            SUM(CASE WHEN idade BETWEEN 0 AND 10 THEN 1 ELSE 0 END) AS age0to10,
            SUM(CASE WHEN idade BETWEEN 11 AND 20 THEN 1 ELSE 0 END) AS age11to20,
            SUM(CASE WHEN idade BETWEEN 21 AND 30 THEN 1 ELSE 0 END) AS age21to30,
            SUM(CASE WHEN idade BETWEEN 31 AND 40 THEN 1 ELSE 0 END) AS age31to40,
            SUM(CASE WHEN idade BETWEEN 41 AND 50 THEN 1 ELSE 0 END) AS age41to50,
            SUM(CASE WHEN idade BETWEEN 51 AND 60 THEN 1 ELSE 0 END) AS age51to60,
            SUM(CASE WHEN idade > 60 THEN 1 ELSE 0 END) AS ageOver60
        FROM (
            SELECT TIMESTAMPDIFF(YEAR, n.dataNascimento, CURDATE()) AS idade
            FROM Notification as n
        ) AS tabela_idades
    """)
    Map<String, Integer> listarContagemPorFaixaDeIdade();

    @Query("""
        SELECT
            SUM(CASE WHEN idade BETWEEN 0 AND 10 THEN 1 ELSE 0 END) AS age0to10,
            SUM(CASE WHEN idade BETWEEN 11 AND 20 THEN 1 ELSE 0 END) AS age11to20,
            SUM(CASE WHEN idade BETWEEN 21 AND 30 THEN 1 ELSE 0 END) AS age21to30,
            SUM(CASE WHEN idade BETWEEN 31 AND 40 THEN 1 ELSE 0 END) AS age31to40,
            SUM(CASE WHEN idade BETWEEN 41 AND 50 THEN 1 ELSE 0 END) AS age41to50,
            SUM(CASE WHEN idade BETWEEN 51 AND 60 THEN 1 ELSE 0 END) AS age51to60,
            SUM(CASE WHEN idade > 60 THEN 1 ELSE 0 END) AS ageOver60
        FROM (
            SELECT TIMESTAMPDIFF(YEAR, n.dataNascimento, CURDATE()) AS idade
            FROM Notification as n where YEAR(n.dataNotification) = :year
        ) AS tabela_idades
    """)
    Map<String, Integer> listarContagemPorFaixaDeIdade(int year);

    @Query("""
        SELECT
            SUM(CASE WHEN idade BETWEEN 0 AND 10 THEN 1 ELSE 0 END) AS age0to10,
            SUM(CASE WHEN idade BETWEEN 11 AND 20 THEN 1 ELSE 0 END) AS age11to20,
            SUM(CASE WHEN idade BETWEEN 21 AND 30 THEN 1 ELSE 0 END) AS age21to30,
            SUM(CASE WHEN idade BETWEEN 31 AND 40 THEN 1 ELSE 0 END) AS age31to40,
            SUM(CASE WHEN idade BETWEEN 41 AND 50 THEN 1 ELSE 0 END) AS age41to50,
            SUM(CASE WHEN idade BETWEEN 51 AND 60 THEN 1 ELSE 0 END) AS age51to60,
            SUM(CASE WHEN idade > 60 THEN 1 ELSE 0 END) AS ageOver60
        FROM (
            SELECT TIMESTAMPDIFF(YEAR, n.dataNascimento, CURDATE()) AS idade
            FROM Notification as n where YEAR(n.dataNotification) = :year and n.idAgravo = :idAgravo
        ) AS tabela_idades
    """)
    Map<String, Integer> listarContagemPorFaixaDeIdadeByIdAgravo(String idAgravo, int year);
}
