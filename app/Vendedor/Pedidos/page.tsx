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
} from "lucide-react";

import ModalPedido from "@/app/componentes/NewsModal";

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
  const [statusAtual, setStatusAtual] = useState("");

  function getStatusColor(status: string) {
    if (status === "Finalizado") return "bg-green-100 text-green-700";
    if (status === "Em produção") return "bg-yellow-100 text-yellow-700";
    if (status === "Em transporte") return "bg-purple-100 text-purple-700";
    if (status === "Pedido confirmado") return "bg-blue-100 text-blue-700";

    return "bg-gray-100 text-gray-600";
  }

  const pedidosFiltrados = pedidosData.filter((pedido) =>
    pedido.cliente.toLowerCase().includes(busca.toLowerCase()) ||
    pedido.id.toLowerCase().includes(busca.toLowerCase()) ||
    pedido.veiculo.toLowerCase().includes(busca.toLowerCase())
  );

  function abrirModal(pedido: any) {
    setPedidoSelecionado(pedido);
    setStatusAtual(pedido.status);
  }

  function fecharModal() {
    setPedidoSelecionado(null);
  }

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
              <p className="font-bold text-gray-900">Toyota</p>
              <p className="text-sm text-gray-500">Painel do Vendedor</p>
            </div>
          </div>

          <nav className="flex flex-col gap-2 px-4">

            <Link
              href="/Vendedor/Dashbord"
              className="flex items-center gap-3 text-gray-600 p-3 rounded-xl hover:bg-gray-100"
            >
              <LayoutDashboard size={18} />
              Dashboard
            </Link>

            <Link
              href="/Vendedor/Pedidos"
              className="flex items-center gap-3 bg-red-600 text-white p-3 rounded-xl"
            >
              <Package size={18} />
              Pedidos
            </Link>

            <Link
              href="/Vendedor/Clientes"
              className="flex items-center gap-3 text-gray-600 p-3 rounded-xl hover:bg-gray-100"
            >
              <Users size={18} />
              Clientes
            </Link>

            <Link
              href="/Vendedor/Perfil"
              className="flex items-center gap-3 text-gray-600 p-3 rounded-xl hover:bg-gray-100"
            >
              <User size={18} />
              Perfil
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
      <div className="flex-1 p-5 md:p-10">

        <h1 className="text-2xl md:text-3xl font-bold text-gray-900">
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
            placeholder="Buscar pedido..."
            value={busca}
            onChange={(e) => setBusca(e.target.value)}
            className="w-full pl-10 pr-4 py-3 rounded-xl border bg-white outline-none focus:ring-2 focus:ring-red-500"
          />
        </div>

        {/* TABELA */}
        <div className="mt-8 bg-white rounded-2xl shadow border overflow-x-auto">

          <div className="min-w-[700px]">

            <div className="grid grid-cols-6 px-8 py-4 text-gray-500 text-sm border-b">
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
                className="grid grid-cols-6 px-8 py-6 items-center border-b hover:bg-gray-50"
              >
                
                <span className="font-semibold text-gray-900">
                  {pedido.id}
                </span>

                <span className="font-medium text-gray-800">
                  {pedido.cliente}
                </span>

                <span className="text-gray-600">
                  {pedido.veiculo}
                </span>

                <span
                  className={`px-3 py-1 text-sm rounded-full w-fit ${getStatusColor(
                    pedido.status
                  )}`}
                >
                  {pedido.status}
                </span>

                <span className="text-gray-600">
                  {pedido.data}
                </span>

                <Eye
                  onClick={() => abrirModal(pedido)}
                  className="text-gray-400 cursor-pointer hover:text-red-600"
                  size={20}
                />

              </div>
            ))}

          </div>

          {pedidosFiltrados.length === 0 && (
            <p className="p-6 text-gray-500 text-center">
              Nenhum pedido encontrado
            </p>
          )}

        </div>

      </div>

      {/* MODAL */}
      {pedidoSelecionado && (
        <ModalPedido
          pedido={pedidoSelecionado}
          statusAtual={statusAtual}
          setStatusAtual={setStatusAtual}
          fecharModal={fecharModal}
        />
      )}

    </div>
  );
}