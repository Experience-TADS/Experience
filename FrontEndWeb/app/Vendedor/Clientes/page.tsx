"use client";

import { useState, useEffect } from "react";
import Link from "next/link";
import { api } from "@/app/lib/api";

import {
  LayoutDashboard,
  Package,
  Users,
  User,
  LogOut,
  Search,
  Shield,
  Plus,
  Edit,
  Trash2,
  Loader2,
} from "lucide-react";

type Cliente = {
  id: number;
  nome: string;
  email: string;
  senha?: string;
};

export default function Clientes() {
  const [busca, setBusca] = useState("");
  const [clientes, setClientes] = useState<Cliente[]>([]);
  const [loading, setLoading] = useState(true);
  const [erro, setErro] = useState("");

  const [modalCadastro, setModalCadastro] = useState(false);
  const [modalEditar, setModalEditar] = useState(false);
  const [clienteEditando, setClienteEditando] = useState<Cliente | null>(null);
  const [salvando, setSalvando] = useState(false);

  const [novoCliente, setNovoCliente] = useState({
    nome: "",
    email: "",
    senha: "",
  });

  // Busca clientes do backend na AWS
  useEffect(() => {
    carregarClientes();
  }, []);

  async function carregarClientes() {
    setLoading(true);
    setErro("");
    try {
      const data = await api.getUsuarios();
      setClientes(data);
    } catch {
      setErro("Não foi possível conectar ao servidor. Verifique a conexão com a AWS.");
    } finally {
      setLoading(false);
    }
  }

  const clientesFiltrados = clientes.filter((c) =>
    c.nome?.toLowerCase().includes(busca.toLowerCase()) ||
    c.email?.toLowerCase().includes(busca.toLowerCase())
  );

  function abrirEditar(cliente: Cliente) {
    setClienteEditando({ ...cliente });
    setModalEditar(true);
  }

  async function salvarEdicao() {
    if (!clienteEditando) return;
    setSalvando(true);
    try {
      await api.updateUsuario(clienteEditando.id, clienteEditando);
      await carregarClientes();
      setModalEditar(false);
    } catch {
      alert("Erro ao atualizar cliente.");
    } finally {
      setSalvando(false);
    }
  }

  async function cadastrarCliente() {
    if (!novoCliente.nome || !novoCliente.email || !novoCliente.senha) {
      alert("Preencha todos os campos.");
      return;
    }
    setSalvando(true);
    try {
      await api.createUsuario(novoCliente);
      await carregarClientes();
      setNovoCliente({ nome: "", email: "", senha: "" });
      setModalCadastro(false);
    } catch {
      alert("Erro ao cadastrar cliente.");
    } finally {
      setSalvando(false);
    }
  }

  async function deletarCliente(id: number) {
    if (!confirm("Deseja remover este cliente?")) return;
    try {
      await api.deleteUsuario(id);
      await carregarClientes();
    } catch {
      alert("Erro ao deletar cliente.");
    }
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
              <p className="font-bold text-black">Toyota</p>
              <p className="text-sm text-black">Painel do Vendedor</p>
            </div>
          </div>

          <nav className="flex flex-col gap-2 px-4">
            <Link href="/Vendedor/Dashbord" className="flex items-center gap-3 text-black p-3 rounded-xl hover:bg-gray-100">
              <LayoutDashboard size={18} /> Dashboard
            </Link>
            <Link href="/Vendedor/Pedidos" className="flex items-center gap-3 text-black p-3 rounded-xl hover:bg-gray-100">
              <Package size={18} /> Pedidos
            </Link>
            <Link href="/Vendedor/Clientes" className="flex items-center gap-3 bg-red-600 text-white p-3 rounded-xl">
              <Users size={18} /> Clientes
            </Link>
            <Link href="/Vendedor/Perfil" className="flex items-center gap-3 text-black p-3 rounded-xl hover:bg-gray-100">
              <User size={18} /> Perfil
            </Link>
            <Link href="/Vendedor/Administracao" className="flex items-center gap-3 text-black p-3 rounded-xl hover:bg-gray-100">
              <Shield size={18} /> Administração
            </Link>
          </nav>
        </div>

        <div className="p-4 border-t">
          <Link href="/Login" className="flex items-center gap-2 text-black hover:text-red-600">
            <LogOut size={18} /> Sair
          </Link>
        </div>
      </div>

      {/* CONTEÚDO */}
      <div className="flex-1 p-5 md:p-10">

        <div className="flex items-center justify-between">
          <div>
            <h1 className="text-2xl md:text-3xl font-bold text-black">Clientes</h1>
            <p className="text-gray-500 mt-1">Dados sincronizados com o servidor AWS</p>
          </div>

          {/* STATUS DE CONEXÃO */}
          <div className={`flex items-center gap-2 px-3 py-1 rounded-full text-sm ${erro ? "bg-red-100 text-red-600" : "bg-green-100 text-green-600"}`}>
            <div className={`w-2 h-2 rounded-full ${erro ? "bg-red-500" : "bg-green-500"}`} />
            {erro ? "Sem conexão" : "AWS conectada"}
          </div>
        </div>

        {/* BUSCA + BOTÃO */}
        <div className="flex justify-between items-center mt-6">
          <div className="relative max-w-lg w-full">
            <Search className="absolute left-4 top-3 text-gray-400" size={18} />
            <input
              type="text"
              placeholder="Buscar cliente..."
              value={busca}
              onChange={(e) => setBusca(e.target.value)}
              className="w-full pl-10 pr-4 py-3 rounded-xl border bg-white text-black outline-none focus:ring-2 focus:ring-red-500"
            />
          </div>

          <button
            onClick={() => setModalCadastro(true)}
            className="ml-4 flex items-center gap-2 bg-red-600 text-white px-5 py-3 rounded-xl hover:bg-red-700"
          >
            <Plus size={18} /> Cadastrar Cliente
          </button>
        </div>

        {/* TABELA */}
        <div className="mt-8 bg-white rounded-2xl shadow border overflow-x-auto">
          {loading ? (
            <div className="flex items-center justify-center py-16 gap-3 text-gray-500">
              <Loader2 className="animate-spin" size={20} />
              Buscando dados na AWS...
            </div>
          ) : erro ? (
            <div className="text-center py-16">
              <p className="text-red-500 mb-3">{erro}</p>
              <button onClick={carregarClientes} className="text-sm text-red-600 underline">
                Tentar novamente
              </button>
            </div>
          ) : clientesFiltrados.length === 0 ? (
            <div className="text-center py-16 text-gray-400">
              Nenhum cliente encontrado.
            </div>
          ) : (
            <div className="min-w-[600px]">
              <div className="grid grid-cols-4 px-8 py-4 text-gray-500 text-sm border-b">
                <span>ID</span>
                <span>Nome</span>
                <span>Email</span>
                <span>Ações</span>
              </div>

              {clientesFiltrados.map((cliente) => (
                <div key={cliente.id} className="grid grid-cols-4 px-8 py-5 items-center border-b hover:bg-gray-50">
                  <span className="text-gray-400 text-sm">#{cliente.id}</span>
                  <span className="font-semibold text-black">{cliente.nome}</span>
                  <span className="text-gray-600">{cliente.email}</span>
                  <div className="flex gap-3">
                    <button onClick={() => abrirEditar(cliente)} className="text-blue-600 hover:text-blue-800">
                      <Edit size={18} />
                    </button>
                    <button onClick={() => deletarCliente(cliente.id)} className="text-red-500 hover:text-red-700">
                      <Trash2 size={18} />
                    </button>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>

      {/* MODAL CADASTRO */}
      {modalCadastro && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
          <div className="bg-white rounded-2xl p-8 w-full max-w-md shadow-xl">
            <h2 className="text-xl font-bold text-black mb-6">Novo Cliente</h2>

            <div className="space-y-4">
              <input
                type="text"
                placeholder="Nome completo"
                value={novoCliente.nome}
                onChange={(e) => setNovoCliente({ ...novoCliente, nome: e.target.value })}
                className="w-full p-3 rounded-xl border text-black outline-none focus:ring-2 focus:ring-red-500"
              />
              <input
                type="email"
                placeholder="Email"
                value={novoCliente.email}
                onChange={(e) => setNovoCliente({ ...novoCliente, email: e.target.value })}
                className="w-full p-3 rounded-xl border text-black outline-none focus:ring-2 focus:ring-red-500"
              />
              <input
                type="password"
                placeholder="Senha"
                value={novoCliente.senha}
                onChange={(e) => setNovoCliente({ ...novoCliente, senha: e.target.value })}
                className="w-full p-3 rounded-xl border text-black outline-none focus:ring-2 focus:ring-red-500"
              />
            </div>

            <div className="flex gap-3 mt-6">
              <button
                onClick={() => setModalCadastro(false)}
                className="flex-1 py-3 rounded-xl border text-black hover:bg-gray-50"
              >
                Cancelar
              </button>
              <button
                onClick={cadastrarCliente}
                disabled={salvando}
                className="flex-1 py-3 rounded-xl bg-red-600 text-white hover:bg-red-700 disabled:opacity-50 flex items-center justify-center gap-2"
              >
                {salvando && <Loader2 className="animate-spin" size={16} />}
                Salvar
              </button>
            </div>
          </div>
        </div>
      )}

      {/* MODAL EDITAR */}
      {modalEditar && clienteEditando && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
          <div className="bg-white rounded-2xl p-8 w-full max-w-md shadow-xl">
            <h2 className="text-xl font-bold text-black mb-6">Editar Cliente</h2>

            <div className="space-y-4">
              <input
                type="text"
                placeholder="Nome"
                value={clienteEditando.nome}
                onChange={(e) => setClienteEditando({ ...clienteEditando, nome: e.target.value })}
                className="w-full p-3 rounded-xl border text-black outline-none focus:ring-2 focus:ring-red-500"
              />
              <input
                type="email"
                placeholder="Email"
                value={clienteEditando.email}
                onChange={(e) => setClienteEditando({ ...clienteEditando, email: e.target.value })}
                className="w-full p-3 rounded-xl border text-black outline-none focus:ring-2 focus:ring-red-500"
              />
            </div>

            <div className="flex gap-3 mt-6">
              <button
                onClick={() => setModalEditar(false)}
                className="flex-1 py-3 rounded-xl border text-black hover:bg-gray-50"
              >
                Cancelar
              </button>
              <button
                onClick={salvarEdicao}
                disabled={salvando}
                className="flex-1 py-3 rounded-xl bg-red-600 text-white hover:bg-red-700 disabled:opacity-50 flex items-center justify-center gap-2"
              >
                {salvando && <Loader2 className="animate-spin" size={16} />}
                Salvar
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
