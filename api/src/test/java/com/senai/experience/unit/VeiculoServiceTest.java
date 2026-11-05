package com.senai.experience.unit;

import com.senai.experience.entities.StatusFabricacao;
import com.senai.experience.entities.StatusHistorico;
import com.senai.experience.entities.Veiculo;
import com.senai.experience.repositories.PedidoRepository;
import com.senai.experience.repositories.ProdutoRepository;
import com.senai.experience.repositories.StatusHistoricoRepository;
import com.senai.experience.repositories.VeiculoRepository;
import com.senai.experience.services.AgendaService;
import com.senai.experience.services.VeiculoService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para {@link VeiculoService}.
 *
 * <p>Cobre os métodos consumidos pelo listener MQTT:
 * <ul>
 *   <li>{@code mapearEtapaParaStatus} — lógica de mapeamento ESP32 → StatusFabricacao</li>
 *   <li>{@code parseChassi} — conversão "CHASSI_00001" → 10001</li>
 *   <li>{@code atualizarStatus} — persistência e registro de histórico</li>
 * </ul>
 * </p>
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("VeiculoService — lógica de negócio IoT")
class VeiculoServiceTest {

    @Mock private VeiculoRepository veiculoRepository;
    @Mock private ProdutoRepository produtoRepository;
    @Mock private StatusHistoricoRepository statusHistoricoRepository;
    @Mock private PedidoRepository pedidoRepository;
    @Mock private AgendaService agendaService;

    @InjectMocks
    private VeiculoService veiculoService;

    // ─────────────────────────────────────────────────────────────────────────
    // mapearEtapaParaStatus
    // ─────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("mapearEtapaParaStatus")
    class MapearEtapaParaStatus {

        @Test
        @DisplayName("MONTAGEM_ESTRUTURAL + Iniciado → MONTAGEM_ESTRUTURAL")
        void montagemEstruturalIniciado() {
            assertThat(veiculoService.mapearEtapaParaStatus("MONTAGEM_ESTRUTURAL", "Iniciado"))
                    .isEqualTo(StatusFabricacao.MONTAGEM_ESTRUTURAL);
        }

        @Test
        @DisplayName("PINTURA + Iniciado → PINTURA")
        void pinturaIniciado() {
            assertThat(veiculoService.mapearEtapaParaStatus("PINTURA", "Iniciado"))
                    .isEqualTo(StatusFabricacao.PINTURA);
        }

        @Test
        @DisplayName("INSTALACAO_MOTOR + Iniciado → INSTALACAO_MOTOR")
        void instalacaoMotorIniciado() {
            assertThat(veiculoService.mapearEtapaParaStatus("INSTALACAO_MOTOR", "Iniciado"))
                    .isEqualTo(StatusFabricacao.INSTALACAO_MOTOR);
        }

        @Test
        @DisplayName("ACABAMENTO_INTERNO + Iniciado → ACABAMENTO_INTERNO")
        void acabamentoInternoIniciado() {
            assertThat(veiculoService.mapearEtapaParaStatus("ACABAMENTO_INTERNO", "Iniciado"))
                    .isEqualTo(StatusFabricacao.ACABAMENTO_INTERNO);
        }

        @Test
        @DisplayName("INSPECAO_FINAL + Iniciado → INSPECAO_FINAL")
        void inspecaoFinalIniciado() {
            assertThat(veiculoService.mapearEtapaParaStatus("INSPECAO_FINAL", "Iniciado"))
                    .isEqualTo(StatusFabricacao.INSPECAO_FINAL);
        }

        @Test
        @DisplayName("LIBERACAO_TRANSPORTE + Finalizado → LIBERACAO_TRANSPORTE")
        void liberacaoTransporteFinalizado() {
            assertThat(veiculoService.mapearEtapaParaStatus("LIBERACAO_TRANSPORTE", "Finalizado"))
                    .isEqualTo(StatusFabricacao.LIBERACAO_TRANSPORTE);
        }

        @Test
        @DisplayName("Etapa inválida → null (evento ignorado)")
        void etapaInvalida_retornaNull() {
            assertThat(veiculoService.mapearEtapaParaStatus("ETAPA_INEXISTENTE", "Iniciado"))
                    .isNull();
        }

