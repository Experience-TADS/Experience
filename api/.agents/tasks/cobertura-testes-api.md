# Relatório de Cobertura de Testes — API Experience

> Investigação **read-only**. Nenhum arquivo de código-fonte foi alterado. Este documento mapeia a cobertura atual de testes contra toda a API, aponta as lacunas de testes unitários e de RESTAssured, e analisa o impacto das correções recentes no `PessoaJuridicaController`.

Módulo analisado: `c:\Users\Paola Costa\OneDrive\Documentos\projetoIntegrador\Experience\api`
Build/Teste: Maven + Spring Boot 3.3.13, Java 17. Testes rodam com `mvn test` (perfil `test`, H2 em memória); cobertura via `mvn verify` (JaCoCo, mínimo de 60% de linhas no bundle).

---

## Resumo executivo (resposta direta)

1. **Testes unitários de service (pasta `unit/`)**: existem apenas **2** de **13** services — `UsuarioServiceTest` e `StatusHistoricoServiceTest`. Faltam **11** testes unitários de service, incluindo o **`PessoaJuridicaServiceTest`** (alvo das correções). Os services com lógica de negócio mais rica e ainda sem teste unitário são `AgendaService`, `VeiculoService`, `PedidoService` e `ItemPedidoService`.

2. **PessoaJuridica (alvo das correções)**:
   - O `getById` já foi corrigido para retornar `ResponseEntity<PessoaJuridicaResponse>` (via `PessoaJuridicaMapper.toResponse`), e o `PessoaJuridicaControllerTest` **já cobre** os cenários GET `/{id}` 200 e 404. Portanto a correção de tipo de retorno, isoladamente, **não quebra** os testes existentes.
   - **Falta** o teste unitário `PessoaJuridicaServiceTest` (nenhum existe).
   - Falta um teste de controller/RESTAssured que **asserte os campos específicos do DTO** no corpo do GET `/{id}` (hoje valida só `id` e ausência de `senhaHash`, não `cnpj`/`razaoSocial`/`role`).

3. **RESTAssured**: dos 12 controllers, **3 não têm** teste RESTAssured: **`AgendaController`**, **`AnalyticsController`** e **`StatusHistoricoController`** (este tem apenas o `StatusHistoricoRestAssuredTest` que exercita o fluxo via `/api/veiculo`, não há cobertura dedicada do controller de status — ver seção C). PessoaJuridica tem cobertura, mas falta assertar o corpo completo do DTO no GET `/{id}`.

4. **RISCO CRÍTICO — `@PutMapping("/{id}")` duplicado** no `PessoaJuridicaController`: há **dois** métodos `update()` mapeados para o mesmo verbo+rota. Isso provoca `IllegalStateException: Ambiguous mapping` **no startup do contexto Spring**. Como **toda** a suíte (`@SpringBootTest` MockMvc e RESTAssured) sobe o contexto, esse defeito **derruba praticamente todos os testes de integração da API**, não apenas os de PessoaJuridica. É o item de maior prioridade.

---

## (A) Visão geral da cobertura atual

### Controllers de produção (12) e cobertura de teste de integração

| Controller | Teste MockMvc (`Test/`) | Teste RESTAssured (`restassured/`) |
|---|---|---|
| AgendaController | ❌ | ❌ |
| AnalyticsController | ❌ | ❌ |
| EnderecoController | ✅ EnderecoControllerTest | ✅ EnderecoRestAssuredTest + EnderecoValidacaoTest |
| ItemPedidoController | ✅ ItemPedidoControllerTest | ✅ ItemPedidoRestAssuredTest |
| PedidoController | ✅ PedidoControllerTest | ✅ PedidoRestAssuredTest |
| PessoaFisicaController | ✅ PessoaFisicaControllerTest | ✅ PessoaFisicaRestAssuredTest |
| PessoaJuridicaController | ✅ PessoaJuridicaControllerTest | ✅ PessoaJuridicaRestAssuredTest |
| ProdutoController | ✅ ProdutoControllerTest | ✅ ProdutoRestAssuredTest + ProdutoValidacaoTest |
| StatusHistoricoController | ❌ (não há `StatusHistoricoControllerTest`) | ⚠️ StatusHistoricoRestAssuredTest exercita via `/api/veiculo` |
| TelefoneController | ✅ TelefoneControllerTest | ✅ TelefoneRestAssuredTest + TelefoneValidacaoTest |
| UsuarioController | ✅ UsuarioControllerTest + UsuarioMeControllerTest | ✅ UsuarioRestAssuredTest + UsuarioEmailDuplicadoTest |
| VeiculoController | ✅ VeiculoControllerTest | ✅ VeiculoRestAssuredTest + VeiculoChassiDuplicadoTest |

