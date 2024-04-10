package com.arboviroses.conectaDengue.Domain.Filters;

import com.arboviroses.conectaDengue.Api.DTO.response.CountAgravoBySexoDTO;
import com.arboviroses.conectaDengue.Api.Exceptions.InvalidAgravoException;
import com.arboviroses.conectaDengue.Domain.Repositories.Notifications.NotificationRepository;
import com.arboviroses.conectaDengue.Utils.ConvertNameToIdAgravo;

import jakarta.servlet.http.HttpServletRequest;

public class NotificationFilters {
    public static CountAgravoBySexoDTO filtersForNotificationsInfoBySexo(HttpServletRequest request, NotificationRepository notificationRepository) throws InvalidAgravoException
    {
        String date = request.getParameter("year");
        String agravoName = request.getParameter("agravo");
        String agravoId = null;

        if (agravoName != null) {
            agravoId = ConvertNameToIdAgravo.convert(agravoName);
        }

        if (date != null) {
            if (agravoId != null) {
                return new CountAgravoBySexoDTO(
                    notificationRepository.countByIdAgravoAndSexoAndYearNotification(agravoId, "M", Long.valueOf(date)),
                    notificationRepository.countByIdAgravoAndSexoAndYearNotification(agravoId, "F", Long.valueOf(date))
                );
            } else {
                return new CountAgravoBySexoDTO(
                    notificationRepository.countBySexoAndYearNotification("M", Long.valueOf(date)),
                    notificationRepository.countBySexoAndYearNotification("F", Long.valueOf(date))
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
}
