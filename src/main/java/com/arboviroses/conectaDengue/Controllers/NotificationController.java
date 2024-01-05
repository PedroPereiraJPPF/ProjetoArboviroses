package com.arboviroses.conectaDengue.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.arboviroses.conectaDengue.Entities.DTO.response.DataNotificationResponseDTO;
import com.arboviroses.conectaDengue.Services.Notifications.NotificationService;

@RestController
public class NotificationController 
{
    @Autowired
    NotificationService notificationService;

    @PostMapping("/savecsvdata")
    public ResponseEntity<String> readCsv(@RequestParam("csv_file") MultipartFile file) throws Exception {
        return ResponseEntity.ok().body(notificationService.saveCSVDataInDatabase(file));
    }

    @GetMapping("/notifications")
    public ResponseEntity<Page<DataNotificationResponseDTO>> getAll(Pageable pageable) {
        return ResponseEntity.ok().body(notificationService.getAllNotificationsPaginated(pageable));
    }
    
    
}