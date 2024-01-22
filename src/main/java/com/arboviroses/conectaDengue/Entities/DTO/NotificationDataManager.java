package com.arboviroses.conectaDengue.Entities.DTO;

import com.arboviroses.conectaDengue.Entities.DTO.response.DataNotificationResponseDTO;
import com.arboviroses.conectaDengue.Entities.Notification.Notification;

public class NotificationDataManager {
    public static DataNotificationResponseDTO NotificationToResponseDTO(Notification notification)
    {   
        return new DataNotificationResponseDTO(
            notification.getIdNotification(),
            notification.getIdAgravo(),
            notification.getDataNotification(),
            notification.getDataNascimento(),
            notification.getClassificacao(),
            notification.getSexo(),
            notification.getNomeBairro(),
            notification.getEvolucao()
        );
    }
}
