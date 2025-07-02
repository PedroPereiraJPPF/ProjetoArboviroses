package com.arboviroses.conectaDengue.Domain.Repositories.Notifications;

import org.springframework.data.jpa.domain.Specification;

import com.arboviroses.conectaDengue.Api.DTO.response.AgravoCountBySemanaEpidemiologica;
import com.arboviroses.conectaDengue.Domain.Entities.Notification.Notification;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import java.util.List;

public class NotificationRepositoryCustomImpl implements NotificationRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<AgravoCountBySemanaEpidemiologica> buscarContagemPorSemanaEpidemiologica(Specification<Notification> spec) {
        
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<AgravoCountBySemanaEpidemiologica> query = cb.createQuery(AgravoCountBySemanaEpidemiologica.class);
        Root<Notification> root = query.from(Notification.class);

        // Aplica os filtros dinâmicos da Specification
        if (spec != null) {
            query.where(spec.toPredicate(root, query, cb));
        }

        // Define o agrupamento (GROUP BY)
        query.groupBy(root.get("semanaEpidemiologica"), root.get("idAgravo"));

        // Define a seleção para o DTO (SELECT new ...)
        query.select(cb.construct(
            AgravoCountBySemanaEpidemiologica.class,
            root.get("semanaEpidemiologica"),
            cb.count(root),
            root.get("idAgravo")
        ));
        
        // Ordena os resultados
        query.orderBy(cb.asc(root.get("semanaEpidemiologica")));

        return entityManager.createQuery(query).getResultList();
    }
}