### Services de produção (13) e cobertura de teste unitário (`unit/`)

| Service | Teste unitário | Lógica de negócio relevante (fora do CRUD trivial) |
|---|---|---|
| AgendaService | ❌ | Validação de conflito de horário, verificação de propriedade, controle de acesso (admin/colaborador/cliente), criação automática de evento de entrega com busca de slot livre |
| AuditService | ❌ (log puro — baixo valor) | Apenas logging; opcional |
| EnderecoService | ❌ | update com cópia campo a campo; findById null |
| ItemPedidoService | ❌ | Resolução de Pedido/Produto por id, exceções "não encontrado", update |
| PedidoService | ❌ | Resolução cliente/vendedor, exceções, update, `findMeusPedidos` por role (ADMIN/VENDEDOR/CLIENTE) |
| PessoaFisicaService | ❌ | Encode condicional de senha (evita duplo BCrypt), findById null |
| **PessoaJuridicaService** | ❌ **(ALVO)** | Encode condicional de senha, findById null, save/delete |
| ProdutoService | ❌ | update só quando `existsById`, findById null |
| SessaoAnalyticsService | ❌ | `registrar` (set timestamp), `resumo` (agregação/map) |
| StatusHistoricoService | ✅ StatusHistoricoServiceTest | — (bem coberto: todas as transições) |
| TelefoneService | ❌ | update campo a campo, findById null |
| UsuarioService | ✅ UsuarioServiceTest | — (findByEmail/login cobertos) |
| VeiculoService | ❌ | save com resolução de Produto, update de status, `findByChassi`, `atualizarStatus` (histórico), `confirmarChegada` (agenda automática), `mapearEtapaParaStatus` (IoT) |

Observações de convenção descobertas no código:
- Testes unitários ficam em `src/test/java/com/senai/experience/unit/`, usam JUnit 5 + Mockito (`@ExtendWith(MockitoExtension.class)`, `@Mock`, `@InjectMocks`) e AssertJ. Não sobem contexto Spring. Padrão de nome: `<Service>Test`, métodos `cenario_condicao_resultado` ou `DisplayName` descritivo.
- Testes MockMvc ficam em `src/test/java/com/senai/experience/Test/`, usam `@SpringBootTest` + `@AutoConfigureMockMvc` + `@ActiveProfiles("test")`, estendem `PostgresTestContainer`, autenticam via `AuthHelper.obterToken(...)`.
- Testes RESTAssured ficam em `src/test/java/com/senai/experience/restassured/`, estendem `RestAssuredBaseTest` (`WebEnvironment.RANDOM_PORT`, perfil `test`), autenticam via `obterTokenAdmin()` / `criarUsuarioEObterToken(...)`.

---

## (B) Testes unitários de service faltantes

Para cada service sem teste, abaixo o caminho sugerido e o esboço de casos (nome do método + cenário). Nenhum código foi escrito.

### B.1 PessoaJuridicaService (PRIORITÁRIO — alvo das correções)
Arquivo sugerido: `src/test/java/com/senai/experience/unit/PessoaJuridicaServiceTest.java`
Mocks: `PessoaJuridicaRepository`, `PasswordEncoder`.
- `findAll_retornaPagina` — repository.findAll(pageable) é delegado e retornado.
- `findById_existente_retornaEntidade` — Optional preenchido.
- `findById_inexistente_retornaNull` — Optional.empty → null (sem NPE).
- `save_senhaTextoPuro_encodaAntesDeSalvar` — senha sem prefixo `$2a$/$2b$` deve chamar `passwordEncoder.encode` uma vez.
- `save_senhaJaEncodada_naoReencoda` — senha iniciando com `$2a$`/`$2b$` NÃO deve chamar `encode` (evita duplo encoding no update).
- `save_senhaNull_naoChamaEncode` — ramo `senha != null` falso.
- `deleteById_delegaAoRepository` — verify `deleteById(id)`.

### B.2 PessoaFisicaService
Arquivo: `src/test/java/com/senai/experience/unit/PessoaFisicaServiceTest.java` (mocks: `PessoaFisicaRepository`, `PasswordEncoder`).
- Mesmos cenários do B.1 (findAll, findById encontrado/não encontrado, save encode condicional ×3 ramos, deleteById).

