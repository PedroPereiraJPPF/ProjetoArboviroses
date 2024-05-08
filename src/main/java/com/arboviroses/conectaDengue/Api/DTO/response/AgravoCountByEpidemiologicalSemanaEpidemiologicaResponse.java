package com.arboviroses.conectaDengue.Api.DTO.response;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AgravoCountByEpidemiologicalSemanaEpidemiologicaResponse
{
    private List<AgravoCountBySemanaEpidemiologica> dengue;
    private List<AgravoCountBySemanaEpidemiologica> zika;
    private List<AgravoCountBySemanaEpidemiologica> chikungunya;

    public AgravoCountByEpidemiologicalSemanaEpidemiologicaResponse(List<AgravoCountBySemanaEpidemiologica> list)
    {
        dengue = new ArrayList<>();
        zika = new ArrayList<>();
        chikungunya = new ArrayList<>();

        for (AgravoCountBySemanaEpidemiologica item : list) 
        {
            if (item.getAgravoId().equals("A90")) {
                dengue.add(item);
            } else if (item.getAgravoId().equals("A92.0")) {
                chikungunya.add(item);
            } else {
                zika.add(item);
            }
        }
    }
}


