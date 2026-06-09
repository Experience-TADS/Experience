package com.senai.experience.config;

import com.senai.experience.entities.EtapaTemplate;
import com.senai.experience.entities.StatusFabricacao;
import com.senai.experience.repositories.EtapaTemplateRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Popula a tb_etapa_template com os status de fabricação na primeira execução.
 * Os nomes dos status espelham as etapas do ESP32.
 */
@Slf4j
@Configuration
public class EtapaTemplateSeeder {

    @Bean
    CommandLineRunner seedEtapaTemplates(EtapaTemplateRepository repo) {
        return args -> {
            if (repo.count() > 0) {
                log.info("Templates de etapa já existem. Seed ignorado.");
                return;
            }

            seed(repo, StatusFabricacao.AGUARDANDO,
                    "Aguardando Fabricação",
                    "Olá, {nome}! Seu {modelo} está na fila de produção e em breve iniciará a fabricação.",
                    "https://images.unsplash.com/photo-1581092160562-40aa08e78837",
                    "Cada veículo Toyota passa por mais de 300 pontos de inspeção antes de sair da fábrica.");

            seed(repo, StatusFabricacao.MONTAGEM_ESTRUTURAL,
                    "Montagem Estrutural",
                    "{nome}, a estrutura do seu {modelo} está sendo montada com precisão milimétrica. 🏭",
                    "https://images.unsplash.com/photo-1565043589221-1a6fd9ae45c7",
                    "A linha de montagem utiliza robôs de alta precisão para garantir a qualidade da solda.");

            seed(repo, StatusFabricacao.PINTURA,
                    "Hora da Cor!",
                    "Seu {modelo} em {cor} está na cabine de pintura agora. 🎨",
                    "https://images.unsplash.com/photo-1503376780353-7e6692767b70",
                    "O processo de pintura envolve até 5 camadas, incluindo primer, base e verniz.");

            seed(repo, StatusFabricacao.INSTALACAO_MOTOR,
                    "Motor Instalado",
                    "O coração do seu {modelo} acabou de ser instalado, {nome}. ⚙️",
                    "https://images.unsplash.com/photo-1486262715619-67b85e0b08d3",
                    "O motor Toyota passa por testes de bancada antes de ser instalado no veículo.");

            seed(repo, StatusFabricacao.ACABAMENTO_INTERNO,
                    "Acabamento Interno",
                    "O interior do seu {modelo} está sendo finalizado com todos os detalhes, {nome}.",
                    "https://images.unsplash.com/photo-1494976388531-d1058494cdd8",
                    "Cada detalhe do acabamento interno é inspecionado manualmente por especialistas Toyota.");

            seed(repo, StatusFabricacao.INSPECAO_FINAL,
                    "Inspeção Final",
                    "Quase lá, {nome}! Seu {modelo} está passando pela inspeção final de qualidade. ✅",
                    "https://images.unsplash.com/photo-1560958089-b8a1929cea89",
                    "O controle de qualidade Toyota é baseado no conceito Kaizen — melhoria contínua.");

            seed(repo, StatusFabricacao.LIBERACAO_TRANSPORTE,
                    "A Caminho!",
                    "Seu {modelo} foi aprovado em todas as etapas e está a caminho, {nome}! 🚗",
                    "https://images.unsplash.com/photo-1449965408869-eaa3f722e40d",
                    "Parabéns! Seu veículo passou por todas as etapas de fabricação com aprovação total.");

            seed(repo, StatusFabricacao.ENTREGUE,
                    "Seu Carro Chegou!",
                    "{nome}, seu {modelo} está na concessionária esperando por você! 🎉",
                    "https://images.unsplash.com/photo-1492144534655-ae79c964c9d7",
                    "A Toyota oferece suporte completo pós-venda. Agende sua primeira revisão em 1.000 km.");

            seed(repo, StatusFabricacao.CANCELADO,
                    "Pedido Cancelado",
                    "{nome}, seu pedido foi cancelado. Entre em contato com a concessionária.",
                    null,
                    null);

            log.info("✅ Templates de etapa criados com sucesso.");
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
