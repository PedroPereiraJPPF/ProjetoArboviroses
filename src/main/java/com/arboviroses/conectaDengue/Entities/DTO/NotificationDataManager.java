package com.arboviroses.conectaDengue.Entities.DTO;

import java.text.ParseException;
import java.util.List;

import com.arboviroses.conectaDengue.Entities.DTO.response.DataNotificationResponseDTO;
import com.arboviroses.conectaDengue.Entities.Notification.Notification;

public class NotificationDataManager {
        public static Notification csvLineToNotification(String[] line, List<String> header) throws NumberFormatException, ParseException
    {
        return new Notification(
            Long.valueOf(line[header.indexOf("NU_NOTIFIC")]),
            line[header.indexOf("ID_AGRAVO")],
            line[header.indexOf("DT_NOTIFIC")],
            line[header.indexOf("DT_NASC")],
            line[header.indexOf("CLASSI_FIN")],
            line[header.indexOf("CS_SEXO")],
            line[header.indexOf("NM_BAIRRO")],
            line[header.indexOf("EVOLUCAO")]
         );
    }


    public static DataNotificationResponseDTO NotificationToResponseDTO(Notification notification)
    {   
        return new DataNotificationResponseDTO(
            notification.getId_notification(),
            notification.getId_agravo(),
            notification.getData_notification(),
            notification.getData_nascimento(),
            notification.getClassificacao(),
            notification.getSexo(),
            notification.getNome_bairro(),
            notification.getEvolucao()
        );
    }
}
