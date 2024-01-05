package com.arboviroses.conectaDengue.Services.Notifications;

import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.arboviroses.conectaDengue.Repositories.Notifications.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.arboviroses.conectaDengue.Entities.DTO.NotificationDataManager;
import com.arboviroses.conectaDengue.Entities.DTO.response.DataNotificationResponseDTO;
import com.arboviroses.conectaDengue.Entities.Notification.Notification;
import com.arboviroses.conectaDengue.Exceptions.InvalidAgravoException;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class NotificationService {
    @Autowired
    private NotificationRepository notificationRepository;

    public String saveCSVDataInDatabase(MultipartFile file) throws IOException, CsvException, NumberFormatException, ParseException
    {
        try (CSVReader csvReader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            List<String[]> csvLines = csvReader.readAll();
            List<Notification> notifications = new ArrayList<>();
            List<String> header = Arrays.asList(csvLines.get(0));
            csvLines.remove(0);

            for(String[] csvLine : csvLines) {
                notifications.add(NotificationDataManager.csvLineToNotification(csvLine, header));
            }

            notificationRepository.saveAll(notifications);

            return "csv data saved successfully";
        }
    } 

    public Page<DataNotificationResponseDTO> getAllNotificationsPaginated(Pageable pageable)
    {
        Page<Notification> notifications = notificationRepository.findAll(pageable);
        return notifications.map(DataNotificationResponseDTO::new);
    }

    public Page<DataNotificationResponseDTO> getNotificationsByIdAgravoPaginated(Pageable pageable, HttpServletRequest request) throws InvalidAgravoException
    {
        if (request.getParameter("filter") == null) {
            return getAllNotificationsPaginated(pageable);
        }
        
        String filter = request.getParameter("filter").toUpperCase();
        
        switch(filter) {
            case "ZIKA":
                filter = "A92.0";
                break;
            case "DENGUE":
                filter = "A90";
                break;
            case "CHIKUNGUNYA":
                filter = "A928";
                break;
            default: 
                throw new InvalidAgravoException("Valor inválido: " + filter + ". valores aceitos: zika, dengue, chikungunya");
        }

        Page<Notification> notifications = notificationRepository.findByIdAgravo(pageable, filter);
        return notifications.map(DataNotificationResponseDTO::new);
    }
}
