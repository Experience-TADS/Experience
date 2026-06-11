package com.senai.experience.config;

import com.senai.experience.entities.*;
import com.senai.experience.entities.role.UserRole;
import com.senai.experience.repositories.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Popula o banco com dados iniciais para demonstração da integração MQTT.
 *
 * Cria:
 *  - 1 vendedor
 *  - 10 clientes (PessoaFisica) — inseridos via JDBC para contornar @CPF Bean Validation
 *  - 5 produtos (modelos Toyota)
 *  - 10 pedidos (1 por cliente)
 *  - 10 veículos em AGUARDANDO, cada um vinculado ao seu pedido
 *
 * Idempotente: verifica se já existem veículos com chassi base antes de inserir.
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class DataSeeder {

    private static final int CHASSI_BASE = 10001;

    private static final Object[][] PRODUTOS = {
        {"Corolla", "Branco Pérola",  "XEi",    2024},
        {"Hilux",   "Prata Metálico", "SRX",    2024},
        {"Yaris",   "Vermelho",       "XLS",    2024},
        {"RAV4",    "Preto",          "GR-S",   2025},
        {"SW4",     "Cinza Grafite",  "Diamond", 2025},
    };

    // CPFs válidos sem formatação (11 dígitos) — compatível com @Column(length=11)
    private static final Object[][] CLIENTES = {
        {"Ana Souza",       "ana.souza@email.com",       "52998224725", LocalDate.of(1990, 3, 15)},
        {"Bruno Lima",      "bruno.lima@email.com",      "02198146005", LocalDate.of(1985, 7, 22)},
        {"Carla Mendes",    "carla.mendes@email.com",    "73951938040", LocalDate.of(1992, 11, 5)},
        {"Diego Ferreira",  "diego.ferreira@email.com",  "49296778054", LocalDate.of(1988, 1, 30)},
        {"Elisa Costa",     "elisa.costa@email.com",     "35041827004", LocalDate.of(1995, 6, 18)},
        {"Felipe Rocha",    "felipe.rocha@email.com",    "11789258090", LocalDate.of(1983, 9, 12)},
        {"Gabriela Nunes",  "gabriela.nunes@email.com",  "85456265080", LocalDate.of(1997, 4, 25)},
        {"Henrique Alves",  "henrique.alves@email.com",  "28344158060", LocalDate.of(1991, 8, 8)},
        {"Isabela Martins", "isabela.martins@email.com", "67132042000", LocalDate.of(1994, 2, 14)},
        {"Joao Pereira",    "joao.pereira@email.com",    "40561989008", LocalDate.of(1987, 12, 3)},
    };

    @Bean
    CommandLineRunner seedDados(
            ProdutoRepository produtoRepo,
            UsuarioRepository usuarioRepo,
            PedidoRepository pedidoRepo,
            ItemPedidoRepository itemPedidoRepo,
            VeiculoRepository veiculoRepo,
            StatusHistoricoRepository statusHistoricoRepo,
            PasswordEncoder passwordEncoder,
            JdbcTemplate jdbc
    ) {
        return args -> {

            if (veiculoRepo.findByChassi(CHASSI_BASE) != null) {
                log.info("[DataSeeder] Dados já existem — seed ignorado.");
                return;
            }

            log.info("[DataSeeder] Iniciando seed de dados...");

            // 1. Admin (primeiro insert → id=1 em banco limpo)
            criarAdmin(usuarioRepo, passwordEncoder);

            // 2. Produtos
            List<Produto> produtos = criarProdutos(produtoRepo);

            // 3. Vendedor
            Usuario vendedor = criarVendedor(usuarioRepo, passwordEncoder);

            // 3. Clientes + Pedidos + Veículos
            for (int i = 0; i < CLIENTES.length; i++) {
                Object[] dados  = CLIENTES[i];
                Produto produto = produtos.get(i % produtos.size());

                Long clienteId = criarClienteJdbc(jdbc, passwordEncoder, dados);
                Usuario clienteRef = usuarioRepo.findById(clienteId).orElseThrow();

                Pedido pedido = criarPedido(pedidoRepo, clienteRef, vendedor, produto);
                criarItemPedido(itemPedidoRepo, pedido, produto);
                Veiculo veiculo = criarVeiculo(veiculoRepo, produto, pedido, CHASSI_BASE + i);
                criarHistoricoInicial(statusHistoricoRepo, veiculo);
            }

            log.info("[DataSeeder] Seed concluído: 10 veículos em AGUARDANDO criados.");
        };
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private void criarAdmin(UsuarioRepository repo, PasswordEncoder encoder) {
        String email = "admin@experience.com";
        if (repo.findByEmail(email) != null) return;

        Usuario admin = new Usuario(
                "Administrador",
                email,
                encoder.encode("admin123"),
                LocalDate.of(1990, 1, 1),
                UserRole.ADMIN
        );
        repo.save(admin);
        log.info("[DataSeeder] Admin criado — email: {} | senha: admin123", email);
    }

    private List<Produto> criarProdutos(ProdutoRepository repo) {
        for (Object[] d : PRODUTOS) {
            Produto p = new Produto();
            p.setModelo((String) d[0]);
            p.setCor((String) d[1]);
            p.setVersao((String) d[2]);
            p.setAno((Integer) d[3]);
            repo.save(p);
        }
        return repo.findAll();
    }

    private Usuario criarVendedor(UsuarioRepository repo, PasswordEncoder encoder) {
        String email = "vendedor.toyota@experience.com";
        Usuario existente = repo.findByEmail(email);
        if (existente != null) return existente;

        Usuario vendedor = new Usuario(
                "Carlos Vendedor Toyota",
                email,
                encoder.encode("vendedor123"),
                LocalDate.of(1980, 5, 10),
                UserRole.VENDEDOR
        );
        return repo.save(vendedor);
    }

    /**
     * Insere PessoaFisica diretamente via JDBC para contornar o @CPF Bean Validation,
     * que rejeita CPFs sem máscara, enquanto o banco exige length=11 (sem máscara).
     * Retorna o id gerado.
     */
    private Long criarClienteJdbc(JdbcTemplate jdbc, PasswordEncoder encoder, Object[] dados) {
        String nome       = (String) dados[0];
        String email      = (String) dados[1];
        String cpf        = (String) dados[2];
        LocalDate nasc    = (LocalDate) dados[3];
        String senhaHash  = encoder.encode("cliente123");

        // Verifica se já existe pelo email
        List<Long> ids = jdbc.queryForList(
                "SELECT id FROM usuario WHERE email = ?", Long.class, email);
        if (!ids.isEmpty()) return ids.get(0);

        // Insere na tabela pai (usuario)
        Long usuarioId = jdbc.queryForObject(
                "INSERT INTO usuario (ativo, data_nascimento, email, nome, role, senha_hash) " +
                "VALUES (true, ?, ?, ?, 'CLIENTE', ?) RETURNING id",
                Long.class,
                java.sql.Date.valueOf(nasc), email, nome, senhaHash
        );

        // Insere na tabela filha (pessoa_fisica)
        jdbc.update(
                "INSERT INTO pessoa_fisica (cpf, id) VALUES (?, ?)",
                cpf, usuarioId
        );

        return usuarioId;
    }

    private Pedido criarPedido(PedidoRepository repo, Usuario cliente,
                                Usuario vendedor, Produto produto) {
        BigDecimal preco = switch (produto.getModelo()) {
            case "Corolla" -> new BigDecimal("149900.00");
            case "Hilux"   -> new BigDecimal("289900.00");
            case "Yaris"   -> new BigDecimal("109900.00");
            case "RAV4"    -> new BigDecimal("319900.00");
            case "SW4"     -> new BigDecimal("399900.00");
            default        -> new BigDecimal("150000.00");
        };
        Pedido pedido = new Pedido();
        pedido.setIdCliente(cliente);
        pedido.setIdVendedor(vendedor);
        pedido.setDataPedido(LocalDateTime.now());
        pedido.setValorTotal(preco);
        return repo.save(pedido);
    }

    private void criarItemPedido(ItemPedidoRepository repo, Pedido pedido, Produto produto) {
        ItemPedido item = new ItemPedido();
        item.setPedido(pedido);
        item.setProduto(produto);
        item.setQuantidade(1);
        repo.save(item);
    }

    private Veiculo criarVeiculo(VeiculoRepository repo, Produto produto,
                                  Pedido pedido, int chassi) {
        Veiculo v = new Veiculo();
        v.setProduto(produto);
        v.setChassi(chassi);
        v.setStatusVeiculo(StatusFabricacao.AGUARDANDO);
        v.setPedido(pedido);
        return repo.save(v);
    }

    private void criarHistoricoInicial(StatusHistoricoRepository repo, Veiculo veiculo) {
        StatusHistorico h = new StatusHistorico();
        h.setVeiculo(veiculo);
        h.setStatus(StatusFabricacao.AGUARDANDO);
        h.setDataAlteracao(LocalDateTime.now());
        repo.save(h);
    }
}