### B.3 AgendaService (lógica mais rica)
Arquivo: `src/test/java/com/senai/experience/unit/AgendaServiceTest.java` (mocks: `AgendaRepository`, `UsuarioRepository`).
- `criar_semConflito_salvaComStatusPendente`.
- `criar_comConflito_lancaExcecao` (`existeConflito` true → "Conflito de horário").
- `criar_usuarioInexistente_lancaExcecao` ("Usuário não encontrado").
- `buscarPorId_admin_retornaQualquer`.
- `buscarPorId_colaboradorDono_retorna`.
- `buscarPorId_clienteDestinatario_retorna`.
- `buscarPorId_semPermissao_lancaAcessoNegado`.
- `buscarPorId_inexistente_lancaExcecao`.
- `atualizar_mudaDataHoraComConflito_lancaExcecao`.
- `atualizar_naoDono_lancaAcessoNegado`.
- `atualizarStatus_admin_ignoraPropriedade`.
- `atualizarStatus_naoAdminNaoDono_lancaAcessoNegado`.
- `deletar_naoDono_lancaAcessoNegado`.
- `consultarHorariosOcupados_retornaLista`.
- `criarEventoEntregaAutomatico_jaExiste_naoCria`.
- `criarEventoEntregaAutomatico_buscaSlotLivre_avança24h`.
- `criarEventoEntregaAutomatico_semSlotEm30Tentativas_lancaExcecao`.

### B.4 VeiculoService (lógica rica + IoT)
Arquivo: `src/test/java/com/senai/experience/unit/VeiculoServiceTest.java` (mocks: `VeiculoRepository`, `ProdutoRepository`, `StatusHistoricoRepository`, `PedidoRepository`, `AgendaService`).
- `findById_inexistente_retornaNull`.
- `save_comProdutoExistente_resolveProduto`.
- `save_comProdutoInexistente_lancaExcecao`.
- `update_existente_atualizaStatus` / `update_inexistente_retornaNull`.
- `findByChassi_delega`.
- `atualizarStatus_chassiInexistente_retornaNull`.
- `atualizarStatus_salvaVeiculoERegistraHistorico`.
- `confirmarChegada_inexistente_retornaNull`.
- `confirmarChegada_defineNaConcessionariaERegistraHistorico`.
- `confirmarChegada_comPedidoDoProduto_criaEventoEntrega` (verify `agendaService.criarEventoEntregaAutomatico`).
- `mapearEtapaParaStatus_*` — tabela de casos: cada etapa com "Iniciado" → status correspondente; LIBERACAO_TRANSPORTE só com "Finalizado"; etapa/status null → null; etapa desconhecida → null.

### B.5 PedidoService
Arquivo: `src/test/java/com/senai/experience/unit/PedidoServiceTest.java` (mocks: `PedidoRepository`, `UsuarioRepository`).
- `findById_existente_carregaItens` / `findById_inexistente_retornaNull`.
- `save_clienteInexistente_lancaExcecao` / `save_vendedorInexistente_lancaExcecao` / `save_ok`.
- `update_inexistente_retornaNull` / `update_ok`.
- `findMeusPedidos_admin_retornaTodos`.
- `findMeusPedidos_vendedor_retornaDoVendedor`.
- `findMeusPedidos_cliente_retornaDoCliente`.
- `findMeusPedidos_usuarioNullOuRoleNull_retornaListaVazia`.
- `delete_delega`.

### B.6 ItemPedidoService
Arquivo: `src/test/java/com/senai/experience/unit/ItemPedidoServiceTest.java` (mocks: `ItemPedidoRepository`, `PedidoRepository`, `ProdutoRepository`).
- `save_resolvePedidoEProduto`.
- `save_pedidoInexistente_lancaExcecao` / `save_produtoInexistente_lancaExcecao`.
- `findById_inexistente_retornaNull`.
- `update_existente_delegaSave` / `update_inexistente_retornaNull`.
- `deleteById_delega`.

### B.7 ProdutoService
Arquivo: `src/test/java/com/senai/experience/unit/ProdutoServiceTest.java` (mock: `ProdutoRepository`).
- `save_delega`; `findById_inexistente_retornaNull`.
- `update_existente_salva` / `update_naoExiste_retornaNull` / `update_idNull_retornaNull`.
- `deleteById_delega`; `findAll_retornaPagina`.

### B.8 EnderecoService / B.9 TelefoneService
Arquivos: `.../unit/EnderecoServiceTest.java`, `.../unit/TelefoneServiceTest.java` (mock: respectivo repository).
- `findById_inexistente_retornaNull`.
- `update_existente_copiaCamposESalva` (Endereco: cep/logradouro/numero/bairro/cidade/estado; Telefone: numero).
- `update_inexistente_retornaNull`.
- `save_delega`; `delete_delega`.

