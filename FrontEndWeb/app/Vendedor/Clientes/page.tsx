"use client";

import { useState, useEffect, useCallback } from "react";
import Link from "next/link";
import {
  LayoutDashboard, Package, Users, User, LogOut,
  Search, Shield, Plus, Edit, Trash2, Loader2, ChevronLeft, ChevronRight,
} from "lucide-react";

const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || "http://54.91.247.129";
const PAGE_SIZE = 10;

type Cliente = {
  id: number;
  nome: string;
  email: string;
  role?: string;
  ativo?: boolean;
};

// ── COMPONENTE DE PAGINAÇÃO ────────────────────────────────────────────────────
function Paginacao({
  pagina,
  total,
  onChange,
}: {
  pagina: number;
  total: number;
  onChange: (p: number) => void;
}) {
  if (total <= 1) return null;

  // Gera os números visíveis com reticências
  function getPages(): (number | "...")[] {
    if (total <= 7) return Array.from({ length: total }, (_, i) => i);

    const pages: (number | "...")[] = [];
    pages.push(0);

    if (pagina > 3) pages.push("...");

    const start = Math.max(1, pagina - 1);
    const end = Math.min(total - 2, pagina + 1);
    for (let i = start; i <= end; i++) pages.push(i);

    if (pagina < total - 4) pages.push("...");
    pages.push(total - 1);

    return pages;
  }

  return (
    <div className="flex items-center justify-center gap-1 mt-6 flex-wrap">
      {/* PREV */}
      <button
        onClick={() => onChange(pagina - 1)}
        disabled={pagina === 0}
        className="flex items-center gap-1 px-3 py-2 rounded-xl border bg-white text-gray-600 text-sm font-medium hover:bg-gray-50 disabled:opacity-40 transition"
      >
        <ChevronLeft size={15} /> PREV
      </button>

      {/* NÚMEROS */}
      {getPages().map((p, i) =>
        p === "..." ? (
          <span key={`dots-${i}`} className="px-2 py-2 text-gray-400 text-sm select-none">
            ...
          </span>
        ) : (
          <button
            key={p}
            onClick={() => onChange(p as number)}
            className={`w-9 h-9 rounded-xl text-sm font-semibold border transition ${
              p === pagina
                ? "bg-red-600 text-white border-red-600 shadow"
                : "bg-white text-gray-700 border-gray-200 hover:bg-gray-50"
            }`}
          >
            {(p as number) + 1}
          </button>
        )
      )}

      {/* NEXT */}
      <button
        onClick={() => onChange(pagina + 1)}
        disabled={pagina >= total - 1}
        className="flex items-center gap-1 px-3 py-2 rounded-xl border bg-white text-gray-600 text-sm font-medium hover:bg-gray-50 disabled:opacity-40 transition"
      >
        NEXT <ChevronRight size={15} />
      </button>
    </div>
  );
}

