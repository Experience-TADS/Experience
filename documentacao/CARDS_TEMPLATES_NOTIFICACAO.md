# Cards de Implementação — Templates de Notificação + Fotos por Etapa

> **Contexto:** Implementar o sistema de mensagens personalizadas e fotos por etapa de produção,
> acoplando no fluxo MQTT → `StatusHistoricoService` já existente.
> Nenhuma estrutura existente será reescrita — apenas novos componentes adicionados.

---

## 🟢 CARD NOT-01 — Criar Entidade `EtapaTemplate`

**Tipo:** Feature  
**Prioridade:** INÍCIO  
**Estimativa:** 30 minutos  
**Depende de:** Nenhuma  
**Responsável:** Dev A (Backend)

---

**Como** sistema  
**Quero** armazenar mensagens e fotos para cada etapa de fabricação  
**Para que** cada mudança de status gere uma notificação personalizada ao cliente

---

### O que criar

**Arquivo:** `entities/EtapaTemplate.java`

```java
package com.senai.experience.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tb_etapa_template")
public class EtapaTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(unique = true, nullable = false)
    private StatusFabricacao status;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false, length = 500)
    private String mensagem; // suporta variáveis: {nome}, {modelo}, {cor}

    private String fotoUrl;      // URL da imagem mockada por etapa

    @Column(length = 300)
    private String curiosidade;  // fato sobre a etapa exibido no app
}
```

**Arquivo:** `repositories/EtapaTemplateRepository.java`

```java
package com.senai.experience.repositories;

import com.senai.experience.entities.EtapaTemplate;
import com.senai.experience.entities.StatusFabricacao;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EtapaTemplateRepository extends JpaRepository<EtapaTemplate, Long> {
    Optional<EtapaTemplate> findByStatus(StatusFabricacao status);
}
```

---

### Critérios de Aceite

- [ ] Entidade `EtapaTemplate` criada em `entities/`
- [ ] Anotação `@Column(unique = true)` no campo `status`
- [ ] Campo `mensagem` com `length = 500`
- [ ] Repositório `EtapaTemplateRepository` criado
- [ ] Método `findByStatus` declarado
- [ ] Aplicação sobe sem erros
- [ ] Tabela `tb_etapa_template` criada no banco

---

### Como Testar

1. Subir a aplicação
2. Verificar no banco que a tabela `tb_etapa_template` foi criada:
```sql
SELECT * FROM tb_etapa_template;
```
3. Deve existir a tabela (vazia por enquanto — seed vem no NOT-02)

---
---

## 🟢 CARD NOT-02 — Popular Templates no Banco (Seed)

**Tipo:** Feature  
**Prioridade:** INÍCIO  
**Estimativa:** 30 minutos  
**Depende de:** NOT-01  
**Responsável:** Dev A (Backend)

---

**Como** sistema  
**Quero** que os templates já existam no banco ao subir a aplicação  
**Para que** não seja necessário cadastrar manualmente antes dos testes

---

### O que criar

**Arquivo:** `config/EtapaTemplateSeeder.java`

