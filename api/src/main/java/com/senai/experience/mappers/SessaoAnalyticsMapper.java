package com.senai.experience.mappers;

import com.senai.experience.DTO.request.SessaoAnalyticsRequest;
import com.senai.experience.DTO.response.SessaoAnalyticsResponse;
import com.senai.experience.DTO.response.SessaoAnalyticsResumoResponse;
import com.senai.experience.entities.SessaoAnalytics;

public class SessaoAnalyticsMapper {

    public static SessaoAnalytics toEntity(SessaoAnalyticsRequest dto) {
        SessaoAnalytics entity = new SessaoAnalytics();
        entity.setClienteId(dto.getClienteId());
        entity.setSecao(dto.getSecao());
        entity.setDuracaoSegundos(dto.getDuracaoSegundos());
        return entity;
    }

    public static SessaoAnalyticsResponse toResponse(SessaoAnalytics entity) {
        SessaoAnalyticsResponse response = new SessaoAnalyticsResponse();
        response.setId(entity.getId());
        response.setClienteId(entity.getClienteId());
        response.setSecao(entity.getSecao());
        response.setDuracaoSegundos(entity.getDuracaoSegundos());
        response.setRegistradoEm(entity.getRegistradoEm());
        return response;
    }

    public static SessaoAnalyticsResumoResponse toResumoResponse(Object[] row) {
        SessaoAnalyticsResumoResponse response = new SessaoAnalyticsResumoResponse();
        response.setSecao((String) row[0]);
        response.setMediaDuracaoSegundos((Double) row[1]);
        response.setTotalRegistros((Long) row[2]);
        return response;
    }
}
