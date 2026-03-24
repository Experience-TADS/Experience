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
          w-11 h-6 flex items-center rounded-full p-1 transition-all duration-300
          ${active ? "bg-red-500" : "bg-gray-300"}
        `}
      >
        <div
          className={`
            bg-white w-5 h-5 rounded-full shadow-md transform transition-all duration-300
            ${active ? "translate-x-5" : ""}
          `}
        />
      </div>
    );
  }

  return (
    <div className="flex min-h-screen bg-gray-100">

      <Sidebar />

      <div className="flex-1 p-5 md:p-10">

        <div className="max-w-md mx-auto">

          {/* HEADER */}
          <h1 className="text-xl font-bold text-gray-900">
            Perfil
          </h1>
          <p className="text-gray-600 text-sm mb-6">
            Seus dados pessoais
          </p>

          {/* AVATAR */}
          <div className="flex justify-center mb-6">
            <div className="bg-red-200 p-5 rounded-full">
              <User className="text-red-600 w-7 h-7" />
            </div>
          </div>

          {/* DADOS */}
          <div className="bg-white rounded-2xl shadow-lg border border-gray-200 overflow-hidden mb-6">

            <div className="flex items-center gap-3 p-4 border-b">
              <div className="bg-gray-100 p-2 rounded-lg">
                <User className="text-gray-600 w-4 h-4" />
              </div>
              <div>
                <p className="text-xs text-gray-600">Nome</p>
                <p className="font-semibold text-gray-900 text-sm">
                  Lauren
                </p>
              </div>
            </div>

            <div className="flex items-center gap-3 p-4 border-b">
              <div className="bg-gray-100 p-2 rounded-lg">
                <Mail className="text-gray-600 w-4 h-4" />
              </div>
              <div>
                <p className="text-xs text-gray-600">Email</p>
                <p className="font-semibold text-gray-900 text-sm break-all">
                  {email}
                </p>
              </div>
            </div>

            <div className="flex items-center gap-3 p-4">
              <div className="bg-gray-100 p-2 rounded-lg">
                <Phone className="text-gray-600 w-4 h-4" />
              </div>
              <div>
                <p className="text-xs text-gray-600">Telefone</p>
                <p className="font-semibold text-gray-900 text-sm">
                  —
                </p>
              </div>
            </div>

          </div>

          {/* PREFERÊNCIAS */}
          <h2 className="text-sm font-semibold text-gray-800 mb-2">
            Preferências
          </h2>
          <p className="text-xs text-gray-600 mb-4">
            Configure suas preferências
          </p>

          <div className="bg-white rounded-2xl shadow-lg border border-gray-200 overflow-hidden mb-6">

            <div
              onClick={() => updatePref("emailNotif")}
              className="flex items-center justify-between p-4 border-b cursor-pointer hover:bg-gray-50 transition"
            >
              <p className="text-sm text-gray-800">
                Notificações por Email
              </p>
              <Switch active={prefs.emailNotif} />
            </div>

            <div
              onClick={() => updatePref("smsNotif")}
              className="flex items-center justify-between p-4 border-b cursor-pointer hover:bg-gray-50 transition"
            >
              <p className="text-sm text-gray-800">
                Notificações por SMS
              </p>
              <Switch active={prefs.smsNotif} />
            </div>

            <div
              onClick={() => updatePref("promo")}
              className="flex items-center justify-between p-4 cursor-pointer hover:bg-gray-50 transition"
            >
              <p className="text-sm text-gray-800">
                Emails Promocionais
              </p>
              <Switch active={prefs.promo} />
            </div>

          </div>

          {/* SAIR */}
          <button
            onClick={sair}
            className="
              w-full 
              border border-red-500 
              text-red-500 
              py-2 
              rounded-lg 
              text-sm 
              flex items-center justify-center gap-2
              hover:bg-red-50
              transition
            "
          >
            <LogOut size={16} />
            Sair da conta
          </button>

        </div>

      </div>
    </div>
  );
}