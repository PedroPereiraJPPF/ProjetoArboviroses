package com.arboviroses.conectaDengue.Entities.DTO.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DataNotificationInfoNoFilterDTO extends DataNotificationInfoDTO{
    private long countTotal, countBySexoMasculino, countBySexoFeminino, countByEvolucao1, countByEvolucao2, countByIdAgravoDengue, countByIdAgravoZika, countByIdAgravoChikungunya;
}
