package com.arboviroses.conectaDengue.Entities.DTO;

import java.text.ParseException;
import java.util.List;

import com.arboviroses.conectaDengue.Entities.DTO.response.DataNotificationResponseDTO;
import com.arboviroses.conectaDengue.Entities.Notification.Notification;
import com.arboviroses.conectaDengue.Exceptions.InvalidDateStringException;
import com.arboviroses.conectaDengue.Utils.StringToDateCSV;

public class NotificationDataManager {
        public static Notification csvLineToNotification(String[] line, List<String> header) throws NumberFormatException, ParseException, InvalidDateStringException
    {

        return new Notification(
            Long.valueOf(line[header.indexOf("NU_NOTIFIC")]),
            line[header.indexOf("ID_AGRAVO")],
            StringToDateCSV.ConvertStringToDate(line[header.indexOf("DT_NOTIFIC")]),
            StringToDateCSV.ConvertStringToDate(line[header.indexOf("DT_NASC")]),
            line[header.indexOf("CLASSI_FIN")],
            line[header.indexOf("CS_SEXO")],
            line[header.indexOf("NM_BAIRRO")],
            line[header.indexOf("EVOLUCAO")]
         );
    }


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
