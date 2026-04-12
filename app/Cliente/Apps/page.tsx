"use client";

import Sidebar from "@/app/componentes/SideBar"; // ✅ SUA SIDEBAR DE CLIENTE
import { Smartphone, ExternalLink } from "lucide-react";

const toyotaApps = [
  {
    name: "Toyota Play",
    description: "Acesse conteúdos exclusivos, vídeos e novidades sobre os modelos Toyota.",
    url: "https://play.google.com/store/apps/details?id=br.com.toyota.toyotaplay",
    icon: "🎬",
  },
  {
    name: "Toyota Mobility Services",
    description: "Gerencie seu veículo conectado e serviços remotos.",
    url: "https://play.google.com/store/apps/details?id=com.toyota.oneapp",
    icon: "🚗",
  },
  {
    name: "Toyota Gazoo Racing",
    description: "Acompanhe corridas e novidades do automobilismo Toyota.",
    url: "https://play.google.com/store/apps/details?id=com.toyota.gazooracing",
    icon: "🏁",
  },
  {
    name: "KINTO Share",
    description: "Alugue veículos por horas ou dias com praticidade.",
    url: "https://play.google.com/store/apps/details?id=com.kinto.share",
    icon: "🔑",
  },
  {
    name: "Toyota Financial Services",
    description: "Consulte financiamentos e serviços financeiros.",
    url: "https://play.google.com/store/apps/details?id=br.com.toyotafinancialservices",
    icon: "💳",
  },
];

export default function Apps() {
  return (
    <div className="bg-gray-100 min-h-screen">

      {/* ✅ SIDEBAR CLIENTE */}
      <Sidebar />

      {/* ✅ CONTEÚDO */}
      <div className="md:ml-20 px-4 pt-10 pb-20 space-y-6">

        <div>
          <h1 className="text-xl font-bold text-black">
            Apps Toyota
          </h1>
          <p className="text-sm text-gray-500">
            Explore os aplicativos oficiais
          </p>
        </div>

        <div className="space-y-3">

          {toyotaApps.map((app) => (
            <div
              key={app.name}
              onClick={() => window.open(app.url, "_blank")}
              className="bg-white p-4 rounded-xl shadow hover:shadow-md transition cursor-pointer flex items-center gap-3"
            >

              <div className="w-10 h-10 flex items-center justify-center bg-red-100 rounded-lg text-lg">
                {app.icon}
              </div>

              <div className="flex-1">
                <p className="font-semibold text-black text-sm">
                  {app.name}
                </p>
                <p className="text-xs text-gray-600 line-clamp-2">
                  {app.description}
                </p>
              </div>

              <ExternalLink className="text-gray-400" size={16} />

            </div>
          ))}

        </div>

        <div className="text-center pt-4 text-gray-500 text-xs flex items-center justify-center gap-2">
          <Smartphone size={14} />
          Disponível na Play Store e App Store
        </div>

      </div>
    </div>
  );
}