```java
package com.senai.experience.config;

import com.senai.experience.entities.EtapaTemplate;
import com.senai.experience.entities.StatusFabricacao;
import com.senai.experience.repositories.EtapaTemplateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class EtapaTemplateSeeder {

    @Bean
    CommandLineRunner seedTemplates(EtapaTemplateRepository repo) {
        return args -> {
            if (repo.count() > 0) {
                log.info("Templates já existem. Seed ignorado.");
                return;
            }

            repo.saveAll(List.of(
                new EtapaTemplate(null,
                    StatusFabricacao.AGUARDANDO,
                    "Pedido confirmado!",
                    "Olá, {nome}! Seu {modelo} entrou na fila de produção. Em breve começamos!",
                    "https://images.unsplash.com/photo-1581092160562-40aa08e78837",
                    "Cada veículo passa por mais de 200 etapas antes de ser entregue."),

                new EtapaTemplate(null,
                    StatusFabricacao.EM_FABRICACAO,
                    "Produção iniciada!",
                    "{nome}, seu {modelo} começou a ser fabricado hoje. 🏭",
                    "https://images.unsplash.com/photo-1565043589221-1a6fd9ae45c7",
                    "A estrutura do seu carro é montada com mais de 300 peças soldadas."),

                new EtapaTemplate(null,
                    StatusFabricacao.PINTURA,
                    "Hora da cor!",
                    "Seu {modelo} em {cor} está na cabine de pintura agora. 🎨",
                    "https://images.unsplash.com/photo-1503376780353-7e6692767b70",
                    "São aplicadas 4 camadas de tinta em um processo de até 8 horas."),

                new EtapaTemplate(null,
                    StatusFabricacao.CONTROLE_QUALIDADE,
                    "Testes finais em andamento",
                    "Quase lá, {nome}! Seu {modelo} está passando pelos testes de qualidade.",
                    "https://images.unsplash.com/photo-1486262715619-67b85e0b08d3",
                    "Seu carro percorre 3km dentro da fábrica antes de ser aprovado."),

                new EtapaTemplate(null,
                    StatusFabricacao.CONCLUIDO,
                    "Produção concluída!",
                    "Seu {modelo} foi aprovado em todos os testes e está a caminho! 🚗",
                    "https://images.unsplash.com/photo-1494976388531-d1058494cdd8",
                    "Parabéns! Seu veículo passou por mais de 127 verificações de qualidade."),

                new EtapaTemplate(null,
                    StatusFabricacao.ENTREGUE,
                    "Seu carro chegou!",
                    "{nome}, seu {modelo} está na concessionária esperando por você! 🎉",
                    "https://images.unsplash.com/photo-1560958089-b8a1929cea89",
                    "Bem-vindo ao clube dos proprietários. Boas estradas!"),

                new EtapaTemplate(null,
                    StatusFabricacao.CANCELADO,
                    "Pedido cancelado",
                    "{nome}, seu pedido foi cancelado. Entre em contato com a concessionária.",
                    null,
                    null)
            ));

            log.info("✅ Templates de etapa criados com sucesso.");
        };
    }
}
```

---

### Critérios de Aceite

- [ ] Classe `EtapaTemplateSeeder` criada em `config/`
- [ ] Verifica `repo.count() > 0` antes de inserir
- [ ] Cria 1 template para cada valor do enum `StatusFabricacao` (7 no total)
- [ ] Todos os templates têm `titulo` e `mensagem` preenchidos
- [ ] Templates de `AGUARDANDO` a `ENTREGUE` têm `fotoUrl` preenchida
- [ ] Template `CANCELADO` tem `fotoUrl` e `curiosidade` nulos
- [ ] Log de confirmação exibido no console
- [ ] Aplicação sobe sem erros

---

### Como Testar

1. Limpar a tabela `tb_etapa_template` se já tiver dados
2. Subir a aplicação e verificar o log:
```
✅ Templates de etapa criados com sucesso.
```
3. Consultar o banco:
```sql
SELECT status, titulo, foto_url FROM tb_etapa_template;
```
Deve retornar 7 linhas, uma por status.

---
---

## 🟢 CARD NOT-03 — Adicionar `fcmToken` no `Usuario`

**Tipo:** Feature  
**Prioridade:** INÍCIO  
**Estimativa:** 30 minutos  
**Depende de:** Nenhuma  
**Responsável:** Dev A (Backend)

---

**Como** sistema  
**Quero** armazenar o token FCM de cada usuário  
**Para que** o backend saiba para qual dispositivo enviar a notificação push

---

### O que alterar

**Arquivo:** `entities/Usuario.java` — adicionar campo:

```java
@Column(name = "fcm_token")
private String fcmToken;
```

**Arquivo:** `controllers/UsuarioController.java` — adicionar endpoint:

```java
@PatchMapping("/fcm-token")
public ResponseEntity<Void> registrarFcmToken(
        @RequestBody Map<String, String> body,
        @AuthenticationPrincipal UserDetails userDetails) {
    usuarioService.salvarFcmToken(userDetails.getUsername(), body.get("token"));
    return ResponseEntity.ok().build();
}
```

