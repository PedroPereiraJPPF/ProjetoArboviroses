package com.arboviroses.conectaDengue.Api.DTO.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CountAgravoBySexoDTO 
{
    private long masculine, feminine;    
    private String agravoName;

    public CountAgravoBySexoDTO(long masculine, long feminine) 
    {
        setMasculine(masculine);
        setFeminine(feminine);
    }
}
