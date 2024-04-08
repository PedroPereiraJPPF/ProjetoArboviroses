package com.arboviroses.conectaDengue.Utils.MossoroData;

import java.util.HashSet;
import java.util.Set;

import com.arboviroses.conectaDengue.Utils.Search.SearchAlgorithms;

public class NeighborhoodsMossoro
{
    private Set<String> neighborhoods;

    public NeighborhoodsMossoro()
    {
        this.neighborhoods = new HashSet<>();
        this.neighborhoods.add("Abolição");
        this.neighborhoods.add("AEROPORTO");
        this.neighborhoods.add("ALTO DA CONCEIÇÃO");
        this.neighborhoods.add("ALTO DE SÃO MANOEL");
        this.neighborhoods.add("ALTO DO SUMARÉ");
        this.neighborhoods.add("ÁREA RURAL DE MOSSORÓ");
        this.neighborhoods.add("BARROCAS");
        this.neighborhoods.add("BELA VISTA");
        this.neighborhoods.add("BELO HORIZONTE");
        this.neighborhoods.add("BOA VISTA");
        this.neighborhoods.add("BOM JARDIM");
        this.neighborhoods.add("BOM JESUS");
        this.neighborhoods.add("CENTRO");
        this.neighborhoods.add("DIX-SEPT ROSADO");
        this.neighborhoods.add("DOM JAIME CÂMARA");
        this.neighborhoods.add("DOZE ANOS");
        this.neighborhoods.add("ILHA DE SANTA LUZIA");
        this.neighborhoods.add("ITAPETINGA");
        this.neighborhoods.add("LAGOA DO MATO");
        this.neighborhoods.add("MONSENHOR AMÉRICO");
        this.neighborhoods.add("NOVA BETÂNIA");
        this.neighborhoods.add("PAREDÕES");
        this.neighborhoods.add("PINTOS");
        this.neighborhoods.add("PLANALTO TREZE DE MAIO");
        this.neighborhoods.add("PRESIDENTE COSTA E SILVA");
        this.neighborhoods.add("REDENÇÃO");
        this.neighborhoods.add("RINCÃO");
        this.neighborhoods.add("SANTA DELMIRA");
        this.neighborhoods.add("SANTA JÚLIA");
        this.neighborhoods.add("SANTO ANTÔNIO");
    }

    public boolean contains(String key)
    {
        return this.neighborhoods.contains(key);
    }

    public String search(String key) 
    {
        int calculatedDistance, shortestDistance = 3;
        String correctedNeighborhood = null;

        if (this.contains(key)) {
            return key;
        }

        for (String neighborhood : this.neighborhoods) {
            if ((calculatedDistance = SearchAlgorithms.levenstein(key, neighborhood)) <= shortestDistance) {
                shortestDistance = calculatedDistance;
                correctedNeighborhood = neighborhood;
            }
        }

        return correctedNeighborhood;
    }

    public Set<String> getNeighborhoods()
    {
        return this.neighborhoods;
    }
}