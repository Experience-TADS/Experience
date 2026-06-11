from flask import Flask, request, jsonify
from flask_cors import CORS

app = Flask(__name__)
CORS(app)

# ── Códigos de diagnóstico OBD2 / Toyota ─────────────────────
#
# Prefixos:
#   P0xxx · Motor e transmissão (padrão OBD2)
#   P1xxx · Códigos específicos Toyota
#   Bxxxx · Carroceria / airbag / conforto
#   Cxxxx · Freios, ABS, direção
#   Uxxxx · Comunicação eletrônica

MANUAL = {
    # ── P0 — Motor e transmissão (OBD2 padrão) ───────────────
    "P0100": {
        "titulo": "⚙️ P0100 — Falha no Sensor MAF",
        "prefixo": "P0xxx · Motor e transmissão (OBD2)",
        "descricao": (
            "Falha no sensor de fluxo de massa de ar (MAF — Mass Air Flow).\n"
            "• O sensor MAF mede a quantidade de ar que entra no motor.\n"
            "• Sintomas: motor falhando, consumo elevado, perda de potência.\n"
            "• Causas comuns: sensor sujo, com defeito ou fiação danificada.\n"
            "• Ação recomendada: limpe o sensor com spray específico ou substitua.\n"
            "• Leve à concessionária Toyota para diagnóstico completo."
        ),
    },
    "P0115": {
        "titulo": "🌡️ P0115 — Sensor de Temperatura do Motor",
        "prefixo": "P0xxx · Motor e transmissão (OBD2)",
        "descricao": (
            "Falha no sensor de temperatura do líquido de arrefecimento (ECT).\n"
            "• O sensor informa à ECU a temperatura atual do motor.\n"
            "• Sintomas: ventoinhas ligando incorretamente, consumo alto, dificuldade na partida a frio.\n"
            "• Causas comuns: sensor com defeito, fiação danificada ou conector oxidado.\n"
            "• Ação recomendada: verifique o nível do líquido de arrefecimento e substitua o sensor.\n"
            "• Não ignore — superaquecimento pode causar danos graves ao motor."
        ),
    },
    "P0120": {
        "titulo": "🦋 P0120 — Sensor de Posição da Borboleta (TPS)",
        "prefixo": "P0xxx · Motor e transmissão (OBD2)",
        "descricao": (
            "Falha no sensor de posição da válvula borboleta (TPS — Throttle Position Sensor).\n"
            "• O TPS informa à ECU o quanto o acelerador está pressionado.\n"
            "• Sintomas: aceleração irregular, marcha lenta instável, falhas ao acelerar.\n"
            "• Causas comuns: sensor desgastado, fiação com mau contato ou corpo de borboleta sujo.\n"
            "• Ação recomendada: limpe o corpo de borboleta e verifique o sensor.\n"
            "• Leve à concessionária para calibração eletrônica após substituição."
        ),
    },
    "P0171": {
        "titulo": "💨 P0171 — Mistura Pobre (Banco 1)",
        "prefixo": "P0xxx · Motor e transmissão (OBD2)",
        "descricao": (
            "Mistura ar/combustível muito pobre no banco 1 (excesso de ar ou falta de combustível).\n"
            "• Sintomas: motor falhando, perda de potência, consumo irregular.\n"
            "• Causas comuns: vazamento de ar no coletor, sensor MAF sujo, injetores entupidos,\n"
            "  sensor de oxigênio com defeito ou pressão de combustível baixa.\n"
            "• Ação recomendada: verifique vazamentos de vácuo e limpe os injetores.\n"
            "• Diagnóstico completo recomendado na concessionária Toyota."
        ),
    },
    "P0300": {
        "titulo": "🔥 P0300 — Falha de Combustão Aleatória",
        "prefixo": "P0xxx · Motor e transmissão (OBD2)",
        "descricao": (
            "Falha de combustão detectada em múltiplos cilindros de forma aleatória.\n"
            "• Sintomas: motor tremendo, perda de potência, luz de check engine piscando.\n"
            "• Causas comuns: velas de ignição desgastadas, cabos de vela com defeito,\n"
            "  bobinas de ignição falhando, injetores entupidos ou compressão baixa.\n"
            "• Ação recomendada: verifique e substitua velas e cabos conforme o manual.\n"
            "• Se a luz piscar, reduza a velocidade e vá à concessionária — risco de dano ao catalisador."
        ),
    },
    "P0301": {
        "titulo": "🔥 P0301 — Falha de Combustão — Cilindro 1",
        "prefixo": "P0xxx · Motor e transmissão (OBD2)",
        "descricao": (
            "Falha de combustão detectada especificamente no cilindro 1.\n"
            "• Sintomas: vibração no motor, perda de potência, consumo elevado.\n"
            "• Causas comuns: vela de ignição do cilindro 1 desgastada ou com defeito,\n"
            "  bobina de ignição falhando, injetor entupido ou válvulas com folga incorreta.\n"
            "• Ação recomendada: substitua a vela do cilindro 1 e verifique a bobina.\n"
            "• Leve à concessionária Toyota para diagnóstico de compressão se persistir."
        ),
    },
    "P0302": {
        "titulo": "🔥 P0302 — Falha de Combustão — Cilindro 2",
        "prefixo": "P0xxx · Motor e transmissão (OBD2)",
        "descricao": (
            "Falha de combustão detectada especificamente no cilindro 2.\n"
            "• Sintomas: vibração no motor, perda de potência, consumo elevado.\n"
            "• Causas comuns: vela de ignição do cilindro 2 desgastada ou com defeito,\n"
            "  bobina de ignição falhando, injetor entupido ou válvulas com folga incorreta.\n"
            "• Ação recomendada: substitua a vela do cilindro 2 e verifique a bobina.\n"
            "• Leve à concessionária Toyota para diagnóstico de compressão se persistir."
        ),
    },
    "P0303": {
        "titulo": "🔥 P0303 — Falha de Combustão — Cilindro 3",
        "prefixo": "P0xxx · Motor e transmissão (OBD2)",
        "descricao": (
            "Falha de combustão detectada especificamente no cilindro 3.\n"
            "• Sintomas: vibração no motor, perda de potência, consumo elevado.\n"
            "• Causas comuns: vela de ignição do cilindro 3 desgastada ou com defeito,\n"
            "  bobina de ignição falhando, injetor entupido ou válvulas com folga incorreta.\n"
            "• Ação recomendada: substitua a vela do cilindro 3 e verifique a bobina.\n"
            "• Leve à concessionária Toyota para diagnóstico de compressão se persistir."
        ),
    },
    "P0304": {
        "titulo": "🔥 P0304 — Falha de Combustão — Cilindro 4",
        "prefixo": "P0xxx · Motor e transmissão (OBD2)",
        "descricao": (
            "Falha de combustão detectada especificamente no cilindro 4.\n"
            "• Sintomas: vibração no motor, perda de potência, consumo elevado.\n"
            "• Causas comuns: vela de ignição do cilindro 4 desgastada ou com defeito,\n"
            "  bobina de ignição falhando, injetor entupido ou válvulas com folga incorreta.\n"
            "• Ação recomendada: substitua a vela do cilindro 4 e verifique a bobina.\n"
            "• Leve à concessionária Toyota para diagnóstico de compressão se persistir."
        ),
    },
    "P0420": {
        "titulo": "🌿 P0420 — Catalisador Ineficiente (Banco 1)",
        "prefixo": "P0xxx · Motor e transmissão (OBD2)",
        "descricao": (
            "Eficiência do catalisador abaixo do limite no banco 1.\n"
            "• O catalisador reduz emissões convertendo gases nocivos em inofensivos.\n"
            "• Sintomas: geralmente sem sintomas perceptíveis, apenas a luz de check engine.\n"
            "• Causas comuns: catalisador desgastado, sensor de oxigênio com defeito,\n"
            "  vazamento de escapamento ou uso de combustível de baixa qualidade.\n"
            "• Ação recomendada: verifique os sensores de oxigênio antes de substituir o catalisador.\n"
            "• Diagnóstico na concessionária Toyota é recomendado."
        ),
    },
    "P0500": {
        "titulo": "🚗 P0500 — Sensor de Velocidade do Veículo (VSS)",
        "prefixo": "P0xxx · Motor e transmissão (OBD2)",
        "descricao": (
            "Falha no sensor de velocidade do veículo (VSS — Vehicle Speed Sensor).\n"
            "• O VSS informa a velocidade do veículo à ECU, ABS e painel.\n"
            "• Sintomas: velocímetro inoperante, falhas no ABS, câmbio automático errático.\n"
            "• Causas comuns: sensor com defeito, anel dentado danificado ou fiação interrompida.\n"
            "• Ação recomendada: verifique o sensor e o anel dentado na caixa de câmbio.\n"
            "• Leve à concessionária Toyota para diagnóstico e substituição."
        ),
    },
    "P0700": {
        "titulo": "⚙️ P0700 — Falha no Sistema de Transmissão",
        "prefixo": "P0xxx · Motor e transmissão (OBD2)",
        "descricao": (
            "Falha geral detectada no módulo de controle da transmissão (TCM).\n"
            "• Este código indica que o TCM registrou um ou mais códigos de falha internos.\n"
            "• Sintomas: câmbio travado em uma marcha, modo de emergência ativado, solavancos.\n"
            "• Causas comuns: falha no TCM, solenoides da transmissão, nível de óleo baixo\n"
            "  ou sensores internos com defeito.\n"
            "• Ação recomendada: verifique o nível e a qualidade do óleo da transmissão.\n"
            "• Leve imediatamente à concessionária Toyota — não ignore este código."
        ),
    },

    # ── P1 — Códigos específicos Toyota ──────────────────────
    "P1604": {
        "titulo": "🔑 P1604 — Dificuldade na Partida (Toyota)",
        "prefixo": "P1xxx · Específico Toyota",
        "descricao": (
            "Código específico Toyota: dificuldade ou falha no processo de partida do motor.\n"
            "• Sintomas: motor demora para ligar, não liga na primeira tentativa, partida irregular.\n"
            "• Causas comuns: bateria fraca ou descarregada, sistema de ignição com defeito,\n"
            "  sensor do virabrequim falhando, imobilizador com problema ou combustível insuficiente.\n"
            "• Ação recomendada: verifique a carga da bateria e os terminais.\n"
            "• Leve à concessionária Toyota para diagnóstico do sistema de partida."
        ),
    },
    "P1605": {
        "titulo": "🔄 P1605 — Marcha Lenta Irregular (Toyota)",
        "prefixo": "P1xxx · Específico Toyota",
        "descricao": (
            "Código específico Toyota: instabilidade ou irregularidade na marcha lenta (idle).\n"
            "• Sintomas: rotação do motor oscilando em ponto morto, motor tremendo parado,\n"
            "  risco de apagar em semáforos ou manobras lentas.\n"
            "• Causas comuns: válvula IAC (controle de ar em marcha lenta) suja ou com defeito,\n"
            "  corpo de borboleta sujo, vazamento de vácuo ou sensor MAF com leitura incorreta.\n"
            "• Ação recomendada: limpe o corpo de borboleta e a válvula IAC.\n"
            "• Leve à concessionária Toyota para calibração eletrônica."
        ),
    },

    # ── B — Carroceria / Airbag / Conforto ───────────────────
    "B0100": {
        "titulo": "💨 B0100 — Falha no Sistema de Airbag (SRS)",
        "prefixo": "Bxxxx · Carroceria / Airbag / Conforto",
        "descricao": (
            "Falha detectada no sistema de retenção suplementar (SRS — airbags).\n"
            "• ATENÇÃO: com este código, os airbags podem não acionar em uma colisão.\n"
            "• Sintomas: luz do airbag acesa no painel.\n"
            "• Causas comuns: sensor de impacto com defeito, conector do airbag solto,\n"
            "  módulo SRS com falha ou cinto de segurança com pretensionador defeituoso.\n"
            "• Ação recomendada: NÃO tente reparar o sistema de airbag por conta própria.\n"
            "• Leve IMEDIATAMENTE à concessionária Toyota — risco de segurança grave."
        ),
    },
    "B2799": {
        "titulo": "🔐 B2799 — Imobilizador / Chave (Toyota)",
        "prefixo": "Bxxxx · Carroceria / Airbag / Conforto",
        "descricao": (
            "Falha no sistema imobilizador ou na comunicação com a chave do veículo.\n"
            "• O imobilizador impede a partida do motor com chaves não autorizadas.\n"
            "• Sintomas: motor não liga, luz do imobilizador piscando no painel.\n"
            "• Causas comuns: chave com transponder danificado, bateria da chave fraca,\n"
            "  antena do imobilizador com defeito ou módulo ECU com falha de comunicação.\n"
            "• Ação recomendada: tente com a chave reserva. Substitua a bateria da chave.\n"
            "• Leve à concessionária Toyota para reprogramação se necessário."
        ),
    },

    # ── C — Freios, ABS, Direção ──────────────────────────────
    "C1201": {
        "titulo": "🛑 C1201 — Falha no Sistema ABS/VSC",
        "prefixo": "Cxxxx · Freios, ABS, Direção",
        "descricao": (
            "Falha geral detectada no sistema ABS (freios antibloqueio) e/ou VSC (controle de estabilidade).\n"
            "• ATENÇÃO: com este código, o ABS e o VSC podem estar desativados.\n"
            "• Sintomas: luzes de ABS e VSC acesas no painel, freios funcionando sem ABS.\n"
            "• Causas comuns: sensor de velocidade de roda com defeito, módulo ABS falhando,\n"
            "  fiação danificada ou baixa tensão na bateria.\n"
            "• Ação recomendada: dirija com mais cautela e evite frenagens bruscas.\n"
            "• Leve à concessionária Toyota para diagnóstico urgente."
        ),
    },
    "C1231": {
        "titulo": "🔄 C1231 — Sensor de Ângulo de Esterço",
        "prefixo": "Cxxxx · Freios, ABS, Direção",
        "descricao": (
            "Falha no sensor de ângulo de esterço (direção).\n"
            "• Este sensor informa ao VSC e ao sistema de estabilidade a posição do volante.\n"
            "• Sintomas: luz do VSC acesa, sistema de estabilidade desativado,\n"
            "  direção elétrica com comportamento irregular.\n"
            "• Causas comuns: sensor descalibrado após alinhamento, sensor com defeito\n"
            "  ou fiação com mau contato.\n"
            "• Ação recomendada: recalibre o sensor após qualquer serviço de alinhamento.\n"
            "• Leve à concessionária Toyota para calibração eletrônica."
        ),
    },

    # ── U — Comunicação Eletrônica ────────────────────────────
    "U0100": {
        "titulo": "📡 U0100 — Falha de Comunicação com a ECU",
        "prefixo": "Uxxxx · Comunicação Eletrônica",
        "descricao": (
            "Perda de comunicação com a unidade de controle do motor (ECU/PCM).\n"
            "• A ECU é o 'cérebro' do motor — controla injeção, ignição e emissões.\n"
            "• Sintomas: múltiplas luzes de falha acesas, motor em modo de emergência,\n"
            "  veículo pode não ligar ou ter desempenho muito reduzido.\n"
            "• Causas comuns: bateria fraca, fusível queimado, conector da ECU solto\n"
            "  ou falha interna na ECU.\n"
            "• Ação recomendada: verifique a bateria e os fusíveis primeiro.\n"
            "• Leve IMEDIATAMENTE à concessionária Toyota — não ignore este código."
        ),
    },
    "U0129": {
        "titulo": "📡 U0129 — Falha de Comunicação com Módulo de Freio",
        "prefixo": "Uxxxx · Comunicação Eletrônica",
        "descricao": (
            "Perda de comunicação com o módulo de controle do sistema de freios (ABS/ESC).\n"
            "• Sintomas: luzes de ABS, VSC e freio acesas no painel,\n"
            "  sistemas de segurança ativa desativados.\n"
            "• Causas comuns: falha no barramento CAN (rede de comunicação interna),\n"
            "  módulo ABS com defeito, bateria fraca ou fiação danificada.\n"
            "• Ação recomendada: verifique a tensão da bateria e os conectores do módulo ABS.\n"
            "• Leve à concessionária Toyota — freios básicos funcionam, mas sem ABS/VSC."
        ),
    },
}

