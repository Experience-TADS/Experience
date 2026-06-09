# Sistema de curadoria inteligente de conteúdo social

Implementar um módulo de filtragem de conteúdos relacionados a marca, para engajamento dos cliente durante o processo de pós venda do carro, focado antes da entrega do veículo, mas escalável em melhoria no pós venda.
A funcionalidade consiste em um sistema de curadoria automatizada de conteúdos audiovisuais, exibidos no formato de stories, integrando dados externos e inteligência artificial.

Inicialmente, devemos utilizar a API do YouTube, focado nos shots(videos rápidos da plataforma), por conter menos burocrácia em ser inseridas no projeto.

![image.png](attachment:46731c45-9976-41c6-ae92-dedb621ec0ad:image.png)

## ⚙️ Funcionamento

O sistema realiza:

1. Coleta de vídeos da plataforma YouTube
2. Filtragem inteligente baseada em IA
3. Armazenamento dos conteúdos relevantes
4. Exibição em formato de stories no sistema

## 🧠 Uso de Inteligência Artificial

A IA é utilizada para:

- análise de texto (título e descrição)
- classificação de relevância
- eliminação de conteúdos inadequados

---

## 📡 Integração com IoT e Tempo Real

Utilizando MQTT:

- novos conteúdos são enviados em tempo real
- atualização dinâmica no frontend

---

## 💡 Diferencial do Projeto

O sistema se diferencia por:

- integração com redes sociais
- uso de IA para curadoria automática
- exibição dinâmica estilo redes sociais
- foco no pós-venda (pouco explorado no setor)

## 💡 Arquitetura

YouTube API
↓
Backend (Spring Boot)
↓
Filtro de relevância
↓
Banco de Dados
↓
API REST (/stories)
↓
App Mobile (Stories)

## 🔮 Possibilidades Futuras

- recomendação personalizada por perfil
- análise de comportamento do usuário
- integração com outras redes sociais