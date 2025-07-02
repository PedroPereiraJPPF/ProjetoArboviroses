package com.arboviroses.conectaDengue.Domain.Repositories.Notifications;

import java.sql.Date;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.arboviroses.conectaDengue.Api.DTO.response.AgravoCountBySemanaEpidemiologica;
import com.arboviroses.conectaDengue.Api.DTO.response.BairroCountDTO;
import com.arboviroses.conectaDengue.Domain.Entities.Notification.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long>, JpaSpecificationExecutor<Notification>, NotificationRepositoryCustom
{
    Page<Notification> findByIdAgravo(Pageable pageable, String idAgravo);

    long count();

    long countBySexo(String sexo);

    long countByIdAgravoAndSexo(String idAgravo, String sexo);

    long countByIdAgravoAndDataNotification(String idAgravo, Date date);

    @Query("""
            SELECT count(n.idNotification) FROM Notification as n where n.idAgravo = :agravo and FUNCTION('YEAR', n.dataNotification) = :year 
    """)
    long countByIdAgravoAndYearNotification(String agravo, long year);

    @Query("""
            SELECT count(n.idNotification) FROM Notification as n where FUNCTION('YEAR', n.dataNotification) = :year 
    """)
    long countByYearNotification( long year);

    @Query("""
            SELECT count(n.idNotification) FROM Notification as n where n.sexo = :sexo and FUNCTION('YEAR', n.dataNotification) = :year 
    """)
    long countBySexoAndYearNotification(String sexo, long year);

    @Query("""
            SELECT count(n.idNotification) FROM Notification as n where n.idAgravo = :idAgravo and n.sexo = :sexo and FUNCTION('YEAR', n.dataNotification) = :year
    """)
    long countByIdAgravoAndSexoAndYearNotification(String idAgravo, String sexo, long year);

    long countByIdAgravo(String idAgravo);

    long countByEvolucao(String evolucao);

    long countByIdAgravoAndEvolucao(String idAgravo, String evolucao);

    @Query("""
            SELECT n FROM Notification n WHERE FUNCTION('YEAR', n.dataNotification) = :year and n.idAgravo = :idAgravo
            """)
    List<Notification> findByYearAndIdAgravo(int year, String idAgravo);

    @Query("""
            SELECT COUNT(n.idNotification) FROM Notification as n where n.evolucao = :evolucao and FUNCTION('YEAR', n.dataNotification) = :year
            """)
    long countByEvolucaoAndYear(String evolucao, int year);

    @Query("""
            SELECT count(n.idNotification) FROM Notification as n where n.idAgravo = :idAgravo and n.evolucao = :evolucao and FUNCTION('YEAR', n.dataNotification) = :year
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
            from Notification as n where FUNCTION('YEAR', n.dataNotification) = :year 
            group by n.semanaEpidemiologica, n.idAgravo
            ORDER BY n.semanaEpidemiologica
            """)
    List<AgravoCountBySemanaEpidemiologica> listarContagemPorSemanaEpidemiologica(int year);

    @Query("""
            Select new com.arboviroses.conectaDengue.Api.DTO.response.AgravoCountBySemanaEpidemiologica(n.semanaEpidemiologica, count(n.idNotification), n.idAgravo) 
            from Notification as n where FUNCTION('YEAR', n.dataNotification) = :year and n.idAgravo = :agravo
            group by n.semanaEpidemiologica, n.idAgravo
            ORDER BY n.semanaEpidemiologica
            """)
    List<AgravoCountBySemanaEpidemiologica> listarContagemPorSemanaEpidemiologica(String agravo, int year);

    @Query("""
            SELECT new com.arboviroses.conectaDengue.Api.DTO.response.BairroCountDTO(
                n.nomeBairro, 
                COUNT(n.nomeBairro), 
                SUM(CASE WHEN n.evolucao = '1' THEN 1 ELSE 0 END),
                SUM(CASE WHEN n.evolucao = '2' THEN 1 ELSE 0 END),
                SUM(CASE WHEN n.evolucao = '9' THEN 1 ELSE 0 END)
            ) FROM Notification n
            WHERE (:idAgravo IS NULL OR n.idAgravo = :idAgravo)
            AND (:year IS NULL OR FUNCTION('YEAR', n.dataNotification) = :year)
            GROUP BY n.nomeBairro
            ORDER BY COUNT(n.nomeBairro) DESC
    """)
    List<BairroCountDTO> listarBairrosMaisAfetados(
        @Param("idAgravo") String idAgravo,
        @Param("year") Integer year
    );
    
    @Query("""
            SELECT 
                sum(case when n.classificacao = '1' then 1 else 0 end) AS confirmados,
                sum(case when n.classificacao = '2' then 1 else 0 end) AS descartados,
                sum(case when n.evolucao = '1' then 1 else 0 end) AS curados,
                sum(case when n.evolucao = '2' then 1 else 0 end) AS morte_por_agravo,
                sum(case when n.evolucao = '3' then 1 else 0 end) AS morte_outras_causas,
                sum(case when n.evolucao = '4' then 1 else 0 end) AS ignorados
            FROM Notification AS n
    """)
    Map<String, Integer> listarDadosEspecificadosDasNotificacoes();

    @Query("""
            SELECT 
                sum(case when n.classificacao = '1' then 1 else 0 end) AS confirmados,
                sum(case when n.classificacao = '2' then 1 else 0 end) AS descartados,
                sum(case when n.evolucao = '1' then 1 else 0 end) AS curados,
                sum(case when n.evolucao = '2' then 1 else 0 end) AS morte_por_agravo,
                sum(case when n.evolucao = '3' then 1 else 0 end) AS morte_outras_causas,
                sum(case when n.evolucao = '4' then 1 else 0 end) AS ignorados
            FROM Notification AS n where n.idAgravo = :idAgravo
    """)
    Map<String, Integer> listarDadosEspecificadosDasNotificacoes(String idAgravo);

    @Query("""
            SELECT 
                sum(case when n.classificacao = '1' then 1 else 0 end) AS confirmados,
                sum(case when n.classificacao = '2' then 1 else 0 end) AS descartados,
                sum(case when n.evolucao = '1' then 1 else 0 end) AS curados,
                sum(case when n.evolucao = '2' then 1 else 0 end) AS morte_por_agravo,
                sum(case when n.evolucao = '3' then 1 else 0 end) AS morte_outras_causas,
                sum(case when n.evolucao = '4' then 1 else 0 end) AS ignorados
            FROM Notification AS n where n.idAgravo = :idAgravo and FUNCTION('YEAR', n.dataNotification) = :year
    """)
    Map<String, Integer> listarDadosEspecificadosDasNotificacoes(String idAgravo, int year);

    @Query("""
            SELECT count(n.idNotification) FROM Notification as n where n.evolucao = :evolucao and n.nomeBairro = :bairro
            """)
    long countByEvolucaoAndBairro(String evolucao, String bairro);

    @Query("""
            SELECT count(n.idNotification) FROM Notification as n where n.evolucao = :evolucao and FUNCTION('YEAR', n.dataNotification) = :year and n.nomeBairro = :bairro
            """)
    long countByEvolucaoAndYearAndBairro(String evolucao, int year, String bairro);

    @Query("""
            SELECT count(n.idNotification) FROM Notification as n where n.idAgravo = :idAgravo and n.evolucao = :evolucao and n.nomeBairro = :bairro
            """)
    long countByIdAgravoAndEvolucaoAndBairro(String idAgravo, String evolucao, String bairro);

    @Query("""
            SELECT count(n.idNotification) FROM Notification as n where n.idAgravo = :idAgravo and n.evolucao = :evolucao and FUNCTION('YEAR', n.dataNotification) = :year and n.nomeBairro = :bairro
            """)
    long countByIdAgravoAndEvolucaoAndYearAndBairro(String idAgravo, String evolucao, long year, String bairro);

    @Query("""
            SELECT count(n.idNotification) FROM Notification as n where FUNCTION('YEAR', n.dataNotification) = :year and n.nomeBairro = :bairro
            """)
    long countByYearAndBairro(long year, String bairro);

    @Query("""
            SELECT count(n.idNotification) FROM Notification as n where n.sexo = :sexo and n.nomeBairro = :bairro
            """)
    long countBySexoAndBairro(String sexo, String bairro);

    @Query("""
            SELECT count(n.idNotification) FROM Notification as n where n.idAgravo = :idAgravo and n.sexo = :sexo and n.nomeBairro = :bairro
            """)
    long countByIdAgravoAndSexoAndBairro(String idAgravo, String sexo, String bairro);

    @Query("""
            SELECT count(n.idNotification) FROM Notification as n where n.idAgravo = :idAgravo and n.sexo = :sexo and FUNCTION('YEAR', n.dataNotification) = :year and n.nomeBairro = :bairro
            """)
    long countByIdAgravoAndSexoAndYearAndBairro(String idAgravo, String sexo, long year, String bairro);

    @Query("""
            SELECT count(n.idNotification) FROM Notification as n where n.sexo = :sexo and FUNCTION('YEAR', n.dataNotification) = :year and n.nomeBairro = :bairro
            """)
    long countBySexoAndYearAndBairro(String sexo, long year, String bairro);

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
            FROM Notification as n where n.nomeBairro = :bairro
    """)
    Map<String, Integer> listarContagemPorFaixaDeIdadeByBairro(String bairro);

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
            FROM Notification as n where FUNCTION('YEAR', n.dataNotification) = :year and n.nomeBairro = :bairro
    """)
    Map<String, Integer> listarContagemPorFaixaDeIdadeByYearAndBairro(int year, String bairro);

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
            FROM Notification as n where FUNCTION('YEAR', n.dataNotification) = :year and n.idAgravo = :idAgravo and n.nomeBairro = :bairro
    """)
    Map<String, Integer> listarContagemPorFaixaDeIdadeByIdAgravoAndYearAndBairro(String idAgravo, int year, String bairro);

    @Query("""
            SELECT 
                sum(case when n.classificacao = '1' then 1 else 0 end) AS confirmados,
                sum(case when n.classificacao = '2' then 1 else 0 end) AS descartados,
                sum(case when n.evolucao = '1' then 1 else 0 end) AS curados,
                sum(case when n.evolucao = '2' then 1 else 0 end) AS morte_por_agravo,
                sum(case when n.evolucao = '3' then 1 else 0 end) AS morte_outras_causas,
                sum(case when n.evolucao = '4' then 1 else 0 end) AS ignorados
            FROM Notification AS n where n.nomeBairro = :bairro
    """)
    Map<String, Integer> listarDadosEspecificadosDasNotificacoesByBairro(String bairro);

    @Query("""
            SELECT 
                sum(case when n.classificacao = '1' then 1 else 0 end) AS confirmados,
                sum(case when n.classificacao = '2' then 1 else 0 end) AS descartados,
                sum(case when n.evolucao = '1' then 1 else 0 end) AS curados,
                sum(case when n.evolucao = '2' then 1 else 0 end) AS morte_por_agravo,
                sum(case when n.evolucao = '3' then 1 else 0 end) AS morte_outras_causas,
                sum(case when n.evolucao = '4' then 1 else 0 end) AS ignorados
            FROM Notification AS n where n.idAgravo = :idAgravo and n.nomeBairro = :bairro
    """)
    Map<String, Integer> listarDadosEspecificadosDasNotificacoesByIdAgravoAndBairro(String idAgravo, String bairro);

    @Query("""
            SELECT 
                sum(case when n.classificacao = '1' then 1 else 0 end) AS confirmados,
                sum(case when n.classificacao = '2' then 1 else 0 end) AS descartados,
                sum(case when n.evolucao = '1' then 1 else 0 end) AS curados,
                sum(case when n.evolucao = '2' then 1 else 0 end) AS morte_por_agravo,
                sum(case when n.evolucao = '3' then 1 else 0 end) AS morte_outras_causas,
                sum(case when n.evolucao = '4' then 1 else 0 end) AS ignorados
            FROM Notification AS n where n.idAgravo = :idAgravo and FUNCTION('YEAR', n.dataNotification) = :year and n.nomeBairro = :bairro
    """)
    Map<String, Integer> listarDadosEspecificadosDasNotificacoesByIdAgravoAndYearAndBairro(String idAgravo, int year, String bairro);
}