# ── Prefixos para busca por categoria ────────────────────────

PREFIXOS = {
    "P0": "⚙️  P0xxx · Motor e transmissão (OBD2 padrão)",
    "P1": "🔧 P1xxx · Códigos específicos Toyota",
    "B0": "🚗 Bxxxx · Carroceria / Airbag / Conforto",
    "B2": "🚗 Bxxxx · Carroceria / Airbag / Conforto",
    "C1": "🛑 Cxxxx · Freios, ABS, Direção",
    "U0": "📡 Uxxxx · Comunicação Eletrônica",
}

# ── Menus de atendimento ──────────────────────────────────────

MENUS = {
    "principal": (
        "Olá! Sou a **Totoya** 😊, assistente virtual do Toyota Experience.\n\n"
        "Como posso te ajudar hoje?\n\n"
        "1 · Processo produtivo\n"
        "2 · Financeiro\n"
        "3 · Retirada\n"
        "4 · Manual de Segurança\n"
        "5 · FAQ\n"
        "6 · Códigos de Diagnóstico (OBD2/Toyota)\n"
        "7 · Outros"
    ),
    "processo": (
        "Escolha o processo do veículo:\n"
        "1 · Prensas\n"
        "2 · Funilaria\n"
        "3 · Pintura\n"
        "4 · Montagem\n"
        "5 · Inspeção"
    ),
    "financeiro": (
        "Assuntos financeiros:\n"
        "1 · Já paguei a entrada, e agora?\n"
        "2 · O banco já autorizou a entrega?\n"
        "3 · Meu pagamento não apareceu no sistema\n"
        "4 · Quanto tempo para atualizar pagamento?\n"
        "5 · Posso antecipar várias parcelas?\n"
        "6 · Posso transferir o financiamento?\n"
        "7 · O carro já está no meu nome?"
    ),
    "retirada": (
        "Retirada do veículo:\n"
        "1 · Onde posso retirar meu carro?\n"
        "2 · Posso retirar sem agendamento?\n"
        "3 · Posso acompanhar o status da entrega?\n"
        "4 · Quem pode retirar além do titular?\n"
        "5 · É necessário levar comprovante?\n"
        "6 · O veículo será entregue com tanque cheio?"
    ),
    "seguranca": (
        "Manual de Segurança — escolha um tema:\n"
        "1 · Cinto de segurança\n"
        "2 · Airbags\n"
        "3 · Freios (ABS e EBD)\n"
        "4 · Controle de estabilidade (VSC)\n"
        "5 · Assistente de partida em rampa (HAC)\n"
        "6 · Câmera de ré e sensores\n"
        "7 · Faróis e visibilidade\n"
        "8 · Pneus e calibragem"
    ),
    "faq": (
        "FAQ:\n"
        "1 · Garantia\n"
        "2 · Revisões\n"
        "3 · Seguro\n"
        "4 · Multas\n"
        "5 · Primeiros passos com o carro"
    ),
    "pos": (
        "O que deseja fazer agora?\n"
        "1 · Escolher outro assunto\n"
        "2 · Voltar ao menu inicial\n"
        "3 · Sair\n"
        "4 · Falar com um atendente (WhatsApp)"
    ),
    "pos_manual": (
        "O que deseja fazer?\n"
        "1 · Consultar outro código\n"
        "2 · Ver lista de códigos\n"
        "3 · Voltar ao menu inicial\n"
        "4 · Sair"
    ),
}

