"use client";

import { useState, useEffect } from "react";
import Link from "next/link";
import { LayoutDashboard, Package, Users, User, LogOut, Search, Shield, Eye } from "lucide-react";
import api from "@/app/lib/api";

const navLinks = [
  { href: "/Vendedor/Dashbord",      label: "Dashboard",     icon: LayoutDashboard },
  { href: "/Vendedor/Pedidos",       label: "Pedidos",       icon: Package, active: true },
  { href: "/Vendedor/Clientes",      label: "Clientes",      icon: Users },
  { href: "/Vendedor/Perfil",        label: "Perfil",        icon: User },
  { href: "/Vendedor/Administracao", label: "Administração", icon: Shield },
];

type Pedido = {
  id: number;
  dataPedido: string;
  valorTotal: number;
  cliente: { id: number; nome: string; email: string };
  vendedor: { id: number; nome: string };
  itens: { id: number; quantidade: number; produto: { id: number; modelo: string; cor: string; versao: string; ano: number } }[];
};

function getStatusColor(s: string) {
  if (s === "Finalizado")        return "bg-green-100 dark:bg-green-900/30 text-green-700 dark:text-green-400";
  if (s === "Em produção")       return "bg-yellow-100 dark:bg-yellow-900/30 text-yellow-700 dark:text-yellow-400";
  if (s === "Em transporte")     return "bg-purple-100 dark:bg-purple-900/30 text-purple-700 dark:text-purple-400";
  if (s === "Pedido confirmado") return "bg-green-100 dark:bg-green-900/30 text-green-700 dark:text-green-400";
  return "bg-gray-100 dark:bg-gray-700 text-gray-600 dark:text-gray-300";
}

function formatarData(iso: string) {
  if (!iso) return "—";
  return new Date(iso).toLocaleDateString("pt-BR");
}

