package com.senai.experience.mappers;



import com.senai.experience.DTO.response.StatusHistoricoResponse;
import com.senai.experience.entities.StatusHistorico;

public class StatusHistoricoMapper {

   
    public static StatusHistoricoResponse toResponse(StatusHistorico s){
        StatusHistoricoResponse h = new StatusHistoricoResponse();

        h.setVeiculoId(s.getVeiculo() != null ? s.getVeiculo().getId() : null);
        h.setStatus(s.getStatus());
        h.setDataAlteracao(s.getDataAlteracao());
        h.setId(s.getId());

        return h;
    }

}
