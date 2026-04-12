"use client";

import { useState } from "react";
import Link from "next/link";
import {
  LayoutDashboard,
  Package,
  Users,
  User,
  LogOut,
  Shield,
  Calendar,
} from "lucide-react";

export default function Perfil() {
  const [form, setForm] = useState({
    nome: "Ricardo Mendes",
    email: "ricardo.mendes@toyota.com",
    telefone: "(11) 99999-0000",
    concessionaria: "Toyota Central São Paulo",
  });

  // ✅ NOVO STATE PARA PREFERÊNCIAS
  const [prefs, setPrefs] = useState({
    emailNotif: true,
    smsNotif: false,
    promo: false,
  });

  // ✅ FUNÇÃO PARA ALTERAR
  function updatePref(key: string) {
    setPrefs((prev) => ({
      ...prev,
      [key]: !prev[key as keyof typeof prev],
    }));
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
              <p className="text-sm text-gray-500">Painel do Vendedor</p>
            </div>
          </div>

          <nav className="flex flex-col gap-2 px-4">

            <Link href="/Vendedor/Dashboard" className="flex items-center gap-3 text-gray-600 p-3 rounded-xl hover:bg-gray-100">
              <LayoutDashboard size={18} />
              Dashboard
            </Link>

            <Link href="/Vendedor/Pedidos" className="flex items-center gap-3 text-gray-600 p-3 rounded-xl hover:bg-gray-100">
              <Package size={18} />
              Pedidos
            </Link>

            <Link href="/Vendedor/Clientes" className="flex items-center gap-3 text-gray-600 p-3 rounded-xl hover:bg-gray-100">
              <Users size={18} />
              Clientes
            </Link>

            <Link href="/Vendedor/Perfil" className="flex items-center gap-3 bg-red-600 text-white p-3 rounded-xl">
              <User size={18} />
              Perfil
            </Link>

          </nav>
        </div>

        <div className="p-4 border-t">
          <Link href="/Login" className="flex items-center gap-2 text-gray-600 hover:text-red-600">
            <LogOut size={18} />
            Sair
          </Link>
        </div>
      </div>

      {/* CONTEÚDO */}
      <div className="flex-1 p-6 md:p-10 space-y-6 max-w-3xl mx-auto">

        {/* PERFIL */}
        <div className="bg-white rounded-2xl shadow p-6">

          <div className="text-center mb-6">
            <div className="w-20 h-20 mx-auto rounded-full bg-red-100 flex items-center justify-center">
              <User className="text-red-600" size={40} />
            </div>

            <p className="font-bold text-black mt-3">{form.nome}</p>

            <span className="inline-block mt-1 px-3 py-1 text-sm bg-green-100 text-green-700 rounded-full">
              Vendedor Sênior
            </span>
          </div>

          <div className="space-y-4">
            <input
              value={form.nome}
              onChange={(e) => setForm({ ...form, nome: e.target.value })}
              className="w-full border p-3 rounded-lg text-black"
            />

            <input
              value={form.email}
              onChange={(e) => setForm({ ...form, email: e.target.value })}
              className="w-full border p-3 rounded-lg text-black"
            />

            <input
              value={form.telefone}
              onChange={(e) => setForm({ ...form, telefone: e.target.value })}
              className="w-full border p-3 rounded-lg text-black"
            />

            <input
              value={form.concessionaria}
              onChange={(e) =>
                setForm({ ...form, concessionaria: e.target.value })
              }
              className="w-full border p-3 rounded-lg text-black"
            />
          </div>

          <div className="flex gap-3 mt-6">
            <button className="flex-1 border py-2 rounded-lg text-black hover:bg-gray-100">
              <LogOut size={16} className="inline mr-1" />
              Sair
            </button>

            <button className="flex-1 bg-red-600 text-white py-2 rounded-lg hover:bg-red-700">
              Salvar alterações
            </button>
          </div>
        </div>

        {/* PREFERÊNCIAS */}
        <div>
          <h2 className="text-sm font-semibold text-gray-800 mb-2">
            Preferências
          </h2>
          <p className="text-xs text-gray-600 mb-4">
            Configure suas preferências
          </p>

          <div className="bg-white rounded-2xl shadow-lg border border-gray-200 overflow-hidden">

            {/* ITEM */}
            <div
              onClick={() => updatePref("emailNotif")}
              className="flex items-center justify-between p-4 border-b cursor-pointer hover:bg-gray-50"
            >
              <p className="text-sm text-black">Notificações por Email</p>

              {/* SWITCH SIMPLES */}
              <div className={`w-10 h-5 flex items-center rounded-full p-1 transition ${prefs.emailNotif ? "bg-red-600" : "bg-gray-300"}`}>
                <div className={`bg-white w-4 h-4 rounded-full shadow transform transition ${prefs.emailNotif ? "translate-x-5" : ""}`} />
              </div>
            </div>

            <div
              onClick={() => updatePref("smsNotif")}
              className="flex items-center justify-between p-4 border-b cursor-pointer hover:bg-gray-50"
            >
              <p className="text-sm text-black">Notificações por SMS</p>

              <div className={`w-10 h-5 flex items-center rounded-full p-1 transition ${prefs.smsNotif ? "bg-red-600" : "bg-gray-300"}`}>
                <div className={`bg-white w-4 h-4 rounded-full shadow transform transition ${prefs.smsNotif ? "translate-x-5" : ""}`} />
              </div>
            </div>

            <div
              onClick={() => updatePref("promo")}
              className="flex items-center justify-between p-4 cursor-pointer hover:bg-gray-50"
            >
              <p className="text-sm text-black">Emails Promocionais</p>

              <div className={`w-10 h-5 flex items-center rounded-full p-1 transition ${prefs.promo ? "bg-red-600" : "bg-gray-300"}`}>
                <div className={`bg-white w-4 h-4 rounded-full shadow transform transition ${prefs.promo ? "translate-x-5" : ""}`} />
              </div>
            </div>

          </div>
        </div>

        {/* INFO CONTA */}
        <div className="bg-white rounded-2xl shadow p-6">

          <h2 className="text-lg font-bold text-black mb-4 flex items-center gap-2">
            <Shield size={18} />
            Informações da Conta
          </h2>

          <div className="space-y-3 text-black text-sm">

            <div className="flex justify-between">
              <span>Cargo</span>
              <span className="font-medium">Vendedor Sênior</span>
            </div>

            <div className="flex justify-between">
              <span>Concessionária</span>
              <span className="font-medium">Toyota Central SP</span>
            </div>

            <div className="flex justify-between items-center">
              <span>Membro desde</span>
              <div className="flex items-center gap-1">
                <Calendar size={14} />
                <span>Mar 2023</span>
              </div>
            </div>

            <div className="flex justify-between">
              <span>Total de vendas</span>
              <span className="font-medium text-red-600">
                247 veículos
              </span>
            </div>

          </div>

        </div>

      </div>
    </div>
  );
}