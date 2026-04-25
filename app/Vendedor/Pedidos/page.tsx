"use client";

import { useState } from "react";
import Link from "next/link";
import {
  LayoutDashboard,
  Package,
  Users,
  User,
  LogOut,
  Search,
  Eye,
  Shield,
} from "lucide-react";

const pedidosData = [
  {
    id: "#4821",
    cliente: "Lauren Silva",
    email: "lauren@gmail.com",
    veiculo: "Corolla Cross",
    status: "Em produção",
    data: "05/03/2026",
  },
  {
    id: "#4820",
    cliente: "Julia Harumi",
    email: "julia@gmail.com",
    veiculo: "Hilux SRV 4x4",
    status: "Pedido confirmado",
    data: "04/03/2026",
  },
  {
    id: "#4819",
    cliente: "Bianca Nunes",
    email: "bia@gmail.com",
    veiculo: "Yaris Sedan",
    status: "Em produção",
    data: "03/03/2026",
  },
  {
    id: "#4818",
    cliente: "Paola Costa",
    email: "paola@gmail.com",
    veiculo: "Corolla",
    status: "Em transporte",
    data: "03/03/2026",
  },
];

export default function Pedidos() {
  const [busca, setBusca] = useState("");
  const [pedidoSelecionado, setPedidoSelecionado] = useState<any>(null);

  function getStatusColor(status: string) {
    if (status === "Finalizado") return "bg-green-100 text-green-700";
    if (status === "Em produção") return "bg-yellow-100 text-yellow-700";
    if (status === "Em transporte") return "bg-purple-100 text-purple-700";
    if (status === "Pedido confirmado") return "bg-green-100 text-green-700";
    return "bg-gray-100 text-gray-600";
  }

  const pedidosFiltrados = pedidosData.filter((pedido) =>
    pedido.cliente.toLowerCase().includes(busca.toLowerCase()) ||
    pedido.id.toLowerCase().includes(busca.toLowerCase()) ||
    pedido.veiculo.toLowerCase().includes(busca.toLowerCase())
  );

  return (
    <div className="flex min-h-screen bg-gray-100">

      {/* SIDEBAR */}
      <div className="hidden md:flex w-64 bg-white border-r flex-col justify-between">

        <div>

          <div className="flex items-center gap-3 p-6">
            <div className="bg-red-600 text-white p-3 rounded-lg">
              <Package size={20} />
            </div>

            <div>
              <p className="font-bold text-black">Toyota</p>
              <p className="text-sm text-gray-500">Painel do Vendedor</p>
            </div>
          </div>

          <nav className="flex flex-col gap-2 px-4">

            {/* ✅ DASHBOARD (corrigido nome) */}
            <Link
              href="/Vendedor/Dashbord"
              className="flex items-center gap-3 text-gray-600 p-3 rounded-xl hover:bg-gray-100"
            >
              <LayoutDashboard size={18} />
              Dashboard
            </Link>

            {/* ✅ PEDIDOS */}
            <Link
              href="/Vendedor/Pedidos"
              className="flex items-center gap-3 bg-red-600 text-white p-3 rounded-xl"
            >
              <Package size={18} />
              Pedidos
            </Link>

            {/* ✅ CLIENTES (corrigido) */}
            <Link
              href="/Vendedor/Clientes"
              className="flex items-center gap-3 text-gray-600 p-3 rounded-xl hover:bg-gray-100"
            >
              <Users size={18} />
              Clientes
            </Link>

            {/* ✅ PERFIL (corrigido) */}
            <Link
              href="/Vendedor/Perfil"
              className="flex items-center gap-3 text-gray-600 p-3 rounded-xl hover:bg-gray-100"
            >
              <User size={18} />
              Perfil
            </Link>
 <Link
              href="/Vendedor/Administracao"
              className="flex items-center gap-3 text-gray-600 p-3 rounded-xl hover:bg-gray-100"
            >
              <User size={18} />
              Administração
            </Link>
          </nav>

        </div>

        <div className="p-4 border-t">
          <Link
            href="/Login"
            className="flex items-center gap-2 text-gray-600 hover:text-red-600"
          >
            <LogOut size={18} />
            Sair
          </Link>
        </div>

      </div>

      {/* CONTEÚDO */}
      <div className="flex-1 p-6 md:p-10">

        <h1 className="text-3xl font-bold text-black">
          Pedidos
        </h1>

        <p className="text-gray-500 mt-1">
          Gerencie os pedidos dos seus clientes
        </p>

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

          {pedidosFiltrados.map((pedido) => (
            <div
              key={pedido.id}
              className="grid grid-cols-6 px-8 py-5 items-center border-b hover:bg-gray-50 transition"
            >
              <span className="font-semibold text-black">{pedido.id}</span>
              <span className="text-black">{pedido.cliente}</span>
              <span className="text-gray-600">{pedido.veiculo}</span>

              <span className={`px-3 py-1 rounded-full text-xs w-fit ${getStatusColor(pedido.status)}`}>
                {pedido.status}
              </span>

              <span className="text-gray-500">{pedido.data}</span>

              <Eye
                onClick={() => setPedidoSelecionado(pedido)}
                className="text-gray-400 cursor-pointer hover:text-red-600 transition"
                size={20}
              />
            </div>
          ))}

        </div>

      </div>

      {/* MODAL */}
      {pedidoSelecionado && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">

          <div className="bg-white rounded-2xl p-6 w-full max-w-md shadow-lg">

            <h2 className="text-xl font-bold text-black mb-4">
              Detalhes do Pedido
            </h2>

            <div className="space-y-2 text-sm text-black">
              <p><strong>ID:</strong> {pedidoSelecionado.id}</p>
              <p><strong>Cliente:</strong> {pedidoSelecionado.cliente}</p>
              <p><strong>Email:</strong> {pedidoSelecionado.email}</p>
              <p><strong>Veículo:</strong> {pedidoSelecionado.veiculo}</p>
              <p><strong>Status:</strong> {pedidoSelecionado.status}</p>
              <p><strong>Data:</strong> {pedidoSelecionado.data}</p>
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