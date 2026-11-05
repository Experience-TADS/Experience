package com.senai.experience.config;

import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Registra o {@link SanitizingStringDeserializer} no ObjectMapper da aplicação — S-011.
 *
 * <p>Com isso, TODA String desserializada a partir de JSON de requisição é sanitizada
 * contra XSS de forma centralizada, sem precisar anotar cada campo de cada DTO.</p>
 */
@Configuration
public class JacksonSanitizationConfig {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer sanitizingStringCustomizer() {
        return builder -> builder.deserializerByType(String.class, new SanitizingStringDeserializer());
    }
}
