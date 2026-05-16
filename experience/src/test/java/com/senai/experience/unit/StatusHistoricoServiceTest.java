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


@Test
void aguardandoParaEmFabricacaoDeveSerPermitido() {
    // prepara o veículo com status AGUARDANDO
    veiculo.setStatusVeiculo(StatusFabricacao.AGUARDANDO);

    // configura os mocks para retornar o veículo e salvar o histórico
    when(veiculoRepository.findById(1L)).thenReturn(Optional.of(veiculo));
    when(veiculoRepository.save(any())).thenReturn(veiculo);
    when(statusHistoricoRepository.save(any())).thenAnswer(i -> i.getArgument(0));

    // executa
    StatusHistorico resultado = service.atualizarStatus(1L, StatusFabricacao.EM_FABRICACAO);

    // verifica
    assertThat(resultado.getStatus()).isEqualTo(StatusFabricacao.EM_FABRICACAO);
}

@Test
void transicaoInvalidaDeveLancarExcecao() {
    // AGUARDANDO não pode ir direto para PINTURA
    veiculo.setStatusVeiculo(StatusFabricacao.AGUARDANDO);

    when(veiculoRepository.findById(1L)).thenReturn(Optional.of(veiculo));

    // verifica que lança exceção com a mensagem correta
    assertThatThrownBy(() -> service.atualizarStatus(1L, StatusFabricacao.PINTURA))
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Transição inválida");
}

@Test
void veiculoNaoEncontradoDeveLancarExcecao() {
    when(veiculoRepository.findById(99L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.atualizarStatus(99L, StatusFabricacao.EM_FABRICACAO))
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

}
