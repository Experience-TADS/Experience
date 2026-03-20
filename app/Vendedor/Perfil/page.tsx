"use client";

import { useEffect } from "react";
import { useRouter } from "next/navigation";
import Link from "next/link";

import {
  LayoutDashboard,
  Package,
  Users,
  User,
  LogOut
} from "lucide-react";

export default function Perfil() {

  const router = useRouter();

  useEffect(() => {
    const user = localStorage.getItem("user");

    if (!user) {
      router.push("/Login"); // corrigido
    }
  }, [router]);

  function sair() {
    localStorage.removeItem("user");
    router.push("/Login");
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
              className="flex items-center gap-3 text-gray-600 p-3 rounded-xl hover:bg-gray-100"
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
              className="flex items-center gap-3 bg-red-600 text-white p-3 rounded-xl"
            >
              <User size={18} />
              Perfil
            </Link>

          </nav>

        </div>

        <div className="p-4 border-t">
          <button
            onClick={sair}
            className="flex items-center gap-2 text-gray-600 hover:text-red-600"
          >
            <LogOut size={18} />
            Sair
          </button>
        </div>

      </div>


      {/* CONTEÚDO */}
      <div className="flex-1 flex justify-center items-center p-5 md:p-10">

        <div className="bg-white rounded-2xl shadow-sm border p-6 md:p-8 w-full max-w-3xl">

          <h1 className="text-2xl md:text-3xl font-bold text-gray-900 mb-2">
            Perfil
          </h1>

          <p className="text-gray-600 mb-8">
            Suas informações de vendedor
          </p>


          {/* TOPO PERFIL */}

          <div className="flex items-center gap-6 mb-8">

            <div className="bg-red-100 p-6 rounded-xl">
              <User className="text-red-600 w-8 h-8" />
            </div>

            <div>
              <h2 className="text-lg md:text-xl font-bold text-gray-900">
                Ricardo Mendes
              </h2>

              <p className="text-gray-600">
                Vendedor Sênior
              </p>
            </div>

          </div>


          {/* FORMULÁRIO */}

          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">

            <div>
              <label className="block text-sm font-semibold text-gray-800 mb-2">
                Nome
              </label>

              <input
                type="text"
                defaultValue="Ricardo Mendes"
                className="w-full bg-gray-100 border rounded-lg px-4 py-3 text-gray-900"
              />
            </div>

            <div>
              <label className="block text-sm font-semibold text-gray-800 mb-2">
                Email
              </label>

              <input
                type="email"
                defaultValue="ricardo.mendes@toyota.com"
                className="w-full bg-gray-100 border rounded-lg px-4 py-3 text-gray-900"
              />
            </div>

            <div>
              <label className="block text-sm font-semibold text-gray-800 mb-2">
                Telefone
              </label>

              <input
                type="text"
                defaultValue="(11) 99876-5432"
                className="w-full bg-gray-100 border rounded-lg px-4 py-3 text-gray-900"
              />
            </div>

            <div>
              <label className="block text-sm font-semibold text-gray-800 mb-2">
                Concessionária
              </label>

              <input
                type="text"
                defaultValue="Toyota Central SP"
                className="w-full bg-gray-100 border rounded-lg px-4 py-3 text-gray-900"
              />
            </div>

          </div>


          {/* BOTÕES */}

          <div className="flex flex-col md:flex-row justify-end mt-8 gap-4">

            <button
              onClick={sair}
              className="px-6 py-3 rounded-lg border text-gray-700 hover:bg-gray-100 transition"
            >
              Sair
            </button>

            <button
              className="px-6 py-3 bg-red-600 text-white rounded-lg hover:bg-red-700 transition"
            >
              Salvar alterações
            </button>

          </div>

        </div>

      </div>

    </div>
  );
}