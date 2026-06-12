"use client";

import { useState } from "react";
import Sidebar from "@/app/componentes/SideBar";
import { Car, Plus, ChevronDown, ChevronUp, Loader2, CheckCircle2, AlertCircle, Info } from "lucide-react";
import Link from "next/link";

const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || "http://54.91.247.129";

const STATUS_LABELS: Record<string, string> = {
  AGUARDANDO: "Aguardando início",
  MONTAGEM_ESTRUTURAL: "Montagem estrutural",
  PINTURA: "Pintura",
  INSTALACAO_MOTOR: "Instalação do motor",
  ACABAMENTO_INTERNO: "Acabamento interno",
  INSPECAO_FINAL: "Inspeção final",
  LIBERACAO_TRANSPORTE: "Liberação para transporte",
  ENTREGUE: "Entregue",
  CANCELADO: "Cancelado",
};

const STATUS_DESC: Record<string, string> = {
  AGUARDANDO: "Seu pedido foi confirmado e está na fila para iniciar a produção.",
  MONTAGEM_ESTRUTURAL: "O chassi e a carroceria do seu veículo estão sendo soldados e montados.",
  PINTURA: "Seu veículo está recebendo as camadas de primer, base e verniz.",
  INSTALACAO_MOTOR: "Motor, câmbio e suspensão estão sendo instalados.",
  ACABAMENTO_INTERNO: "Interior, sistema elétrico e eletrônico estão sendo montados.",
  INSPECAO_FINAL: "Seu veículo está passando pela verificação final de qualidade.",
  LIBERACAO_TRANSPORTE: "Aprovado na inspeção! Seu veículo está a caminho da concessionária.",
  ENTREGUE: "Seu veículo foi entregue. Boas estradas!",
  CANCELADO: "Este pedido foi cancelado.",
};

const ETAPAS_ORDEM = [
  "AGUARDANDO",
  "MONTAGEM_ESTRUTURAL",
  "PINTURA",
  "INSTALACAO_MOTOR",
  "ACABAMENTO_INTERNO",
  "INSPECAO_FINAL",
  "LIBERACAO_TRANSPORTE",
  "ENTREGUE",
];

type Pedido = {
  id: string | number;
  modelo: string;
  cor: string;
  ano: string;
  statusAtual: string;
  chassi?: number;
};

function getStatusIndex(status: string) {
  const idx = ETAPAS_ORDEM.indexOf(status);
  return idx === -1 ? 0 : idx;
}

