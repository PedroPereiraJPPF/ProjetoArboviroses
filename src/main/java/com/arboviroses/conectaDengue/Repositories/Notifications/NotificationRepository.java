package com.arboviroses.conectaDengue.Repositories.Notifications;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.arboviroses.conectaDengue.Entities.Notification.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> 
{
    Page<Notification> findByIdAgravo(Pageable pageable, String idAgravo);

    long countBySexo(String sexo);

    long countByIdAgravo(String idAgravo);

    long countByIdAgravoAndEvolucao(String idAgravo, String evolucao);

    long countByEvolucao(String evolucao);

    long countByidAgravoAndNomeBairro(String idAgravo, String nomeBairro);

    long countByNomeBairro(String nomeBairro);
}
