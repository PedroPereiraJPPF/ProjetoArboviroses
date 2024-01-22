package com.arboviroses.conectaDengue.Utils; 
import java.text.ParseException;
import java.util.List;
import com.arboviroses.conectaDengue.Entities.Notification.Notification;
import com.arboviroses.conectaDengue.Exceptions.InvalidDateStringException;

public class ConvertCSVLineToNotifications {
    public static Notification convert(String[] line, List<String> header) throws NumberFormatException, InvalidDateStringException, ParseException
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
}
