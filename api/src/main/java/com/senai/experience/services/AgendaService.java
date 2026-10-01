package com.senai.experience.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.senai.experience.DTO.request.AgendaRequest;
import com.senai.experience.DTO.request.AtualizarStatusAgendaRequest;
import com.senai.experience.entities.Agenda;
import com.senai.experience.entities.Usuario;
import com.senai.experience.entities.agenda.StatusAgenda;
import com.senai.experience.entities.agenda.TipoAgenda;
import com.senai.experience.repositories.AgendaRepository;
import com.senai.experience.repositories.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AgendaService {

    private final AgendaRepository agendaRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public Agenda criar(AgendaRequest request, Long colaboradorId) {
        Usuario colaborador = buscarUsuario(colaboradorId);
        Usuario cliente = buscarUsuario(request.getClienteId());

        validarConflito(colaboradorId, request.getDataHora(), null);

        Agenda agenda = new Agenda();
        agenda.setColaborador(colaborador);
        agenda.setCliente(cliente);
        agenda.setDataHora(request.getDataHora());
        agenda.setTipo(request.getTipo());
        agenda.setStatus(StatusAgenda.PENDENTE);
        agenda.setObservacao(request.getObservacao());

        return agendaRepository.save(agenda);
    }

    @Transactional(readOnly = true)
    public List<Agenda> listarPorColaborador(Long colaboradorId) {
        Usuario colaborador = buscarUsuario(colaboradorId);
        return agendaRepository.findByColaborador(colaborador);
    }

    @Transactional(readOnly = true)
    public Agenda buscarPorId(Long id, Long usuarioId, boolean isAdmin) {
        Agenda agenda = agendaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Agendamento não encontrado: " + id));

        // Admin pode ver qualquer agendamento
        if (isAdmin) return agenda;

        // Colaborador pode ver seus próprios agendamentos
        if (agenda.getColaborador().getId().equals(usuarioId)) return agenda;

        // Cliente pode ver agendamentos dos quais é o destinatário
        if (agenda.getCliente().getId().equals(usuarioId)) return agenda;

        throw new RuntimeException("Acesso negado: você não tem permissão para visualizar este agendamento.");
    }

    @Transactional
    public Agenda atualizar(Long id, AgendaRequest request, Long colaboradorId) {
        Agenda agenda = findById(id);
        verificarPropriedade(agenda, colaboradorId);

        if (!agenda.getDataHora().equals(request.getDataHora())) {
            validarConflito(colaboradorId, request.getDataHora(), id);
        }

        Usuario cliente = buscarUsuario(request.getClienteId());
        agenda.setCliente(cliente);
        agenda.setDataHora(request.getDataHora());
        agenda.setTipo(request.getTipo());
        agenda.setObservacao(request.getObservacao());

        return agendaRepository.save(agenda);
    }

    @Transactional
    public Agenda atualizarStatus(Long id, AtualizarStatusAgendaRequest request, Long colaboradorId, boolean isAdmin) {
        Agenda agenda = findById(id);

        if (!isAdmin) {
            verificarPropriedade(agenda, colaboradorId);
        }

        agenda.setStatus(request.getStatus());
        return agendaRepository.save(agenda);
    }

    @Transactional
    public void deletar(Long id, Long colaboradorId) {
        Agenda agenda = findById(id);
        verificarPropriedade(agenda, colaboradorId);
        agendaRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<LocalDateTime> consultarHorariosOcupados(Long colaboradorId) {
        buscarUsuario(colaboradorId);
        return agendaRepository.findHorariosOcupados(
                colaboradorId,
                StatusAgenda.CANCELADO,
                LocalDateTime.now()
        );
    }

    @Transactional
    public void criarEventoEntregaAutomatico(Usuario colaborador, Usuario cliente) {
        boolean jaExiste = agendaRepository.existsByColaboradorAndClienteAndTipoAndStatus(
                colaborador, cliente, TipoAgenda.ENTREGA, StatusAgenda.PENDENTE
        );

        if (jaExiste) return;

        // Tenta encontrar um horário disponível a partir de 24h no futuro,
        // avançando de 24h em 24h até encontrar um slot livre.
        LocalDateTime dataEntrega = LocalDateTime.now().plusHours(24);
        int maxTentativas = 30; // evita loop infinito em agendas muito cheias
        int tentativas = 0;

        while (agendaRepository.existeConflito(colaborador.getId(), dataEntrega, StatusAgenda.CANCELADO, null)) {
            dataEntrega = dataEntrega.plusHours(24);
            if (++tentativas >= maxTentativas) {
                throw new RuntimeException("Não foi possível encontrar um horário disponível para a entrega após " + maxTentativas + " tentativas.");
            }
        }

        Agenda agenda = new Agenda();
        agenda.setColaborador(colaborador);
        agenda.setCliente(cliente);
        agenda.setDataHora(dataEntrega);
        agenda.setTipo(TipoAgenda.ENTREGA);
        agenda.setStatus(StatusAgenda.PENDENTE);
        agenda.setObservacao("Entrega gerada automaticamente pela confirmação de chegada do veículo.");

        agendaRepository.save(agenda);
    }


    private Usuario buscarUsuario(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + id));
    }

    // Lookup interno sem validação de acesso — use buscarPorId para chamadas externas
    private Agenda findById(Long id) {
        return agendaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Agendamento não encontrado: " + id));
    }

    private void validarConflito(Long colaboradorId, LocalDateTime dataHora, Long excludeId) {
        boolean conflito = agendaRepository.existeConflito(
                colaboradorId, dataHora, StatusAgenda.CANCELADO, excludeId
        );
        if (conflito) {
            throw new RuntimeException(
                "Conflito de horário: o colaborador já possui um agendamento em " + dataHora
            );
        }
    }

    private void verificarPropriedade(Agenda agenda, Long colaboradorId) {
        if (!agenda.getColaborador().getId().equals(colaboradorId)) {
            throw new RuntimeException("Acesso negado: você só pode gerenciar sua própria agenda.");
        }
    }
}
