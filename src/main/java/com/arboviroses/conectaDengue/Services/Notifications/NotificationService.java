package com.arboviroses.conectaDengue.Services.Notifications;

import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.arboviroses.conectaDengue.Repositories.Notifications.NotificationRepository;
import com.arboviroses.conectaDengue.Utils.ConvertNameToIdAgravo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.arboviroses.conectaDengue.Entities.DTO.response.DataNotificationInfoDTO;
import com.arboviroses.conectaDengue.Entities.DTO.response.DataNotificationInfoNoFilterDTO;
import com.arboviroses.conectaDengue.Entities.DTO.response.DataNotificationResponseDTO;
import com.arboviroses.conectaDengue.Entities.DTO.response.SaveCsvResponseDTO;
import com.arboviroses.conectaDengue.Entities.Notification.Notification;
import com.arboviroses.conectaDengue.Exceptions.InvalidAgravoException;
import com.arboviroses.conectaDengue.Exceptions.InvalidDateStringException;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import static com.arboviroses.conectaDengue.Utils.ConvertCSVLineToNotifications.convert;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class NotificationService {
    @Autowired
    private NotificationRepository notificationRepository;

    public SaveCsvResponseDTO saveCSVDataInDatabase(MultipartFile file) throws IOException, CsvException, NumberFormatException, ParseException, InvalidDateStringException
    {
        try (CSVReader csvReader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            List<String[]> csvLines = csvReader.readAll();
            List<Notification> notifications = new ArrayList<>();
            List<String> header = Arrays.asList(csvLines.get(0));
            csvLines.remove(0);

            for(String[] csvLine : csvLines) {
                try {
                    notifications.add(convert(csvLine, header));
                } catch (Exception e) {
                    // no futuro pode ser adicionada uma tabela para salvar esses dados
                    System.out.println(e.getMessage());
                }
            }

            notificationRepository.saveAll(notifications);

            return new SaveCsvResponseDTO("sucesso", "dados do csv salvos com sucesso");
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