LISTA_CODIGOS = (
    "📋 Códigos de Diagnóstico OBD2 / Toyota\n\n"
    "Prefixos:\n"
    "  P0xxx · Motor e transmissão (OBD2 padrão)\n"
    "  P1xxx · Códigos específicos Toyota\n"
    "  Bxxxx · Carroceria / airbag / conforto\n"
    "  Cxxxx · Freios, ABS, direção\n"
    "  Uxxxx · Comunicação eletrônica\n\n"
    "⚙️  Motor e Transmissão (P0)\n"
    "  P0100 · Falha sensor MAF\n"
    "  P0115 · Sensor temperatura do motor\n"
    "  P0120 · Sensor posição borboleta\n"
    "  P0171 · Mistura pobre\n"
    "  P0300 · Falha de combustão aleatória\n"
    "  P0301 · Falha cilindro 1\n"
    "  P0302 · Falha cilindro 2\n"
    "  P0303 · Falha cilindro 3\n"
    "  P0304 · Falha cilindro 4\n"
    "  P0420 · Catalisador ineficiente\n"
    "  P0500 · Sensor de velocidade\n"
    "  P0700 · Falha transmissão\n\n"
    "🔧 Específicos Toyota (P1)\n"
    "  P1604 · Dificuldade na partida\n"
    "  P1605 · Marcha lenta irregular\n\n"
    "🚗 Carroceria / Airbag (B)\n"
    "  B0100 · Sistema airbag (SRS)\n"
    "  B2799 · Imobilizador / chave\n\n"
    "🛑 Freios / ABS / Direção (C)\n"
    "  C1201 · Sistema ABS/VSC\n"
    "  C1231 · Sensor ângulo esterço\n\n"
    "📡 Comunicação Eletrônica (U)\n"
    "  U0100 · Falha comunicação ECU\n"
    "  U0129 · Falha módulo freio\n\n"
    "Digite o código desejado (ex: P0300, C1201):"
)

