package com.arboviroses.conectaDengue.Api.DTO.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class AgravoCountBySemanaEpidemiologica 
{
    private int epidemiologicalWeek;
    private long casesCount;    
}
