package com.arboviroses.conectaDengue.Domain.Services.Notifications;

import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.arboviroses.conectaDengue.Utils.ConvertNameToIdAgravo;
import com.arboviroses.conectaDengue.Utils.File.DataToNotificationObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.arboviroses.conectaDengue.Api.DTO.response.AgravoCountByAgeRange;
import com.arboviroses.conectaDengue.Api.DTO.response.AgravoCountByEpidemiologicalSemanaEpidemiologicaResponse;
import com.arboviroses.conectaDengue.Api.DTO.response.BairroCountDTO;
import com.arboviroses.conectaDengue.Api.DTO.response.CountAgravoBySexoDTO;
import com.arboviroses.conectaDengue.Api.DTO.response.DataNotificationInfoDTO;
import com.arboviroses.conectaDengue.Api.DTO.response.DataNotificationInfoNoFilterDTO;
import com.arboviroses.conectaDengue.Api.DTO.response.DataNotificationResponseDTO;
import com.arboviroses.conectaDengue.Api.DTO.response.SaveCsvResponseDTO;
import com.arboviroses.conectaDengue.Api.Exceptions.InvalidAgravoException;
import com.arboviroses.conectaDengue.Domain.Entities.Notification.Notification;
import com.arboviroses.conectaDengue.Domain.Filters.NotificationFilters;
import com.arboviroses.conectaDengue.Domain.Repositories.Notifications.NotificationRepository;
import com.opencsv.exceptions.CsvException;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class NotificationService {
    @Autowired
    private NotificationRepository notificationRepository;

    public SaveCsvResponseDTO saveNotificationsData(MultipartFile file) throws IOException, CsvException, NumberFormatException, ParseException
    {
            List<Notification> notifications = null;

            String fileName = file.getOriginalFilename();

            if (fileName == null) {
                throw new IOException("Nome do arquivo não pode ser nulo.");
            }
        
            if (fileName.endsWith(".csv")) {
                notifications = DataToNotificationObject.processCSVFile(file);
            } else if (fileName.endsWith(".xlsx")) {
                notifications = DataToNotificationObject.processXLSXFile(file);
            } else {
                throw new IOException("Tipo de arquivo não suportado: " + fileName);
            }

            notificationRepository.saveAll(notifications);

            return new SaveCsvResponseDTO(true);
    } 

    public Page<DataNotificationResponseDTO> getAllNotificationsPaginated(Pageable pageable)
    {
        Page<Notification> notifications = notificationRepository.findAll(pageable);
        return notifications.map(DataNotificationResponseDTO::new);
    }

    public Page<DataNotificationResponseDTO> getNotificationsByIdAgravoPaginated(Pageable pageable, HttpServletRequest request) throws InvalidAgravoException
    {
        if (request.getParameter("agravo") == null) {
            return getAllNotificationsPaginated(pageable);
        }
        
        String agravo = ConvertNameToIdAgravo.convert(request.getParameter("agravo"));

        Page<Notification> notifications = notificationRepository.findByIdAgravo(pageable, agravo);
        return notifications.map(DataNotificationResponseDTO::new);
    }

    public CountAgravoBySexoDTO getNotificationsInfoBySexo(HttpServletRequest request) throws InvalidAgravoException 
    {
        return NotificationFilters.filtersForNotificationsInfoBySexo(request, notificationRepository);
    }

    public AgravoCountByEpidemiologicalSemanaEpidemiologicaResponse countNotificationsBySemanaEpidemiologica(HttpServletRequest request) throws InvalidAgravoException
    {
        return new AgravoCountByEpidemiologicalSemanaEpidemiologicaResponse(NotificationFilters.filtersForNotificationsInfoBySemanaEpidemiologica(request, notificationRepository)); 
    }

    public AgravoCountByEpidemiologicalSemanaEpidemiologicaResponse countNotificationsBySemanaEpidemiologicaAccumulated(HttpServletRequest request) throws InvalidAgravoException
    {
        return new AgravoCountByEpidemiologicalSemanaEpidemiologicaResponse(NotificationFilters.filtersForNotificationsInfoBySemanaEpidemiologica(request, notificationRepository), true); 
    }

    public AgravoCountByAgeRange getNotificationsCountByAgeRange(HttpServletRequest request) throws InvalidAgravoException {
        return new AgravoCountByAgeRange(NotificationFilters.filtersForNotificationsByAgeRange(request, notificationRepository));
    }

    public List<BairroCountDTO> getBairroCount(HttpServletRequest request) throws InvalidAgravoException 
    {   
        return NotificationFilters.filtersForNotificationsCountNeighborhoods(request, notificationRepository);
    }

    public long countByEvolucao(HttpServletRequest request) throws Exception {
        return NotificationFilters.filterForCountByEvolucao(request, notificationRepository);
    }

    public long countByIdAgravo(HttpServletRequest request) throws Exception {
        return NotificationFilters.filterForCountByIdAgravo(request, notificationRepository);
    }

    public Map<Integer, Map<Integer, Long>> getNotificationCountsByYear(List<Integer> years) {
        Map<Integer, Map<Integer, Long>> result = new HashMap<>();

        for (Integer year : years) {
            List<Notification> notificationsForYear = notificationRepository.findByYearAndIdAgravo(year, "A90");
            Map<Integer, Long> notificationsByMonth = new HashMap<>();

            for (Notification notification : notificationsForYear) {
                int month = getMonth(notification.getDataNotification());
                notificationsByMonth.merge(month, 1L, Long::sum);
            }

            result.put(year, notificationsByMonth);
        }

        return result;
    }

    private int getMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.MONTH) + 1;
    }

    /**
     * @test essas são funções feitas para testes, não devem ficar disponiveis em produção
     * @return
     */

    public DataNotificationInfoDTO getNotificationsInfoForGraphics()
    {
        return new DataNotificationInfoNoFilterDTO(
            notificationRepository.count(),
            notificationRepository.countBySexo("M"),
            notificationRepository.countBySexo("F"),
            notificationRepository.countByEvolucao("1"),
            notificationRepository.countByEvolucao("2"),
            notificationRepository.listarBairrosMaisAfetados(null, null),
            notificationRepository.countByIdAgravo("A90"),
            notificationRepository.countByIdAgravo("A92.0"), 
            notificationRepository.countByIdAgravo("A928")
        );
    }

    public DataNotificationInfoDTO getNotificationsInfoForGraphicsByIdAgravo(HttpServletRequest request) throws InvalidAgravoException
    {
        if (request.getParameter("agravo") == null) {
            return getNotificationsInfoForGraphics();
        }
        
        String agravo = ConvertNameToIdAgravo.convert(request.getParameter("agravo"));

        return new DataNotificationInfoDTO(
            notificationRepository.countByIdAgravo(agravo),
            notificationRepository.countByIdAgravoAndSexo(agravo, "M"),
            notificationRepository.countByIdAgravoAndSexo(agravo, "F"),
            notificationRepository.countByIdAgravoAndEvolucao(agravo, "0"),
            notificationRepository.countByIdAgravoAndEvolucao(agravo, "1"),
            null
        );
    }

    public Map<String, Integer> listEspecificNotificationData(HttpServletRequest request) throws Exception {
        return NotificationFilters.filterForListByDadosEspecificados(request, notificationRepository);
    }
}
