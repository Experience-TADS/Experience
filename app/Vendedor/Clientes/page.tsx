"use client";

import { useState } from "react";
import Link from "next/link";
import { LayoutDashboard, Package, Users, User, LogOut, Search, Shield, Plus, Edit } from "lucide-react";

const navLinks = [
  { href: "/Vendedor/Dashbord",      label: "Dashboard",     icon: LayoutDashboard },
  { href: "/Vendedor/Pedidos",       label: "Pedidos",       icon: Package },
  { href: "/Vendedor/Clientes",      label: "Clientes",      icon: Users,  active: true },
  { href: "/Vendedor/Perfil",        label: "Perfil",        icon: User },
  { href: "/Vendedor/Administracao", label: "Administração", icon: Shield },
];

export default function Clientes() {
  const [busca, setBusca] = useState("");
  const [clientes, setClientes] = useState([
    { id: 1, nome: "Lauren Silva", email: "lauren@gmail.com", telefone: "11 98888-1111", veiculo: "Corolla Cross", status: "Ativo" },
    { id: 2, nome: "Julia Harumi", email: "julia@gmail.com",  telefone: "11 97777-2222", veiculo: "Hilux SRV",    status: "Inativo" },
  ]);
  const [clienteEditando, setClienteEditando] = useState<any>(null);
  const [modalEditar, setModalEditar] = useState(false);
  const [novoCliente, setNovoCliente] = useState({ nome: "", email: "", telefone: "", veiculo: "", status: "Ativo" });
  const [modalCadastro, setModalCadastro] = useState(false);

  const clientesFiltrados = clientes.filter((c) => c.nome.toLowerCase().includes(busca.toLowerCase()));

  function getStatusColor(status: string) {
    return status === "Ativo"
      ? "bg-green-100 dark:bg-green-900/30 text-green-700 dark:text-green-400"
      : "bg-red-100 dark:bg-red-900/30 text-red-700 dark:text-red-400";
  }

  function salvarEdicao() {
    setClientes(clientes.map((c) => c.id === clienteEditando.id ? clienteEditando : c));
    setModalEditar(false);
  }

  function cadastrarCliente() {
    setClientes([...clientes, { ...novoCliente, id: Date.now() }]);
    setNovoCliente({ nome: "", email: "", telefone: "", veiculo: "", status: "Ativo" });
    setModalCadastro(false);
  }

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
      <div className="flex-1 p-5 md:p-10">

        <h1 className="text-2xl md:text-3xl font-bold text-gray-900 dark:text-gray-100">Clientes</h1>
        <p className="text-gray-500 dark:text-gray-400 mt-1">Gerencie os clientes da concessionária</p>

        <div className="flex justify-between items-center mt-6">
          <div className="relative max-w-lg w-full">
            <Search className="absolute left-4 top-3 text-gray-400" size={18} />
            <input type="text" placeholder="Buscar cliente..." value={busca} onChange={(e) => setBusca(e.target.value)}
              className="w-full pl-10 pr-4 py-3 rounded-xl border border-gray-200 dark:border-gray-700 bg-white dark:bg-gray-900 text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 outline-none focus:ring-2 focus:ring-red-500" />
          </div>
          <button onClick={() => setModalCadastro(true)}
            className="ml-4 flex items-center gap-2 bg-red-600 text-white px-5 py-3 rounded-xl hover:bg-red-700 transition">
            <Plus size={18} /> Cadastrar Cliente
          </button>
        </div>

        {/* TABELA */}
        <div className="mt-8 bg-white dark:bg-gray-900 rounded-2xl shadow border border-transparent dark:border-gray-700 overflow-x-auto">
          <div className="min-w-[700px]">
            <div className="grid grid-cols-6 px-8 py-4 text-gray-500 dark:text-gray-400 text-sm border-b border-gray-100 dark:border-gray-700">
              <span>Nome</span><span>Email</span><span>Telefone</span><span>Veículo</span><span>Status</span><span>Ações</span>
            </div>
            {clientesFiltrados.map((cliente) => (
              <div key={cliente.id} className="grid grid-cols-6 px-8 py-6 items-center border-b border-gray-100 dark:border-gray-700 hover:bg-gray-50 dark:hover:bg-gray-800 transition">
                <span className="font-semibold text-gray-900 dark:text-gray-100">{cliente.nome}</span>
                <span className="text-gray-700 dark:text-gray-300">{cliente.email}</span>
                <span className="text-gray-700 dark:text-gray-300">{cliente.telefone}</span>
                <span className="text-gray-700 dark:text-gray-300">{cliente.veiculo}</span>
                <span className={`px-3 py-1 text-xs rounded-full w-fit ${getStatusColor(cliente.status)}`}>{cliente.status}</span>
                <button onClick={() => { setClienteEditando({ ...cliente }); setModalEditar(true); }} className="text-blue-600 dark:text-blue-400 hover:text-blue-800 dark:hover:text-blue-300">
                  <Edit size={18} />
                </button>
              </div>
            ))}
          </div>
        </div>
      </div>

      {/* MODAL EDITAR */}
      {modalEditar && clienteEditando && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
          <div className="bg-white dark:bg-gray-900 border border-transparent dark:border-gray-700 rounded-2xl p-6 w-full max-w-md shadow-lg">
            <h2 className="text-xl font-bold text-gray-900 dark:text-gray-100 mb-4">Editar Cliente</h2>
            <div className="space-y-3">
              {["nome","email","telefone","veiculo"].map((f) => (
                <input key={f} value={clienteEditando[f]} onChange={(e) => setClienteEditando({ ...clienteEditando, [f]: e.target.value })}
                  placeholder={f.charAt(0).toUpperCase() + f.slice(1)}
                  className="w-full border border-gray-200 dark:border-gray-700 bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100 p-3 rounded-lg focus:outline-none focus:ring-2 focus:ring-red-500" />
              ))}
            </div>
            <div className="flex gap-3 mt-5">
              <button onClick={() => setModalEditar(false)} className="flex-1 border border-gray-200 dark:border-gray-700 py-2 rounded-lg text-gray-700 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-800 transition">Cancelar</button>
              <button onClick={salvarEdicao} className="flex-1 bg-red-600 text-white py-2 rounded-lg hover:bg-red-700 transition">Salvar</button>
            </div>
          </div>
        </div>
      )}

      {/* MODAL CADASTRO */}
      {modalCadastro && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
          <div className="bg-white dark:bg-gray-900 border border-transparent dark:border-gray-700 rounded-2xl p-6 w-full max-w-md shadow-lg">
            <h2 className="text-xl font-bold text-gray-900 dark:text-gray-100 mb-4">Cadastrar Cliente</h2>
            <div className="space-y-3">
              {["nome","email","telefone","veiculo"].map((f) => (
                <input key={f} value={novoCliente[f as keyof typeof novoCliente]} onChange={(e) => setNovoCliente({ ...novoCliente, [f]: e.target.value })}
                  placeholder={f.charAt(0).toUpperCase() + f.slice(1)}
                  className="w-full border border-gray-200 dark:border-gray-700 bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100 p-3 rounded-lg focus:outline-none focus:ring-2 focus:ring-red-500" />
              ))}
            </div>
            <div className="flex gap-3 mt-5">
              <button onClick={() => setModalCadastro(false)} className="flex-1 border border-gray-200 dark:border-gray-700 py-2 rounded-lg text-gray-700 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-800 transition">Cancelar</button>
              <button onClick={cadastrarCliente} className="flex-1 bg-red-600 text-white py-2 rounded-lg hover:bg-red-700 transition">Cadastrar</button>
            </div>
          </div>
        </div>
      )}

    </div>
  );
}
