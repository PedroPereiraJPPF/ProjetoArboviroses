package com.arboviroses.conectaDengue.Domain.Repositories.Notifications;

import java.sql.Date;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.arboviroses.conectaDengue.Api.DTO.response.AgravoCountBySemanaEpidemiologica;
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
            Select new com.arboviroses.conectaDengue.Api.DTO.response.AgravoCountBySemanaEpidemiologica(n.semanaEpidemiologica, count(n.idNotification)) 
            from Notification as n where YEAR(n.dataNotification) = :year 
            group by n.semanaEpidemiologica
            """)
    List<AgravoCountBySemanaEpidemiologica> listarContagemPorSemanaEpidemiologica(int year);

    @Query("""
            Select new com.arboviroses.conectaDengue.Api.DTO.response.AgravoCountBySemanaEpidemiologica(n.semanaEpidemiologica, count(n.idNotification)) 
            from Notification as n
            group by n.semanaEpidemiologica
            """)
    List<AgravoCountBySemanaEpidemiologica> listarContagemPorSemanaEpidemiologica();

    @Query("""
        SELECT new com.arboviroses.conectaDengue.Api.DTO.response.BairroCountDTO(n.nomeBairro, count(n.nomeBairro)) FROM Notification as n
        group by n.nomeBairro
        order by count(n.nomeBairro) desc
        """)
    List<BairroCountDTO> listarBairrosMaisAfetados();

    @Query("""
        SELECT new com.arboviroses.conectaDengue.Api.DTO.response.BairroCountDTO(n.nomeBairro, count(n.nomeBairro)) FROM Notification as n
        where n.idAgravo = :idAgravo
        group by n.nomeBairro
        order by count(n.nomeBairro) desc
        """)
    List<BairroCountDTO> listarBairrosMaisAfetadosByIdAgravo(String idAgravo);

    
}
