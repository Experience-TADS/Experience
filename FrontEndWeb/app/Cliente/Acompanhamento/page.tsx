"use client";

import { useState, useEffect } from "react";
import Sidebar from "@/app/componentes/SideBar";
import { Car, ChevronDown, ChevronUp, Loader2, Plus } from "lucide-react";
import { useRouter } from "next/navigation";
import { api, type PedidoResponse } from "@/app/lib/api";

function formatarData(dataPedido: any): string {
  if (!dataPedido) return "—";
  try {
    if (Array.isArray(dataPedido)) {
      const [ano, mes, dia] = dataPedido;
      return new Date(ano, mes - 1, dia).toLocaleDateString("pt-BR");
    }
    return new Date(dataPedido).toLocaleDateString("pt-BR");
  } catch {
    return "—";
  }
}

const STATUS_MAP: Record<string, number> = {
  MONTAGEM_ESTRUTURAL: 1,
  PINTURA: 2,
  INSTALACAO_MOTOR: 3,
  ACABAMENTO_INTERNO: 4,
  INSPECAO_FINAL: 5,
  LIBERACAO_TRANSPORTE: 6,
};

const etapas = [
  "Compra realizada",
  "Início de produção",
  "Pintura",
  "Instalação do motor",
  "Acabamento interno",
  "Inspeção final",
  "Liberado para transporte",
];

const horarios = ["08:00", "09:00", "10:00", "11:00", "13:00", "14:00", "15:00", "16:00"];
const STORAGE_KEY = "pedidos_vinculados";

function getPedidosVinculados(): number[] {
  try {
    const raw = localStorage.getItem(STORAGE_KEY);
    return raw ? JSON.parse(raw) : [];
  } catch {
    return [];
  }
}

function salvarPedidoVinculado(id: number) {
  const lista = getPedidosVinculados();
  if (!lista.includes(id)) {
    localStorage.setItem(STORAGE_KEY, JSON.stringify([...lista, id]));
  }
}

