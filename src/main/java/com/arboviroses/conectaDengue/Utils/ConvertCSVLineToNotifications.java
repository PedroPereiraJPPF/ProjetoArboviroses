package com.arboviroses.conectaDengue.Utils; 
import java.text.ParseException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import com.arboviroses.conectaDengue.Api.Exceptions.InvalidDateStringException;
import com.arboviroses.conectaDengue.Domain.Entities.Notification.Notification;

public class ConvertCSVLineToNotifications {
    public static Notification convertCsvLineToNotificationObject(String[] line, List<String> header) throws NumberFormatException, InvalidDateStringException, ParseException
    {   
        Date dataNotification = StringToDateCSV.ConvertStringToDate(line[header.indexOf("DT_NOTIFIC")]);
        int epidemiologicalWeek = calculateEpidemiologicalWeek(dataNotification);

        return new Notification(
            Long.valueOf(line[header.indexOf("NU_NOTIFIC")]),
            line[header.indexOf("ID_AGRAVO")],
            dataNotification,
            StringToDateCSV.ConvertStringToDate(line[header.indexOf("DT_NASC")]),
            line[header.indexOf("CLASSI_FIN")],
            line[header.indexOf("CS_SEXO")],
            line[header.indexOf("NM_BAIRRO")],
            line[header.indexOf("EVOLUCAO")],
            epidemiologicalWeek
         );
    }

    public static int calculateEpidemiologicalWeek(Date date) {
        LocalDate caseDate = date.toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDate();;
        
        LocalDate firstSundayOfYear = caseDate.withDayOfYear(1);
        while (firstSundayOfYear.getDayOfWeek() != DayOfWeek.SUNDAY) {
            firstSundayOfYear = firstSundayOfYear.plusDays(1);
        }
        
        int daysSinceFirstSunday = (int) firstSundayOfYear.until(caseDate, java.time.temporal.ChronoUnit.DAYS);
        int epidemiologicalWeek = (daysSinceFirstSunday / 7) + 1;
        
        return epidemiologicalWeek;
    }
}
