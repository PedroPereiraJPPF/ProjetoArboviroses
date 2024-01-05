package com.arboviroses.conectaDengue.Services.Notifications;

import java.io.InputStreamReader;
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
import com.opencsv.CSVReader;

@Service
public class NotificationService {
    @Autowired
    private NotificationRepository notificationRepository;

    public String saveCSVDataInDatabase(MultipartFile file) throws Exception
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
        } catch (Exception e) {
            throw e;
        }
    } 

    public Page<DataNotificationResponseDTO> getAllNotificationsPaginated(Pageable pageable)
    {
        Page<Notification> notifications = notificationRepository.findAll(pageable);
        return notifications.map(DataNotificationResponseDTO::new);
    }
}
