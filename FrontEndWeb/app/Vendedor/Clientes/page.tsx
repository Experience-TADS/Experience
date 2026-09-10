"use client";

import { useState, useEffect } from "react";
import { api, type UsuarioResponse } from "@/app/lib/api";
import { Search, Plus, Edit, Trash2, Loader2 } from "lucide-react";
import VendedorSidebar from "@/app/Vendedor/componentes/VendedorSidebar";

type Cliente = UsuarioResponse & { senha?: string };

export default function Clientes() {
  const [busca, setBusca] = useState("");
  const [clientes, setClientes] = useState<Cliente[]>([]);
  const [loading, setLoading] = useState(true);
  const [erro, setErro] = useState("");
  const [modalCadastro, setModalCadastro] = useState(false);
  const [modalEditar, setModalEditar] = useState(false);
  const [clienteEditando, setClienteEditando] = useState<Cliente | null>(null);
  const [salvando, setSalvando] = useState(false);
  const [novoCliente, setNovoCliente] = useState({ nome: "", email: "", senha: "" });

  useEffect(() => { carregarClientes(); }, []);

  async function carregarClientes() {
    setLoading(true); setErro("");
    try { const d = await api.getUsuarios(); setClientes(d.content); }
    catch { setErro("Não foi possível conectar ao servidor."); }
    finally { setLoading(false); }
  }

  const filtrados = clientes.filter((c) =>
    c.nome?.toLowerCase().includes(busca.toLowerCase()) || c.email?.toLowerCase().includes(busca.toLowerCase())
  );

  async function salvarEdicao() {
    if (!clienteEditando) return;
    setSalvando(true);
    try { await api.updateUsuario(clienteEditando.id, clienteEditando); await carregarClientes(); setModalEditar(false); }
    catch { alert("Erro ao atualizar cliente."); }
    finally { setSalvando(false); }
  }

  async function cadastrarCliente() {
    if (!novoCliente.nome || !novoCliente.email || !novoCliente.senha) { alert("Preencha todos os campos."); return; }
    setSalvando(true);
    try { await api.createUsuario(novoCliente); await carregarClientes(); setNovoCliente({ nome: "", email: "", senha: "" }); setModalCadastro(false); }
    catch { alert("Erro ao cadastrar cliente."); }
    finally { setSalvando(false); }
  }

  async function deletarCliente(id: number) {
    if (!confirm("Deseja remover este cliente?")) return;
    try { await api.deleteUsuario(id); await carregarClientes(); }
    catch { alert("Erro ao deletar cliente."); }
  }

  const inputCls = "w-full p-3 rounded-xl border border-gray-200 dark:border-gray-700 bg-white dark:bg-gray-800 text-gray-900 dark:text-white outline-none focus:ring-2 focus:ring-red-500";

  return (
    <div className="flex min-h-screen bg-gray-100 dark:bg-gray-950">
      <VendedorSidebar />

      <div className="flex-1 p-5 md:p-10">
        <div className="flex items-center justify-between mb-6">
          <div>
            <h1 className="text-2xl md:text-3xl font-bold text-gray-900 dark:text-white">Clientes</h1>
            <p className="text-gray-500 mt-1">Gerenciamento de clientes</p>
          </div>
          <div className={`flex items-center gap-2 px-3 py-1 rounded-full text-sm ${erro ? "bg-red-100 text-red-600" : "bg-green-100 text-green-700"}`}>
            <div className={`w-2 h-2 rounded-full ${erro ? "bg-red-500" : "bg-green-500"}`} />
            {erro ? "Sem conexão" : "Conectado"}
          </div>
        </div>

        <div className="flex justify-between items-center mb-8">
          <div className="relative max-w-lg w-full">
            <Search className="absolute left-4 top-3 text-gray-400" size={18} />
            <input type="text" placeholder="Buscar cliente..." value={busca} onChange={(e) => setBusca(e.target.value)}
              className="w-full pl-10 pr-4 py-3 rounded-xl border border-gray-200 dark:border-gray-700 bg-white dark:bg-gray-900 text-gray-900 dark:text-white outline-none focus:ring-2 focus:ring-red-500" />
          </div>
          <button onClick={() => setModalCadastro(true)} className="ml-4 flex items-center gap-2 bg-red-600 text-white px-5 py-3 rounded-xl hover:bg-red-700 transition">
            <Plus size={18} /> Cadastrar Cliente
          </button>
        </div>

        <div className="bg-white dark:bg-gray-900 rounded-2xl border border-gray-100 dark:border-gray-800 shadow overflow-x-auto">
          {loading ? (
            <div className="flex items-center justify-center py-16 gap-3 text-gray-500"><Loader2 className="animate-spin" size={20} /> Carregando...</div>
          ) : erro ? (
            <div className="text-center py-16"><p className="text-red-500 mb-3">{erro}</p><button onClick={carregarClientes} className="text-sm text-red-600 underline">Tentar novamente</button></div>
          ) : filtrados.length === 0 ? (
            <div className="text-center py-16 text-gray-400">Nenhum cliente encontrado.</div>
          ) : (
            <div className="min-w-[600px]">
              <div className="grid grid-cols-4 px-8 py-4 text-gray-400 text-sm border-b border-gray-100 dark:border-gray-800">
                <span>ID</span><span>Nome</span><span>Email</span><span>Ações</span>
              </div>
              {filtrados.map((c) => (
                <div key={c.id} className="grid grid-cols-4 px-8 py-5 items-center border-b border-gray-100 dark:border-gray-800 hover:bg-gray-50 dark:hover:bg-gray-800 transition">
                  <span className="text-gray-400 text-sm">#{c.id}</span>
                  <span className="font-semibold text-gray-900 dark:text-white">{c.nome}</span>
                  <span className="text-gray-500">{c.email}</span>
                  <div className="flex gap-3">
                    <button onClick={() => { setClienteEditando({ ...c }); setModalEditar(true); }} className="text-blue-500 hover:text-blue-700"><Edit size={18} /></button>
                    <button onClick={() => deletarCliente(c.id)} className="text-red-500 hover:text-red-700"><Trash2 size={18} /></button>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>

      {modalCadastro && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
          <div className="bg-white dark:bg-gray-900 rounded-2xl p-8 w-full max-w-md shadow-xl border border-gray-100 dark:border-gray-800">
            <h2 className="text-xl font-bold text-gray-900 dark:text-white mb-6">Novo Cliente</h2>
            <div className="space-y-4">
              <input type="text" placeholder="Nome completo" value={novoCliente.nome} onChange={(e) => setNovoCliente({ ...novoCliente, nome: e.target.value })} className={inputCls} />
              <input type="email" placeholder="Email" value={novoCliente.email} onChange={(e) => setNovoCliente({ ...novoCliente, email: e.target.value })} className={inputCls} />
              <input type="password" placeholder="Senha" value={novoCliente.senha} onChange={(e) => setNovoCliente({ ...novoCliente, senha: e.target.value })} className={inputCls} />
            </div>
            <div className="flex gap-3 mt-6">
              <button onClick={() => setModalCadastro(false)} className="flex-1 py-3 rounded-xl border border-gray-200 dark:border-gray-700 text-gray-700 dark:text-gray-300 hover:bg-gray-50 dark:hover:bg-gray-800">Cancelar</button>
              <button onClick={cadastrarCliente} disabled={salvando} className="flex-1 py-3 rounded-xl bg-red-600 text-white hover:bg-red-700 disabled:opacity-50 flex items-center justify-center gap-2">
                {salvando && <Loader2 className="animate-spin" size={16} />} Salvar
              </button>
            </div>
          </div>
        </div>
      )}

      {modalEditar && clienteEditando && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
          <div className="bg-white dark:bg-gray-900 rounded-2xl p-8 w-full max-w-md shadow-xl border border-gray-100 dark:border-gray-800">
            <h2 className="text-xl font-bold text-gray-900 dark:text-white mb-6">Editar Cliente</h2>
            <div className="space-y-4">
              <input type="text" placeholder="Nome" value={clienteEditando.nome} onChange={(e) => setClienteEditando({ ...clienteEditando, nome: e.target.value })} className={inputCls} />
              <input type="email" placeholder="Email" value={clienteEditando.email} onChange={(e) => setClienteEditando({ ...clienteEditando, email: e.target.value })} className={inputCls} />
            </div>
            <div className="flex gap-3 mt-6">
              <button onClick={() => setModalEditar(false)} className="flex-1 py-3 rounded-xl border border-gray-200 dark:border-gray-700 text-gray-700 dark:text-gray-300 hover:bg-gray-50 dark:hover:bg-gray-800">Cancelar</button>
              <button onClick={salvarEdicao} disabled={salvando} className="flex-1 py-3 rounded-xl bg-red-600 text-white hover:bg-red-700 disabled:opacity-50 flex items-center justify-center gap-2">
                {salvando && <Loader2 className="animate-spin" size={16} />} Salvar
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
