"use client";

import { useState, useEffect } from "react";
import Sidebar from "@/app/componentes/SideBar";
import { Car, Plus, ChevronDown, ChevronUp } from "lucide-react";
import { useRouter } from "next/navigation";
import api from "@/app/lib/api";

const horarios = ["08:00","09:00","10:00","11:00","13:00","14:00","15:00","16:00"];

// Mapeamento do enum do backend para etapas legíveis
const statusMap: Record<string, string> = {
  AGUARDANDO:         "Compra realizada",
  EM_FABRICACAO:      "Início de produção",
  PINTURA:            "Pintura",
  CONTROLE_QUALIDADE: "Qualidade",
  CONCLUIDO:          "Pedido pronto",
  ENTREGUE:           "Vistoria",
  CANCELADO:          "Cancelado",
};

const etapasOrdem = [
  "AGUARDANDO",
  "EM_FABRICACAO",
  "PINTURA",
  "CONTROLE_QUALIDADE",
  "CONCLUIDO",
  "ENTREGUE",
];

type Pedido = {
  id: number;
  dataPedido: string;
  valorTotal: number;
  cliente: { id: number; nome: string; email: string };
  vendedor: { id: number; nome: string };
  itens: { id: number; quantidade: number; produto: { id: number; modelo: string; cor: string; versao: string; ano: number } }[];
  _demo?: boolean;
};

type StatusHistorico = {
  id: number;
  status: string;
  dataAlteracao: string;
};

// Pedido de demonstração exibido quando não há pedidos reais cadastrados
const PEDIDO_DEMO: Pedido = {
  id: 4821,
  dataPedido: "2026-03-05T10:00:00",
  valorTotal: 189900,
  cliente: { id: 1, nome: "Lauren Silva", email: "lauren@gmail.com" },
  vendedor: { id: 1, nome: "Ricardo Mendes" },
  itens: [{ id: 1, quantidade: 1, produto: { id: 1, modelo: "Corolla Cross", cor: "Prata Nívea", versao: "XRE", ano: 2026 } }],
  _demo: true,
};

const STATUS_DEMO: StatusHistorico[] = [
  { id: 1, status: "AGUARDANDO",    dataAlteracao: "2026-03-05T08:00:00" },
  { id: 2, status: "EM_FABRICACAO", dataAlteracao: "2026-03-06T09:00:00" },
  { id: 3, status: "PINTURA",       dataAlteracao: "2026-03-08T11:00:00" },
];

