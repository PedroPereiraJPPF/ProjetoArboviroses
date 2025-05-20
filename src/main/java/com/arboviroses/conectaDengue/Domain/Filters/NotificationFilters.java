package com.arboviroses.conectaDengue.Domain.Filters;

import java.util.List;
import java.util.Map;
import java.util.Optional;

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
        String bairro = request.getParameter("bairro");
        String agravoId = null;

        if (agravoName != null) {
            agravoId = ConvertNameToIdAgravo.convert(agravoName);
        }

        return new CountAgravoBySexoDTO(
            notificationRepository.countByOptionalParams(agravoId, "M", bairro, date, null),
            notificationRepository.countByOptionalParams(agravoId, "F", bairro, date, null)
        );
    }

    public static List<AgravoCountBySemanaEpidemiologica> filtersForNotificationsInfoBySemanaEpidemiologica(HttpServletRequest request, NotificationRepository notificationRepository) throws InvalidAgravoException
    {
        Integer ano = request.getParameter("year") != null ? Integer.valueOf(request.getParameter("year")) : null;
        String agravoName = request.getParameter("agravo");
        String bairro = request.getParameter("bairro");
        String agravoId = null;

        if (agravoName != null) {
            agravoId = ConvertNameToIdAgravo.convert(agravoName);
        }

        return notificationRepository.buscarContagemPorSemanaEpidemiologica(agravoId, ano, bairro);
    }

    public static Map<String, Integer> filtersForNotificationsByAgeRange(HttpServletRequest request, NotificationRepository notificationRepository) throws InvalidAgravoException {
        Integer year = request.getParameter("year") != null ? Integer.valueOf(request.getParameter("year")) : null;
        String agravoName = request.getParameter("agravo");
        String bairro = request.getParameter("bairro");
        String agravoId = null;

        if (agravoName != null) {
            agravoId = ConvertNameToIdAgravo.convert(agravoName);
        }

        return notificationRepository.listarContagemPorFaixaDeIdadeComFiltros(agravoId, year, bairro);
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
        Integer year = request.getParameter("year") != null ? Integer.valueOf(request.getParameter("year")) : null;
        String agravoName = request.getParameter("agravo");
        String bairro = request.getParameter("bairro");

        String agravoId = ConvertNameToIdAgravo.convert(agravoName);

        return notificationRepository.countByOptionalParams(agravoId, null, bairro, year, null);
    }

    public static long filterForCountByEvolucao(HttpServletRequest request, NotificationRepository notificationRepository) throws Exception {
        Integer year = request.getParameter("year") != null ? Integer.valueOf(request.getParameter("year")) : null;
        String agravoName = request.getParameter("agravo");
        String bairro = request.getParameter("bairro");
        String agravoId = ConvertNameToIdAgravo.convert(agravoName);
        String evolucao = Optional.ofNullable(request.getParameter("evolucao"))
            .orElseThrow(() -> new Exception("Informe o nivel da evolucao"));

        return notificationRepository.countByOptionalParams(agravoId, agravoId, bairro, year, evolucao);
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
