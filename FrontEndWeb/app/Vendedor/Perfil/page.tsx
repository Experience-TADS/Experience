"use client";

import { useState, useEffect } from "react";
import { User, Shield, Calendar, LogOut, Moon, Sun } from "lucide-react";
import { removeToken } from "@/app/lib/api";
import { getTheme, toggleTheme } from "@/app/lib/theme";
import VendedorSidebar from "@/app/Vendedor/componentes/VendedorSidebar";

export default function Perfil() {
  const [form, setForm] = useState({
    nome: "Ricardo Mendes",
    email: "ricardo.mendes@toyota.com",
    telefone: "(11) 99999-0000",
    concessionaria: "Toyota Central São Paulo",
  });

  const [prefs, setPrefs] = useState({ emailNotif: true, smsNotif: false, promo: false });
  const [isDark, setIsDark] = useState(false);

  useEffect(() => {
    const user = localStorage.getItem("user");
    if (user) setForm((f) => ({ ...f, email: user }));
    setIsDark(getTheme() === "dark");
  }, []);

  function updatePref(key: string) {
    setPrefs((prev) => ({ ...prev, [key]: !prev[key as keyof typeof prev] }));
  }

  function handleToggleTheme() {
    const next = toggleTheme();
    setIsDark(next === "dark");
  }

  function Switch({ active }: { active: boolean }) {
    return (
      <div className={`w-10 h-5 flex items-center rounded-full p-1 transition-all duration-300 ${active ? "bg-red-600" : "bg-gray-300 dark:bg-gray-600"}`}>
        <div className={`bg-white w-4 h-4 rounded-full shadow transform transition-all duration-300 ${active ? "translate-x-5" : ""}`} />
      </div>
    );
  }

  return (
    <div className="flex min-h-screen bg-gray-100 dark:bg-gray-950">
      <VendedorSidebar />

      <div className="flex-1 p-6 md:p-10 space-y-6 max-w-3xl mx-auto">

        <div className="bg-white dark:bg-gray-900 rounded-2xl border border-gray-100 dark:border-gray-800 shadow p-6">
          <div className="text-center mb-6">
            <div className="w-20 h-20 mx-auto rounded-full bg-red-100 dark:bg-red-900/30 flex items-center justify-center">
              <User className="text-red-600" size={40} />
            </div>
            <p className="font-bold text-gray-900 dark:text-white mt-3">{form.nome}</p>
            <span className="inline-block mt-1 px-3 py-1 text-sm bg-green-100 dark:bg-green-900/30 text-green-700 dark:text-green-400 rounded-full">Vendedor Sênior</span>
          </div>

          <div className="space-y-4">
            {[
              { value: form.nome, key: "nome", placeholder: "Nome" },
              { value: form.email, key: "email", placeholder: "Email" },
              { value: form.telefone, key: "telefone", placeholder: "Telefone" },
              { value: form.concessionaria, key: "concessionaria", placeholder: "Concessionária" },
            ].map((f) => (
              <input key={f.key} value={f.value} placeholder={f.placeholder}
                onChange={(e) => setForm({ ...form, [f.key]: e.target.value })}
                className="w-full border border-gray-200 dark:border-gray-700 bg-white dark:bg-gray-800 text-gray-900 dark:text-white p-3 rounded-lg outline-none focus:ring-2 focus:ring-red-500" />
            ))}
          </div>

          <div className="flex gap-3 mt-6">
            <button onClick={() => { removeToken(); window.location.href = "/Login"; }}
              className="flex-1 border border-gray-200 dark:border-gray-700 py-2 rounded-lg text-gray-600 dark:text-gray-400 hover:bg-gray-50 dark:hover:bg-gray-800 flex items-center justify-center gap-1">
              <LogOut size={16} /> Sair
            </button>
            <button className="flex-1 bg-red-600 text-white py-2 rounded-lg hover:bg-red-700">Salvar alterações</button>
          </div>
        </div>

        <div>
          <h2 className="text-sm font-semibold text-gray-700 dark:text-gray-400 mb-2">Preferências</h2>
          <div className="bg-white dark:bg-gray-900 rounded-2xl border border-gray-100 dark:border-gray-800 shadow-lg overflow-hidden">
            {[
              { key: "emailNotif", label: "Notificações por Email" },
              { key: "smsNotif", label: "Notificações por SMS" },
              { key: "promo", label: "Emails Promocionais" },
            ].map((item) => (
              <div key={item.key} onClick={() => updatePref(item.key)}
                className="flex items-center justify-between p-4 border-b border-gray-100 dark:border-gray-800 cursor-pointer hover:bg-gray-50 dark:hover:bg-gray-800 transition">
                <p className="text-sm text-gray-700 dark:text-gray-300">{item.label}</p>
                <Switch active={prefs[item.key as keyof typeof prefs]} />
              </div>
            ))}

            <div onClick={handleToggleTheme}
              className="flex items-center justify-between p-4 cursor-pointer hover:bg-gray-50 dark:hover:bg-gray-800 transition">
              <p className="text-sm text-gray-700 dark:text-gray-300">Tema escuro</p>
              <div className={`w-10 h-5 flex items-center rounded-full p-1 transition-all duration-300 ${isDark ? "bg-red-600" : "bg-gray-300 dark:bg-gray-600"}`}>
                <div className={`flex items-center justify-center bg-white w-4 h-4 rounded-full shadow transform transition-all duration-300 ${isDark ? "translate-x-5" : ""}`}>
                  {isDark ? <Moon size={8} className="text-gray-600" /> : <Sun size={8} className="text-yellow-500" />}
                </div>
              </div>
            </div>
          </div>
        </div>

        <div className="bg-white dark:bg-gray-900 rounded-2xl border border-gray-100 dark:border-gray-800 shadow p-6">
          <h2 className="text-lg font-bold text-gray-900 dark:text-white mb-4 flex items-center gap-2">
            <Shield size={18} /> Informações da Conta
          </h2>
          <div className="space-y-3 text-sm">
            {[
              { label: "Cargo", value: "Vendedor Sênior" },
              { label: "Concessionária", value: "Toyota Central SP" },
              { label: "Total de vendas", value: "247 veículos", red: true },
            ].map((item) => (
              <div key={item.label} className="flex justify-between">
                <span className="text-gray-500">{item.label}</span>
                <span className={`font-medium ${item.red ? "text-red-600" : "text-gray-900 dark:text-white"}`}>{item.value}</span>
              </div>
            ))}
            <div className="flex justify-between items-center">
              <span className="text-gray-500">Membro desde</span>
              <div className="flex items-center gap-1 text-gray-900 dark:text-white font-medium">
                <Calendar size={14} /> Mar 2023
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
