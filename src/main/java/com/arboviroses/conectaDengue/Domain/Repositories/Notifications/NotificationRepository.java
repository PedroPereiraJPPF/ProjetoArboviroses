package com.arboviroses.conectaDengue.Domain.Repositories.Notifications;

import java.sql.Date;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.arboviroses.conectaDengue.Api.DTO.response.BairroCountDTO;
import com.arboviroses.conectaDengue.Domain.Entities.Notification.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> 
{
    Page<Notification> findByIdAgravo(Pageable pageable, String idAgravo);

    long countBySexo(String sexo);

    long countByIdAgravoAndSexo(String idAgravo, String sexo);

    long countByIdAgravo(String idAgravo);

    long countByIdAgravoAndEvolucao(String idAgravo, String evolucao);

    long countByEvolucao(String evolucao);

    long countByidAgravoAndNomeBairro(String idAgravo, String nomeBairro);

    long countByNomeBairro(String nomeBairro);

    long countByDataNascimentoBetween(Date dataInicio, Date dataFinal);

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
