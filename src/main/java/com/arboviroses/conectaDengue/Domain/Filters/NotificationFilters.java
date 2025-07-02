package com.arboviroses.conectaDengue.Domain.Filters;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.jpa.domain.Specification;

import com.arboviroses.conectaDengue.Api.DTO.response.AgravoCountBySemanaEpidemiologica;
import com.arboviroses.conectaDengue.Api.DTO.response.BairroCountDTO;
import com.arboviroses.conectaDengue.Api.DTO.response.CountAgravoBySexoDTO;
import com.arboviroses.conectaDengue.Api.Exceptions.InvalidAgravoException;
import com.arboviroses.conectaDengue.Domain.Entities.Notification.Notification;
import com.arboviroses.conectaDengue.Domain.Repositories.Notifications.NotificationRepository;
import com.arboviroses.conectaDengue.Utils.ConvertNameToIdAgravo;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.Predicate;
import jakarta.servlet.http.HttpServletRequest;

public class NotificationFilters {
    @PersistenceContext
    private EntityManager entityManager;

    public static CountAgravoBySexoDTO filtersForNotificationsInfoBySexo(HttpServletRequest request, NotificationRepository notificationRepository) throws InvalidAgravoException {
        String agravoName = request.getParameter("agravo");
        String bairro = request.getParameter("bairro");
        Integer year = request.getParameter("year") != null ? Integer.valueOf(request.getParameter("year")) : null;
        String agravoId = null;

        if (agravoName != null && !agravoName.isEmpty()) {
            agravoId = ConvertNameToIdAgravo.convert(agravoName);
        }

        final String finalAgravoId = agravoId;
        
        Specification<Notification> baseSpec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (finalAgravoId != null) {
                predicates.add(criteriaBuilder.equal(root.get("idAgravo"), finalAgravoId));
            }
            if (bairro != null && !bairro.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("nomeBairro"), bairro));
            }
            if (year != null) {
                predicates.add(criteriaBuilder.equal(criteriaBuilder.function("YEAR", Integer.class, root.get("dataNotification")), year));
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        Specification<Notification> specMasculino = baseSpec.and((root, query, criteriaBuilder) -> 
            criteriaBuilder.equal(root.get("sexo"), "M")
        );

        Specification<Notification> specFeminino = baseSpec.and((root, query, criteriaBuilder) -> 
            criteriaBuilder.equal(root.get("sexo"), "F")
        );
        
        return new CountAgravoBySexoDTO(
            notificationRepository.count(specMasculino),
            notificationRepository.count(specFeminino)
        );
    }

    public static List<AgravoCountBySemanaEpidemiologica> filtersForNotificationsInfoBySemanaEpidemiologica(HttpServletRequest request, NotificationRepository notificationRepository) throws InvalidAgravoException {
        // 1. Obter parâmetros da requisição
        Integer ano = request.getParameter("year") != null ? Integer.valueOf(request.getParameter("year")) : null;
        String agravoName = request.getParameter("agravo");
        String bairro = request.getParameter("bairro");
        String agravoId = null;

        if (agravoName != null && !agravoName.isEmpty()) {
            agravoId = ConvertNameToIdAgravo.convert(agravoName);
        }
        
        // 2. Construir a Specification com os filtros dinâmicos
        final String finalAgravoId = agravoId;
        Specification<Notification> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (finalAgravoId != null) {
                predicates.add(criteriaBuilder.equal(root.get("idAgravo"), finalAgravoId));
            }
            if (ano != null) {
                // Supondo que o campo do ano na sua entidade se chama 'dataNotification'
                predicates.add(criteriaBuilder.equal(criteriaBuilder.function("YEAR", Integer.class, root.get("dataNotification")), ano)); 
            }
            if (bairro != null && !bairro.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("nomeBairro"), bairro));
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
        
        // 3. Chamar o método customizado e seguro
        return notificationRepository.buscarContagemPorSemanaEpidemiologica(spec);
    }

    public static Map<String, Integer> filtersForNotificationsByAgeRange(HttpServletRequest request, EntityManager entityManager) throws InvalidAgravoException {
        Integer year = request.getParameter("year") != null && !request.getParameter("year").isEmpty()
                ? Integer.valueOf(request.getParameter("year"))
                : null;
        String agravoName = request.getParameter("agravo");
        String bairro = request.getParameter("bairro");
        String agravoId = null;

        if (agravoName != null && !agravoName.isEmpty()) {
            agravoId = ConvertNameToIdAgravo.convert(agravoName);
        }

        List<String> ageBrackets = List.of(
            "age0to1", "age2to3", "age4to5", "age6to7", "age8to9",
            "age10to19", "age20to29", "age30to39", "age40to49", "age50to59",
            "age60to69", "age70to79", "age80to89", "age90to99"
        );

        StringBuilder jpql = new StringBuilder();
        jpql.append("""
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
            FROM Notification n WHERE 1=1
        """);

        Map<String, Object> parameters = new LinkedHashMap<>();

        if (agravoId != null) {
            jpql.append(" AND n.idAgravo = :idAgravo");
            parameters.put("idAgravo", agravoId);
        }
        if (year != null) {
            jpql.append(" AND FUNCTION('YEAR', n.dataNotification) = :year");
            parameters.put("year", year);
        }
        if (bairro != null && !bairro.isEmpty()) {
            jpql.append(" AND n.nomeBairro = :nomeBairro");
            parameters.put("nomeBairro", bairro);
        }

        var query = entityManager.createQuery(jpql.toString(), Tuple.class);
        parameters.forEach(query::setParameter);

        Tuple resultTuple;
        try {
            resultTuple = query.getSingleResult();
        } catch (NoResultException e) {
            resultTuple = null;
        }

        Map<String, Integer> resultMap = new LinkedHashMap<>();
        for (String bracket : ageBrackets) {
            if (resultTuple != null) {
                Long count = resultTuple.get(bracket, Long.class);
                resultMap.put(bracket, count != null ? count.intValue() : 0);
            } else {
                resultMap.put(bracket, 0);
            }
        }

        return resultMap;
    }

    public static List<BairroCountDTO> filtersForNotificationsCountNeighborhoods(HttpServletRequest request, NotificationRepository notificationRepository) throws InvalidAgravoException {
        Integer year = request.getParameter("year") != null ? Integer.valueOf(request.getParameter("year")) : null;
        String agravoName = request.getParameter("agravo");
        String agravoId = null;

        if (agravoName != null) {
            agravoId = ConvertNameToIdAgravo.convert(agravoName);
        }

        return notificationRepository.listarBairrosMaisAfetados(agravoId, year);
    }

    public static long filterForCountByIdAgravo(HttpServletRequest request, NotificationRepository notificationRepository) throws Exception {
        // 1. Obter os parâmetros da requisição
        String agravoName = request.getParameter("agravo");
        String bairro = request.getParameter("bairro");
        Integer year = request.getParameter("year") != null ? Integer.valueOf(request.getParameter("year")) : null;

        // A conversão do ID do agravo é mantida
        String agravoId = ConvertNameToIdAgravo.convert(agravoName);

        // 2. Criar a especificação dinâmica
        Specification<Notification> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Adiciona a condição para 'agravoId' (considerado obrigatório neste método)
            predicates.add(criteriaBuilder.equal(root.get("idAgravo"), agravoId));

            // Adiciona os filtros opcionais APENAS SE eles existirem
            if (bairro != null && !bairro.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("nomeBairro"), bairro));
            }
            if (year != null) {
                predicates.add(criteriaBuilder.equal(criteriaBuilder.function("YEAR", Integer.class, root.get("dataNotification")), year));
            }

            // Combina as condições com "AND"
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
        
        // 3. Executa a contagem com a especificação e retorna o resultado
        return notificationRepository.count(spec);
    }

    public static long filterForCountByEvolucao(HttpServletRequest request, NotificationRepository notificationRepository) throws Exception {
        // 1. Obter os parâmetros da requisição
        Integer year = request.getParameter("year") != null ? Integer.valueOf(request.getParameter("year")) : null;
        String agravoName = request.getParameter("agravo");
        String bairro = request.getParameter("bairro");
        
        // Converte o nome do agravo (considerado obrigatório)
        String agravoId = ConvertNameToIdAgravo.convert(agravoName);

        // Valida o parâmetro 'evolucao' (obrigatório)
        String evolucao = Optional.ofNullable(request.getParameter("evolucao"))
                .orElseThrow(() -> new Exception("Informe o nivel da evolucao"));

        // 2. Criar a especificação dinâmica
        Specification<Notification> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Adiciona as condições obrigatórias
            predicates.add(criteriaBuilder.equal(root.get("idAgravo"), agravoId));
            predicates.add(criteriaBuilder.equal(root.get("evolucao"), evolucao));

            // Adiciona os filtros opcionais APENAS SE eles existirem
            if (bairro != null && !bairro.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("nomeBairro"), bairro));
            }
            if (year != null) {
                predicates.add(criteriaBuilder.equal(criteriaBuilder.function("YEAR", Integer.class, root.get("dataNotification")), year));
            }

            // Combina as condições com "AND"
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        // 3. Executa a contagem com a especificação e retorna o resultado
        return notificationRepository.count(spec);
    }

    public static Map<String, Integer> filterForListByDadosEspecificados(HttpServletRequest request, NotificationRepository notificationRepository) throws Exception {
        Integer year = request.getParameter("year") != null ? Integer.valueOf(request.getParameter("year")) : null;
        String agravoName = request.getParameter("agravo");
        String bairro = request.getParameter("bairro");
        String agravoId = null;

        if (agravoName == null) {
            if (year == null) {
                if (bairro == null) {
                    return notificationRepository.listarDadosEspecificadosDasNotificacoes();
                }
                return notificationRepository.listarDadosEspecificadosDasNotificacoesByBairro(bairro);
            } 
        }

        agravoId = ConvertNameToIdAgravo.convert(agravoName);

        if (year != null) {
            if (bairro != null) {
                return notificationRepository.listarDadosEspecificadosDasNotificacoesByIdAgravoAndYearAndBairro(agravoId, year, bairro);
            }
            return notificationRepository.listarDadosEspecificadosDasNotificacoes(agravoId, year);
        }

        if (bairro != null) {
            return notificationRepository.listarDadosEspecificadosDasNotificacoesByIdAgravoAndBairro(agravoId, bairro);
        }

        return notificationRepository.listarDadosEspecificadosDasNotificacoes(agravoId);
    } 
}