RESPOSTAS = {
    "processo": {
        "1": "🔩 A prensa conforma as chapas metálicas que formam a carroceria do veículo.",
        "2": "🔧 A funilaria monta a estrutura metálica do carro, unindo as peças prensadas.",
        "3": "🎨 A pintura protege a carroceria contra corrosão e dá o acabamento visual ao veículo.",
        "4": "⚙️ A montagem integra todos os sistemas do veículo: motor, suspensão, elétrica e interior.",
        "5": "✅ A inspeção final garante a qualidade e segurança do veículo antes da entrega.",
    },
    "financeiro": {
        "1": "💳 Pagamento da entrada confirmado! Agora aguarde a liberação do financiamento pelo banco.",
        "2": "🏦 Financiamento aprovado. Aguarde a conclusão da produção do veículo.",
        "3": "⏳ O sistema pode levar até 48h úteis para atualizar o pagamento.",
        "4": "🕐 A atualização ocorre em até 48h úteis após a compensação bancária.",
        "5": "📅 Sim, é possível antecipar parcelas diretamente pelo banco financiador.",
        "6": "🔄 A transferência do financiamento está sujeita à análise de crédito pelo banco.",
        "7": "📄 A transferência para o seu nome ocorre após a entrega e quitação dos documentos.",
    },
    "retirada": {
        "1": "📍 A retirada será feita na Toyota Ramires em Sorocaba.",
        "2": "📆 Não. É necessário agendar a retirada na página 'Agendar Retirada' do Toyota Experience.",
        "3": "📊 Sim! O status pode ser acompanhado aqui no sistema do Toyota Experience!",
        "4": "👤 Outra pessoa pode retirar com autorização assinada pelo titular comprador.",
        "5": "📋 Sim. Leve comprovante e documento com foto, os mesmos enviados na página 'Dados Pessoais'.",
        "6": "⛽ O veículo será entregue com combustível básico. Recomenda-se abastecer após a retirada.",
    },
    "seguranca": {
        "1": "🔒 CINTO DE SEGURANÇA\n• Todos os ocupantes devem usar o cinto antes de o veículo se mover.\n• Ajuste sobre o ombro e o quadril, nunca sobre o pescoço.\n• O pretensionador aciona automaticamente em colisões.",
        "2": "💨 AIRBAGS\n• O veículo possui airbags frontais, laterais e de cortina.\n• Acionam em colisões de alta intensidade junto com o cinto.\n• Nunca instale cadeirinha voltada para trás no banco dianteiro com airbag ativo.",
        "3": "🛑 FREIOS (ABS e EBD)\n• O ABS evita o travamento das rodas em frenagens bruscas.\n• O EBD distribui a força de frenagem conforme o peso do veículo.\n• Em emergências, pise no freio com força total.",
        "4": "🔄 CONTROLE DE ESTABILIDADE (VSC)\n• Detecta perda de controle e reduz a potência do motor automaticamente.\n• Atua individualmente nos freios de cada roda para corrigir a trajetória.\n• Não desative em condições normais de direção.",
        "5": "⛰️ ASSISTENTE DE PARTIDA EM RAMPA (HAC)\n• Mantém o veículo parado por alguns segundos ao soltar o freio em subidas.\n• Evita que o carro recue ao transferir o pé do freio para o acelerador.\n• Ativado automaticamente em rampas acima de 5 graus.",
        "6": "📷 CÂMERA DE RÉ E SENSORES\n• A câmera é ativada automaticamente ao engatar a marcha à ré.\n• As linhas guias indicam a trajetória estimada do veículo.\n• Os sensores emitem alertas sonoros conforme a proximidade de obstáculos.",
        "7": "💡 FARÓIS E VISIBILIDADE\n• Use faróis baixos em túneis, chuva forte e neblina.\n• O sistema automático acende as luzes conforme a luminosidade.\n• Mantenha faróis e lanternas limpos.",
        "8": "🔧 PNEUS E CALIBRAGEM\n• Verifique a calibragem a cada 15 dias ou antes de viagens longas.\n• A pressão recomendada está na etiqueta na coluna da porta do motorista.\n• Substitua quando os sulcos atingirem 1,6 mm.",
    },
    "faq": {
        "1": "🛡️ Garantia de fábrica entre 3 e 5 anos, dependendo do modelo.",
        "2": "🔧 As revisões devem ocorrer a cada 10.000 km ou 12 meses, o que ocorrer primeiro.",
        "3": "📋 Recomendamos contratar o seguro imediatamente após a retirada do veículo.",
        "4": "⚠️ As multas ficam vinculadas ao proprietário registrado no DETRAN.",
        "5": "📖 Leia o manual do proprietário e familiarize-se com todos os comandos antes de dirigir.",
    },
}