export default function Pedidos() {
  const [busca, setBusca] = useState("");
  const [pedidos, setPedidos] = useState<Pedido[]>([]);
  const [pedidoSelecionado, setPedidoSelecionado] = useState<Pedido | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    api.get("/api/pedido?size=50&sort=dataPedido,desc")
      .then(({ data }) => setPedidos(data.content ?? []))
      .catch(() => setPedidos([]))
      .finally(() => setLoading(false));
  }, []);

  const pedidosFiltrados = pedidos.filter((p) => {
    const termo = busca.toLowerCase();
    return (
      String(p.id).includes(termo) ||
      p.cliente?.nome?.toLowerCase().includes(termo) ||
      p.itens?.[0]?.produto?.modelo?.toLowerCase().includes(termo)
    );
  });

  return (
    <div className="flex min-h-screen bg-gray-100 dark:bg-gray-950">

      {/* SIDEBAR */}
      <div className="hidden md:flex w-64 bg-white dark:bg-gray-900 border-r border-gray-100 dark:border-gray-800 flex-col justify-between">
        <div>
          <div className="flex items-center gap-3 p-6">
            <div className="bg-red-600 text-white p-3 rounded-lg"><Package size={20} /></div>
            <div>
              <p className="font-bold text-gray-900 dark:text-gray-100">Toyota</p>
              <p className="text-sm text-gray-500 dark:text-gray-400">Painel do Vendedor</p>
            </div>
          </div>
          <nav className="flex flex-col gap-2 px-4">
            {navLinks.map((l) => (
              <Link key={l.href} href={l.href}
                className={`flex items-center gap-3 p-3 rounded-xl transition ${l.active ? "bg-red-600 text-white" : "text-gray-600 dark:text-gray-400 hover:bg-gray-100 dark:hover:bg-gray-800"}`}>
                <l.icon size={18} /> {l.label}
              </Link>
            ))}
          </nav>
        </div>
        <div className="p-4 border-t border-gray-100 dark:border-gray-800">
          <Link href="/Login" className="flex items-center gap-2 text-gray-600 dark:text-gray-400 hover:text-red-600">
            <LogOut size={18} /> Sair
          </Link>
        </div>
      </div>

      {/* CONTEÚDO */}
      <div className="flex-1 p-6 md:p-10">

        <h1 className="text-3xl font-bold text-gray-900 dark:text-gray-100">Pedidos</h1>
        <p className="text-gray-500 dark:text-gray-400 mt-1">Gerencie os pedidos dos seus clientes</p>

        <div className="mt-6 relative max-w-lg">
          <Search className="absolute left-4 top-3 text-gray-400" size={18} />
          <input type="text" placeholder="Buscar por cliente, pedido ou veículo..." value={busca} onChange={(e) => setBusca(e.target.value)}
            className="w-full pl-10 pr-4 py-3 rounded-xl border border-gray-200 dark:border-gray-700 bg-white dark:bg-gray-900 text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 outline-none focus:ring-2 focus:ring-red-500" />
        </div>

        {/* TABELA */}
        <div className="mt-8 bg-white dark:bg-gray-900 rounded-2xl shadow border border-transparent dark:border-gray-700 overflow-hidden">

          <div className="grid grid-cols-6 px-8 py-4 text-gray-500 dark:text-gray-400 text-sm border-b border-gray-100 dark:border-gray-700 bg-gray-50 dark:bg-gray-800/50">
            <span>Pedido</span><span>Cliente</span><span>Veículo</span><span>Valor</span><span>Data</span><span></span>
          </div>

          {loading && (
            <p className="px-8 py-6 text-sm text-gray-500 dark:text-gray-400">Carregando pedidos...</p>
          )}

          {!loading && pedidosFiltrados.length === 0 && (
            <p className="px-8 py-6 text-sm text-gray-500 dark:text-gray-400">Nenhum pedido encontrado.</p>
          )}

          {pedidosFiltrados.map((pedido) => (
            <div key={pedido.id} className="grid grid-cols-6 px-8 py-5 items-center border-b border-gray-100 dark:border-gray-700 hover:bg-gray-50 dark:hover:bg-gray-800 transition">
              <span className="font-semibold text-gray-900 dark:text-gray-100">#{pedido.id}</span>
              <span className="text-gray-800 dark:text-gray-200">{pedido.cliente?.nome ?? "—"}</span>
              <span className="text-gray-600 dark:text-gray-400">{pedido.itens?.[0]?.produto?.modelo ?? "—"}</span>
              <span className="text-gray-600 dark:text-gray-400">
                {pedido.valorTotal != null ? `R$ ${Number(pedido.valorTotal).toLocaleString("pt-BR")}` : "—"}
              </span>
              <span className="text-gray-500 dark:text-gray-400">{formatarData(pedido.dataPedido)}</span>
              <Eye onClick={() => setPedidoSelecionado(pedido)} className="text-gray-400 dark:text-gray-500 cursor-pointer hover:text-red-600 dark:hover:text-red-500 transition" size={20} />
            </div>
          ))}

        </div>
      </div>

      {/* MODAL DETALHES */}
      {pedidoSelecionado && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
          <div className="bg-white dark:bg-gray-900 border border-transparent dark:border-gray-700 rounded-2xl p-6 w-full max-w-md shadow-lg">
            <h2 className="text-xl font-bold text-gray-900 dark:text-gray-100 mb-4">Detalhes do Pedido</h2>
            <div className="space-y-2 text-sm text-gray-800 dark:text-gray-200">
              <p><strong>ID:</strong> #{pedidoSelecionado.id}</p>
              <p><strong>Cliente:</strong> {pedidoSelecionado.cliente?.nome ?? "—"}</p>
              <p><strong>Email:</strong> {pedidoSelecionado.cliente?.email ?? "—"}</p>
              <p><strong>Veículo:</strong> {pedidoSelecionado.itens?.[0]?.produto?.modelo ?? "—"}</p>
              <p><strong>Cor:</strong> {pedidoSelecionado.itens?.[0]?.produto?.cor ?? "—"}</p>
              <p><strong>Ano:</strong> {pedidoSelecionado.itens?.[0]?.produto?.ano ?? "—"}</p>
              <p><strong>Valor Total:</strong> {pedidoSelecionado.valorTotal != null ? `R$ ${Number(pedidoSelecionado.valorTotal).toLocaleString("pt-BR")}` : "—"}</p>
              <p><strong>Data:</strong> {formatarData(pedidoSelecionado.dataPedido)}</p>
              <p><strong>Vendedor:</strong> {pedidoSelecionado.vendedor?.nome ?? "—"}</p>
            </div>
            <button onClick={() => setPedidoSelecionado(null)} className="mt-6 w-full bg-red-600 text-white py-2 rounded-lg hover:bg-red-700 transition">
              Fechar
            </button>
          </div>
        </div>
      )}

    </div>
  );
}