// ── PÁGINA PRINCIPAL ──────────────────────────────────────────────────────────
export default function Clientes() {
  const [busca, setBusca] = useState("");
  const [clientes, setClientes] = useState<Cliente[]>([]);
  const [loading, setLoading] = useState(true);
  const [erro, setErro] = useState("");
  const [pagina, setPagina] = useState(0);
  const [totalPaginas, setTotalPaginas] = useState(1);
  const [totalItens, setTotalItens] = useState(0);

  const [modalCadastro, setModalCadastro] = useState(false);
  const [modalEditar, setModalEditar] = useState(false);
  const [clienteEditando, setClienteEditando] = useState<Cliente | null>(null);
  const [salvando, setSalvando] = useState(false);
  const [erroModal, setErroModal] = useState("");

  const [novoCliente, setNovoCliente] = useState({ nome: "", email: "", senha: "" });

  const carregarClientes = useCallback(async (page: number, termo: string) => {
    setLoading(true);
    setErro("");
    try {
      const token = localStorage.getItem("token");
      const url = `${API_BASE_URL}/api/usuario?page=${page}&size=${PAGE_SIZE}&sort=id,desc`;
      const res = await fetch(url, {
        headers: { Authorization: `Bearer ${token}` },
      });
      if (!res.ok) throw new Error();
      const data = await res.json();

      // Suporte a resposta paginada Spring Boot { content, totalPages, totalElements }
      const lista: Cliente[] = data.content ?? (Array.isArray(data) ? data : []);

      // Filtra localmente se houver busca (quando paginado no servidor, filtra na página atual)
      const filtrados = termo
        ? lista.filter(
            (c) =>
              c.nome?.toLowerCase().includes(termo.toLowerCase()) ||
              c.email?.toLowerCase().includes(termo.toLowerCase())
          )
        : lista;

      setClientes(filtrados);
      setTotalPaginas(data.totalPages ?? 1);
      setTotalItens(data.totalElements ?? filtrados.length);
    } catch {
      setErro("Não foi possível conectar ao servidor AWS.");
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    carregarClientes(pagina, busca);
  }, [pagina, carregarClientes]);

  // Busca com debounce — volta pra página 0 ao buscar
  useEffect(() => {
    const timer = setTimeout(() => {
      setPagina(0);
      carregarClientes(0, busca);
    }, 400);
    return () => clearTimeout(timer);
  }, [busca]);

  function abrirEditar(cliente: Cliente) {
    setClienteEditando({ ...cliente });
    setErroModal("");
    setModalEditar(true);
  }

  async function salvarEdicao() {
    if (!clienteEditando) return;
    setSalvando(true);
    setErroModal("");
    try {
      const token = localStorage.getItem("token");
      const res = await fetch(`${API_BASE_URL}/api/usuario/${clienteEditando.id}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json", Authorization: `Bearer ${token}` },
        body: JSON.stringify(clienteEditando),
      });
      if (!res.ok) throw new Error();
      await carregarClientes(pagina, busca);
      setModalEditar(false);
    } catch {
      setErroModal("Erro ao atualizar cliente. Tente novamente.");
    } finally {
      setSalvando(false);
    }
  }

  async function cadastrarCliente() {
    if (!novoCliente.nome || !novoCliente.email || !novoCliente.senha) {
      setErroModal("Preencha todos os campos.");
      return;
    }
    setSalvando(true);
    setErroModal("");
    try {
      const token = localStorage.getItem("token");
      const res = await fetch(`${API_BASE_URL}/api/usuario`, {
        method: "POST",
        headers: { "Content-Type": "application/json", Authorization: `Bearer ${token}` },
        body: JSON.stringify(novoCliente),
      });
      if (!res.ok) {
        const body = await res.json().catch(() => null);
        const msg = body?.message || "";
        if (msg.toLowerCase().includes("email")) {
          setErroModal("E-mail já cadastrado.");
        } else {
          setErroModal("Erro ao cadastrar. Verifique os dados.");
        }
        return;
      }
      await carregarClientes(pagina, busca);
      setNovoCliente({ nome: "", email: "", senha: "" });
      setModalCadastro(false);
    } catch {
      setErroModal("Erro de conexão. Tente novamente.");
    } finally {
      setSalvando(false);
    }
  }

  async function deletarCliente(id: number) {
    if (!confirm("Deseja remover este cliente?")) return;
    try {
      const token = localStorage.getItem("token");
      await fetch(`${API_BASE_URL}/api/usuario/${id}`, {
        method: "DELETE",
        headers: { Authorization: `Bearer ${token}` },
      });
      await carregarClientes(pagina, busca);
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
            <Link href="/Vendedor/Pedidos" className="flex items-center gap-3 text-gray-600 p-3 rounded-xl hover:bg-gray-100">
              <Package size={18} /> Pedidos
            </Link>
            <Link href="/Vendedor/Clientes" className="flex items-center gap-3 bg-red-600 text-white p-3 rounded-xl">
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
      <div className="flex-1 p-5 md:p-10">

        {/* HEADER */}
        <div className="flex items-center justify-between">
          <div>
            <h1 className="text-2xl md:text-3xl font-bold text-black">Clientes</h1>
            <p className="text-gray-500 mt-1">
              {totalItens > 0 ? `${totalItens} clientes cadastrados` : "Dados sincronizados com a AWS"}
            </p>
          </div>
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
              placeholder="Buscar por nome ou email..."
              value={busca}
              onChange={(e) => setBusca(e.target.value)}
              className="w-full pl-10 pr-4 py-3 rounded-xl border bg-white text-black outline-none focus:ring-2 focus:ring-red-500"
            />
          </div>
          <button
            onClick={() => { setErroModal(""); setModalCadastro(true); }}
            className="ml-4 flex items-center gap-2 bg-red-600 text-white px-5 py-3 rounded-xl hover:bg-red-700 whitespace-nowrap"
          >
            <Plus size={18} /> Cadastrar
          </button>
        </div>

        {/* TABELA */}
        <div className="mt-6 bg-white rounded-2xl shadow border overflow-x-auto">
          {loading ? (
            <div className="flex items-center justify-center py-16 gap-3 text-gray-500">
              <Loader2 className="animate-spin" size={20} /> Buscando dados na AWS...
            </div>
          ) : erro ? (
            <div className="text-center py-16">
              <p className="text-red-500 mb-3">{erro}</p>
              <button onClick={() => carregarClientes(pagina, busca)} className="text-sm text-red-600 underline">
                Tentar novamente
              </button>
            </div>
          ) : clientes.length === 0 ? (
            <div className="text-center py-16 text-gray-400">Nenhum cliente encontrado.</div>
          ) : (
            <div className="min-w-[600px]">
              <div className="grid grid-cols-5 px-8 py-4 text-gray-400 text-xs font-semibold uppercase tracking-wider border-b bg-gray-50">
                <span>#</span>
                <span className="col-span-2">Nome</span>
                <span>Email</span>
                <span className="text-right">Ações</span>
              </div>
              {clientes.map((cliente) => (
                <div key={cliente.id} className="grid grid-cols-5 px-8 py-4 items-center border-b hover:bg-gray-50 transition">
                  <span className="text-gray-400 text-sm">#{cliente.id}</span>
                  <span className="col-span-2 font-semibold text-black">{cliente.nome || "—"}</span>
                  <span className="text-gray-600 text-sm truncate pr-4">{cliente.email || "—"}</span>
                  <div className="flex gap-3 justify-end">
                    <button
                      onClick={() => abrirEditar(cliente)}
                      title="Editar"
                      className="text-blue-500 hover:text-blue-700 transition"
                    >
                      <Edit size={17} />
                    </button>
                    <button
                      onClick={() => deletarCliente(cliente.id)}
                      title="Remover"
                      className="text-red-400 hover:text-red-600 transition"
                    >
                      <Trash2 size={17} />
                    </button>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>

        {/* PAGINAÇÃO */}
        {!loading && !erro && (
          <Paginacao pagina={pagina} total={totalPaginas} onChange={setPagina} />
        )}

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
            {erroModal && <p className="text-red-500 text-sm mt-3">{erroModal}</p>}
            <div className="flex gap-3 mt-6">
              <button onClick={() => setModalCadastro(false)} className="flex-1 py-3 rounded-xl border text-black hover:bg-gray-50">
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
            {erroModal && <p className="text-red-500 text-sm mt-3">{erroModal}</p>}
            <div className="flex gap-3 mt-6">
              <button onClick={() => setModalEditar(false)} className="flex-1 py-3 rounded-xl border text-black hover:bg-gray-50">
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