# ── Lógica principal ──────────────────────────────────────────

def processar(entrada, estado):
    entrada_original = entrada.strip()
    entrada_upper    = entrada_original.upper()
    menu             = estado.get("menu", "principal")
    ultimo_menu      = estado.get("ultimo_menu", "principal")
    novo_estado      = dict(estado)

    # ── Pós-resposta dos menus normais ────────────────────────
    if menu == "pos":
        if entrada_original == "1":
            novo_estado["menu"] = ultimo_menu
            return MENUS[ultimo_menu], novo_estado
        elif entrada_original == "2":
            novo_estado["menu"] = "principal"
            return MENUS["principal"], novo_estado
        elif entrada_original == "3":
            novo_estado["menu"] = "principal"
            return "__SAIR__", novo_estado
        elif entrada_original == "4":
            return "📱 Entre em contato pelo WhatsApp: (15) 99999-9999", novo_estado
        else:
            return "Por favor, digite uma opção de 1 a 4.", novo_estado

    # ── Pós-resposta do manual de códigos ─────────────────────
    if menu == "pos_manual":
        if entrada_original == "1":
            novo_estado["menu"] = "codigos"
            return "Digite o código que deseja consultar (ex: P0300, C1201):", novo_estado
        elif entrada_original == "2":
            novo_estado["menu"] = "codigos"
            return LISTA_CODIGOS, novo_estado
        elif entrada_original == "3":
            novo_estado["menu"] = "principal"
            return MENUS["principal"], novo_estado
        elif entrada_original == "4":
            novo_estado["menu"] = "principal"
            return "__SAIR__", novo_estado
        else:
            return "Por favor, digite uma opção de 1 a 4.", novo_estado

    # ── Menu de consulta de códigos OBD2 ─────────────────────
    if menu == "codigos":
        if entrada_upper in ("LISTA", "LIST"):
            return LISTA_CODIGOS, novo_estado

        if entrada_upper in MANUAL:
            item = MANUAL[entrada_upper]
            novo_estado["menu"] = "pos_manual"
            resposta = (
                f"[{item['prefixo']}]\n\n"
                f"{item['titulo']}\n\n"
                f"{item['descricao']}\n\n"
                + MENUS["pos_manual"]
            )
            return resposta, novo_estado

        # Sugestão por prefixo (ex: digitou "P03" → sugere P0300..P0304)
        sugestoes = [k for k in MANUAL if k.startswith(entrada_upper[:3])] if len(entrada_upper) >= 3 else \
                    [k for k in MANUAL if k.startswith(entrada_upper[:2])] if len(entrada_upper) >= 2 else []
        if sugestoes:
            lista = "\n".join(f"  · {s} — {MANUAL[s]['titulo'].split('—')[1].strip()}" for s in sugestoes)
            return (
                f"Código '{entrada_original}' não encontrado.\n\n"
                f"Códigos similares:\n{lista}\n\n"
                f"Ou digite 'lista' para ver todos."
            ), novo_estado

        return (
            f"Código '{entrada_original}' não encontrado.\n"
            f"Digite 'lista' para ver todos os códigos disponíveis."
        ), novo_estado

    # ── Menu principal ────────────────────────────────────────
    if menu == "principal":
        # Permite digitar código OBD2 diretamente do menu principal
        if entrada_upper in MANUAL:
            item = MANUAL[entrada_upper]
            novo_estado["menu"] = "pos_manual"
            resposta = (
                f"[{item['prefixo']}]\n\n"
                f"{item['titulo']}\n\n"
                f"{item['descricao']}\n\n"
                + MENUS["pos_manual"]
            )
            return resposta, novo_estado

        mapa = {
            "1": "processo",
            "2": "financeiro",
            "3": "retirada",
            "4": "seguranca",
            "5": "faq",
        }
        if entrada_original in mapa:
            novo_estado["menu"] = mapa[entrada_original]
            return MENUS[mapa[entrada_original]], novo_estado
        elif entrada_original == "6":
            novo_estado["menu"] = "codigos"
            return LISTA_CODIGOS, novo_estado
        elif entrada_original == "7":
            return "🚧 Funcionalidade em desenvolvimento. Em breve teremos mais informações!", novo_estado
        else:
            return "Por favor, digite um número de 1 a 7.", novo_estado

    # ── Submenus com respostas ────────────────────────────────
    if menu in RESPOSTAS:
        resps = RESPOSTAS[menu]
        if entrada_original in resps:
            novo_estado["ultimo_menu"] = menu
            novo_estado["menu"] = "pos"
            return resps[entrada_original] + "\n\n" + MENUS["pos"], novo_estado
        else:
            limite = len(resps)
            return f"Por favor, digite um número de 1 a {limite}.", novo_estado

    # Fallback
    novo_estado["menu"] = "principal"
    return MENUS["principal"], novo_estado


# ── Rotas ─────────────────────────────────────────────────────

@app.route("/api/chat", methods=["POST"])
def chat():
    data     = request.get_json()
    mensagem = data.get("message", "").strip()
    estado   = data.get("state", {"menu": "principal"})

    if not mensagem:
        return jsonify({"error": "Mensagem vazia"}), 400

    resposta, novo_estado = processar(mensagem, estado)
    return jsonify({"reply": resposta, "state": novo_estado})


@app.route("/api/start", methods=["GET"])
def start():
    estado_inicial = {"menu": "principal", "ultimo_menu": "principal"}
    return jsonify({"reply": MENUS["principal"], "state": estado_inicial})


if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000, debug=False)
