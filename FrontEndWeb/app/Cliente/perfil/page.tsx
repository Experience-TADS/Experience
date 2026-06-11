"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { User, Mail, Phone, LogOut } from "lucide-react";
import Sidebar from "@/app/componentes/SideBar";

export default function Perfil() {
  const router = useRouter();

  const [email, setEmail] = useState("");

  const [prefs, setPrefs] = useState({
    emailNotif: false,
    smsNotif: true,
    promo: false,
  });

  useEffect(() => {
    const user = localStorage.getItem("user");

    if (!user) {
      router.push("/");
    } else {
      setEmail(user);
    }

    const savedPrefs = localStorage.getItem("prefs");
    if (savedPrefs) {
      setPrefs(JSON.parse(savedPrefs));
    }
  }, [router]);

  function updatePref(key: string) {
    const updated = {
      ...prefs,
      [key]: !prefs[key as keyof typeof prefs],
    };

    setPrefs(updated);
    localStorage.setItem("prefs", JSON.stringify(updated));
  }

  function sair() {
    localStorage.removeItem("user");
    router.push("/Login");
  }

  function Switch({ active }: { active: boolean }) {
    return (
      <div
        className={`
          w-12 h-6 flex items-center rounded-full p-1 transition-all duration-300
          ${active ? "bg-red-500" : "bg-gray-300"}
        `}
      >
        <div
          className={`
            bg-white w-5 h-5 rounded-full shadow-md transform transition-all duration-300
            ${active ? "translate-x-6" : ""}
          `}
        />
      </div>
    );
  }

  return (
    <div className="flex min-h-screen bg-gray-100">

      <Sidebar />

      <div className="flex-1 px-5 md:px-12 py-8 md:ml-20">

        <div className="max-w-3xl mx-auto">

          {/* HEADER */}
          <div className="mb-8">
            <h1 className="text-3xl font-bold text-gray-900">
              Perfil
            </h1>
            <p className="text-gray-600 text-sm mt-1">
              Seus dados pessoais e preferências
            </p>
          </div>

          {/* AVATAR */}
          <div className="flex justify-center mb-8">
            <div className="bg-red-100 p-6 rounded-full shadow-md">
              <User className="text-red-600 w-10 h-10" />
            </div>
          </div>

          {/* DADOS */}
          <div className="bg-white rounded-3xl shadow-xl border border-gray-100 overflow-hidden mb-8">

            <div className="flex items-center gap-4 p-5 border-b">
              <div className="bg-gray-100 p-3 rounded-xl">
                <User className="text-gray-600 w-5 h-5" />
              </div>
              <div>
                <p className="text-xs text-gray-500">Nome</p>
                <p className="font-semibold text-gray-900">
                  Lauren
                </p>
              </div>
            </div>

            <div className="flex items-center gap-4 p-5 border-b">
              <div className="bg-gray-100 p-3 rounded-xl">
                <Mail className="text-gray-600 w-5 h-5" />
              </div>
              <div>
                <p className="text-xs text-gray-500">Email</p>
                <p className="font-semibold text-gray-900 break-all">
                  {email}
                </p>
              </div>
            </div>

            <div className="flex items-center gap-4 p-5">
              <div className="bg-gray-100 p-3 rounded-xl">
                <Phone className="text-gray-600 w-5 h-5" />
              </div>
              <div>
                <p className="text-xs text-gray-500">Telefone</p>
                <p className="font-semibold text-gray-900">
                  —
                </p>
              </div>
            </div>

          </div>

          {/* PREFERÊNCIAS */}
          <div className="mb-4">
            <h2 className="text-lg font-semibold text-gray-900">
              Preferências
            </h2>
            <p className="text-sm text-gray-500">
              Controle como você recebe notificações
            </p>
          </div>

          <div className="bg-white rounded-3xl shadow-xl border border-gray-100 overflow-hidden mb-8">

            <div
              onClick={() => updatePref("emailNotif")}
              className="flex items-center justify-between p-5 border-b cursor-pointer hover:bg-gray-50 transition"
            >
              <div>
                <p className="text-sm font-medium text-gray-900">
                  Notificações por Email
                </p>
                <p className="text-xs text-gray-500">
                  Receber atualizações por email
                </p>
              </div>
              <Switch active={prefs.emailNotif} />
            </div>

            <div
              onClick={() => updatePref("smsNotif")}
              className="flex items-center justify-between p-5 border-b cursor-pointer hover:bg-gray-50 transition"
            >
              <div>
                <p className="text-sm font-medium text-gray-900">
                  Notificações por SMS
                </p>
                <p className="text-xs text-gray-500">
                  Alertas diretos no celular
                </p>
              </div>
              <Switch active={prefs.smsNotif} />
            </div>

            <div
              onClick={() => updatePref("promo")}
              className="flex items-center justify-between p-5 cursor-pointer hover:bg-gray-50 transition"
            >
              <div>
                <p className="text-sm font-medium text-gray-900">
                  Emails Promocionais
                </p>
                <p className="text-xs text-gray-500">
                  Ofertas e novidades
                </p>
              </div>
              <Switch active={prefs.promo} />
            </div>

          </div>

          {/* BOTÃO SAIR */}
          <button
            onClick={sair}
            className="
              w-full 
              bg-white
              border border-red-500 
              text-red-500 
              py-3 
              rounded-xl 
              font-semibold
              flex items-center justify-center gap-2
              hover:bg-red-50
              hover:scale-[1.01]
              active:scale-[0.98]
              transition
              shadow-sm
            "
          >
            <LogOut size={18} />
            Sair da conta
          </button>

        </div>

      </div>
    </div>
  );
}