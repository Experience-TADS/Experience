package com.senai.experience.unit;

import com.senai.experience.repositories.*;
import com.senai.experience.services.StatusHistoricoService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.senai.experience.entities.StatusFabricacao;
import com.senai.experience.entities.StatusHistorico;
import com.senai.experience.entities.Veiculo;
import org.junit.jupiter.api.BeforeEach;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class StatusHistoricoServiceTest {

    private Veiculo veiculo;

    @BeforeEach
    void setUp() {
        veiculo = new Veiculo();
        veiculo.setId(1L);
    }

    @Mock VeiculoRepository veiculoRepository;
    @Mock StatusHistoricoRepository statusHistoricoRepository;
    @InjectMocks StatusHistoricoService service;

    // ── Transição inicial ─────────────────────────────────────────────────────

    @Test
    void aguardandoParaMontagemEstruturalDeveSerPermitido() {
        veiculo.setStatusVeiculo(StatusFabricacao.AGUARDANDO);
        when(veiculoRepository.findById(1L)).thenReturn(Optional.of(veiculo));
        when(veiculoRepository.save(any())).thenReturn(veiculo);
        when(statusHistoricoRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        StatusHistorico resultado = service.atualizarStatus(1L, StatusFabricacao.MONTAGEM_ESTRUTURAL);

        assertThat(resultado.getStatus()).isEqualTo(StatusFabricacao.MONTAGEM_ESTRUTURAL);
    }

    // ── Transição inválida ────────────────────────────────────────────────────

    @Test
    void transicaoInvalidaDeveLancarExcecao() {
        // AGUARDANDO não pode pular direto para PINTURA
        veiculo.setStatusVeiculo(StatusFabricacao.AGUARDANDO);
        when(veiculoRepository.findById(1L)).thenReturn(Optional.of(veiculo));

        assertThatThrownBy(() -> service.atualizarStatus(1L, StatusFabricacao.PINTURA))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Transição inválida");
    }

    @Test
    void veiculoNaoEncontradoDeveLancarExcecao() {
        when(veiculoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.atualizarStatus(99L, StatusFabricacao.MONTAGEM_ESTRUTURAL))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Veículo não encontrado");
    }

    @Test
    void canceladoNaoPermiteNenhumaTransicao() {
        veiculo.setStatusVeiculo(StatusFabricacao.CANCELADO);
        when(veiculoRepository.findById(1L)).thenReturn(Optional.of(veiculo));

        assertThatThrownBy(() -> service.atualizarStatus(1L, StatusFabricacao.AGUARDANDO))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Transição inválida");
    }

    // ── Cadeia completa de transições válidas ─────────────────────────────────

    @Test
    void montagemParaPinturaDeveSerPermitido() {
        veiculo.setStatusVeiculo(StatusFabricacao.MONTAGEM_ESTRUTURAL);
        when(veiculoRepository.findById(1L)).thenReturn(Optional.of(veiculo));
        when(veiculoRepository.save(any())).thenReturn(veiculo);
        when(statusHistoricoRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        StatusHistorico resultado = service.atualizarStatus(1L, StatusFabricacao.PINTURA);

        assertThat(resultado.getStatus()).isEqualTo(StatusFabricacao.PINTURA);
    }

    @Test
    void pinturaParaInstalacaoMotorDeveSerPermitido() {
        veiculo.setStatusVeiculo(StatusFabricacao.PINTURA);
        when(veiculoRepository.findById(1L)).thenReturn(Optional.of(veiculo));
        when(veiculoRepository.save(any())).thenReturn(veiculo);
        when(statusHistoricoRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        StatusHistorico resultado = service.atualizarStatus(1L, StatusFabricacao.INSTALACAO_MOTOR);

        assertThat(resultado.getStatus()).isEqualTo(StatusFabricacao.INSTALACAO_MOTOR);
    }

    @Test
    void instalacaoMotorParaAcabamentoInternoDeveSerPermitido() {
        veiculo.setStatusVeiculo(StatusFabricacao.INSTALACAO_MOTOR);
        when(veiculoRepository.findById(1L)).thenReturn(Optional.of(veiculo));
        when(veiculoRepository.save(any())).thenReturn(veiculo);
        when(statusHistoricoRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        StatusHistorico resultado = service.atualizarStatus(1L, StatusFabricacao.ACABAMENTO_INTERNO);

        assertThat(resultado.getStatus()).isEqualTo(StatusFabricacao.ACABAMENTO_INTERNO);
    }

    @Test
    void acabamentoInternoParaInspecaoFinalDeveSerPermitido() {
        veiculo.setStatusVeiculo(StatusFabricacao.ACABAMENTO_INTERNO);
        when(veiculoRepository.findById(1L)).thenReturn(Optional.of(veiculo));
        when(veiculoRepository.save(any())).thenReturn(veiculo);
        when(statusHistoricoRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        StatusHistorico resultado = service.atualizarStatus(1L, StatusFabricacao.INSPECAO_FINAL);

        assertThat(resultado.getStatus()).isEqualTo(StatusFabricacao.INSPECAO_FINAL);
    }

    @Test
    void inspecaoFinalParaLiberacaoTransporteDeveSerPermitido() {
        veiculo.setStatusVeiculo(StatusFabricacao.INSPECAO_FINAL);
        when(veiculoRepository.findById(1L)).thenReturn(Optional.of(veiculo));
        when(veiculoRepository.save(any())).thenReturn(veiculo);
        when(statusHistoricoRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        StatusHistorico resultado = service.atualizarStatus(1L, StatusFabricacao.LIBERACAO_TRANSPORTE);

        assertThat(resultado.getStatus()).isEqualTo(StatusFabricacao.LIBERACAO_TRANSPORTE);
    }

    @Test
    void liberacaoTransporteParaEntregueDeveSerPermitido() {
        veiculo.setStatusVeiculo(StatusFabricacao.LIBERACAO_TRANSPORTE);
        when(veiculoRepository.findById(1L)).thenReturn(Optional.of(veiculo));
        when(veiculoRepository.save(any())).thenReturn(veiculo);
        when(statusHistoricoRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        StatusHistorico resultado = service.atualizarStatus(1L, StatusFabricacao.ENTREGUE);

        assertThat(resultado.getStatus()).isEqualTo(StatusFabricacao.ENTREGUE);
    }

    @Test
    void aguardandoParaCanceladoDeveSerPermitido() {
        veiculo.setStatusVeiculo(StatusFabricacao.AGUARDANDO);
        when(veiculoRepository.findById(1L)).thenReturn(Optional.of(veiculo));
        when(veiculoRepository.save(any())).thenReturn(veiculo);
        when(statusHistoricoRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        StatusHistorico resultado = service.atualizarStatus(1L, StatusFabricacao.CANCELADO);

        assertThat(resultado.getStatus()).isEqualTo(StatusFabricacao.CANCELADO);
    }

    @Test
    void entregueNaoPermiteNenhumaTransicao() {
        veiculo.setStatusVeiculo(StatusFabricacao.ENTREGUE);
        when(veiculoRepository.findById(1L)).thenReturn(Optional.of(veiculo));

        assertThatThrownBy(() -> service.atualizarStatus(1L, StatusFabricacao.LIBERACAO_TRANSPORTE))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Transição inválida");
    }

    @Test
    void liberacaoTransporteNaoPodeCancelar() {
        // LIBERACAO_TRANSPORTE só pode ir para ENTREGUE
        veiculo.setStatusVeiculo(StatusFabricacao.LIBERACAO_TRANSPORTE);
        when(veiculoRepository.findById(1L)).thenReturn(Optional.of(veiculo));

        assertThatThrownBy(() -> service.atualizarStatus(1L, StatusFabricacao.CANCELADO))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Transição inválida");
    }
}