        @Test
        @DisplayName("LIBERACAO_TRANSPORTE + Iniciado → null (status errado)")
        void liberacaoTransporteIniciado_retornaNull() {
            assertThat(veiculoService.mapearEtapaParaStatus("LIBERACAO_TRANSPORTE", "Iniciado"))
                    .isNull();
        }

        @Test
        @DisplayName("PINTURA + Finalizado → null (status errado)")
        void pinturaFinalizado_retornaNull() {
            assertThat(veiculoService.mapearEtapaParaStatus("PINTURA", "Finalizado"))
                    .isNull();
        }

        @Test
        @DisplayName("Etapa null → null")
        void etapaNull_retornaNull() {
            assertThat(veiculoService.mapearEtapaParaStatus(null, "Iniciado"))
                    .isNull();
        }

        @Test
        @DisplayName("Status null → null")
        void statusNull_retornaNull() {
            assertThat(veiculoService.mapearEtapaParaStatus("PINTURA", null))
                    .isNull();
        }

        @Test
        @DisplayName("Comparação de etapa é case-insensitive")
        void etapaMinuscula_mapeaCorretamente() {
            assertThat(veiculoService.mapearEtapaParaStatus("pintura", "Iniciado"))
                    .isEqualTo(StatusFabricacao.PINTURA);
        }

        @Test
        @DisplayName("Comparação de status é case-insensitive")
        void statusMaiusculo_mapeaCorretamente() {
            assertThat(veiculoService.mapearEtapaParaStatus("PINTURA", "INICIADO"))
                    .isEqualTo(StatusFabricacao.PINTURA);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // parseChassi
    // ─────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("parseChassi")
    class ParseChassi {

        @Test
        @DisplayName("CHASSI_00001 → 10001")
        void chassi00001() {
            assertThat(veiculoService.parseChassi("CHASSI_00001")).isEqualTo(10001);
        }

        @Test
        @DisplayName("CHASSI_00010 → 10010")
        void chassi00010() {
            assertThat(veiculoService.parseChassi("CHASSI_00010")).isEqualTo(10010);
        }

        @Test
        @DisplayName("Chassi null → -1")
        void chassiNull() {
            assertThat(veiculoService.parseChassi(null)).isEqualTo(-1);
        }

        @Test
        @DisplayName("Formato inválido (sem prefixo numérico) → -1")
        void chassiFormatoInvalido() {
            assertThat(veiculoService.parseChassi("CHASSI_ABC")).isEqualTo(-1);
        }

        @Test
        @DisplayName("String vazia → -1")
        void chassiVazio() {
            assertThat(veiculoService.parseChassi("CHASSI_")).isEqualTo(-1);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // atualizarStatus
    // ─────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("atualizarStatus")
    class AtualizarStatus {

        private Veiculo veiculo;

        @BeforeEach
        void setUp() {
            veiculo = new Veiculo();
            veiculo.setStatusVeiculo(StatusFabricacao.AGUARDANDO);
        }

        @Test
        @DisplayName("Veículo encontrado — atualiza status e registra histórico")
        void veiculoEncontrado_atualizaEPersiste() {
            when(veiculoRepository.findByChassi(10001)).thenReturn(veiculo);
            when(veiculoRepository.save(any(Veiculo.class))).thenAnswer(inv -> inv.getArgument(0));
            when(statusHistoricoRepository.save(any(StatusHistorico.class))).thenAnswer(inv -> inv.getArgument(0));

            Veiculo resultado = veiculoService.atualizarStatus(10001, StatusFabricacao.PINTURA);

            assertThat(resultado).isNotNull();
            assertThat(resultado.getStatusVeiculo()).isEqualTo(StatusFabricacao.PINTURA);

            verify(veiculoRepository).save(veiculo);
            verify(statusHistoricoRepository).save(any(StatusHistorico.class));
        }

        @Test
        @DisplayName("Veículo não encontrado → retorna null")
        void veiculoNaoEncontrado_retornaNull() {
            when(veiculoRepository.findByChassi(anyInt())).thenReturn(null);

            Veiculo resultado = veiculoService.atualizarStatus(99999, StatusFabricacao.PINTURA);

            assertThat(resultado).isNull();
            verify(veiculoRepository, never()).save(any());
        }
    }
}