### B.10 SessaoAnalyticsService
Arquivo: `.../unit/SessaoAnalyticsServiceTest.java` (mock: `SessaoAnalyticsRepository`).
- `registrar_setaRegistradoEmESalva_retornaResponse`.
- `resumo_mapeiaResultadosPorSecao`.

### B.11 AuditService (opcional / baixa prioridade)
Service apenas de logging. Pode ser verificado com um appender de teste do Logback (asserta mensagem sem senha/token). Baixo valor frente às demais lacunas.

> Nota: `config`, `entities` e `DTO` estão **excluídos** da verificação JaCoCo (ver `pom.xml`), então mappers e services são o foco de cobertura unitária real.

---

## (C) Testes RESTAssured faltantes

### C.1 AgendaController — SEM cobertura (criar `AgendaRestAssuredTest.java`)
Endpoints: `POST /api/agenda`, `GET /api/agenda`, `GET /api/agenda/{id}`, `PUT /api/agenda/{id}`, `PATCH /api/agenda/{id}/status`, `GET /api/agenda/colaborador/{id}/disponibilidade`. Há `@PreAuthorize` por papel → cenários de autorização são essenciais.
Esboço:
- `criar_vendedor_retorna201` / `criar_cliente_retorna403` / `criar_semToken_retorna403`.
- `criar_conflitoHorario_retornaErro`.
- `listar_vendedor_retornaSuasAgendas`.
- `buscarPorId_dono_retorna200` / `buscarPorId_outroColaborador_retornaAcessoNegado` / `buscarPorId_admin_retornaQualquer`.
- `atualizar_dono_retorna200` / `atualizar_naoDono_retornaAcessoNegado`.
- `atualizarStatus_patch_retorna200`.
- `deletar_dono_retorna204`.
- `disponibilidade_retornaHorariosOcupados`.

### C.2 AnalyticsController — SEM cobertura (criar `AnalyticsRestAssuredTest.java`)
Endpoints: `POST /api/analytics/sessao`, `GET /api/analytics/sessao/resumo`.
Esboço:
- `registrarSessao_valida_retorna201`.
- `registrarSessao_dadosInvalidos_retorna400` (clienteId/secao/duracaoSegundos ausentes).
- `registrarSessao_semToken_retorna403`.
- `resumo_comToken_retorna200_listaPorSecao`.
- `resumo_semToken_retorna403`.

### C.3 StatusHistoricoController — sem teste dedicado (avaliar `StatusHistoricoControllerRestAssuredTest.java`)
O `StatusHistoricoRestAssuredTest` atual exercita o histórico pelo fluxo de `/api/veiculo`. Se o `StatusHistoricoController` expõe rotas próprias (ex.: `/api/veiculo/{id}/status`), convém um teste que valide diretamente leitura autenticada (200) e escrita por ADMIN/VENDEDOR, além de 403 sem token. (Confirmar as rotas do controller antes de implementar.)

### C.4 PessoaJuridica — reforço para cobrir a correção (editar `PessoaJuridicaRestAssuredTest.java`)
Hoje `buscarPorId_existente_retorna200` valida apenas `id` e ausência de `senhaHash`. Como o `getById` passou a retornar o DTO `PessoaJuridicaResponse`, acrescentar asserções do corpo:
- `buscarPorId_existente_retornaCamposDoDto` — assertar `cnpj`, `razaoSocial`, `nome`, `email`, `role`, `ativo` no corpo (confirma o contrato do DTO).
- Confirmar que `buscarPorId_inexistente_retorna404` continua válido.
- (Ver seção E) adicionar/ajustar cenário de PUT que falhará enquanto o mapeamento duplicado existir.

---

## (D) Impacto das correções do PessoaJuridicaController nos testes

Correção aplicada (confirmada no código): `getById` agora é
`public ResponseEntity<PessoaJuridicaResponse> getById(...)` e retorna
`ResponseEntity.ok(PessoaJuridicaMapper.toResponse(pessoaJuridica))`, com `404` quando `findById` devolve `null`.

