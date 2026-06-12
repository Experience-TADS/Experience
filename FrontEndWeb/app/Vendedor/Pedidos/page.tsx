"use client";

import { useState, useEffect } from "react";
import Link from "next/link";
import {
  LayoutDashboard, Package, Users, User, LogOut,
  Search, Eye, Shield, ChevronLeft, ChevronRight, Loader2,
} from "lucide-react";

const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || "http://54.91.247.129";

const STATUS_LABELS: Record<string, string> = {
  AGUARDANDO: "Aguardando",
  MONTAGEM_ESTRUTURAL: "Montagem estrutural",
  PINTURA: "Pintura",
  INSTALACAO_MOTOR: "Instalação do motor",
  ACABAMENTO_INTERNO: "Acabamento interno",
  INSPECAO_FINAL: "Inspeção final",
  LIBERACAO_TRANSPORTE: "Em transporte",
  ENTREGUE: "Entregue",
  CANCELADO: "Cancelado",
};

type Pedido = {
  id: number;
  cliente?: { nome?: string; email?: string };
  veiculo?: { produto?: { modelo?: string }; statusVeiculo?: string };
  dataPedido?: string;
  valorTotal?: number;
};

const PAGE_SIZE = 10;

function getStatusColor(status: string) {
  if (status === "ENTREGUE") return "bg-green-100 text-green-700";
  if (status === "CANCELADO") return "bg-red-100 text-red-700";
  if (status === "LIBERACAO_TRANSPORTE") return "bg-purple-100 text-purple-700";
  if (status === "INSPECAO_FINAL") return "bg-blue-100 text-blue-700";
  return "bg-yellow-100 text-yellow-700";
}

