package com.arboviroses.conectaDengue.Domain.Services.Notifications;

import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.arboviroses.conectaDengue.Utils.ConvertNameToIdAgravo;
import com.arboviroses.conectaDengue.Utils.MossoroData.NeighborhoodsMossoro;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.arboviroses.conectaDengue.Api.DTO.response.AgravoCountByAgeRange;
import com.arboviroses.conectaDengue.Api.DTO.response.AgravoCountByEpidemiologicalSemanaEpidemiologicaResponse;
import com.arboviroses.conectaDengue.Api.DTO.response.CountAgravoBySexoDTO;
import com.arboviroses.conectaDengue.Api.DTO.response.DataNotificationInfoDTO;
import com.arboviroses.conectaDengue.Api.DTO.response.DataNotificationInfoNoFilterDTO;
import com.arboviroses.conectaDengue.Api.DTO.response.DataNotificationResponseDTO;
import com.arboviroses.conectaDengue.Api.DTO.response.SaveCsvResponseDTO;
import com.arboviroses.conectaDengue.Api.Exceptions.InvalidAgravoException;
import com.arboviroses.conectaDengue.Api.Exceptions.InvalidDateStringException;
import com.arboviroses.conectaDengue.Domain.Entities.Notification.Notification;
import com.arboviroses.conectaDengue.Domain.Filters.NotificationFilters;
import com.arboviroses.conectaDengue.Domain.Repositories.Notifications.NotificationRepository;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import static com.arboviroses.conectaDengue.Utils.ConvertCSVLineToNotifications.convertCsvLineToNotificationObject;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class NotificationService {
    @Autowired
    private NotificationRepository notificationRepository;

    public SaveCsvResponseDTO saveCSVDataInDatabase(MultipartFile file) throws IOException, CsvException, NumberFormatException, ParseException, InvalidDateStringException
    {
        try (CSVReader csvReader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            NeighborhoodsMossoro neighborhoods = new NeighborhoodsMossoro();
            String neighborhoodFromNotification;
            List<String[]> csvLines = csvReader.readAll();
            List<Notification> notifications = new ArrayList<>();
            List<String> header = Arrays.asList(csvLines.get(0));
            csvLines.remove(0);

            for(String[] csvLine : csvLines) {
                try {
                    neighborhoodFromNotification = csvLine[header.indexOf("NM_BAIRRO")];
                    
                    if((neighborhoodFromNotification = neighborhoods.search(neighborhoodFromNotification)) != null) {
                        csvLine[header.indexOf("NM_BAIRRO")] = neighborhoodFromNotification;
                        notifications.add(convertCsvLineToNotificationObject(csvLine, header));
                    }
                } catch (Exception e) {
                    // no futuro pode ser adicionada uma tabela para salvar esses dados
                    System.out.println(e.getMessage());
                }
            }

            notificationRepository.saveAll(notifications);

            return new SaveCsvResponseDTO(true);
        }
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

    public AgravoCountByAgeRange getNotificationsCountByAgeRange(HttpServletRequest request) throws InvalidAgravoException {
        return new AgravoCountByAgeRange(NotificationFilters.filtersForNotificationsByAgeRange(request, notificationRepository));
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
            notificationRepository.listarBairrosMaisAfetados(),
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
            notificationRepository.listarBairrosMaisAfetadosByIdAgravo(agravo)
        );
    }
}
