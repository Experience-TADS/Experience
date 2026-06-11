package com.senai.experience.config;

import com.senai.experience.entities.EtapaTemplate;
import com.senai.experience.entities.StatusFabricacao;
import com.senai.experience.repositories.EtapaTemplateRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Popula a tb_etapa_template com os status de fabricação na primeira execução.
 * Os nomes dos status espelham as etapas do ESP32.
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

            seed(repo, StatusFabricacao.MONTAGEM_ESTRUTURAL,
                    "Montagem Estrutural",
                    "A estrutura do seu veículo está sendo montada com precisão milimétrica.",
                    null,
                    "A linha de montagem utiliza robôs de alta precisão para garantir a qualidade da solda.");

            seed(repo, StatusFabricacao.PINTURA,
                    "Pintura",
                    "Seu veículo está recebendo a pintura. São aplicadas múltiplas camadas para durabilidade e brilho.",
                    null,
                    "O processo de pintura envolve até 5 camadas, incluindo primer, base e verniz.");

            seed(repo, StatusFabricacao.INSTALACAO_MOTOR,
                    "Instalação do Motor",
                    "O motor e os componentes mecânicos estão sendo instalados no seu veículo.",
                    null,
                    "O motor Toyota passa por testes de bancada antes de ser instalado no veículo.");

            seed(repo, StatusFabricacao.ACABAMENTO_INTERNO,
                    "Acabamento Interno",
                    "O interior do seu veículo está sendo finalizado com todos os detalhes.",
                    null,
                    "Cada detalhe do acabamento interno é inspecionado manualmente por especialistas Toyota.");

            seed(repo, StatusFabricacao.INSPECAO_FINAL,
                    "Inspeção Final",
                    "Seu veículo está passando pela inspeção final para garantir os mais altos padrões Toyota.",
                    null,
                    "O controle de qualidade Toyota é baseado no conceito Kaizen — melhoria contínua.");

            seed(repo, StatusFabricacao.LIBERACAO_TRANSPORTE,
                    "Liberado para Transporte",
                    "Seu veículo foi aprovado em todas as etapas e está pronto para ser entregue.",
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