export default function Pedidos() {
  const [pedidos, setPedidos] = useState<Pedido[]>([]);
  const [loading, setLoading] = useState(true);
  const [erro, setErro] = useState("");
  const [busca, setBusca] = useState("");
  const [pagina, setPagina] = useState(0);
  const [totalPaginas, setTotalPaginas] = useState(1);
  const [pedidoSelecionado, setPedidoSelecionado] = useState<Pedido | null>(null);

  useEffect(() => {
    carregarPedidos(pagina);
  }, [pagina]);

  async function carregarPedidos(page: number) {
    setLoading(true);
    setErro("");
    try {
      const res = await fetch(
        `${API_BASE_URL}/api/pedido?page=${page}&size=${PAGE_SIZE}&sort=id,desc`,
        { headers: { Authorization: `Bearer ${localStorage.getItem("token")}` } }
      );
      if (!res.ok) throw new Error();
      const data = await res.json();

      // Suporte a resposta paginada Spring Boot { content, totalPages } ou array simples
      if (data.content) {
        setPedidos(data.content);
        setTotalPaginas(data.totalPages || 1);
      } else {
        setPedidos(Array.isArray(data) ? data : []);
        setTotalPaginas(1);
      }
    } catch {
      setErro("Não foi possível carregar os pedidos.");
    } finally {
      setLoading(false);
    }
  }

  const pedidosFiltrados = pedidos.filter((p) => {
    const termo = busca.toLowerCase();
    return (
      String(p.id).includes(termo) ||
      p.cliente?.nome?.toLowerCase().includes(termo) ||
      p.veiculo?.produto?.modelo?.toLowerCase().includes(termo)
    );
  });

  function formatarData(dataStr?: string) {
    if (!dataStr) return "—";
    return new Date(dataStr).toLocaleDateString("pt-BR");
  }

  return (
    <div className="flex min-h-screen bg-gray-100">

      {/* SIDEBAR */}
      <div className="hidden md:flex w-64 bg-white border-r flex-col justify-between">
        <div>
          <div className="flex items-center gap-3 p-6">
            <div className="bg-red-600 text-white p-3 rounded-lg"><Package size={20} /></div>
            <div>
              <p className="font-bold text-black">Toyota</p>
              <p className="text-sm text-gray-500">Painel do Vendedor</p>
            </div>
          </div>
          <nav className="flex flex-col gap-2 px-4">
            <Link href="/Vendedor/Dashbord" className="flex items-center gap-3 text-gray-600 p-3 rounded-xl hover:bg-gray-100">
              <LayoutDashboard size={18} /> Dashboard
            </Link>
            <Link href="/Vendedor/Pedidos" className="flex items-center gap-3 bg-red-600 text-white p-3 rounded-xl">
              <Package size={18} /> Pedidos
            </Link>
            <Link href="/Vendedor/Clientes" className="flex items-center gap-3 text-gray-600 p-3 rounded-xl hover:bg-gray-100">
              <Users size={18} /> Clientes
            </Link>
            <Link href="/Vendedor/Perfil" className="flex items-center gap-3 text-gray-600 p-3 rounded-xl hover:bg-gray-100">
              <User size={18} /> Perfil
            </Link>
            <Link href="/Vendedor/Administracao" className="flex items-center gap-3 text-gray-600 p-3 rounded-xl hover:bg-gray-100">
              <Shield size={18} /> Administração
            </Link>
          </nav>
        </div>
        <div className="p-4 border-t">
          <Link href="/Login" className="flex items-center gap-2 text-gray-600 hover:text-red-600">
            <LogOut size={18} /> Sair
          </Link>
        </div>
      </div>

      {/* CONTEÚDO */}
      <div className="flex-1 p-6 md:p-10">
        <h1 className="text-3xl font-bold text-black">Pedidos</h1>
        <p className="text-gray-500 mt-1">Gerencie os pedidos dos seus clientes</p>

        {/* BUSCA */}
        <div className="mt-6 relative max-w-lg">
          <Search className="absolute left-4 top-3 text-gray-400" size={18} />
          <input
            type="text"
            placeholder="Buscar por cliente, pedido ou veículo..."
            value={busca}
            onChange={(e) => setBusca(e.target.value)}
            className="w-full pl-10 pr-4 py-3 rounded-xl border bg-white outline-none focus:ring-2 focus:ring-red-500 text-black"
          />
        </div>

        {/* TABELA */}
        <div className="mt-8 bg-white rounded-2xl shadow border overflow-hidden">
          <div className="grid grid-cols-6 px-8 py-4 text-gray-500 text-sm border-b bg-gray-50">
            <span>Pedido</span>
            <span>Cliente</span>
            <span>Veículo</span>
            <span>Status</span>
            <span>Data</span>
            <span></span>
          </div>

          {loading ? (
            <div className="flex items-center justify-center py-16 gap-3 text-gray-500">
              <Loader2 className="animate-spin" size={20} /> Carregando pedidos...
            </div>
          ) : erro ? (
            <div className="text-center py-16">
              <p className="text-red-500 mb-3">{erro}</p>
              <button onClick={() => carregarPedidos(pagina)} className="text-sm text-red-600 underline">Tentar novamente</button>
            </div>
          ) : pedidosFiltrados.length === 0 ? (
            <div className="text-center py-16 text-gray-400">Nenhum pedido encontrado.</div>
          ) : (
            pedidosFiltrados.map((pedido) => {
              const status = pedido.veiculo?.statusVeiculo || "AGUARDANDO";
              return (
                <div key={pedido.id} className="grid grid-cols-6 px-8 py-5 items-center border-b hover:bg-gray-50 transition">
                  <span className="font-semibold text-black">#{pedido.id}</span>
                  <span className="text-black">{pedido.cliente?.nome || "—"}</span>
                  <span className="text-gray-600">{pedido.veiculo?.produto?.modelo || "—"}</span>
                  <span className={`px-3 py-1 rounded-full text-xs w-fit ${getStatusColor(status)}`}>
                    {STATUS_LABELS[status] || status}
                  </span>
                  <span className="text-gray-500">{formatarData(pedido.dataPedido)}</span>
                  <button
                    onClick={() => setPedidoSelecionado(pedido)}
                    title="Ver detalhes"
                    className="text-gray-400 hover:text-red-600 transition"
                  >
                    <Eye size={20} />
                  </button>
                </div>
              );
            })
          )}
        </div>

        {/* PAGINAÇÃO */}
        {!loading && !erro && totalPaginas > 1 && (
          <div className="flex items-center justify-center gap-4 mt-6">
            <button
              onClick={() => setPagina((p) => Math.max(0, p - 1))}
              disabled={pagina === 0}
              className="flex items-center gap-1 px-4 py-2 rounded-xl border bg-white text-gray-600 hover:bg-gray-50 disabled:opacity-40 transition"
            >
              <ChevronLeft size={16} /> Anterior
            </button>

            <span className="text-sm text-gray-600">
              Página {pagina + 1} de {totalPaginas}
            </span>

            <button
              onClick={() => setPagina((p) => Math.min(totalPaginas - 1, p + 1))}
              disabled={pagina >= totalPaginas - 1}
              className="flex items-center gap-1 px-4 py-2 rounded-xl border bg-white text-gray-600 hover:bg-gray-50 disabled:opacity-40 transition"
            >
              Próxima <ChevronRight size={16} />
            </button>
          </div>
        )}
      </div>

      {/* MODAL DETALHES */}
      {pedidoSelecionado && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
          <div className="bg-white rounded-2xl p-6 w-full max-w-md shadow-lg">
            <h2 className="text-xl font-bold text-black mb-4">Detalhes do Pedido</h2>
            <div className="space-y-2 text-sm text-black">
              <p><strong>ID:</strong> #{pedidoSelecionado.id}</p>
              <p><strong>Cliente:</strong> {pedidoSelecionado.cliente?.nome || "—"}</p>
              <p><strong>Email:</strong> {pedidoSelecionado.cliente?.email || "—"}</p>
              <p><strong>Veículo:</strong> {pedidoSelecionado.veiculo?.produto?.modelo || "—"}</p>
              <p><strong>Status:</strong> {STATUS_LABELS[pedidoSelecionado.veiculo?.statusVeiculo || ""] || pedidoSelecionado.veiculo?.statusVeiculo || "—"}</p>
              <p><strong>Data:</strong> {formatarData(pedidoSelecionado.dataPedido)}</p>
              {pedidoSelecionado.valorTotal && (
                <p><strong>Valor:</strong> R$ {pedidoSelecionado.valorTotal.toLocaleString("pt-BR", { minimumFractionDigits: 2 })}</p>
              )}
            </div>
            <button
              onClick={() => setPedidoSelecionado(null)}
              className="mt-6 w-full bg-red-600 text-white py-2 rounded-lg hover:bg-red-700 transition"
            >
              Fechar
            </button>
          </div>
        </div>
      )}
    </div>
  );
}