export default function Acompanhamento() {
  const [pedidos, setPedidos] = useState<Pedido[]>([
    { id: "1", modelo: "Hilux", cor: "Prata Nívea", ano: "2026", statusAtual: "MONTAGEM_ESTRUTURAL", chassi: 10001 },
  ]);

  const [expandido, setExpandido] = useState<string | null>(null);
  const [modalAdd, setModalAdd] = useState(false);
  const [codigo, setCodigo] = useState("");
  const [buscandoCodigo, setBuscandoCodigo] = useState(false);
  const [erroCodigo, setErroCodigo] = useState("");
  const [sucessoCodigo, setSucessoCodigo] = useState("");

  async function adicionarPedido(e: React.FormEvent) {
    e.preventDefault();
    setErroCodigo("");
    setSucessoCodigo("");

    const chassiNum = parseInt(codigo.replace(/\D/g, ""));
    if (!codigo.trim() || isNaN(chassiNum)) {
      setErroCodigo("Informe um código de chassi válido (somente números).");
      return;
    }

    setBuscandoCodigo(true);

    try {
      const res = await fetch(`${API_BASE_URL}/api/veiculo/chassi/${chassiNum}`, {
        headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
      });

      if (res.status === 404) {
        setErroCodigo("Veículo não encontrado. Verifique o código informado.");
        return;
      }
      if (!res.ok) {
        setErroCodigo("Erro ao buscar veículo. Tente novamente.");
        return;
      }

      const veiculo = await res.json();
      const jaAdicionado = pedidos.some((p) => String(p.chassi) === String(chassiNum));
      if (jaAdicionado) {
        setErroCodigo("Este veículo já está na sua lista.");
        return;
      }

      setPedidos([...pedidos, {
        id: Date.now().toString(),
        modelo: veiculo.produto?.modelo || "Veículo Toyota",
        cor: veiculo.produto?.cor || "—",
        ano: String(veiculo.produto?.ano || "—"),
        statusAtual: veiculo.statusVeiculo || "AGUARDANDO",
        chassi: chassiNum,
      }]);

      setSucessoCodigo("Veículo adicionado com sucesso!");
      setCodigo("");
      setTimeout(() => { setModalAdd(false); setSucessoCodigo(""); }, 1500);
    } catch {
      setErroCodigo("Não foi possível conectar ao servidor.");
    } finally {
      setBuscandoCodigo(false);
    }
  }

  return (
    <div className="flex min-h-screen bg-gray-100">
      <Sidebar />

      <div className="flex-1 flex flex-col items-center px-5 md:px-12 py-8 md:ml-20 pb-24 md:pb-8">

        {/* HEADER */}
        <div className="w-full max-w-3xl sticky top-0 z-20 bg-gray-100 pb-5 mb-8">
          <div className="flex justify-between items-center pt-4">
            <div>
              <h1 className="text-2xl font-bold text-gray-900">Seus Veículos</h1>
              <p className="text-sm text-gray-600 mt-1">{pedidos.length} pedido(s)</p>
            </div>
            <button
              onClick={() => { setModalAdd(true); setErroCodigo(""); setSucessoCodigo(""); }}
              className="bg-red-600 hover:bg-red-700 transition text-white px-5 py-2.5 rounded-xl flex items-center gap-2 shadow-md"
            >
              <Plus size={18} />
              Adicionar
            </button>
          </div>
        </div>

        {/* LISTA */}
        {pedidos.map((p) => {
          const aberto = expandido === String(p.id);
          const statusIdx = getStatusIndex(p.statusAtual);
          const isCancelado = p.statusAtual === "CANCELADO";

          return (
            <div key={p.id} className="w-full max-w-3xl mb-8">

              {/* CARD */}
              <div
                onClick={() => setExpandido(aberto ? null : String(p.id))}
                className="bg-white p-7 rounded-3xl shadow-md hover:shadow-xl transition duration-300 cursor-pointer border border-gray-100"
              >
                <div className="flex justify-between items-center gap-6">
                  <div>
                    <p className="text-xs text-gray-500">Pedido #{p.chassi || p.id}</p>
                    <h2 className="text-2xl font-bold text-gray-900 mt-1">{p.modelo}</h2>
                    <p className="text-sm text-gray-600 mt-1">{p.cor} • {p.ano}</p>
                    <span className={`mt-4 inline-block text-xs font-semibold px-4 py-1.5 rounded-full ${
                      isCancelado ? "bg-red-100 text-red-700"
                      : p.statusAtual === "ENTREGUE" ? "bg-green-100 text-green-700"
                      : "bg-yellow-100 text-yellow-700"
                    }`}>
                      {STATUS_LABELS[p.statusAtual] || p.statusAtual}
                    </span>
                  </div>
                  <div className="flex items-center gap-4">
                    <div className="w-14 h-14 rounded-2xl bg-red-100 flex items-center justify-center">
                      <Car className="w-7 h-7 text-red-600" />
                    </div>
                    {aberto ? <ChevronUp size={22} /> : <ChevronDown size={22} />}
                  </div>
                </div>
              </div>

              {/* TIMELINE */}
              {aberto && (
                <div className="mt-6">
                  <div className="bg-white p-7 rounded-3xl shadow-md border border-gray-100">
                    <h3 className="text-xl font-bold text-gray-900 mb-7">Acompanhamento</h3>

                    <div className="relative">
                      <div className="absolute left-[14px] top-0 bottom-0 w-[2px] bg-gray-200" />

                      {ETAPAS_ORDEM.map((etapa, i) => {
                        const concluida = !isCancelado && i <= statusIdx;
                        const atual = etapa === p.statusAtual;

                        return (
                          <div key={etapa} className="flex items-start gap-5 mb-8">
                            <div className="z-10 flex-shrink-0">
                              <div className={`w-8 h-8 flex items-center justify-center rounded-full border-2 transition ${
                                atual ? "bg-red-600 border-red-600"
                                : concluida ? "bg-green-500 border-green-500"
                                : "bg-white border-gray-300"
                              }`}>
                                {concluida && !atual && <span className="text-white text-xs">✓</span>}
                                {atual && <span className="text-white text-xs font-bold">●</span>}
                              </div>
                            </div>

                            <div className="flex-1">
                              <p className={`text-sm font-semibold ${atual ? "text-red-600" : concluida ? "text-gray-900" : "text-gray-400"}`}>
                                {STATUS_LABELS[etapa]}
                              </p>

                              {/* Descrição da etapa atual */}
                              {atual && STATUS_DESC[etapa] && (
                                <p className="text-xs text-gray-500 mt-1 leading-relaxed">
                                  {STATUS_DESC[etapa]}
                                </p>
                              )}

                              {/* Link "Mais detalhes" abre a página SaibaMais */}
                              <Link
                                href="/Cliente/SaibaMais"
                                onClick={(e) => e.stopPropagation()}
                                className="inline-flex items-center gap-1 text-xs text-gray-400 mt-1.5 hover:text-red-600 transition"
                              >
                                <Info size={12} />
                                Ver detalhes do processo
                              </Link>
                            </div>
                          </div>
                        );
                      })}

                      {isCancelado && (
                        <div className="flex items-start gap-5 mb-8">
                          <div className="z-10">
                            <div className="w-8 h-8 flex items-center justify-center rounded-full bg-red-600 border-2 border-red-600">
                              <span className="text-white text-xs">✕</span>
                            </div>
                          </div>
                          <div>
                            <p className="text-sm font-semibold text-red-600">Cancelado</p>
                            <p className="text-xs text-red-400 mt-1">Pedido encerrado. Entre em contato com a concessionária.</p>
                          </div>
                        </div>
                      )}
                    </div>
                  </div>
                </div>
              )}
            </div>
          );
        })}

      </div>

      {/* MODAL ADICIONAR VEÍCULO */}
      {modalAdd && (
        <div className="fixed inset-0 bg-black/40 backdrop-blur-sm flex items-center justify-center z-50">
          <div className="bg-white p-7 rounded-3xl w-[90%] max-w-sm shadow-2xl relative">
            <button onClick={() => setModalAdd(false)} className="absolute top-4 right-4 text-gray-400 hover:text-gray-700 transition">✕</button>

            <div className="mb-5">
              <h2 className="text-xl font-bold text-gray-900">Adicionar veículo</h2>
              <p className="text-sm text-gray-500 mt-1">Insira o número do chassi do seu veículo</p>
            </div>

            <form onSubmit={adicionarPedido}>
              <div className="mb-4">
                <label className="text-sm text-gray-600">Número do chassi</label>
                <input
                  value={codigo}
                  onChange={(e) => { setCodigo(e.target.value); setErroCodigo(""); }}
                  placeholder="Ex: 10001"
                  className="w-full border border-gray-200 p-3 mt-1 rounded-xl focus:outline-none focus:ring-2 focus:ring-red-500 text-gray-900"
                />
              </div>

              {erroCodigo && (
                <div className="flex items-center gap-2 text-red-500 text-sm mb-3">
                  <AlertCircle size={16} /> {erroCodigo}
                </div>
              )}
              {sucessoCodigo && (
                <div className="flex items-center gap-2 text-green-600 text-sm mb-3">
                  <CheckCircle2 size={16} /> {sucessoCodigo}
                </div>
              )}

              <button
                type="submit"
                disabled={buscandoCodigo}
                className="w-full bg-red-600 text-white py-3 rounded-xl font-semibold hover:bg-red-700 transition shadow-md disabled:opacity-50 flex items-center justify-center gap-2"
              >
                {buscandoCodigo && <Loader2 className="animate-spin" size={16} />}
                {buscandoCodigo ? "Buscando..." : "Adicionar veículo"}
              </button>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