**Arquivo:** `services/UsuarioService.java` — adicionar método:

```java
public void salvarFcmToken(String email, String token) {
    Usuario usuario = usuarioRepository.findByEmail(email);
    if (usuario != null) {
        usuario.setFcmToken(token);
        usuarioRepository.save(usuario);
    }
}
```

---

### Critérios de Aceite

- [ ] Campo `fcmToken` adicionado na entidade `Usuario`
- [ ] Coluna `fcm_token` criada no banco após subir a aplicação
- [ ] Endpoint `PATCH /api/usuario/fcm-token` criado
- [ ] Endpoint exige autenticação JWT
- [ ] Método `salvarFcmToken` implementado no service
- [ ] Aplicação compila sem erros

---

### Como Testar

```bash
# 1. Fazer login e obter token JWT
TOKEN="<jwt_obtido_no_login>"

# 2. Registrar FCM token
curl -X PATCH http://localhost:8080/api/usuario/fcm-token \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"token": "fcm_mock_token_123"}'
```

Deve retornar `200 OK`. Verificar no banco:
```sql
SELECT email, fcm_token FROM tb_usuario WHERE email = 'cliente@gmail.com';
```

---
---

## 🟡 CARD NOT-04 — Criar `NotificacaoService`

**Tipo:** Feature  
**Prioridade:** CORE  
**Estimativa:** 2 horas  
**Depende de:** NOT-01, NOT-02, NOT-03  
**Responsável:** Dev A (Backend)

---

**Como** sistema  
**Quero** montar e enviar notificações personalizadas ao cliente  
**Para que** cada mudança de etapa gere uma mensagem com nome, modelo e cor do veículo

---

### Dependência — Adicionar Firebase Admin no `pom.xml`

```xml
<!-- Firebase Admin SDK -->
<dependency>
    <groupId>com.google.firebase</groupId>
    <artifactId>firebase-admin</artifactId>
    <version>9.2.0</version>
</dependency>
```

### Configuração Firebase

**Arquivo:** `config/FirebaseConfig.java`

```java
package com.senai.experience.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Configuration;
import jakarta.annotation.PostConstruct;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void initialize() throws Exception {
        if (FirebaseApp.getApps().isEmpty()) {
            InputStream serviceAccount =
                getClass().getClassLoader().getResourceAsStream("firebase-service-account.json");

            FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

            FirebaseApp.initializeApp(options);
        }
    }
}
```

> **Atenção:** O arquivo `firebase-service-account.json` deve ser colocado em
> `src/main/resources/` e **nunca commitado no Git**. Adicionar ao `.gitignore`.

### O que criar

**Arquivo:** `services/NotificacaoService.java`

```java
package com.senai.experience.services;

import com.google.firebase.messaging.*;
import com.senai.experience.entities.*;
import com.senai.experience.repositories.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificacaoService {

    private final EtapaTemplateRepository templateRepo;
    private final PedidoRepository pedidoRepo;

    public void notificarMudancaEtapa(Veiculo veiculo, StatusFabricacao novoStatus) {
        // 1. Busca o template da etapa
        EtapaTemplate template = templateRepo.findByStatus(novoStatus)
            .orElse(null);
        if (template == null) {
            log.warn("Template não encontrado para status: {}", novoStatus);
            return;
        }

        // 2. Busca o cliente dono do veículo via ItemPedido → Pedido → Usuario
        Usuario cliente = buscarClientePorVeiculo(veiculo);
        if (cliente == null || cliente.getFcmToken() == null) {
            log.warn("Cliente ou FCM token não encontrado para veículo: {}", veiculo.getId());
            return;
        }

        // 3. Personaliza a mensagem com os dados do cliente e veículo
        String mensagem = template.getMensagem()
            .replace("{nome}", extrairPrimeiroNome(cliente.getNome()))
            .replace("{modelo}", veiculo.getProduto().getModelo())
            .replace("{cor}", veiculo.getProduto().getCor());

        // 4. Monta e envia a notificação via FCM
        enviarPush(cliente.getFcmToken(), template.getTitulo(), mensagem,
                   template.getFotoUrl(), novoStatus.name());
    }

    private void enviarPush(String fcmToken, String titulo, String corpo,
                             String fotoUrl, String etapa) {
        try {
            Notification.Builder notifBuilder = Notification.builder()
                .setTitle(titulo)
                .setBody(corpo);

            if (fotoUrl != null) {
                notifBuilder.setImage(fotoUrl);
            }

            Message message = Message.builder()
                .setToken(fcmToken)
                .setNotification(notifBuilder.build())
                .putData("etapa", etapa)       // mobile usa para navegar à tela certa
                .putData("fotoUrl", fotoUrl != null ? fotoUrl : "")
                .build();

            String response = FirebaseMessaging.getInstance().send(message);
            log.info("Notificação enviada com sucesso. ID: {}", response);

        } catch (FirebaseMessagingException e) {
            log.error("Erro ao enviar notificação FCM: {}", e.getMessage());
        }
    }

    private Usuario buscarClientePorVeiculo(Veiculo veiculo) {
        return pedidoRepo.findClienteByVeiculoId(veiculo.getId()).orElse(null);
    }

    private String extrairPrimeiroNome(String nomeCompleto) {
        if (nomeCompleto == null) return "";
        return nomeCompleto.split(" ")[0];
    }
}
```

