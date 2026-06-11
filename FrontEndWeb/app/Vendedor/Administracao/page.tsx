"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import {
  LayoutDashboard,
  Package,
  Users,
  User,
  LogOut,
  Shield,
  Search
} from "lucide-react";

export default function Administracao() {
  const [status, setStatus] = useState<"loading" | "admin" | "blocked">("loading");

  const [vendedores, setVendedores] = useState([
    { id: 1, nome: "Ricardo Lima", email: "ricardo@toyota.com", ativo: true },
    { id: 2, nome: "Ana Costa", email: "ana@toyota.com", ativo: true },
    { id: 3, nome: "Lucas Martins", email: "lucas@toyota.com", ativo: true },
    { id: 4, nome: "Juliana Alves", email: "juliana@toyota.com", ativo: true },
    { id: 5, nome: "Carlos Souza", email: "carlos@toyota.com", ativo: true },
    { id: 6, nome: "Mariana Silva", email: "mariana@toyota.com", ativo: false },
  ]);

  const [nome, setNome] = useState("");
  const [email, setEmail] = useState("");

  const [modalAtivo, setModalAtivo] = useState(false);
  const [usuarioSelecionado, setUsuarioSelecionado] = useState<number | null>(null);

  const [busca, setBusca] = useState("");
  const [filtroStatus, setFiltroStatus] = useState("todos");
  const [paginaAtual, setPaginaAtual] = useState(1);

  const itensPorPagina = 5;

  useEffect(() => {
    const role = localStorage.getItem("userRole");

    setTimeout(() => {
      if (role === "admin") setStatus("admin");
      else setStatus("blocked");
    }, 800);
  }, []);

  if (status === "loading") {
    return <div className="flex items-center justify-center min-h-screen">Carregando...</div>;
  }

  if (status === "blocked") {
    return (
      <div className="flex flex-col items-center justify-center min-h-screen text-center">
        <h1 className="text-2xl font-bold text-red-600">Acesso negado 🚫</h1>
        <p className="mt-2">Você não tem permissão para acessar esta página.</p>

        <Link href="/Vendedor/Dashbord" className="mt-4 bg-red-600 text-white px-6 py-2 rounded">
          Voltar
        </Link>
      </div>
    );
  }

  function adicionar() {
    if (!nome || !email) return;

    setVendedores([
      ...vendedores,
      { id: Date.now(), nome, email, ativo: true }
    ]);

    setNome("");
    setEmail("");
  }

  function abrirModal(id: number) {
    setUsuarioSelecionado(id);
    setModalAtivo(true);
  }

  function confirmarToggle() {
    if (usuarioSelecionado === null) return;

    setVendedores((prev) =>
      prev.map((v) =>
        v.id === usuarioSelecionado ? { ...v, ativo: !v.ativo } : v
      )
    );

    setModalAtivo(false);
    setUsuarioSelecionado(null);
  }

  function cancelarModal() {
    setModalAtivo(false);
    setUsuarioSelecionado(null);
  }

  const vendedoresFiltrados = vendedores.filter((v) => {
    const matchBusca =
      v.nome.toLowerCase().includes(busca.toLowerCase()) ||
      v.email.toLowerCase().includes(busca.toLowerCase());

    const matchStatus =
      filtroStatus === "todos" ||
      (filtroStatus === "ativo" && v.ativo) ||
      (filtroStatus === "inativo" && !v.ativo);

    return matchBusca && matchStatus;
  });

  const totalPaginas = Math.ceil(vendedoresFiltrados.length / itensPorPagina);
  const inicio = (paginaAtual - 1) * itensPorPagina;
  const vendedoresPaginados = vendedoresFiltrados.slice(inicio, inicio + itensPorPagina);

  return (
    <div className="flex min-h-screen bg-gray-100 text-black">

      {/* SIDEBAR */}
      <div className="hidden md:flex w-64 bg-white flex-col justify-between">
        <div>
          <div className="flex items-center gap-3 p-6">
            <div className="bg-red-600 text-white p-3 rounded-lg">
              <Package size={20} />
            </div>
            <div>
              <p className="font-bold">Toyota</p>
              <p className="text-sm">Administração</p>
            </div>
          </div>

          <nav className="flex flex-col gap-2 px-4">
            <Link href="Dashbord" className="flex items-center gap-3 p-3 rounded-xl hover:bg-gray-100">
              <LayoutDashboard size={18} /> Dashboard
            </Link>
            <Link href="Pedidos" className="flex items-center gap-3 p-3 rounded-xl hover:bg-gray-100">
              <Package size={18} /> Pedidos
            </Link>
            <Link href="Clientes" className="flex items-center gap-3 p-3 rounded-xl hover:bg-gray-100">
              <Users size={18} /> Clientes
            </Link>
            <Link href="Perfil" className="flex items-center gap-3 p-3 rounded-xl hover:bg-gray-100">
              <User size={18} /> Perfil
            </Link>
            <Link href="Administracao" className="flex items-center gap-3 bg-red-600 text-white p-3 rounded-xl">
              <Shield size={18} /> Administração
            </Link>
          </nav>
        </div>

        <div className="p-4">
          <Link
            href="/Login"
            onClick={() => localStorage.removeItem("userRole")}
            className="flex items-center gap-2 hover:text-red-600"
          >
            <LogOut size={18} /> Sair
          </Link>
        </div>
      </div>

      {/* CONTEÚDO */}
      <div className="flex-1 p-8 space-y-6">

        <h1 className="text-2xl font-bold">Painel Administrativo</h1>

        {/* FORM */}
        <div className="bg-white p-4 rounded-xl shadow flex gap-4">
          <input value={nome} onChange={(e) => setNome(e.target.value)} placeholder="Nome" className="border p-3 rounded w-full" />
          <input value={email} onChange={(e) => setEmail(e.target.value)} placeholder="Email" className="border p-3 rounded w-full" />
          <button onClick={adicionar} className="bg-red-600 text-white px-6 rounded">Adicionar</button>
        </div>

        {/* FILTRO */}
        <div className="bg-white p-4 rounded-xl shadow flex flex-col md:flex-row gap-4 items-center justify-between">

          <div className="relative w-full">
            <Search className="absolute left-3 top-3 text-gray-400" size={18} />
            <input
              value={busca}
              onChange={(e) => setBusca(e.target.value)}
              placeholder="Buscar vendedor..."
              className="w-full pl-10 p-3 border rounded-lg outline-none focus:ring-2 focus:ring-red-500"
            />
          </div>

          <div className="flex gap-2">
            {["todos", "ativo", "inativo"].map((f) => (
              <button
                key={f}
                onClick={() => setFiltroStatus(f)}
                className={`px-4 py-2 rounded-full text-sm ${
                  filtroStatus === f
                    ? "bg-red-600 text-white"
                    : "bg-gray-100 hover:bg-gray-200"
                }`}
              >
                {f === "todos" ? "Todos" : f === "ativo" ? "Ativos" : "Inativos"}
              </button>
            ))}
          </div>
        </div>

        {/* TABELA */}
        <div className="bg-white p-6 rounded-xl shadow">
          <table className="w-full text-sm">
            <thead>
              <tr className="border-b border-gray-200 text-left">
                <th>Nome</th>
                <th>Email</th>
                <th>Status</th>
                <th>Ações</th>
              </tr>
            </thead>

            <tbody>
              {vendedoresPaginados.map((v) => (
                <tr key={v.id} className="border-b border-gray-200 hover:bg-gray-50">
                  <td className="py-3 font-semibold">{v.nome}</td>
                  <td>{v.email}</td>

                  <td>
                    <span className={`px-3 py-1 rounded-full text-xs font-semibold ${
                      v.ativo
                        ? "bg-green-100 text-green-700"
                        : "bg-red-100 text-red-700"
                    }`}>
                      {v.ativo ? "Ativo" : "Inativo"}
                    </span>
                  </td>

                  <td>
                    <button
                      onClick={() => abrirModal(v.id)}
                      className={`text-sm ${
                        v.ativo ? "text-red-600" : "text-green-600"
                      }`}
                    >
                      {v.ativo ? "Desativar" : "Ativar"}
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>

          <div className="flex justify-center gap-4 mt-4">
            <button onClick={() => setPaginaAtual(p => Math.max(p - 1, 1))} className="px-3 py-1 border rounded">
              Anterior
            </button>

            <span>Página {paginaAtual} de {totalPaginas || 1}</span>

            <button onClick={() => setPaginaAtual(p => Math.min(p + 1, totalPaginas))} className="px-3 py-1 border rounded">
              Próxima
            </button>
          </div>
        </div>
      </div>

      {/* MODAL */}
      {modalAtivo && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center">
          <div className="bg-white p-6 rounded-xl text-center">
            <h2 className="mb-4 font-bold">
              Deseja alterar o status deste usuário?
            </h2>

            <div className="flex gap-4 justify-center">
              <button onClick={cancelarModal} className="border px-4 py-2 rounded">
                Cancelar
              </button>

              <button onClick={confirmarToggle} className="bg-red-600 text-white px-4 py-2 rounded">
                Confirmar
              </button>
            </div>
          </div>
        </div>
      )}

    </div>
  );
}