package com.arboviroses.conectaDengue.Domain.Repositories.Notifications;

import java.sql.Date;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
        SELECT COUNT(n) 
        FROM Notification as n 
        WHERE (:agravoId IS NULL OR n.idAgravo = :agravoId)
        AND (:sexo IS NULL OR n.sexo = :sexo)
        AND (:nomeBairro IS NULL OR n.nomeBairro = :nomeBairro)
        AND (:year IS NULL OR YEAR(n.dataNotification) = :year)
        AND (:evolucao IS NULL OR n.evolucao = :evolucao)
    """)
    long countByOptionalParams(
        @Param("agravoId") String agravoId,
        @Param("sexo") String sexo,
        @Param("nomeBairro") String nomeBairro,
        @Param("year") Integer year,
        @Param("evolucao") String evolucao
    );

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
        SELECT n FROM Notification n WHERE YEAR(n.dataNotification) = :year and n.idAgravo = :idAgravo
        """)
    List<Notification> findByYearAndIdAgravo(int year, String idAgravo);

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
        SELECT new com.arboviroses.conectaDengue.Api.DTO.response.BairroCountDTO(
            n.nomeBairro, 
            COUNT(n.nomeBairro), 
            SUM(CASE WHEN n.evolucao = '1' THEN 1 ELSE 0 END),
            SUM(CASE WHEN n.evolucao = '2' THEN 1 ELSE 0 END),
            SUM(CASE WHEN n.evolucao = '9' THEN 1 ELSE 0 END)
        ) FROM Notification n
        WHERE (:idAgravo IS NULL OR n.idAgravo = :idAgravo)
        AND (:year IS NULL OR YEAR(n.dataNotification) = :year)
        GROUP BY n.nomeBairro
        ORDER BY COUNT(n.nomeBairro) DESC
    """)
    List<BairroCountDTO> listarBairrosMaisAfetados(
        @Param("idAgravo") String idAgravo,
        @Param("year") Integer year
    );

    @Query("""
        SELECT 
            SUM(CASE WHEN n.idadePaciente BETWEEN 0 AND 1 THEN 1 ELSE 0 END) AS age0to1,
            SUM(CASE WHEN n.idadePaciente BETWEEN 2 AND 3 THEN 1 ELSE 0 END) AS age2to3,
            SUM(CASE WHEN n.idadePaciente BETWEEN 4 AND 5 THEN 1 ELSE 0 END) AS age4to5,
            SUM(CASE WHEN n.idadePaciente BETWEEN 6 AND 7 THEN 1 ELSE 0 END) AS age6to7,
            SUM(CASE WHEN n.idadePaciente BETWEEN 8 AND 9 THEN 1 ELSE 0 END) AS age8to9,
            SUM(CASE WHEN n.idadePaciente BETWEEN 10 AND 19 THEN 1 ELSE 0 END) AS age10to19,
            SUM(CASE WHEN n.idadePaciente BETWEEN 20 AND 29 THEN 1 ELSE 0 END) AS age20to29,
            SUM(CASE WHEN n.idadePaciente BETWEEN 30 AND 39 THEN 1 ELSE 0 END) AS age30to39,
            SUM(CASE WHEN n.idadePaciente BETWEEN 40 AND 49 THEN 1 ELSE 0 END) AS age40to49,
            SUM(CASE WHEN n.idadePaciente BETWEEN 50 AND 59 THEN 1 ELSE 0 END) AS age50to59,
            SUM(CASE WHEN n.idadePaciente BETWEEN 60 AND 69 THEN 1 ELSE 0 END) AS age60to69,
            SUM(CASE WHEN n.idadePaciente BETWEEN 70 AND 79 THEN 1 ELSE 0 END) AS age70to79,
            SUM(CASE WHEN n.idadePaciente BETWEEN 80 AND 89 THEN 1 ELSE 0 END) AS age80to89,
            SUM(CASE WHEN n.idadePaciente BETWEEN 90 AND 99 THEN 1 ELSE 0 END) AS age90to99
        FROM Notification n
        WHERE (:year IS NULL OR FUNCTION('YEAR', n.dataNotification) = :year)
          AND (:idAgravo IS NULL OR n.idAgravo = :idAgravo)
          AND (:nomeBairro IS NULL Or n.nomeBairro = :nomeBairro)
    """)
    Map<String, Integer> listarContagemPorFaixaDeIdadeComFiltros(
        @Param("idAgravo") String idAgravo,
        @Param("year") Integer year,
        @Param("nomeBairro") String nomeBairro
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
        FROM Notification AS n where n.idAgravo = :idAgravo and YEAR(n.dataNotification) = :year
    """)
    Map<String, Integer> listarDadosEspecificadosDasNotificacoes(String idAgravo, int year);

    @Query("""
        SELECT count(n.idNotification) FROM Notification as n where n.evolucao = :evolucao and n.nomeBairro = :bairro
        """)
    long countByEvolucaoAndBairro(String evolucao, String bairro);

    @Query("""
        SELECT count(n.idNotification) FROM Notification as n where n.evolucao = :evolucao and YEAR(n.dataNotification) = :year and n.nomeBairro = :bairro
        """)
    long countByEvolucaoAndYearAndBairro(String evolucao, int year, String bairro);

    @Query("""
        SELECT count(n.idNotification) FROM Notification as n where n.idAgravo = :idAgravo and n.evolucao = :evolucao and n.nomeBairro = :bairro
        """)
    long countByIdAgravoAndEvolucaoAndBairro(String idAgravo, String evolucao, String bairro);

    @Query("""
        SELECT count(n.idNotification) FROM Notification as n where n.idAgravo = :idAgravo and n.evolucao = :evolucao and YEAR(n.dataNotification) = :year and n.nomeBairro = :bairro
        """)
    long countByIdAgravoAndEvolucaoAndYearAndBairro(String idAgravo, String evolucao, long year, String bairro);

    @Query("""
        SELECT count(n.idNotification) FROM Notification as n where YEAR(n.dataNotification) = :year and n.nomeBairro = :bairro
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
        SELECT count(n.idNotification) FROM Notification as n where n.idAgravo = :idAgravo and n.sexo = :sexo and YEAR(n.dataNotification) = :year and n.nomeBairro = :bairro
        """)
    long countByIdAgravoAndSexoAndYearAndBairro(String idAgravo, String sexo, long year, String bairro);

    @Query("""
        SELECT count(n.idNotification) FROM Notification as n where n.sexo = :sexo and YEAR(n.dataNotification) = :year and n.nomeBairro = :bairro
        """)
    long countBySexoAndYearAndBairro(String sexo, long year, String bairro);

    @Query("""
        SELECT new com.arboviroses.conectaDengue.Api.DTO.response.AgravoCountBySemanaEpidemiologica(n.semanaEpidemiologica, count(n.idNotification), n.idAgravo) 
        from Notification as n where n.nomeBairro = :bairro
        group by n.semanaEpidemiologica, n.idAgravo
        ORDER BY n.semanaEpidemiologica
        """)
    List<AgravoCountBySemanaEpidemiologica> listarContagemPorSemanaEpidemiologicaByBairro(String bairro);

    @Query("""
        SELECT new com.arboviroses.conectaDengue.Api.DTO.response.AgravoCountBySemanaEpidemiologica(n.semanaEpidemiologica, count(n.idNotification), n.idAgravo) 
        from Notification as n where YEAR(n.dataNotification) = :year and n.nomeBairro = :bairro
        group by n.semanaEpidemiologica, n.idAgravo
        ORDER BY n.semanaEpidemiologica
        """)
    List<AgravoCountBySemanaEpidemiologica> listarContagemPorSemanaEpidemiologicaByYearAndBairro(int year, String bairro);

    @Query("""
        SELECT new com.arboviroses.conectaDengue.Api.DTO.response.AgravoCountBySemanaEpidemiologica(n.semanaEpidemiologica, count(n.idNotification), n.idAgravo) 
        from Notification as n where YEAR(n.dataNotification) = :year and n.idAgravo = :agravo and n.nomeBairro = :bairro
        group by n.semanaEpidemiologica, n.idAgravo
        ORDER BY n.semanaEpidemiologica
        """)
    List<AgravoCountBySemanaEpidemiologica> listarContagemPorSemanaEpidemiologicaByAgravoAndYearAndBairro(String agravo, int year, String bairro);

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
        FROM Notification as n where YEAR(n.dataNotification) = :year and n.nomeBairro = :bairro
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
        FROM Notification as n where YEAR(n.dataNotification) = :year and n.idAgravo = :idAgravo and n.nomeBairro = :bairro
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
        FROM Notification AS n where n.idAgravo = :idAgravo and YEAR(n.dataNotification) = :year and n.nomeBairro = :bairro
    """)
    Map<String, Integer> listarDadosEspecificadosDasNotificacoesByIdAgravoAndYearAndBairro(String idAgravo, int year, String bairro);
}
