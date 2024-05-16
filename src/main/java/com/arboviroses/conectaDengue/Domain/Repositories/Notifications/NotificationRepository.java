package com.arboviroses.conectaDengue.Domain.Repositories.Notifications;

import java.sql.Date;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.arboviroses.conectaDengue.Api.DTO.response.AgravoCountBySemanaEpidemiologica;
import com.arboviroses.conectaDengue.Api.DTO.response.BairroCountDTO;
import com.arboviroses.conectaDengue.Domain.Entities.Notification.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> 
{
    Page<Notification> findByIdAgravo(Pageable pageable, String idAgravo);

    long count();

    long countBySexo(String sexo);

    long countByIdAgravoAndSexo(String idAgravo, String sexo);

    long countByIdAgravoAndDataNotification(String idAgravo, Date date);

    @Query("""
        SELECT count(n.idNotification) FROM Notification as n where n.idAgravo = :agravo and YEAR(n.dataNotification) = :year 
    """)
    long countByIdAgravoAndYearNotification(String agravo, long year);

    @Query("""
        SELECT count(n.idNotification) FROM Notification as n where YEAR(n.dataNotification) = :year 
    """)
    long countByYearNotification( long year);

    @Query("""
        SELECT count(n.idNotification) FROM Notification as n where n.sexo = :sexo and YEAR(n.dataNotification) = :year 
    """)
    long countBySexoAndYearNotification(String sexo, long year);

    @Query("""
        SELECT count(n.idNotification) FROM Notification as n where n.idAgravo = :idAgravo and n.sexo = :sexo and YEAR(n.dataNotification) = :year
    """)
    long countByIdAgravoAndSexoAndYearNotification(String idAgravo, String sexo, long year);

    long countByIdAgravo(String idAgravo);

    long countByEvolucao(String evolucao);

    long countByIdAgravoAndEvolucao(String idAgravo, String evolucao);

    @Query("""
        SELECT COUNT(n.idNotification) FROM Notification as n where n.evolucao = :evolucao and YEAR(n.dataNotification) = :year
        """)
    long countByEvolucaoAndYear(String evolucao, int year);

    @Query("""
        SELECT count(n.idNotification) FROM Notification as n where n.idAgravo = :idAgravo and n.evolucao = :evolucao and YEAR(n.dataNotification) = :year
            """)
    long countByIdAgravoAndEvolucaoAndYear(String idAgravo, String evolucao, long year);

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
            SUM(CASE WHEN n.idadePaciente BETWEEN 1 AND 10 THEN 1 ELSE 0 END) AS age1to10,
            SUM(CASE WHEN n.idadePaciente BETWEEN 11 AND 20 THEN 1 ELSE 0 END) AS age11to20,
            SUM(CASE WHEN n.idadePaciente BETWEEN 21 AND 30 THEN 1 ELSE 0 END) AS age21to30,
            SUM(CASE WHEN n.idadePaciente BETWEEN 31 AND 40 THEN 1 ELSE 0 END) AS age31to40,
            SUM(CASE WHEN n.idadePaciente BETWEEN 41 AND 50 THEN 1 ELSE 0 END) AS age41to50,
            SUM(CASE WHEN n.idadePaciente BETWEEN 51 AND 60 THEN 1 ELSE 0 END) AS age51to60,
            SUM(CASE WHEN n.idadePaciente BETWEEN 61 AND 70 THEN 1 ELSE 0 END) AS age61to70,
            SUM(CASE WHEN n.idadePaciente BETWEEN 71 AND 80 THEN 1 ELSE 0 END) AS age71to80,
            SUM(CASE WHEN n.idadePaciente BETWEEN 81 AND 90 THEN 1 ELSE 0 END) AS age81to90,
            SUM(CASE WHEN n.idadePaciente BETWEEN 91 AND 99 THEN 1 ELSE 0 END) AS age91to99
        FROM Notification as n
    """)
    Map<String, Integer> listarContagemPorFaixaDeIdade();

    @Query("""
        SELECT 
            SUM(CASE WHEN n.idadePaciente BETWEEN 1 AND 10 THEN 1 ELSE 0 END) AS age1to10,
            SUM(CASE WHEN n.idadePaciente BETWEEN 11 AND 20 THEN 1 ELSE 0 END) AS age11to20,
            SUM(CASE WHEN n.idadePaciente BETWEEN 21 AND 30 THEN 1 ELSE 0 END) AS age21to30,
            SUM(CASE WHEN n.idadePaciente BETWEEN 31 AND 40 THEN 1 ELSE 0 END) AS age31to40,
            SUM(CASE WHEN n.idadePaciente BETWEEN 41 AND 50 THEN 1 ELSE 0 END) AS age41to50,
            SUM(CASE WHEN n.idadePaciente BETWEEN 51 AND 60 THEN 1 ELSE 0 END) AS age51to60,
            SUM(CASE WHEN n.idadePaciente BETWEEN 61 AND 70 THEN 1 ELSE 0 END) AS age61to70,
            SUM(CASE WHEN n.idadePaciente BETWEEN 71 AND 80 THEN 1 ELSE 0 END) AS age71to80,
            SUM(CASE WHEN n.idadePaciente BETWEEN 81 AND 90 THEN 1 ELSE 0 END) AS age81to90,
            SUM(CASE WHEN n.idadePaciente BETWEEN 91 AND 99 THEN 1 ELSE 0 END) AS age91to99
        FROM Notification as n where YEAR(n.dataNotification) = :year
    """)
    Map<String, Integer> listarContagemPorFaixaDeIdade(int year);

    @Query("""
        SELECT 
            SUM(CASE WHEN n.idadePaciente BETWEEN 1 AND 10 THEN 1 ELSE 0 END) AS age1to10,
            SUM(CASE WHEN n.idadePaciente BETWEEN 11 AND 20 THEN 1 ELSE 0 END) AS age11to20,
            SUM(CASE WHEN n.idadePaciente BETWEEN 21 AND 30 THEN 1 ELSE 0 END) AS age21to30,
            SUM(CASE WHEN n.idadePaciente BETWEEN 31 AND 40 THEN 1 ELSE 0 END) AS age31to40,
            SUM(CASE WHEN n.idadePaciente BETWEEN 41 AND 50 THEN 1 ELSE 0 END) AS age41to50,
            SUM(CASE WHEN n.idadePaciente BETWEEN 51 AND 60 THEN 1 ELSE 0 END) AS age51to60,
            SUM(CASE WHEN n.idadePaciente BETWEEN 61 AND 70 THEN 1 ELSE 0 END) AS age61to70,
            SUM(CASE WHEN n.idadePaciente BETWEEN 71 AND 80 THEN 1 ELSE 0 END) AS age71to80,
            SUM(CASE WHEN n.idadePaciente BETWEEN 81 AND 90 THEN 1 ELSE 0 END) AS age81to90,
            SUM(CASE WHEN n.idadePaciente BETWEEN 91 AND 99 THEN 1 ELSE 0 END) AS age91to99
        FROM Notification as n where YEAR(n.dataNotification) = :year and idAgravo = :idAgravo
    """)
    Map<String, Integer> listarContagemPorFaixaDeIdadeByIdAgravo(String idAgravo, int year);
}
