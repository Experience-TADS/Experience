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
  Sun,
  Moon,
} from "lucide-react";
import { useTheme } from "@/app/contexts/ThemeContext";

export default function Perfil() {
  const { theme, toggleTheme } = useTheme();

  const [form, setForm] = useState({
    nome: "Ricardo Mendes",
    email: "ricardo.mendes@toyota.com",
    telefone: "(11) 99999-0000",
    concessionaria: "Toyota Central São Paulo",
  });

  const [prefs, setPrefs] = useState({
    emailNotif: true,
    smsNotif: false,
    promo: false,
  });

  function updatePref(key: string) {
    setPrefs((prev) => ({ ...prev, [key]: !prev[key as keyof typeof prev] }));
  }

  function Switch({ active }: { active: boolean }) {
    return (
      <div className={`w-10 h-5 flex items-center rounded-full p-1 transition ${active ? "bg-red-600" : "bg-gray-300 dark:bg-gray-600"}`}>
        <div className={`bg-white w-4 h-4 rounded-full shadow transform transition ${active ? "translate-x-5" : ""}`} />
      </div>
    );
  }

  return (
    <div className="flex min-h-screen bg-gray-100 dark:bg-gray-950">

      {/* SIDEBAR */}
      <div className="hidden md:flex w-64 bg-white dark:bg-gray-900 border-r border-gray-100 dark:border-gray-800 flex-col justify-between">
        <div>
          <div className="flex items-center gap-3 p-6">
            <div className="bg-red-600 text-white p-3 rounded-lg">
              <Package size={20} />
            </div>
            <div>
              <p className="font-bold text-gray-900 dark:text-gray-100">Toyota</p>
              <p className="text-sm text-gray-500 dark:text-gray-400">Painel do Vendedor</p>
            </div>
          </div>

          <nav className="flex flex-col gap-2 px-4">
            <Link href="/Vendedor/Dashbord" className="flex items-center gap-3 text-gray-600 dark:text-gray-400 p-3 rounded-xl hover:bg-gray-100 dark:hover:bg-gray-800">
              <LayoutDashboard size={18} /> Dashboard
            </Link>
            <Link href="/Vendedor/Pedidos" className="flex items-center gap-3 text-gray-600 dark:text-gray-400 p-3 rounded-xl hover:bg-gray-100 dark:hover:bg-gray-800">
              <Package size={18} /> Pedidos
            </Link>
            <Link href="/Vendedor/Clientes" className="flex items-center gap-3 text-gray-600 dark:text-gray-400 p-3 rounded-xl hover:bg-gray-100 dark:hover:bg-gray-800">
              <Users size={18} /> Clientes
            </Link>
            <Link href="/Vendedor/Perfil" className="flex items-center gap-3 bg-red-600 text-white p-3 rounded-xl">
              <User size={18} /> Perfil
            </Link>
            <Link href="/Vendedor/Administracao" className="flex items-center gap-3 text-gray-600 dark:text-gray-400 p-3 rounded-xl hover:bg-gray-100 dark:hover:bg-gray-800">
              <Shield size={18} /> Administração
            </Link>
          </nav>
        </div>

        <div className="p-4 border-t border-gray-100 dark:border-gray-800">
          <Link href="/Login" className="flex items-center gap-2 text-gray-600 dark:text-gray-400 hover:text-red-600">
            <LogOut size={18} /> Sair
          </Link>
        </div>
      </div>

      {/* CONTEÚDO */}
      <div className="flex-1 p-6 md:p-10 space-y-6 max-w-3xl mx-auto">

        {/* CARD PERFIL */}
        <div className="bg-white dark:bg-gray-900 rounded-2xl shadow border border-transparent dark:border-gray-700 p-6">
          <div className="text-center mb-6">
            <div className="w-20 h-20 mx-auto rounded-full bg-red-100 dark:bg-red-900/30 flex items-center justify-center">
              <User className="text-red-600" size={40} />
            </div>
            <p className="font-bold text-gray-900 dark:text-gray-100 mt-3">{form.nome}</p>
            <span className="inline-block mt-1 px-3 py-1 text-sm bg-green-100 dark:bg-green-900/30 text-green-700 dark:text-green-400 rounded-full">
              Vendedor Sênior
            </span>
          </div>

          <div className="space-y-4">
            {[
              { field: "nome", value: form.nome },
              { field: "email", value: form.email },
              { field: "telefone", value: form.telefone },
              { field: "concessionaria", value: form.concessionaria },
            ].map((item) => (
              <input
                key={item.field}
                value={item.value}
                onChange={(e) => setForm({ ...form, [item.field]: e.target.value })}
                className="w-full border border-gray-200 dark:border-gray-700 p-3 rounded-lg text-gray-900 dark:text-gray-100 bg-white dark:bg-gray-800 focus:outline-none focus:ring-2 focus:ring-red-500"
              />
            ))}
          </div>

          <div className="flex gap-3 mt-6">
            <button className="flex-1 border border-gray-200 dark:border-gray-700 py-2 rounded-lg text-gray-700 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-800 transition">
              <LogOut size={16} className="inline mr-1" />
              Sair
            </button>
            <button className="flex-1 bg-red-600 text-white py-2 rounded-lg hover:bg-red-700 transition">
              Salvar alterações
            </button>
          </div>
        </div>

        {/* PREFERÊNCIAS */}
        <div>
          <h2 className="text-sm font-semibold text-gray-800 dark:text-gray-200 mb-2">
            Preferências
          </h2>

          <div className="bg-white dark:bg-gray-900 rounded-2xl shadow-lg border border-transparent dark:border-gray-700 overflow-hidden">

            {[
              { key: "emailNotif", label: "Notificações por Email" },
              { key: "smsNotif",   label: "Notificações por SMS" },
              { key: "promo",      label: "Emails Promocionais" },
            ].map((item) => (
              <div
                key={item.key}
                onClick={() => updatePref(item.key)}
                className="flex items-center justify-between p-4 border-b border-gray-100 dark:border-gray-700 cursor-pointer hover:bg-gray-50 dark:hover:bg-gray-800 transition"
              >
                <p className="text-sm text-gray-900 dark:text-gray-100">{item.label}</p>
                <Switch active={prefs[item.key as keyof typeof prefs]} />
              </div>
            ))}

            {/* Toggle de tema */}
            <div
              onClick={toggleTheme}
              className="flex items-center justify-between p-4 cursor-pointer hover:bg-gray-50 dark:hover:bg-gray-800 transition"
            >
              <div className="flex items-center gap-3">
                <div className="bg-gray-100 dark:bg-gray-800 p-2 rounded-xl">
                  {theme === "dark"
                    ? <Sun className="text-yellow-500 w-4 h-4" />
                    : <Moon className="text-gray-600 w-4 h-4" />
                  }
                </div>
                <div>
                  <p className="text-sm text-gray-900 dark:text-gray-100">
                    {theme === "dark" ? "Modo Escuro" : "Modo Claro"}
                  </p>
                  <p className="text-xs text-gray-500 dark:text-gray-400">
                    {theme === "dark" ? "Clique para usar o modo claro" : "Clique para usar o modo escuro"}
                  </p>
                </div>
              </div>
              <div className={`w-10 h-5 flex items-center rounded-full p-1 transition ${theme === "dark" ? "bg-red-600" : "bg-gray-300 dark:bg-gray-600"}`}>
                <div className={`bg-white w-4 h-4 rounded-full shadow transform transition ${theme === "dark" ? "translate-x-5" : ""}`} />
              </div>
            </div>

          </div>
        </div>

        {/* INFO CONTA */}
        <div className="bg-white dark:bg-gray-900 rounded-2xl shadow border border-transparent dark:border-gray-700 p-6">
          <h2 className="text-lg font-bold text-gray-900 dark:text-gray-100 mb-4 flex items-center gap-2">
            <Shield size={18} />
            Informações da Conta
          </h2>

          <div className="space-y-3 text-sm">
            {[
              { label: "Cargo",          value: "Vendedor Sênior",    className: "" },
              { label: "Concessionária", value: "Toyota Central SP",  className: "" },
              { label: "Total de vendas",value: "247 veículos",       className: "text-red-600 font-medium" },
            ].map((item) => (
              <div key={item.label} className="flex justify-between text-gray-900 dark:text-gray-100">
                <span className="text-gray-600 dark:text-gray-400">{item.label}</span>
                <span className={item.className || "font-medium"}>{item.value}</span>
              </div>
            ))}
            <div className="flex justify-between items-center text-gray-900 dark:text-gray-100">
              <span className="text-gray-600 dark:text-gray-400">Membro desde</span>
              <div className="flex items-center gap-1 font-medium">
                <Calendar size={14} />
                <span>Mar 2023</span>
              </div>
            </div>
          </div>
        </div>

      </div>
    </div>
  );
}
