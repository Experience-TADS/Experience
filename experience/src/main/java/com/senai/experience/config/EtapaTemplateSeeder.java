package com.senai.experience.config;

import com.senai.experience.entities.EtapaTemplate;
import com.senai.experience.entities.StatusFabricacao;
import com.senai.experience.repositories.EtapaTemplateRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Popula a tb_etapa_template com os 6 status de fabricação na primeira execução.
 * Usa findByStatus para não duplicar registros em reinicializações.
 */
@Configuration
public class EtapaTemplateSeeder {

    @Bean
    CommandLineRunner seedEtapaTemplates(EtapaTemplateRepository repo) {
        return args -> {
            seed(repo, StatusFabricacao.AGUARDANDO,
                    "Aguardando Fabricação",
                    "Seu veículo está na fila de produção e em breve iniciará a fabricação.",
                    null,
                    "Cada veículo Toyota passa por mais de 300 pontos de inspeção antes de sair da fábrica.");

            seed(repo, StatusFabricacao.EM_FABRICACAO,
                    "Em Fabricação",
                    "A estrutura do seu veículo está sendo montada com precisão milimétrica.",
                    null,
                    "A linha de montagem utiliza robôs de alta precisão para garantir a qualidade da solda.");

            seed(repo, StatusFabricacao.PINTURA,
                    "Pintura",
                    "Seu veículo está recebendo a pintura. São aplicadas múltiplas camadas para durabilidade e brilho.",
                    null,
                    "O processo de pintura envolve até 5 camadas, incluindo primer, base e verniz.");

            seed(repo, StatusFabricacao.CONTROLE_QUALIDADE,
                    "Controle de Qualidade",
                    "Seu veículo está passando pela inspeção final para garantir os mais altos padrões Toyota.",
                    null,
                    "O controle de qualidade Toyota é baseado no conceito Kaizen — melhoria contínua.");

            seed(repo, StatusFabricacao.CONCLUIDO,
                    "Fabricação Concluída",
                    "Seu veículo foi fabricado com sucesso e está pronto para entrega.",
                    null,
                    "Parabéns! Seu veículo passou por todas as etapas de fabricação com aprovação total.");

            seed(repo, StatusFabricacao.ENTREGUE,
                    "Entregue",
                    "Seu veículo foi entregue. Boas viagens!",
                    null,
                    "A Toyota oferece suporte completo pós-venda. Agende sua primeira revisão em 1.000 km.");
        };
    }

    private void seed(EtapaTemplateRepository repo, StatusFabricacao status,
                      String titulo, String mensagem, String fotoUrl, String curiosidade) {
        if (repo.findByStatus(status).isEmpty()) {
            EtapaTemplate etapa = new EtapaTemplate();
            etapa.setStatus(status);
            etapa.setTitulo(titulo);
            etapa.setMensagem(mensagem);
            etapa.setFotoUrl(fotoUrl);
            etapa.setCuriosidade(curiosidade);
            repo.save(etapa);
        }
    }
}