- **Consistência**: o `getById` agora alinha-se a `getAll`, `create` e ao primeiro `update`, todos usando `PessoaJuridicaResponse`. O `PessoaJuridicaMapper.toResponse` **não** copia `senhaHash`, preservando o contrato de não vazar o hash.
- **Testes existentes**: `PessoaJuridicaControllerTest` já cobre `getPessoaJuridicaPorIdExistenteDeveRetornar200` e `getPessoaJuridicaPorIdInexistenteDeveRetornar404`; `PessoaJuridicaRestAssuredTest` cobre `buscarPorId_existente_retorna200` e `buscarPorId_inexistente_retorna404`. A mudança de tipo **isoladamente não quebra** esses testes, pois o JSON de resposta (campos do DTO) permanece compatível com as asserções atuais (`id`, ausência de `senhaHash`).
- **Lacuna de asserção**: nenhum teste assegura explicitamente que o corpo do GET `/{id}` traz os campos próprios do DTO (`cnpj`, `razaoSocial`, `role`, `ativo`). Recomenda-se reforçar (ver C.4) para "travar" o contrato do DTO contra regressões futuras.
- **Falta o teste unitário** `PessoaJuridicaServiceTest` (ver B.1), que é independente do controller e cobre a regra de encode condicional de senha e o `findById` null — base para o comportamento do `getById`/`update`.

**Ressalva importante**: embora a correção de tipo não quebre os testes por si só, **todos** os testes de PessoaJuridica (e da API inteira) estão atualmente **bloqueados** pelo defeito da seção E — ver abaixo.

---

## (E) Risco: `@PutMapping("/{id}")` duplicado (NÃO corrigido — apenas documentado)

No `PessoaJuridicaController` existem **dois** métodos anotados com `@PutMapping("/{id}")`:

1. `public ResponseEntity<PessoaJuridicaResponse> update(Long id, @Valid @RequestBody PessoaJuridicaRequest request)` — recebe o DTO de request, valida, usa o mapper e retorna o DTO de response.
2. `public ResponseEntity<PessoaJuridica> update(Long id, @RequestBody PessoaJuridica pessoaJuridica)` — recebe a **entidade crua**, sem `@Valid`, e retorna a entidade (expondo potencialmente `senhaHash`).

**Efeito**: dois handlers com o mesmo verbo HTTP + mesma rota (`PUT /api/pessoaJuridica/{id}`) produzem mapeamento ambíguo. O Spring MVC detecta isso **no startup**, lançando `IllegalStateException: Ambiguous mapping. Cannot map ... There is already '...' bean method ... mapped.` O contexto da aplicação **não inicializa**.

**Impacto nos testes**: toda a suíte de integração usa `@SpringBootTest` (MockMvc em `Test/` e RESTAssured em `restassured/`), que **sobe o contexto completo**. Com o contexto falhando no startup, **praticamente todos os testes de integração da API falham** com erro de inicialização de contexto — não só os de PessoaJuridica. Os testes unitários de `unit/` (Mockito puro, sem contexto) são os únicos imunes.

**Riscos adicionais do 2º método** (mesmo após resolver a ambiguidade): aceita a entidade JPA diretamente (sem validação `@Valid`) e retorna `ResponseEntity<PessoaJuridica>`, o que pode **vazar `senhaHash`** e burlar o padrão DTO adotado no restante do controller.

**Recomendação (implementação fora do escopo desta investigação)**: remover o 2º método `update(Long, PessoaJuridica)` e manter apenas o que usa `PessoaJuridicaRequest`/`PessoaJuridicaResponse`. Após a remoção:
- Reexecutar `mvn test` e confirmar que o contexto sobe e `PessoaJuridicaControllerTest`/`PessoaJuridicaRestAssuredTest` passam.
- Garantir cobertura de PUT `/{id}` 200 e 404 (já existe no MockMvc; considerar adicionar ao RESTAssured).

---

## Prioridades recomendadas

1. **Corrigir o `@PutMapping` duplicado** (seção E) — desbloqueia toda a suíte de integração. **Maior prioridade.**
2. **Criar `PessoaJuridicaServiceTest`** (B.1) — fecha a lacuna unitária do alvo das correções.
3. **Reforçar asserções do GET `/{id}`** de PessoaJuridica no RESTAssured/MockMvc (C.4/D).
4. Adicionar RESTAssured para **Agenda** e **Analytics** (C.1, C.2) — controllers hoje sem cobertura.
5. Preencher os demais testes unitários de service por ordem de risco: Agenda, Veiculo, Pedido, ItemPedido, depois os CRUD simples (PessoaFisica, Produto, Endereco, Telefone, SessaoAnalytics).

## Como verificar (comandos reais do projeto)

A partir de `c:\Users\Paola Costa\OneDrive\Documentos\projetoIntegrador\Experience\api`:
- Rodar toda a suíte: `mvn test`
- Rodar um teste específico: `mvn test -Dtest=PessoaJuridicaServiceTest`
- Cobertura (JaCoCo, falha abaixo de 60% de linhas): `mvn verify` → relatório em `api/target/site/jacoco/index.html`