export default function Acompanhamento() {
  const router = useRouter();

  const [pedidos, setPedidos] = useState<PedidoResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [erro, setErro] = useState("");
  const [expandido, setExpandido] = useState<number | null>(null);

  const [modalAdd, setModalAdd] = useState(false);
  const [codigoPedido, setCodigoPedido] = useState("");
  const [buscando, setBuscando] = useState(false);
  const [erroBusca, setErroBusca] = useState("");

  const [modalAgendar, setModalAgendar] = useState(false);
  const [data, setData] = useState("");
  const [hora, setHora] = useState("");

  useEffect(() => { carregarPedidos(); }, []);

  async function carregarPedidos() {
    setLoading(true);
    setErro("");
    try {
      let lista: PedidoResponse[] = [];
      try {
        const meus = await api.getMeusPedidos();
        lista = meus;
        meus.forEach((p) => salvarPedidoVinculado(p.id));
      } catch {}

      const idsExtras = getPedidosVinculados().filter((id) => !lista.some((p) => p.id === id));
      const extras = (await Promise.all(idsExtras.map((id) => api.getPedido(id).catch(() => null)))).filter(Boolean) as PedidoResponse[];
      setPedidos([...lista, ...extras]);
    } catch (err: unknown) {
      const msg = err instanceof Error ? err.message : "";
      if (msg.includes("401") || msg.includes("403")) router.push("/Login");
      else setErro("Não foi possível carregar seus pedidos.");
    } finally {
      setLoading(false);
    }
  }

  async function adicionarPedido(e: React.FormEvent) {
    e.preventDefault();
    setErroBusca("");
    const id = Number(codigoPedido.trim());
    if (!id || isNaN(id)) { setErroBusca("Digite um número de pedido válido."); return; }
    if (pedidos.some((p) => p.id === id)) { setErroBusca("Este pedido já está na sua lista."); return; }
    setBuscando(true);
    try {
      const pedido = await api.getPedido(id);
      salvarPedidoVinculado(pedido.id);
      setPedidos((prev) => [...prev, pedido]);
      setModalAdd(false);
      setCodigoPedido("");
    } catch {
      setErroBusca("Pedido não encontrado. Verifique o número e tente novamente.");
    } finally {
      setBuscando(false);
    }
  }

  function confirmarAgendamento() {
    if (!data || !hora) return;
    alert(`Retirada agendada para ${data} às ${hora}`);
    setModalAgendar(false);
    setData("");
    setHora("");
  }

  if (loading) {
    return (
      <div className="flex min-h-screen bg-gray-100 dark:bg-gray-950">
        <Sidebar />
        <div className="flex-1 flex items-center justify-center gap-2 text-gray-500">
          <Loader2 className="animate-spin" size={20} /> Carregando...
        </div>
      </div>
    );
  }

  return (
    <div className="flex min-h-screen bg-gray-100 dark:bg-gray-950">
      <Sidebar />

      <div className="flex-1 flex flex-col items-center px-5 md:px-12 py-8 md:ml-20">

        <div className="w-full max-w-3xl pb-5 mb-8">
          <div className="flex justify-between items-center pt-4">
            <div>
              <h1 className="text-2xl font-bold text-gray-900 dark:text-white">Seus Pedidos</h1>
              <p className="text-sm text-gray-500 mt-1">{pedidos.length} pedido(s)</p>
            </div>
            <button
              onClick={() => { setModalAdd(true); setErroBusca(""); setCodigoPedido(""); }}
              className="bg-red-600 hover:bg-red-700 transition text-white px-5 py-2.5 rounded-xl flex items-center gap-2 shadow-md"
            >
              <Plus size={18} /> Adicionar
            </button>
          </div>
        </div>

        {erro && (
          <div className="w-full max-w-3xl mb-6 text-center">
            <p className="text-red-500 mb-2">{erro}</p>
            <button onClick={carregarPedidos} className="text-sm text-red-600 underline">Tentar novamente</button>
          </div>
        )}

        {!erro && pedidos.length === 0 && (
          <div className="w-full max-w-3xl text-center py-16 text-gray-400">
            <Car className="mx-auto mb-4 text-gray-300" size={48} />
            <p className="mb-4">Você ainda não tem pedidos vinculados.</p>
            <button
              onClick={() => { setModalAdd(true); setErroBusca(""); setCodigoPedido(""); }}
              className="bg-red-600 text-white px-6 py-2.5 rounded-xl text-sm font-semibold hover:bg-red-700 transition"
            >
              Adicionar pedido
            </button>
          </div>
        )}

        {pedidos.map((p) => {
          const aberto = expandido === p.id;
          const veiculo = p.itens?.[0];
          const statusStr = (veiculo as any)?.statusVeiculo ?? "";
          const statusIndex = STATUS_MAP[statusStr] ?? 0;
          const modelo = veiculo?.produto?.modelo ?? "Veículo";
          const cor = veiculo?.produto?.cor ?? "";
          const ano = veiculo?.produto?.ano ?? "";

          return (
            <div key={p.id} className="w-full max-w-3xl mb-8">
              <div
                onClick={() => setExpandido(aberto ? null : p.id)}
                className="bg-white dark:bg-gray-900 p-7 rounded-3xl shadow-md hover:shadow-xl transition duration-300 cursor-pointer border border-gray-100 dark:border-gray-800"
              >
                <div className="flex justify-between items-center gap-6">
                  <div>
                    <p className="text-xs text-gray-400">Pedido #{p.id}</p>
                    <h2 className="text-2xl font-bold text-gray-900 dark:text-white mt-1">{modelo}</h2>
                    <p className="text-sm text-gray-500 mt-1">{[cor, String(ano)].filter(Boolean).join(" • ")}</p>
                    <span className="mt-4 inline-block text-xs font-semibold text-red-700 bg-red-100 dark:bg-red-900/30 dark:text-red-400 px-4 py-1.5 rounded-full">
                      {etapas[statusIndex]}
                    </span>
                  </div>
                  <div className="flex items-center gap-4">
                    <div className="w-14 h-14 rounded-2xl bg-red-100 dark:bg-red-900/30 flex items-center justify-center">
                      <Car className="w-7 h-7 text-red-600" />
                    </div>
                    {aberto ? <ChevronUp size={22} className="text-gray-500" /> : <ChevronDown size={22} className="text-gray-500" />}
                  </div>
                </div>
              </div>

              {aberto && (
                <div className="mt-6 space-y-6">
                  <div className="bg-white dark:bg-gray-900 p-6 rounded-3xl shadow-md border border-gray-100 dark:border-gray-800 text-sm text-gray-700 dark:text-gray-300 space-y-2">
                    <p><strong className="text-gray-900 dark:text-white">Data do pedido:</strong> {formatarData(p.dataPedido)}</p>
                    <p><strong className="text-gray-900 dark:text-white">Valor total:</strong>{" "}
                      {p.valorTotal != null ? `R$ ${Number(p.valorTotal).toLocaleString("pt-BR", { minimumFractionDigits: 2 })}` : "—"}
                    </p>
                    <p><strong className="text-gray-900 dark:text-white">Vendedor:</strong> {p.vendedor?.nome ?? "—"}</p>
                  </div>

                  <div className="bg-white dark:bg-gray-900 p-7 rounded-3xl shadow-md border border-gray-100 dark:border-gray-800">
                    <h3 className="text-xl font-bold text-gray-900 dark:text-white mb-7">Acompanhamento</h3>

                    <div className="relative">
                      <div className="absolute left-[14px] top-0 bottom-0 w-[2px] bg-red-200 dark:bg-red-900/50" />
                      {etapas.map((etapa, i) => {
                        const concluida = i <= statusIndex;
                        return (
                          <div key={i} className="flex items-start gap-5 mb-10">
                            <div className="z-10">
                              <div className={`w-8 h-8 flex items-center justify-center rounded-full ${concluida ? "bg-red-600" : "bg-gray-300 dark:bg-gray-700"}`}>
                                {concluida && <span className="text-white text-xs">✓</span>}
                              </div>
                            </div>
                            <div>
                              <p className="text-sm font-semibold text-gray-900 dark:text-white">{etapa}</p>
                              {concluida && <p className="text-xs text-gray-400 mt-1">{formatarData(p.dataPedido)}</p>}
                              <button
                                onClick={(e) => { e.stopPropagation(); router.push("/Cliente/SaibaMais"); }}
                                className="text-xs text-gray-400 hover:text-red-600 transition mt-1"
                              >
                                ⓘ Saiba mais
                              </button>
                            </div>
                          </div>
                        );
                      })}
                    </div>

                    <button
                      onClick={(e) => { e.stopPropagation(); setModalAgendar(true); }}
                      className="mt-6 w-full bg-red-600 text-white py-3.5 rounded-xl font-semibold hover:bg-red-700 transition shadow"
                    >
                      Agendar retirada
                    </button>
                  </div>
                </div>
              )}
            </div>
          );
        })}
      </div>

      {modalAdd && (
        <div className="fixed inset-0 bg-black/50 backdrop-blur-sm flex items-center justify-center z-50">
          <div className="bg-white dark:bg-gray-900 p-7 rounded-3xl w-[90%] max-w-sm shadow-2xl relative border border-gray-100 dark:border-gray-800">
            <button onClick={() => setModalAdd(false)} className="absolute top-4 right-4 text-gray-400 hover:text-gray-700 dark:hover:text-gray-200">✕</button>
            <div className="mb-5">
              <h2 className="text-xl font-bold text-gray-900 dark:text-white">Adicionar pedido</h2>
              <p className="text-sm text-gray-500 mt-1">Informe o número do pedido fornecido pelo vendedor</p>
            </div>
            <form onSubmit={adicionarPedido}>
              <div className="mb-5">
                <label className="text-sm text-gray-600 dark:text-gray-400">Número do pedido</label>
                <input
                  type="number"
                  min={1}
                  placeholder="Ex: 3"
                  className="w-full border border-gray-200 dark:border-gray-700 bg-white dark:bg-gray-800 text-gray-900 dark:text-white p-3 mt-1 rounded-xl focus:outline-none focus:ring-2 focus:ring-red-500"
                  value={codigoPedido}
                  onChange={(e) => setCodigoPedido(e.target.value)}
                  autoFocus
                />
              </div>
              {erroBusca && <p className="text-red-500 text-sm mb-4">{erroBusca}</p>}
              <button
                type="submit"
                disabled={buscando || !codigoPedido}
                className="w-full bg-red-600 text-white py-3 rounded-xl font-semibold hover:bg-red-700 transition disabled:opacity-40 flex items-center justify-center gap-2"
              >
                {buscando && <Loader2 className="animate-spin" size={16} />}
                {buscando ? "Buscando..." : "Adicionar"}
              </button>
            </form>
          </div>
        </div>
      )}

      {modalAgendar && (
        <div className="fixed inset-0 bg-black/50 backdrop-blur-sm flex items-center justify-center z-50">
          <div className="bg-white dark:bg-gray-900 p-7 rounded-3xl w-[90%] max-w-sm shadow-2xl relative border border-gray-100 dark:border-gray-800">
            <button onClick={() => setModalAgendar(false)} className="absolute top-4 right-4 text-gray-400 hover:text-gray-700 dark:hover:text-gray-200">✕</button>
            <div className="mb-5">
              <h2 className="text-xl font-bold text-gray-900 dark:text-white">Agendar retirada</h2>
              <p className="text-sm text-gray-500 mt-1">Escolha a data e o horário</p>
            </div>
            <div className="mb-4">
              <label className="text-sm text-gray-600 dark:text-gray-400">Data</label>
              <input
                type="date"
                className="w-full border border-gray-200 dark:border-gray-700 bg-white dark:bg-gray-800 text-gray-900 dark:text-white p-3 mt-1 rounded-xl focus:outline-none focus:ring-2 focus:ring-red-500"
                value={data}
                onChange={(e) => setData(e.target.value)}
              />
            </div>
            <div className="mb-5">
              <label className="text-sm text-gray-600 dark:text-gray-400 mb-2 block">Horário</label>
              <div className="grid grid-cols-3 gap-3">
                {horarios.map((h) => (
                  <button
                    key={h}
                    onClick={() => setHora(h)}
                    className={`py-2.5 rounded-xl text-sm font-medium border transition ${
                      hora === h
                        ? "bg-red-600 text-white border-red-600"
                        : "bg-white dark:bg-gray-800 text-gray-700 dark:text-gray-300 border-gray-200 dark:border-gray-700 hover:bg-gray-100 dark:hover:bg-gray-700"
                    }`}
                  >
                    {h}
                  </button>
                ))}
              </div>
            </div>
            <button
              onClick={confirmarAgendamento}
              className="w-full bg-red-600 text-white py-3 rounded-xl font-semibold hover:bg-red-700 transition shadow-md"
            >
              Confirmar agendamento
            </button>
          </div>
        </div>
      )}
    </div>
  );
}
