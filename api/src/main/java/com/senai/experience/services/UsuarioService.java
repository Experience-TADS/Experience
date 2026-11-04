package com.senai.experience.services;

import com.senai.experience.entities.Usuario;
import com.senai.experience.repositories.UsuarioRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditService auditService;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          PasswordEncoder passwordEncoder,
                          AuditService auditService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditService = auditService;
    }

    public Usuario save(Usuario usuario) {
        usuario.setSenhaHash(passwordEncoder.encode(usuario.getSenhaHash()));
        return usuarioRepository.save(usuario);
    }

    public void deleteById(Long id) {
        usuarioRepository.deleteById(id);
    }

    public Usuario findById(Long id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    public Usuario update(Usuario usuario) {
        if (usuario.getId() == null || !usuarioRepository.existsById(usuario.getId())) {
            return null;
        }
        Usuario existente = usuarioRepository.findById(usuario.getId()).orElse(null);

        // S-010: detecta alteração de senha e de role para audit log
        String realizadoPor = resolverUsuarioLogado();
        if (existente != null) {
            boolean senhaAlterada = !passwordEncoder.matches(usuario.getSenhaHash(), existente.getSenhaHash());
            if (senhaAlterada) {
                auditService.logAlteracaoSenha(realizadoPor, existente.getEmail());
            }
            if (existente.getRole() != null && !existente.getRole().equals(usuario.getRole())) {
                auditService.logAlteracaoRole(realizadoPor, existente.getEmail(),
                        usuario.getRole() != null ? usuario.getRole().name() : "null");
            }
        }

        // Sempre encoda a nova senha — o campo vem como texto puro do request
        usuario.setSenhaHash(passwordEncoder.encode(usuario.getSenhaHash()));
        return usuarioRepository.save(usuario);
    }

    public Page<Usuario> findAll(Pageable pageable) {
        return usuarioRepository.findAll(pageable);
    }

    public Usuario findByEmail(String email) {
        return usuarioRepository.findByEmail(email).orElse(null);
    }

    // Valida email e senha, retorna o usuário se válido ou null se inválido
    public Usuario login(String email, String senha) {
        return usuarioRepository.findByEmail(email)
                .filter(u -> passwordEncoder.matches(senha, u.getSenhaHash()))
                .filter(Usuario::isAtivo)
                .orElse(null);
    }

    public Usuario ativar(Long id) {
        Usuario usuario = usuarioRepository.findById(id).orElse(null);
        if (usuario == null) return null;
        usuario.setAtivo(true);
        // S-010: log de ativação de conta
        auditService.logAlteracaoSensivel("ATIVACAO_CONTA", resolverUsuarioLogado(), usuario.getEmail());
        return usuarioRepository.save(usuario);
    }

    public Usuario desativar(Long id) {
        Usuario usuario = usuarioRepository.findById(id).orElse(null);
        if (usuario == null) return null;
        usuario.setAtivo(false);
        // S-010: log de desativação de conta
        auditService.logAlteracaoSensivel("DESATIVACAO_CONTA", resolverUsuarioLogado(), usuario.getEmail());
        return usuarioRepository.save(usuario);
    }

    /** Extrai o email do usuário autenticado no contexto atual, ou "sistema" se não houver. */
    private String resolverUsuarioLogado() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()
                && !"anonymousUser".equals(auth.getPrincipal())) {
            return auth.getName();
        }
        return "sistema";
    }
}
