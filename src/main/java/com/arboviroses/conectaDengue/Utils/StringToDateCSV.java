package com.arboviroses.conectaDengue.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.arboviroses.conectaDengue.Api.Exceptions.InvalidDateStringException;

public class StringToDateCSV
{
    public static Date ConvertStringToDate(String dateString) throws InvalidDateStringException, ParseException 
    {
        if (dateString == null) {
            throw new InvalidDateStringException("Date String cannot be null");
        }

        SimpleDateFormat defaultDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String[] dateInfos = dateString.split("/");
        
        if (dateInfos.length < 3) {
            throw new InvalidDateStringException("Invalid date format: " + dateString);
        }   

        if (dateInfos[0].length() > 2 || dateInfos[1].length() > 2 || dateInfos[2].length() > 4) {
            throw new InvalidDateStringException("Invalid date format: " + dateString);
        }

        String mes = formatFieldsOfDate(dateInfos[0], "0", 2);
        String dia = formatFieldsOfDate(dateInfos[1], "0", 2);
        String formatedDateComplete = dia + "/" + mes + "/" + dateInfos[2];

        Date finalDate = defaultDateFormat.parse(formatedDateComplete);

        return finalDate;
    }

    private static String formatFieldsOfDate(String field, String stringToAdd, int lenght)
    {   
        while (field.length() < lenght) {
            field = stringToAdd + field;
        }

        return field;
    }
}