**Arquivo:** `repositories/PedidoRepository.java` — adicionar query:

```java
@Query("SELECT p.idCliente FROM Pedido p " +
       "JOIN p.itens i " +
       "WHERE i.veiculo.id = :veiculoId")
Optional<Usuario> findClienteByVeiculoId(@Param("veiculoId") Long veiculoId);
```

---

### Critérios de Aceite

- [ ] Dependência `firebase-admin` adicionada no `pom.xml`
- [ ] `FirebaseConfig` criado em `config/`
- [ ] Arquivo `firebase-service-account.json` em `src/main/resources/`
- [ ] `firebase-service-account.json` adicionado ao `.gitignore`
- [ ] `NotificacaoService` criado em `services/`
- [ ] Substitui `{nome}`, `{modelo}` e `{cor}` corretamente
- [ ] Usa apenas o primeiro nome do cliente
- [ ] Loga warning se template ou FCM token não encontrado
- [ ] Loga erro se FCM falhar (sem lançar exceção — não bloqueia o fluxo)
- [ ] Query `findClienteByVeiculoId` adicionada no `PedidoRepository`
- [ ] Aplicação compila sem erros

---

### Como Testar

Teste unitário com mock do FCM (sem precisar de Firebase real):

```java
// Verificar que a mensagem substitui as variáveis corretamente
String mensagem = "Olá, {nome}! Seu {modelo} em {cor} está pronto.";
String resultado = mensagem
    .replace("{nome}", "Maria")
    .replace("{modelo}", "Corolla")
    .replace("{cor}", "Branco Polar");

assert resultado.equals("Olá, Maria! Seu Corolla em Branco Polar está pronto.");
```

Para teste de integração, usar FCM token mockado e verificar os logs.

---
---

## 🟡 CARD NOT-05 — Acoplar Notificação no `StatusHistoricoService`

**Tipo:** Feature  
**Prioridade:** CORE  
**Estimativa:** 30 minutos  
**Depende de:** NOT-04  
**Responsável:** Dev A (Backend)

---

**Como** sistema  
**Quero** que toda mudança de status dispare automaticamente a notificação  
**Para que** o cliente seja avisado em tempo real sem nenhuma chamada extra

---

### O que alterar

**Arquivo:** `services/StatusHistoricoService.java`

Injetar o `NotificacaoService` e chamar após salvar o histórico:

