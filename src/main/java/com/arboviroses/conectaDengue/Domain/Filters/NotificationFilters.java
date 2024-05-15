package com.arboviroses.conectaDengue.Domain.Filters;

import java.util.List;
import java.util.Map;

import com.arboviroses.conectaDengue.Api.DTO.response.AgravoCountBySemanaEpidemiologica;
import com.arboviroses.conectaDengue.Api.DTO.response.BairroCountDTO;
import com.arboviroses.conectaDengue.Api.DTO.response.CountAgravoBySexoDTO;
import com.arboviroses.conectaDengue.Api.Exceptions.InvalidAgravoException;
import com.arboviroses.conectaDengue.Domain.Repositories.Notifications.NotificationRepository;
import com.arboviroses.conectaDengue.Utils.ConvertNameToIdAgravo;

import jakarta.servlet.http.HttpServletRequest;

public class NotificationFilters {
    public static CountAgravoBySexoDTO filtersForNotificationsInfoBySexo(HttpServletRequest request, NotificationRepository notificationRepository) throws InvalidAgravoException
    {
        Integer date = request.getParameter("year") != null ? Integer.valueOf(request.getParameter("year")) : null;
        String agravoName = request.getParameter("agravo");
        String agravoId = null;

        if (agravoName != null) {
            agravoId = ConvertNameToIdAgravo.convert(agravoName);
        }

        if (date != null) {
            if (agravoId != null) {
                return new CountAgravoBySexoDTO(
                    notificationRepository.countByIdAgravoAndSexoAndYearNotification(agravoId, "M", date),
                    notificationRepository.countByIdAgravoAndSexoAndYearNotification(agravoId, "F", date)
                );
            } else {
                return new CountAgravoBySexoDTO(
                    notificationRepository.countBySexoAndYearNotification("M", date),
                    notificationRepository.countBySexoAndYearNotification("F", date)
                );
            }

        }   

        if (agravoId != null) {  
            return new CountAgravoBySexoDTO(
                notificationRepository.countByIdAgravoAndSexo(agravoId, "M"),
                notificationRepository.countByIdAgravoAndSexo(agravoId, "F")
            );
        }

        return new CountAgravoBySexoDTO(
            notificationRepository.countBySexo("M"),
            notificationRepository.countBySexo("F")
        );
    }

    public static List<AgravoCountBySemanaEpidemiologica> filtersForNotificationsInfoBySemanaEpidemiologica(HttpServletRequest request, NotificationRepository notificationRepository) throws InvalidAgravoException
    {
        Integer date = request.getParameter("year") != null ? Integer.valueOf(request.getParameter("year")) : null;
        String agravoName = request.getParameter("agravo");
        String agravoId = null;

        if (agravoName != null) {
            agravoId = ConvertNameToIdAgravo.convert(agravoName);
        }

        if (date != null) {
            if (agravoId != null) {
                return notificationRepository.listarContagemPorSemanaEpidemiologica(agravoId, date);
            }

            return notificationRepository.listarContagemPorSemanaEpidemiologica(date);
        } 

        return notificationRepository.listarContagemPorSemanaEpidemiologica();
    }

    public static Map<String, Integer> filtersForNotificationsByAgeRange(HttpServletRequest request, NotificationRepository notificationRepository) throws InvalidAgravoException {
        Integer year = request.getParameter("year") != null ? Integer.valueOf(request.getParameter("year")) : null;
        String agravoName = request.getParameter("agravo");
        String agravoId = null;

        if (agravoName != null) {
            agravoId = ConvertNameToIdAgravo.convert(agravoName);
        }

        if (year != null) {
            if (agravoId != null) {
                return notificationRepository.listarContagemPorFaixaDeIdadeByIdAgravo(agravoId, year);
            }

            return notificationRepository.listarContagemPorFaixaDeIdade(year);
        }

        return notificationRepository.listarContagemPorFaixaDeIdade();
    }

    public static List<BairroCountDTO> filtersForNotificationsCountNeighborhoods(HttpServletRequest request, NotificationRepository notificationRepository) throws InvalidAgravoException {
        Integer year = request.getParameter("year") != null ? Integer.valueOf(request.getParameter("year")) : null;
        String agravoName = request.getParameter("agravo");
        String agravoId = null;

        if (agravoName != null) {
            agravoId = ConvertNameToIdAgravo.convert(agravoName);
        }

        if (year != null) {
            if (agravoId != null) {
                return notificationRepository.listarBairrosMaisAfetadosByIdAgravoAndYear(agravoId, year);
            }

            return notificationRepository.listarBairrosMaisAfetadosByYear(year);
        }

        return notificationRepository.listarBairrosMaisAfetados();
    }

    public static long filterForCountByIdAgravo(HttpServletRequest request, NotificationRepository notificationRepository) throws Exception {
        Integer year = request.getParameter("year") != null ? Integer.valueOf(request.getParameter("year")) : null;
        String agravoName = request.getParameter("agravo");
        String agravoId = null;

        if (agravoName == null) {
            if (year == null) {
                return notificationRepository.count();
            } 

            return notificationRepository.countByYearNotification(year);
        }

        agravoId = ConvertNameToIdAgravo.convert(agravoName);

        if (year != null) {
            return notificationRepository.countByIdAgravoAndYearNotification(agravoId, year);
        }

        return notificationRepository.countByIdAgravo(agravoId);
    }

    public static long filterForCountByEvolucao(HttpServletRequest request, NotificationRepository notificationRepository) throws Exception {
        // Integer year = request.getParameter("year") != null ? Integer.valueOf(request.getParameter("year")) : null;
        // String agravoName = request.getParameter("agravo");
        // String agravoId = null;
        // String evolucao;

        // if (request.getParameter("evolucao") == null) {
        //     throw new Exception("Informe o nivel da evolucao");
        // }

        // evolucao = request.getParameter("evolucao");

        // if (agravoName == null) {
        //     if (year == null) {
        //         return notificationRepository.countByEvolucao(evolucao);
        //     } 

        //     return notificationRepository.countByEvolucaoAndYear(evolucao, year);
        // }

        // agravoId = ConvertNameToIdAgravo.convert(agravoName);

        // if (year != null) {
        //     return notificationRepository.countByIdAgravoAndEvolucaoAndYear(agravoId, evolucao, year);
        // }

        // return notificationRepository.countByIdAgravoAndEvolucao(agravoId, evolucao);
        return 2;
    } 
}
