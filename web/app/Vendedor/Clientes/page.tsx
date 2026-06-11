"use client";

import { useState, useEffect } from "react";
import Link from "next/link";
import { LayoutDashboard, Package, Users, User, LogOut, Search, Shield, Plus, Edit } from "lucide-react";
import api from "@/app/lib/api";

const navLinks = [
  { href: "/Vendedor/Dashbord",      label: "Dashboard",     icon: LayoutDashboard },
  { href: "/Vendedor/Pedidos",       label: "Pedidos",       icon: Package },
  { href: "/Vendedor/Clientes",      label: "Clientes",      icon: Users,  active: true },
  { href: "/Vendedor/Perfil",        label: "Perfil",        icon: User },
  { href: "/Vendedor/Administracao", label: "Administração", icon: Shield },
];

type Cliente = {
  id: number;
  nome: string;
  email: string;
  role: string;
  ativo: boolean;
};

export default function Clientes() {
  const [busca, setBusca] = useState("");
  const [clientes, setClientes] = useState<Cliente[]>([]);
  const [clienteEditando, setClienteEditando] = useState<Cliente | null>(null);
  const [modalEditar, setModalEditar] = useState(false);
  const [novoCliente, setNovoCliente] = useState({ nome: "", email: "", senha: "", dataNascimento: "", role: "CLIENTE" });
  const [modalCadastro, setModalCadastro] = useState(false);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    api.get("/api/usuario?size=100")
      .then(({ data }) => {
        const todos: Cliente[] = data.content ?? [];
        setClientes(todos.filter((u) => u.role === "CLIENTE"));
      })
      .catch(() => setClientes([]))
      .finally(() => setLoading(false));
  }, []);

  const clientesFiltrados = clientes.filter((c) =>
    c.nome.toLowerCase().includes(busca.toLowerCase()) ||
    c.email.toLowerCase().includes(busca.toLowerCase())
  );

  function getStatusColor(ativo: boolean) {
    return ativo
      ? "bg-green-100 dark:bg-green-900/30 text-green-700 dark:text-green-400"
      : "bg-red-100 dark:bg-red-900/30 text-red-700 dark:text-red-400";
  }

  async function salvarEdicao() {
    if (!clienteEditando) return;
    try {
      await api.put(`/api/usuario/${clienteEditando.id}`, {
        nome: clienteEditando.nome,
        email: clienteEditando.email,
        role: clienteEditando.role,
      });
      setClientes(clientes.map((c) => c.id === clienteEditando.id ? clienteEditando : c));
    } catch {
      alert("Erro ao salvar alterações.");
    }
    setModalEditar(false);
  }

  async function cadastrarCliente() {
    if (!novoCliente.nome || !novoCliente.email || !novoCliente.senha) return;
    try {
      const { data } = await api.post("/api/usuario", {
        nome: novoCliente.nome,
        email: novoCliente.email,
        senha: novoCliente.senha,
        dataNascimento: novoCliente.dataNascimento || null,
        role: "CLIENTE",
      });
      setClientes([...clientes, data]);
      setNovoCliente({ nome: "", email: "", senha: "", dataNascimento: "", role: "CLIENTE" });
    } catch {
      alert("Erro ao cadastrar cliente.");
    }
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
            <div className="grid grid-cols-5 px-8 py-4 text-gray-500 dark:text-gray-400 text-sm border-b border-gray-100 dark:border-gray-700">
              <span>Nome</span><span>Email</span><span>Perfil</span><span>Status</span><span>Ações</span>
            </div>

            {loading && (
              <p className="px-8 py-6 text-sm text-gray-500 dark:text-gray-400">Carregando clientes...</p>
            )}

            {clientesFiltrados.map((cliente) => (
              <div key={cliente.id} className="grid grid-cols-5 px-8 py-6 items-center border-b border-gray-100 dark:border-gray-700 hover:bg-gray-50 dark:hover:bg-gray-800 transition">
                <span className="font-semibold text-gray-900 dark:text-gray-100">{cliente.nome}</span>
                <span className="text-gray-700 dark:text-gray-300">{cliente.email}</span>
                <span className="text-gray-700 dark:text-gray-300">{cliente.role}</span>
                <span className={`px-3 py-1 text-xs rounded-full w-fit ${getStatusColor(cliente.ativo)}`}>
                  {cliente.ativo ? "Ativo" : "Inativo"}
                </span>
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
              <input value={clienteEditando.nome} onChange={(e) => setClienteEditando({ ...clienteEditando, nome: e.target.value })}
                placeholder="Nome"
                className="w-full border border-gray-200 dark:border-gray-700 bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100 p-3 rounded-lg focus:outline-none focus:ring-2 focus:ring-red-500" />
              <input value={clienteEditando.email} onChange={(e) => setClienteEditando({ ...clienteEditando, email: e.target.value })}
                placeholder="Email"
                className="w-full border border-gray-200 dark:border-gray-700 bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100 p-3 rounded-lg focus:outline-none focus:ring-2 focus:ring-red-500" />
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
              <input value={novoCliente.nome} onChange={(e) => setNovoCliente({ ...novoCliente, nome: e.target.value })}
                placeholder="Nome"
                className="w-full border border-gray-200 dark:border-gray-700 bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100 p-3 rounded-lg focus:outline-none focus:ring-2 focus:ring-red-500" />
              <input value={novoCliente.email} onChange={(e) => setNovoCliente({ ...novoCliente, email: e.target.value })}
                placeholder="Email"
                className="w-full border border-gray-200 dark:border-gray-700 bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100 p-3 rounded-lg focus:outline-none focus:ring-2 focus:ring-red-500" />
              <input type="password" value={novoCliente.senha} onChange={(e) => setNovoCliente({ ...novoCliente, senha: e.target.value })}
                placeholder="Senha"
                className="w-full border border-gray-200 dark:border-gray-700 bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100 p-3 rounded-lg focus:outline-none focus:ring-2 focus:ring-red-500" />
              <input type="date" value={novoCliente.dataNascimento} onChange={(e) => setNovoCliente({ ...novoCliente, dataNascimento: e.target.value })}
                placeholder="Data de Nascimento"
                className="w-full border border-gray-200 dark:border-gray-700 bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100 p-3 rounded-lg focus:outline-none focus:ring-2 focus:ring-red-500" />
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