```java
@Service
@RequiredArgsConstructor
public class StatusHistoricoService {

    private final StatusHistoricoRepository statusHistoricoRepository;
    private final VeiculoRepository veiculoRepository;
    private final NotificacaoService notificacaoService; // ← injetar

    public StatusHistorico atualizarStatus(Long veiculoId, StatusFabricacao novoStatus) {
        Veiculo veiculo = veiculoRepository.findById(veiculoId)
            .orElseThrow(() -> new RuntimeException("Veículo não encontrado: " + veiculoId));

        // Atualiza status atual do veículo
        veiculo.setStatusVeiculo(novoStatus);
        veiculoRepository.save(veiculo);

        // Registra no histórico
        StatusHistorico historico = new StatusHistorico();
        historico.setVeiculo(veiculo);
        historico.setStatus(novoStatus);
        historico.setDataAlteracao(LocalDateTime.now());
        statusHistoricoRepository.save(historico);

        // 🔔 Dispara notificação com template + foto
        notificacaoService.notificarMudancaEtapa(veiculo, novoStatus);

        return historico;
    }
}
```

> **Ponto de atenção:** A notificação é disparada de forma síncrona.
> Se o FCM falhar, o erro é apenas logado — o status é salvo normalmente.
> O fluxo principal nunca é bloqueado por falha de notificação.

---

### Critérios de Aceite

- [ ] `NotificacaoService` injetado no `StatusHistoricoService`
- [ ] Chamada `notificacaoService.notificarMudancaEtapa()` após `save` do histórico
- [ ] Falha no FCM não impede o salvamento do status
- [ ] Aplicação compila sem erros

---

### Como Testar

```bash
# 1. Fazer login como admin/vendedor
TOKEN="<jwt_obtido_no_login>"

# 2. Atualizar status de um veículo
curl -X POST http://localhost:8080/api/veiculo/1/status \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '"PINTURA"'
```

Verificar nos logs:
```
Notificação enviada com sucesso. ID: projects/xxx/messages/yyy
```

Verificar no banco que o histórico foi salvo:
```sql
SELECT * FROM tb_status_historico WHERE id_veiculo = 1 ORDER BY data_alteracao DESC;
```

---
---

## 🔵 CARD NOT-06 — Tela de Detalhe da Etapa no Mobile

**Tipo:** Feature  
**Prioridade:** MOBILE  
**Estimativa:** 3 horas  
**Depende de:** NOT-05  
**Responsável:** Dev B (Mobile)

---

**Como** cliente  
**Quero** ver a foto e a mensagem da etapa ao tocar na notificação  
**Para que** eu me sinta informado e conectado com a produção do meu carro

---

### Layout da Tela

```
┌─────────────────────────────────┐
│  [FOTO DA ETAPA — fullwidth]    │
│  altura: 45% da tela            │
├─────────────────────────────────┤
│                                 │
│  ✅ Pintura concluída           │  ← titulo do template
│                                 │
│  "Seu Corolla em Branco Polar   │  ← mensagem personalizada
│   está na cabine de pintura     │
│   agora. 🎨"                    │
│                                 │
│  ──────────────────────────     │
│  💡 Você sabia?                 │
│  "São aplicadas 4 camadas de    │  ← curiosidade
│   tinta em até 8 horas."        │
│                                 │
│  [Ver linha do tempo completa]  │  ← botão de navegação
│                                 │
└─────────────────────────────────┘
```

### Estrutura do Componente

```javascript
// screens/EtapaDetalheScreen.js
import { View, Text, Image, TouchableOpacity, StyleSheet } from 'react-native';

const EtapaDetalheScreen = ({ route, navigation }) => {
  const { etapa, titulo, mensagem, fotoUrl, curiosidade } = route.params;

  return (
    <View style={styles.container}>

      {/* Foto da etapa */}
      {fotoUrl ? (
        <Image
          source={{ uri: fotoUrl }}
          style={styles.foto}
          resizeMode="cover"
        />
      ) : (
        <View style={styles.fotoPlaceholder} />
      )}

      <View style={styles.conteudo}>

        {/* Título */}
        <Text style={styles.titulo}>{titulo}</Text>

        {/* Mensagem personalizada */}
        <Text style={styles.mensagem}>{mensagem}</Text>

        {/* Curiosidade */}
        {curiosidade && (
          <View style={styles.curiosidadeBox}>
            <Text style={styles.curiosidadeLabel}>💡 Você sabia?</Text>
            <Text style={styles.curiosidadeTexto}>{curiosidade}</Text>
          </View>
        )}

        {/* Botão para linha do tempo */}
        <TouchableOpacity
          style={styles.botao}
          onPress={() => navigation.navigate('LinhaDoTempo')}
        >
          <Text style={styles.botaoTexto}>Ver linha do tempo completa</Text>
        </TouchableOpacity>

      </View>
    </View>
  );
};
```

