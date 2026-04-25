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
} from "lucide-react";

export default function Administracao() {
  const [status, setStatus] = useState<"loading" | "admin" | "blocked">("loading");

  const [vendedores, setVendedores] = useState([
    { id: 1, nome: "Ricardo Lima", email: "ricardo@toyota.com", role: "vendedor", ativo: true },
    { id: 2, nome: "Ana Costa", email: "ana@toyota.com", role: "vendedor", ativo: true },
    { id: 3, nome: "Lucas Martins", email: "lucas@toyota.com", role: "vendedor", ativo: true },
    { id: 4, nome: "Juliana Alves", email: "juliana@toyota.com", role: "vendedor", ativo: true },
    { id: 5, nome: "Carlos Souza", email: "carlos@toyota.com", role: "vendedor", ativo: true },
  ]);

  const [nome, setNome] = useState("");
  const [email, setEmail] = useState("");

  // MODAL
  const [modalAtivo, setModalAtivo] = useState(false);
  const [usuarioSelecionado, setUsuarioSelecionado] = useState<number | null>(null);

  useEffect(() => {
    const role = localStorage.getItem("userRole");

    setTimeout(() => {
      if (role === "admin") setStatus("admin");
      else setStatus("blocked");
    }, 800);
  }, []);

  function adicionar() {
    if (!nome || !email) return;

    const novo = {
      id: Date.now(),
      nome,
      email,
      role: "vendedor",
      ativo: true,
    };

    setVendedores([...vendedores, novo]);
    setNome("");
    setEmail("");
  }

  function abrirModal(id: number) {
    setUsuarioSelecionado(id);
    setModalAtivo(true);
  }

  function confirmarToggle() {
    if (!usuarioSelecionado) return;

    setVendedores((prev) =>
      prev.map((v) =>
        v.id === usuarioSelecionado ? { ...v, ativo: !v.ativo } : v
      )
    );

    setModalAtivo(false);
    setUsuarioSelecionado(null);
  }

  if (status === "loading") {
    return (
      <div className="flex items-center justify-center min-h-screen bg-gray-100">
        <div className="w-10 h-10 border-4 border-red-600 border-t-transparent rounded-full animate-spin"></div>
      </div>
    );
  }

  if (status === "blocked") {
    return (
      <div className="flex flex-col items-center justify-center min-h-screen bg-gray-100 text-center">
        <h1 className="text-2xl font-bold text-red-600">Acesso negado 🚫</h1>
        <p className="text-gray-600 mt-2">
          Apenas administradores podem acessar essa página.
        </p>

        <Link
          href="/Vendedor/Dashbord"
          className="mt-4 bg-red-600 text-white px-6 py-2 rounded-lg"
        >
          Voltar ao Dashboard
        </Link>
      </div>
    );
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
              <p className="text-sm text-gray-500">Administração</p>
            </div>
          </div>

          <nav className="flex flex-col gap-2 px-4">

            <Link href="/Vendedor/Dashbord" className="flex items-center gap-3 text-gray-700 p-3 rounded-xl hover:bg-gray-100">
              <LayoutDashboard size={18} />
              Dashboard
            </Link>

            <Link href="/Vendedor/Pedidos" className="flex items-center gap-3 text-gray-700 p-3 rounded-xl hover:bg-gray-100">
              <Package size={18} />
              Pedidos
            </Link>

            <Link href="/Vendedor/Clientes" className="flex items-center gap-3 text-gray-700 p-3 rounded-xl hover:bg-gray-100">
              <Users size={18} />
              Clientes
            </Link>

            <Link href="/Vendedor/Perfil" className="flex items-center gap-3 text-gray-700 p-3 rounded-xl hover:bg-gray-100">
              <User size={18} />
              Perfil
            </Link>

            <Link href="/Vendedor/Administracao" className="flex items-center gap-3 bg-red-600 text-white p-3 rounded-xl">
              <Shield size={18} />
              Administração
            </Link>

          </nav>
        </div>

        <div className="p-4 border-t">
          <Link href="/login" className="flex items-center gap-2 text-gray-600 hover:text-red-600">
            <LogOut size={18} />
            Sair
          </Link>
        </div>
      </div>

      {/* CONTEÚDO */}
      <div className="flex-1 p-6 md:p-10 space-y-6">

        <h1 className="text-2xl font-bold text-gray-900">
          Painel Administrativo 👑
        </h1>

        {/* FORM */}
        <div className="bg-white p-4 rounded-xl shadow flex gap-4">
          <input
            value={nome}
            onChange={(e) => setNome(e.target.value)}
            placeholder="Nome"
            className="border p-3 rounded w-full"
          />

          <input
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            placeholder="Email"
            className="border p-3 rounded w-full"
          />

          <button
            onClick={adicionar}
            className="bg-red-600 text-white px-6 rounded"
          >
            Adicionar
          </button>
        </div>

        {/* TABELA */}
        <div className="bg-white rounded-xl shadow p-6">
          <table className="w-full text-sm">

            <thead>
              <tr className="text-left text-gray-500 border-b">
                <th>Nome</th>
                <th>Email</th>
                <th>Status</th>
                <th>Ações</th>
              </tr>
            </thead>

            <tbody>
              {vendedores.map((v) => (
                <tr key={v.id} className="border-b">

                  <td className="py-3 font-semibold">{v.nome}</td>
                  <td>{v.email}</td>

                  <td>
                    <span className={`px-3 py-1 rounded-full text-xs ${
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
        </div>
      </div>

      {/* MODAL */}
      {modalAtivo && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center">
          <div className="bg-white p-6 rounded-xl w-96 text-center">

            <h2 className="text-lg font-bold mb-4">
              Deseja realmente alterar o status deste usuário?
            </h2>

            <div className="flex gap-4 justify-center">
              <button
                onClick={() => setModalAtivo(false)}
                className="px-4 py-2 border rounded"
              >
                Não
              </button>

              <button
                onClick={confirmarToggle}
                className="px-4 py-2 bg-red-600 text-white rounded"
              >
                Sim
              </button>
            </div>

          </div>
        </div>
      )}

    </div>
  );
}