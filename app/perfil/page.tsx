"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { User, Mail, Phone } from "lucide-react";

import Sidebar from "@/app/componentes/SideBar";

export default function Perfil() {
  const router = useRouter();
  const [email, setEmail] = useState("");

  useEffect(() => {
    const user = localStorage.getItem("user");

    if (!user) {
      router.push("/perfil");
    } else {
      setEmail(user);
    }
  }, [router]);

  function sair() {
    localStorage.removeItem("user");
    router.push("/Login");
  }

  return (
    <div className="flex min-h-screen bg-gray-100">

      <Sidebar />

      <div className="flex-1 flex items-center justify-center p-6">

        <div className="w-full max-w-sm">

          <h1 className="text-xl font-bold text-gray-900 mb-1">
            Perfil
          </h1>
          <p className="text-gray-700 mb-5 text-sm">
            Seus dados pessoais
          </p>

          <div className="flex justify-center mb-5">
            <div className="bg-red-100 p-4 rounded-full">
              <User className="text-red-600 w-6 h-6" />
            </div>
          </div>

          <div className="bg-white rounded-xl shadow-sm divide-y">

            <div className="flex items-center gap-3 p-3">
              <User className="text-gray-700 w-4 h-4" />
              <div>
                <p className="text-xs text-gray-600">Nome</p>
                <p className="font-medium text-gray-900 text-sm">
                  Lauren Julia Reis
                </p>
              </div>
            </div>

            <div className="flex items-center gap-3 p-3">
              <Mail className="text-gray-700 w-4 h-4" />
              <div>
                <p className="text-xs text-gray-600">Email</p>
                <p className="font-medium text-gray-900 text-sm">
                  Laurenjulia@gmail.com
                </p>
              </div>
            </div>

            <div className="flex items-center gap-3 p-3">
              <Phone className="text-gray-700 w-4 h-4" />
              <div>
                <p className="text-xs text-gray-600">Telefone</p>
                <p className="font-medium text-gray-900 text-sm">
                  15 99876-5432
                </p>
              </div>
            </div>

          </div>

          <button
            onClick={sair}
            className="mt-5 w-full bg-red-600 text-white text-sm py-2 rounded-lg hover:bg-red-700 transition"
          >
            Sair da conta
          </button>

        </div>

      </div>
    </div>
  );
}