export default function Veiculo() {
  const router = useRouter();
  const [pedidos, setPedidos] = useState<Pedido[]>([]);
  const [statusPorPedido, setStatusPorPedido] = useState<Record<number, StatusHistorico[]>>({});
  const [expandido, setExpandido] = useState<number | null>(null);
  const [modalAdd, setModalAdd] = useState(false);
  const [codigo, setCodigo] = useState("");
  const [modalAgendar, setModalAgendar] = useState(false);
  const [data, setData] = useState("");
  const [hora, setHora] = useState("");
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    api.get("/api/pedido/meus-pedidos")
      .then(({ data }) => {
        const lista = Array.isArray(data) ? data : [];
        // Se não houver pedidos reais, exibe o pedido demo
        if (lista.length === 0) {
          setPedidos([PEDIDO_DEMO]);
          setStatusPorPedido({ [PEDIDO_DEMO.id]: STATUS_DEMO });
        } else {
          setPedidos(lista);
        }
      })
      .catch(() => {
        // Sem conexão com backend: exibe demo mesmo assim
        setPedidos([PEDIDO_DEMO]);
        setStatusPorPedido({ [PEDIDO_DEMO.id]: STATUS_DEMO });
      })
      .finally(() => setLoading(false));
  }, []);

  async function expandirPedido(pedidoId: number) {
    if (expandido === pedidoId) {
      setExpandido(null);
      return;
    }
    setExpandido(pedidoId);

    // Busca o veículo associado ao pedido para pegar o status
    const pedido = pedidos.find((p) => p.id === pedidoId);
    if (!pedido || statusPorPedido[pedidoId]) return;

    // Tenta buscar status pelo primeiro item do pedido
    if (pedido.itens && pedido.itens.length > 0) {
      try {
        // Busca veículos e tenta encontrar pelo produto
        const { data: veiculos } = await api.get("/api/veiculo");
        const veiculo = veiculos.content?.find(
          (v: any) => v.produto?.id === pedido.itens[0].produto?.id
        );
        if (veiculo) {
          const { data: historico } = await api.get(`/api/veiculo/${veiculo.id}/status`);
          setStatusPorPedido((prev) => ({ ...prev, [pedidoId]: historico }));
        }
      } catch {
        // sem status disponível
      }
    }
  }

  function getStatusAtual(pedidoId: number): string {
    const historico = statusPorPedido[pedidoId];
    if (!historico || historico.length === 0) return "AGUARDANDO";
    return historico[historico.length - 1].status;
  }

  function getIndiceEtapa(status: string): number {
    return etapasOrdem.indexOf(status);
  }

  function adicionarPedido() {
    if (!codigo) return;
    setCodigo("");
    setModalAdd(false);
    // Recarrega pedidos após adicionar
    api.get("/api/pedido/meus-pedidos")
      .then(({ data }) => setPedidos(data))
      .catch(() => {});
  }

  function confirmarAgendamento() {
    if (!data || !hora) return;
    alert(`Retirada agendada para ${data} às ${hora}`);
    setModalAgendar(false);
  }

  function formatarData(iso: string) {
    if (!iso) return "—";
    return new Date(iso).toLocaleDateString("pt-BR");
  }

  return (
    <div className="flex min-h-screen bg-gray-100 dark:bg-gray-950">
      <Sidebar />

      <div className="flex-1 flex flex-col items-center px-5 md:px-12 py-8 md:ml-20 pb-24 md:pb-8">

        {/* HEADER */}
        <div className="w-full max-w-3xl sticky top-0 z-20 bg-gray-100 dark:bg-gray-950 pb-5 mb-8">
          <div className="flex justify-between items-center pt-4">
            <div>
              <h1 className="text-2xl font-bold text-gray-900 dark:text-gray-100">Seus Veículos</h1>
              <p className="text-sm text-gray-600 dark:text-gray-400 mt-1">{pedidos.length} pedidos</p>
            </div>
            <button onClick={() => setModalAdd(true)} className="bg-red-600 hover:bg-red-700 transition text-white px-5 py-2.5 rounded-xl flex items-center gap-2 shadow-md">
              <Plus size={18} /> Adicionar
            </button>
          </div>
        </div>

        {loading && (
          <p className="text-gray-500 dark:text-gray-400 text-sm">Carregando pedidos...</p>
        )}

        {!loading && pedidos.length === 0 && (
          <p className="text-gray-500 dark:text-gray-400 text-sm">Nenhum pedido encontrado.</p>
        )}

        {/* LISTA */}
        {pedidos.map((p) => {
          const aberto = expandido === p.id;
          const statusAtual = getStatusAtual(p.id);
          const indiceAtual = getIndiceEtapa(statusAtual);
          const modelo = p.itens?.[0]?.produto?.modelo ?? "Veículo";
          const cor = p.itens?.[0]?.produto?.cor ?? "—";
          const ano = p.itens?.[0]?.produto?.ano ?? "—";

          return (
            <div key={p.id} className="w-full max-w-3xl mb-8">
              <div onClick={() => expandirPedido(p.id)} className="bg-white dark:bg-gray-900 p-7 rounded-3xl shadow-md hover:shadow-xl transition duration-300 cursor-pointer border border-gray-100 dark:border-gray-700">
                <div className="flex justify-between items-center gap-6">
                  <div>
                    <div className="flex items-center gap-2">
                      <p className="text-xs text-gray-500 dark:text-gray-400">Pedido #{p.id}</p>
                      {p._demo && (
                        <span className="text-[10px] font-semibold bg-yellow-100 dark:bg-yellow-900/30 text-yellow-700 dark:text-yellow-400 px-2 py-0.5 rounded-full">
                          Demo
                        </span>
                      )}
                    </div>
                    <h2 className="text-2xl font-bold text-gray-900 dark:text-gray-100 mt-1">{modelo}</h2>
                    <p className="text-sm text-gray-600 dark:text-gray-400 mt-1">{cor} • {ano}</p>
                    <span className="mt-4 inline-block text-xs font-semibold text-red-700 dark:text-red-400 bg-red-100 dark:bg-red-900/30 px-4 py-1.5 rounded-full">
                      {statusMap[statusAtual] ?? statusAtual}
                    </span>
                  </div>
                  <div className="flex items-center gap-4">
                    <div className="w-14 h-14 rounded-2xl bg-red-100 dark:bg-red-900/30 flex items-center justify-center">
                      <Car className="w-7 h-7 text-red-600" />
                    </div>
                    {aberto ? <ChevronUp size={22} className="text-gray-600 dark:text-gray-400" /> : <ChevronDown size={22} className="text-gray-600 dark:text-gray-400" />}
                  </div>
                </div>
              </div>

              {aberto && (
                <div className="mt-6 space-y-6">
                  {/* Imagem do veículo */}
                  <div className="w-full h-56 rounded-3xl overflow-hidden shadow">
                    <img
                      src={p._demo ? "/corolla-cross.jpg" : "/carro1.png"}
                      alt={modelo}
                      className="w-full h-full object-cover"
                    />
                  </div>
                  <div className="bg-white dark:bg-gray-900 p-7 rounded-3xl shadow-md border border-gray-100 dark:border-gray-700">
                    <h3 className="text-xl font-bold text-gray-900 dark:text-gray-100 mb-7">Acompanhamento</h3>
                    <div className="relative">
                      <div className="absolute left-[14px] top-0 bottom-0 w-[2px] bg-red-500" />
                      {etapasOrdem.map((etapa, i) => {
                        const concluida = i <= indiceAtual;
                        const historicoEtapa = statusPorPedido[p.id]?.find((h) => h.status === etapa);
                        return (
                          <div key={i} className="flex items-start gap-5 mb-10">
                            <div className="z-10">
                              <div className={`w-8 h-8 flex items-center justify-center rounded-full ${concluida ? "bg-red-600" : "bg-gray-300 dark:bg-gray-600"}`}>
                                {concluida && <span className="text-white text-xs">✓</span>}
                              </div>
                            </div>
                            <div>
                              <p className="text-sm font-semibold text-gray-900 dark:text-gray-100">{statusMap[etapa]}</p>
                              <p className="text-xs text-gray-400 dark:text-gray-500 mt-1">
                                {historicoEtapa ? formatarData(historicoEtapa.dataAlteracao) : "—"}
                              </p>
                              <button onClick={(e) => { e.stopPropagation(); router.push("/Cliente/SaibaMais"); }} className="text-xs text-gray-500 dark:text-gray-400 mt-2 hover:text-red-600 transition">
                                ⓘ Mais detalhes
                              </button>
                            </div>
                          </div>
                        );
                      })}
                    </div>
                    <button onClick={(e) => { e.stopPropagation(); setModalAgendar(true); }} className="mt-6 w-full bg-red-600 text-white py-3.5 rounded-xl font-semibold hover:bg-red-700 transition shadow">
                      Agendar retirada
                    </button>
                  </div>
                </div>
              )}
            </div>
          );
        })}
      </div>

      {/* MODAL ADD */}
      {modalAdd && (
        <div className="fixed inset-0 bg-black/40 backdrop-blur-sm flex items-center justify-center z-50">
          <div className="bg-white dark:bg-gray-900 p-7 rounded-3xl w-[90%] max-w-sm shadow-2xl relative border border-transparent dark:border-gray-700">
            <button onClick={() => setModalAdd(false)} className="absolute top-4 right-4 text-gray-400 hover:text-gray-700 dark:hover:text-gray-200 transition">✕</button>
            <h2 className="text-xl font-bold text-gray-900 dark:text-gray-100 mb-1">Adicionar veículo</h2>
            <p className="text-sm text-gray-500 dark:text-gray-400 mb-5">Insira o código do seu pedido</p>
            <label className="text-sm text-gray-600 dark:text-gray-400">Código do veículo</label>
            <input value={codigo} onChange={(e) => setCodigo(e.target.value)} placeholder="Ex: 123ABC"
              className="w-full border border-gray-200 dark:border-gray-700 bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100 p-3 mt-1 mb-5 rounded-xl focus:outline-none focus:ring-2 focus:ring-red-500" />
            <button onClick={adicionarPedido} className="w-full bg-red-600 text-white py-3 rounded-xl font-semibold hover:bg-red-700 transition shadow-md">Adicionar veículo</button>
          </div>
        </div>
      )}

      {/* MODAL AGENDAR */}
      {modalAgendar && (
        <div className="fixed inset-0 bg-black/40 backdrop-blur-sm flex items-center justify-center z-50">
          <div className="bg-white dark:bg-gray-900 p-7 rounded-3xl w-[90%] max-w-sm shadow-2xl relative border border-transparent dark:border-gray-700">
            <button onClick={() => setModalAgendar(false)} className="absolute top-4 right-4 text-gray-400 hover:text-gray-700 dark:hover:text-gray-200 transition">✕</button>
            <h2 className="text-xl font-bold text-gray-900 dark:text-gray-100 mb-1">Agendar retirada</h2>
            <p className="text-sm text-gray-500 dark:text-gray-400 mb-4">Escolha a data e o horário disponível</p>
            <label className="text-sm text-gray-600 dark:text-gray-400">Data</label>
            <input type="date" value={data} onChange={(e) => setData(e.target.value)}
              className="w-full border border-gray-200 dark:border-gray-700 bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100 p-3 mt-1 mb-4 rounded-xl focus:outline-none focus:ring-2 focus:ring-red-500" />
            <label className="text-sm text-gray-600 dark:text-gray-400 mb-2 block">Horário</label>
            <div className="grid grid-cols-3 gap-3 mb-5">
              {horarios.map((h) => (
                <button key={h} onClick={() => setHora(h)}
                  className={`py-2.5 rounded-xl text-sm font-medium border transition ${hora === h ? "bg-red-600 text-white border-red-600 shadow-md" : "bg-white dark:bg-gray-800 text-gray-700 dark:text-gray-300 border-gray-200 dark:border-gray-600 hover:bg-gray-100 dark:hover:bg-gray-700"}`}>
                  {h}
                </button>
              ))}
            </div>
            <button onClick={confirmarAgendamento} className="w-full bg-red-600 text-white py-3 rounded-xl font-semibold hover:bg-red-700 transition shadow-md">Confirmar agendamento</button>
          </div>
        </div>
      )}
    </div>
  );
}
