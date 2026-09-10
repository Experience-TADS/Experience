"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import { Search, Loader2 } from "lucide-react";
import { api, removeToken, type UsuarioResponse } from "@/app/lib/api";
import VendedorSidebar from "@/app/Vendedor/componentes/VendedorSidebar";

export default function Administracao() {
  const [status, setStatus] = useState<"loading" | "admin" | "blocked">("loading");
  const [usuarios, setUsuarios] = useState<UsuarioResponse[]>([]);
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState("");
  const [busca, setBusca] = useState("");
  const [filtroStatus, setFiltroStatus] = useState("todos");
  const [paginaAtual, setPaginaAtual] = useState(1);
  const [modalAtivo, setModalAtivo] = useState(false);
  const [usuarioSelecionado, setUsuarioSelecionado] = useState<UsuarioResponse | null>(null);
  const itensPorPagina = 5;

  useEffect(() => {
    const role = localStorage.getItem("userRole");
    if (role === "admin") { setStatus("admin"); carregarUsuarios(); }
    else setStatus("blocked");
  }, []);

  async function carregarUsuarios() {
    setCarregando(true); setErro("");
    try { const d = await api.getUsuarios(0, 100); setUsuarios(d.content); }
    catch { setErro("Não foi possível carregar os usuários."); }
    finally { setCarregando(false); }
  }

  async function confirmarToggle() {
    if (!usuarioSelecionado) return;
    try {
      if (usuarioSelecionado.ativo) await api.desativarUsuario(usuarioSelecionado.id);
      else await api.ativarUsuario(usuarioSelecionado.id);
      await carregarUsuarios();
    } catch { alert("Erro ao alterar status."); }
    finally { setModalAtivo(false); setUsuarioSelecionado(null); }
  }

  if (status === "loading") return (
    <div className="flex items-center justify-center min-h-screen gap-2 text-gray-500 dark:bg-gray-950">
      <Loader2 className="animate-spin" size={20} /> Carregando...
    </div>
  );

  if (status === "blocked") return (
    <div className="flex flex-col items-center justify-center min-h-screen text-center dark:bg-gray-950">
      <h1 className="text-2xl font-bold text-red-600">Acesso negado</h1>
      <p className="mt-2 text-gray-500">Você não tem permissão para acessar esta página.</p>
      <Link href="/Vendedor/Dashbord" className="mt-4 bg-red-600 text-white px-6 py-2 rounded-lg">Voltar</Link>
    </div>
  );

  const filtrados = usuarios.filter((v) => {
    const m = v.nome?.toLowerCase().includes(busca.toLowerCase()) || v.email?.toLowerCase().includes(busca.toLowerCase());
    const s = filtroStatus === "todos" || (filtroStatus === "ativo" && v.ativo) || (filtroStatus === "inativo" && !v.ativo);
    return m && s;
  });

  const totalPaginas = Math.ceil(filtrados.length / itensPorPagina);
  const paginados = filtrados.slice((paginaAtual - 1) * itensPorPagina, paginaAtual * itensPorPagina);

  return (
    <div className="flex min-h-screen bg-gray-100 dark:bg-gray-950">
      <VendedorSidebar />

      <div className="flex-1 p-8 space-y-6">
        <div className="flex items-center justify-between">
          <h1 className="text-2xl font-bold text-gray-900 dark:text-white">Painel Administrativo</h1>
          <div className={`flex items-center gap-2 px-3 py-1 rounded-full text-sm ${erro ? "bg-red-100 text-red-600" : "bg-green-100 text-green-700"}`}>
            <div className={`w-2 h-2 rounded-full ${erro ? "bg-red-500" : "bg-green-500"}`} />
            {erro ? "Sem conexão" : "Conectado"}
          </div>
        </div>

        <div className="bg-white dark:bg-gray-900 p-4 rounded-xl border border-gray-100 dark:border-gray-800 shadow flex flex-col md:flex-row gap-4 items-center justify-between">
          <div className="relative w-full">
            <Search className="absolute left-3 top-3 text-gray-400" size={18} />
            <input value={busca} onChange={(e) => { setBusca(e.target.value); setPaginaAtual(1); }} placeholder="Buscar usuário..."
              className="w-full pl-10 p-3 border border-gray-200 dark:border-gray-700 rounded-lg bg-white dark:bg-gray-800 text-gray-900 dark:text-white outline-none focus:ring-2 focus:ring-red-500" />
          </div>
          <div className="flex gap-2">
            {["todos", "ativo", "inativo"].map((f) => (
              <button key={f} onClick={() => { setFiltroStatus(f); setPaginaAtual(1); }}
                className={`px-4 py-2 rounded-full text-sm transition ${filtroStatus === f ? "bg-red-600 text-white" : "bg-gray-100 dark:bg-gray-800 text-gray-600 dark:text-gray-400 hover:bg-gray-200 dark:hover:bg-gray-700"}`}>
                {f === "todos" ? "Todos" : f === "ativo" ? "Ativos" : "Inativos"}
              </button>
            ))}
          </div>
        </div>

        <div className="bg-white dark:bg-gray-900 p-6 rounded-xl border border-gray-100 dark:border-gray-800 shadow">
          {carregando ? (
            <div className="flex items-center justify-center py-12 gap-2 text-gray-500"><Loader2 className="animate-spin" size={20} /> Carregando...</div>
          ) : erro ? (
            <div className="text-center py-12"><p className="text-red-500 mb-3">{erro}</p><button onClick={carregarUsuarios} className="text-sm text-red-600 underline">Tentar novamente</button></div>
          ) : (
            <table className="w-full text-sm">
              <thead>
                <tr className="border-b border-gray-100 dark:border-gray-800 text-left text-gray-400">
                  <th className="pb-3">Nome</th><th className="pb-3">Email</th><th className="pb-3">Role</th><th className="pb-3">Status</th><th className="pb-3">Ações</th>
                </tr>
              </thead>
              <tbody>
                {paginados.map((v) => (
                  <tr key={v.id} className="border-b border-gray-100 dark:border-gray-800 hover:bg-gray-50 dark:hover:bg-gray-800 transition">
                    <td className="py-3 font-semibold text-gray-900 dark:text-white">{v.nome}</td>
                    <td className="text-gray-500">{v.email}</td>
                    <td><span className="text-xs bg-gray-100 dark:bg-gray-800 text-gray-600 dark:text-gray-400 px-2 py-1 rounded-full">{v.role ?? "—"}</span></td>
                    <td><span className={`px-3 py-1 rounded-full text-xs font-semibold ${v.ativo ? "bg-green-100 text-green-700" : "bg-red-100 text-red-700"}`}>{v.ativo ? "Ativo" : "Inativo"}</span></td>
                    <td><button onClick={() => { setUsuarioSelecionado(v); setModalAtivo(true); }} className={`text-sm ${v.ativo ? "text-red-600" : "text-green-600"}`}>{v.ativo ? "Desativar" : "Ativar"}</button></td>
                  </tr>
                ))}
                {paginados.length === 0 && <tr><td colSpan={5} className="text-center text-gray-400 py-8">Nenhum usuário encontrado.</td></tr>}
              </tbody>
            </table>
          )}

          <div className="flex justify-center gap-4 mt-4">
            <button onClick={() => setPaginaAtual((p) => Math.max(p - 1, 1))} disabled={paginaAtual === 1} className="px-3 py-1 border border-gray-200 dark:border-gray-700 rounded text-gray-600 dark:text-gray-400 disabled:opacity-40">Anterior</button>
            <span className="text-gray-500 text-sm self-center">Página {paginaAtual} de {totalPaginas || 1}</span>
            <button onClick={() => setPaginaAtual((p) => Math.min(p + 1, totalPaginas))} disabled={paginaAtual >= totalPaginas} className="px-3 py-1 border border-gray-200 dark:border-gray-700 rounded text-gray-600 dark:text-gray-400 disabled:opacity-40">Próxima</button>
          </div>
        </div>
      </div>

      {modalAtivo && usuarioSelecionado && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center">
          <div className="bg-white dark:bg-gray-900 p-6 rounded-xl text-center border border-gray-100 dark:border-gray-800 shadow-xl">
            <h2 className="mb-2 font-bold text-gray-900 dark:text-white">Alterar status do usuário?</h2>
            <p className="text-sm text-gray-500 mb-4">{usuarioSelecionado.nome} será {usuarioSelecionado.ativo ? "desativado" : "ativado"}.</p>
            <div className="flex gap-4 justify-center">
              <button onClick={() => { setModalAtivo(false); setUsuarioSelecionado(null); }} className="border border-gray-200 dark:border-gray-700 px-4 py-2 rounded text-gray-600 dark:text-gray-300">Cancelar</button>
              <button onClick={confirmarToggle} className="bg-red-600 text-white px-4 py-2 rounded hover:bg-red-700">Confirmar</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
