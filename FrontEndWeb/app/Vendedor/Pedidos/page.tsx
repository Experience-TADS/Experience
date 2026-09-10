"use client";

import { useState, useEffect } from "react";
import { Search, Eye, Loader2 } from "lucide-react";
import { api, type PedidoResponse } from "@/app/lib/api";
import VendedorSidebar from "@/app/Vendedor/componentes/VendedorSidebar";

function formatarData(dataPedido: any): string {
  if (!dataPedido) return "—";
  try {
    if (Array.isArray(dataPedido)) {
      const [ano, mes, dia] = dataPedido;
      return new Date(ano, mes - 1, dia).toLocaleDateString("pt-BR");
    }
    return new Date(dataPedido).toLocaleDateString("pt-BR");
  } catch { return "—"; }
}

export default function Pedidos() {
  const [busca, setBusca] = useState("");
  const [pedidos, setPedidos] = useState<PedidoResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [erro, setErro] = useState("");
  const [pedidoSelecionado, setPedidoSelecionado] = useState<PedidoResponse | null>(null);

  useEffect(() => { carregarPedidos(); }, []);

  async function carregarPedidos() {
    setLoading(true); setErro("");
    try {
      const data = await api.getPedidos();
      setPedidos(data.content);
    } catch { setErro("Não foi possível carregar os pedidos."); }
    finally { setLoading(false); }
  }

  const filtrados = pedidos.filter((p) => {
    const t = busca.toLowerCase();
    return String(p.id).includes(t) || p.cliente?.nome?.toLowerCase().includes(t) || p.itens?.[0]?.produto?.modelo?.toLowerCase().includes(t);
  });

  return (
    <div className="flex min-h-screen bg-gray-100 dark:bg-gray-950">
      <VendedorSidebar />

      <div className="flex-1 p-6 md:p-10">
        <div className="flex items-center justify-between mb-6">
          <div>
            <h1 className="text-3xl font-bold text-gray-900 dark:text-white">Pedidos</h1>
            <p className="text-gray-500 mt-1">Gerencie os pedidos dos clientes</p>
          </div>
          <div className={`flex items-center gap-2 px-3 py-1 rounded-full text-sm ${erro ? "bg-red-100 text-red-600" : "bg-green-100 text-green-700"}`}>
            <div className={`w-2 h-2 rounded-full ${erro ? "bg-red-500" : "bg-green-500"}`} />
            {erro ? "Sem conexão" : "Conectado"}
          </div>
        </div>

        <div className="relative max-w-lg mb-8">
          <Search className="absolute left-4 top-3 text-gray-400" size={18} />
          <input
            type="text"
            placeholder="Buscar por cliente, pedido ou veículo..."
            value={busca}
            onChange={(e) => setBusca(e.target.value)}
            className="w-full pl-10 pr-4 py-3 rounded-xl border border-gray-200 dark:border-gray-700 bg-white dark:bg-gray-900 text-gray-900 dark:text-white outline-none focus:ring-2 focus:ring-red-500"
          />
        </div>

        <div className="bg-white dark:bg-gray-900 rounded-2xl border border-gray-100 dark:border-gray-800 shadow overflow-hidden">
          {loading ? (
            <div className="flex items-center justify-center py-16 gap-3 text-gray-500">
              <Loader2 className="animate-spin" size={20} /> Carregando...
            </div>
          ) : erro ? (
            <div className="text-center py-16">
              <p className="text-red-500 mb-3">{erro}</p>
              <button onClick={carregarPedidos} className="text-sm text-red-600 underline">Tentar novamente</button>
            </div>
          ) : filtrados.length === 0 ? (
            <p className="text-center text-gray-400 py-16">Nenhum pedido encontrado.</p>
          ) : (
            <>
              <div className="grid grid-cols-6 px-8 py-4 text-gray-400 text-sm border-b border-gray-100 dark:border-gray-800 bg-gray-50 dark:bg-gray-800/50">
                <span>Pedido</span><span>Cliente</span><span>Veículo</span><span>Valor</span><span>Data</span><span />
              </div>
              {filtrados.map((p) => (
                <div key={p.id} className="grid grid-cols-6 px-8 py-5 items-center border-b border-gray-100 dark:border-gray-800 hover:bg-gray-50 dark:hover:bg-gray-800 transition">
                  <span className="font-semibold text-gray-900 dark:text-white">#{p.id}</span>
                  <span className="text-gray-700 dark:text-gray-300">{p.cliente?.nome ?? "—"}</span>
                  <span className="text-gray-500">{p.itens?.[0]?.produto?.modelo ?? "—"}</span>
                  <span className="text-gray-500">
                    {p.valorTotal != null ? `R$ ${Number(p.valorTotal).toLocaleString("pt-BR", { minimumFractionDigits: 2 })}` : "—"}
                  </span>
                  <span className="text-gray-400">{formatarData(p.dataPedido)}</span>
                  <Eye onClick={() => setPedidoSelecionado(p)} className="text-gray-400 cursor-pointer hover:text-red-600 transition" size={20} />
                </div>
              ))}
            </>
          )}
        </div>
      </div>

      {pedidoSelecionado && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
          <div className="bg-white dark:bg-gray-900 rounded-2xl p-6 w-full max-w-md shadow-lg border border-gray-100 dark:border-gray-800">
            <h2 className="text-xl font-bold text-gray-900 dark:text-white mb-4">Detalhes do Pedido</h2>
            <div className="space-y-2 text-sm text-gray-700 dark:text-gray-300">
              <p><strong className="text-gray-900 dark:text-white">ID:</strong> #{pedidoSelecionado.id}</p>
              <p><strong className="text-gray-900 dark:text-white">Cliente:</strong> {pedidoSelecionado.cliente?.nome ?? "—"}</p>
              <p><strong className="text-gray-900 dark:text-white">Email:</strong> {pedidoSelecionado.cliente?.email ?? "—"}</p>
              <p><strong className="text-gray-900 dark:text-white">Vendedor:</strong> {pedidoSelecionado.vendedor?.nome ?? "—"}</p>
              <p><strong className="text-gray-900 dark:text-white">Veículo:</strong> {pedidoSelecionado.itens?.[0]?.produto?.modelo ?? "—"}</p>
              <p><strong className="text-gray-900 dark:text-white">Cor:</strong> {pedidoSelecionado.itens?.[0]?.produto?.cor ?? "—"}</p>
              <p><strong className="text-gray-900 dark:text-white">Ano:</strong> {pedidoSelecionado.itens?.[0]?.produto?.ano ?? "—"}</p>
              <p><strong className="text-gray-900 dark:text-white">Valor:</strong> {pedidoSelecionado.valorTotal != null ? `R$ ${Number(pedidoSelecionado.valorTotal).toLocaleString("pt-BR", { minimumFractionDigits: 2 })}` : "—"}</p>
              <p><strong className="text-gray-900 dark:text-white">Data:</strong> {formatarData(pedidoSelecionado.dataPedido)}</p>
            </div>
            <button onClick={() => setPedidoSelecionado(null)} className="mt-6 w-full bg-red-600 text-white py-2 rounded-lg hover:bg-red-700 transition">Fechar</button>
          </div>
        </div>
      )}
    </div>
  );
}
