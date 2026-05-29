package com.senai.experience.config;

import com.senai.experience.entities.EtapaTemplate;
import com.senai.experience.entities.StatusFabricacao;
import com.senai.experience.repositories.EtapaTemplateRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EtapaTemplateSeeder implements CommandLineRunner {

    private final EtapaTemplateRepository repo;

    public EtapaTemplateSeeder(EtapaTemplateRepository repo) {
        this.repo = repo;
    }

    @Override
    public void run(String... args) {
        if (repo.count() > 0) return;

        repo.save(new EtapaTemplate(null, StatusFabricacao.AGUARDANDO,
                "Pedido Recebido",
                "Seu pedido foi recebido e está aguardando o início da produção.",
                "https://example.com/fotos/aguardando.jpg",
                "Cada Toyota passa por mais de 2.000 inspeções durante a fabricação."));

        repo.save(new EtapaTemplate(null, StatusFabricacao.EM_FABRICACAO,
                "Em Fabricação",
                "Seu veículo está sendo montado na linha de produção.",
                "https://example.com/fotos/em_fabricacao.jpg",
                "A soldagem da carroceria utiliza mais de 3.000 pontos de solda."));

        repo.save(new EtapaTemplate(null, StatusFabricacao.PINTURA,
                "Pintura",
                "Seu veículo está recebendo as camadas de pintura e acabamento.",
                "https://example.com/fotos/pintura.jpg",
                "São aplicadas até 4 camadas de tinta para garantir durabilidade."));

        repo.save(new EtapaTemplate(null, StatusFabricacao.CONTROLE_QUALIDADE,
                "Controle de Qualidade",
                "Seu veículo está passando pelas inspeções finais de qualidade.",
                "https://example.com/fotos/controle_qualidade.jpg",
                "O controle de qualidade Toyota segue o padrão Jidoka."));

        repo.save(new EtapaTemplate(null, StatusFabricacao.CONCLUIDO,
                "Concluído",
                "A fabricação do seu veículo foi concluída com sucesso!",
                "https://example.com/fotos/concluido.jpg",
                "Seu veículo está pronto e aguardando o transporte."));

        repo.save(new EtapaTemplate(null, StatusFabricacao.ENTREGUE,
                "Entregue",
                "Seu veículo foi entregue. Aproveite a experiência Toyota!",
                "https://example.com/fotos/entregue.jpg",
                "A Toyota é a maior montadora do mundo em volume de produção."));

        repo.save(new EtapaTemplate(null, StatusFabricacao.CANCELADO,
                "Cancelado",
                "O pedido foi cancelado.",
                null,
                null));

        System.out.println("Templates de etapa criados com sucesso.");
    }
}
