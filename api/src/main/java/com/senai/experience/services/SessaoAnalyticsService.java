package com.senai.experience.services;

import com.senai.experience.DTO.request.SessaoAnalyticsRequest;
import com.senai.experience.DTO.response.SessaoAnalyticsResponse;
import com.senai.experience.DTO.response.SessaoAnalyticsResumoResponse;
import com.senai.experience.entities.SessaoAnalytics;
import com.senai.experience.mappers.SessaoAnalyticsMapper;
import com.senai.experience.repositories.SessaoAnalyticsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class SessaoAnalyticsService {

    private final SessaoAnalyticsRepository sessaoAnalyticsRepository;

    public SessaoAnalyticsResponse registrar(SessaoAnalyticsRequest request) {
        SessaoAnalytics entity = SessaoAnalyticsMapper.toEntity(request);
        entity.setRegistradoEm(LocalDateTime.now());
        return SessaoAnalyticsMapper.toResponse(sessaoAnalyticsRepository.save(entity));
    }

    public List<SessaoAnalyticsResumoResponse> resumo() {
        return sessaoAnalyticsRepository.findResumoBySecao()
                .stream()
                .map(SessaoAnalyticsMapper::toResumoResponse)
                .toList();
    }
}