### Receber Notificação e Navegar para a Tela

```javascript
// No componente raiz (App.js ou NavigationContainer)
import * as Notifications from 'expo-notifications';

useEffect(() => {
  // Listener para quando o usuário TOCA na notificação
  const subscription = Notifications.addNotificationResponseReceivedListener(response => {
    const { etapa, fotoUrl } = response.notification.request.content.data;

    // Navega direto para o detalhe da etapa
    navigation.navigate('EtapaDetalhe', {
      etapa,
      fotoUrl,
      titulo: response.notification.request.content.title,
      mensagem: response.notification.request.content.body,
    });
  });

  return () => subscription.remove();
}, []);
```

### Registrar Token FCM ao Fazer Login

```javascript
// services/notificacaoService.js
import * as Notifications from 'expo-notifications';
import api from './api';

export async function registrarFcmToken() {
  const { status } = await Notifications.requestPermissionsAsync();
  if (status !== 'granted') return;

  const token = await Notifications.getExpoPushTokenAsync();

  await api.patch('/api/usuario/fcm-token', { token: token.data });
}

// Chamar após login bem-sucedido:
// await registrarFcmToken();
```

---

### Critérios de Aceite

- [ ] Tela `EtapaDetalheScreen` criada
- [ ] Exibe foto em fullwidth (45% da altura da tela)
- [ ] Exibe título, mensagem e curiosidade
- [ ] Foto ausente exibe placeholder sem quebrar a tela
- [ ] Botão "Ver linha do tempo" navega corretamente
- [ ] Listener de notificação configurado no componente raiz
- [ ] Toque na notificação abre a tela `EtapaDetalhe` com os dados corretos
- [ ] Token FCM registrado após login
- [ ] Funciona em Android (iOS é secundário nesta fase)

---

### Como Testar

1. Fazer login no app — verificar nos logs do backend que o FCM token foi salvo
2. Via Postman, atualizar o status de um veículo vinculado ao cliente logado:
```json
POST /api/veiculo/1/status
Body: "PINTURA"
```
3. O dispositivo deve receber a notificação push
4. Tocar na notificação deve abrir a tela `EtapaDetalhe` com foto e mensagem

---
---

## 📋 Resumo dos Cards

| Card | Tipo | Prioridade | Tempo | Responsável |
|------|------|-----------|-------|-------------|
| NOT-01 | Feature | INÍCIO | 30 min | Dev A |
| NOT-02 | Feature | INÍCIO | 30 min | Dev A |
| NOT-03 | Feature | INÍCIO | 30 min | Dev A |
| NOT-04 | Feature | CORE | 2h | Dev A |
| NOT-05 | Feature | CORE | 30 min | Dev A |
| NOT-06 | Feature | MOBILE | 3h | Dev B |

**Total Dev A (Backend):** ~4 horas  
**Total Dev B (Mobile):** ~3 horas  
**Total Geral:** ~7 horas — aproximadamente 2 dias trabalhando em paralelo

---

## 🚀 Ordem de Execução

```
Dia 1 — Paralelo:
  Dev A: NOT-01 → NOT-02 → NOT-03 (estrutura + seed + FCM token)
  Dev B: Configurar expo-notifications + registrar token no login

Dia 2 — Paralelo:
  Dev A: NOT-04 → NOT-05 (NotificacaoService + acoplamento)
  Dev B: NOT-06 (tela de detalhe + listener de notificação)

Dia 3 — Juntos:
  Teste de integração ponta a ponta
  Ajustes de UI e tratamento de erros
```

**Após o Dia 2, o fluxo completo já funciona:**
```
MQTT → StatusHistoricoService → NotificacaoService → FCM → Push no celular → Tela de detalhe
```
