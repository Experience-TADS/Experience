"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { User, Mail, Phone, LogOut, Sun, Moon } from "lucide-react";
import Sidebar from "@/app/componentes/SideBar";
import { useTheme } from "@/app/contexts/ThemeContext";

export default function Perfil() {
  const router = useRouter();
  const { theme, toggleTheme } = useTheme();

  const [email, setEmail] = useState("");
  const [prefs, setPrefs] = useState({ emailNotif: false, smsNotif: true, promo: false });

  useEffect(() => {
    const user = localStorage.getItem("user");
    if (!user) router.push("/");
    else setEmail(user);
    const savedPrefs = localStorage.getItem("prefs");
    if (savedPrefs) setPrefs(JSON.parse(savedPrefs));
  }, [router]);

  function updatePref(key: string) {
    const updated = { ...prefs, [key]: !prefs[key as keyof typeof prefs] };
    setPrefs(updated);
    localStorage.setItem("prefs", JSON.stringify(updated));
  }

  function sair() {
    localStorage.removeItem("user");
    router.push("/Login");
  }

  function Switch({ active }: { active: boolean }) {
    return (
      <div className={`w-12 h-6 flex items-center rounded-full p-1 transition-all duration-300 ${active ? "bg-red-500" : "bg-gray-300 dark:bg-gray-600"}`}>
        <div className={`bg-white w-5 h-5 rounded-full shadow-md transform transition-all duration-300 ${active ? "translate-x-6" : ""}`} />
      </div>
    );
  }

  return (
    <div className="flex min-h-screen bg-gray-100 dark:bg-gray-950">
      <Sidebar />

      <div className="flex-1 px-5 md:px-12 py-8 md:ml-20 pb-24 md:pb-8">
        <div className="max-w-3xl mx-auto">

          {/* HEADER */}
          <div className="mb-8">
            <h1 className="text-3xl font-bold text-gray-900 dark:text-gray-100">Perfil</h1>
            <p className="text-gray-600 dark:text-gray-400 text-sm mt-1">Seus dados pessoais e preferências</p>
          </div>

          {/* AVATAR */}
          <div className="flex justify-center mb-8">
            <div className="bg-red-100 dark:bg-red-900/30 p-6 rounded-full shadow-md">
              <User className="text-red-600 w-10 h-10" />
            </div>
          </div>

          {/* DADOS */}
          <div className="bg-white dark:bg-gray-900 rounded-3xl shadow-xl border border-gray-100 dark:border-gray-700 overflow-hidden mb-8">
            {[
              { icon: <User className="text-gray-600 dark:text-gray-400 w-5 h-5" />, label: "Nome", value: "Lauren" },
              { icon: <Mail className="text-gray-600 dark:text-gray-400 w-5 h-5" />, label: "Email", value: email },
              { icon: <Phone className="text-gray-600 dark:text-gray-400 w-5 h-5" />, label: "Telefone", value: "—" },
            ].map((item, i, arr) => (
              <div key={item.label} className={`flex items-center gap-4 p-5 ${i < arr.length - 1 ? "border-b border-gray-100 dark:border-gray-700" : ""}`}>
                <div className="bg-gray-100 dark:bg-gray-800 p-3 rounded-xl">{item.icon}</div>
                <div>
                  <p className="text-xs text-gray-500 dark:text-gray-400">{item.label}</p>
                  <p className="font-semibold text-gray-900 dark:text-gray-100 break-all">{item.value}</p>
                </div>
              </div>
            ))}
          </div>

          {/* PREFERÊNCIAS */}
          <div className="mb-4">
            <h2 className="text-lg font-semibold text-gray-900 dark:text-gray-100">Preferências</h2>
            <p className="text-sm text-gray-500 dark:text-gray-400">Notificações e aparência</p>
          </div>

          <div className="bg-white dark:bg-gray-900 rounded-3xl shadow-xl border border-gray-100 dark:border-gray-700 overflow-hidden mb-8">

            {/* Notificações */}
            {[
              { key: "emailNotif", label: "Notificações por Email", sub: "Receber atualizações por email" },
              { key: "smsNotif",   label: "Notificações por SMS",   sub: "Alertas diretos no celular" },
              { key: "promo",      label: "Emails Promocionais",    sub: "Ofertas e novidades" },
            ].map((item) => (
              <div
                key={item.key}
                onClick={() => updatePref(item.key)}
                className="flex items-center justify-between p-5 border-b border-gray-100 dark:border-gray-700 cursor-pointer hover:bg-gray-50 dark:hover:bg-gray-800 transition"
              >
                <div>
                  <p className="text-sm font-medium text-gray-900 dark:text-gray-100">{item.label}</p>
                  <p className="text-xs text-gray-500 dark:text-gray-400">{item.sub}</p>
                </div>
                <Switch active={prefs[item.key as keyof typeof prefs]} />
              </div>
            ))}

            {/* Toggle de tema — integrado nas preferências */}
            <div
              onClick={toggleTheme}
              className="flex items-center justify-between p-5 cursor-pointer hover:bg-gray-50 dark:hover:bg-gray-800 transition"
            >
              <div className="flex items-center gap-3">
                <div className="bg-gray-100 dark:bg-gray-800 p-2 rounded-xl">
                  {theme === "dark"
                    ? <Sun className="text-yellow-500 w-5 h-5" />
                    : <Moon className="text-gray-600 w-5 h-5" />
                  }
                </div>
                <div>
                  <p className="text-sm font-medium text-gray-900 dark:text-gray-100">
                    {theme === "dark" ? "Modo Escuro" : "Modo Claro"}
                  </p>
                  <p className="text-xs text-gray-500 dark:text-gray-400">
                    {theme === "dark" ? "Clique para usar o modo claro" : "Clique para usar o modo escuro"}
                  </p>
                </div>
              </div>
              {/* Switch visual mostrando estado atual */}
              <div className={`w-12 h-6 flex items-center rounded-full p-1 transition-all duration-300 ${theme === "dark" ? "bg-red-500" : "bg-gray-300 dark:bg-gray-600"}`}>
                <div className={`bg-white w-5 h-5 rounded-full shadow-md transform transition-all duration-300 ${theme === "dark" ? "translate-x-6" : ""}`} />
              </div>
            </div>

          </div>

          {/* BOTÃO SAIR */}
          <button
            onClick={sair}
            className="w-full bg-white dark:bg-gray-900 border border-red-500 text-red-500 py-3 rounded-xl font-semibold flex items-center justify-center gap-2 hover:bg-red-50 dark:hover:bg-red-900/20 hover:scale-[1.01] active:scale-[0.98] transition shadow-sm"
          >
            <LogOut size={18} />
            Sair da conta
          </button>

        </div>
      </div>
    </div>
  );
}
