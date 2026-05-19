"use client";

import Sidebar from "@/app/componentes/SideBar";
import { Smartphone, ExternalLink } from "lucide-react";

const toyotaApps = [
  { name: "Toyota App", description: "Aplicativo oficial da Toyota para acompanhar informações do veículo, agendar serviços, acessar manuais digitais e receber notificações importantes.", url: "https://www.toyota.com.br/servicos/toyota-app", icon: "📱" },
  { name: "Toyota Play", description: "Central multimídia com acesso a conteúdos exclusivos, conectividade com apps e integração com seu veículo Toyota.", url: "https://www.toyota.com.br/inovacao/toyota-play", icon: "🎬" },
  { name: "Toyota Mobility", description: "Gerencie veículos conectados, acompanhe diagnósticos em tempo real e utilize serviços inteligentes de mobilidade.", url: "https://www.toyota.com.br/inovacao/mobilidade", icon: "🚗" },
  { name: "KINTO Share", description: "Serviço de compartilhamento de carros da Toyota. Alugue veículos por horas ou dias com praticidade e segurança.", url: "https://www.kintomobility.com.br/", icon: "🔑" },
  { name: "Toyota Gazoo Racing", description: "Acompanhe o mundo das corridas, eventos automobilísticos e novidades esportivas da Toyota Gazoo Racing.", url: "https://www.toyota.com.br/mundo-toyota/gazoo-racing", icon: "🏁" },
  { name: "Toyota Financial", description: "Acesse seus financiamentos, consulte parcelas, contratos e serviços financeiros diretamente pelo app oficial.", url: "https://www.toyotafinancial.com.br/", icon: "💳" },
];

export default function Apps() {
  return (
    <div className="bg-gray-100 dark:bg-gray-950 min-h-screen flex">
      <Sidebar />
      <div className="flex-1 md:ml-20 flex flex-col pb-24 md:pb-0">
        <div className="flex-1 max-w-4xl mx-auto w-full px-4 pt-10 pb-10">

          <div className="mb-6">
            <h1 className="text-xl font-bold text-gray-900 dark:text-gray-100">Apps Toyota</h1>
            <p className="text-sm text-gray-500 dark:text-gray-400">Explore os aplicativos oficiais da Toyota</p>
          </div>

          <div className="grid grid-cols-2 gap-4">
            {toyotaApps.map((app) => (
              <div key={app.name} onClick={() => window.open(app.url, "_blank")}
                className="bg-white dark:bg-gray-900 border border-transparent dark:border-gray-700 p-5 rounded-2xl shadow hover:shadow-md transition cursor-pointer flex flex-col justify-between min-h-[150px]">
                <div className="flex items-center gap-3 mb-3">
                  <div className="w-10 h-10 flex items-center justify-center bg-red-100 dark:bg-red-900/30 rounded-lg text-lg">{app.icon}</div>
                  <p className="font-semibold text-gray-900 dark:text-gray-100 text-sm">{app.name}</p>
                </div>
                <p className="text-xs text-gray-600 dark:text-gray-400 leading-relaxed">{app.description}</p>
                <div className="flex justify-end mt-3">
                  <ExternalLink className="text-gray-400 dark:text-gray-500" size={14} />
                </div>
              </div>
            ))}
          </div>
        </div>

        <div className="text-center pb-6 text-gray-500 dark:text-gray-400 text-xs flex items-center justify-center gap-2">
          <Smartphone size={14} /> Disponível na Play Store e App Store
        </div>
      </div>
    </div>
  );
}
