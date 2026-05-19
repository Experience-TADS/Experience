"use client";

import { MapPin, Phone } from "lucide-react";
import Sidebar from "@/app/componentes/SideBar";

export default function Loja() {
  return (
    <div className="flex min-h-screen bg-gray-100 dark:bg-gray-950">
      <Sidebar />
      <div className="flex-1 px-5 md:px-12 py-8 md:ml-20 pb-24 md:pb-8">
        <div className="max-w-3xl mx-auto">

          <div className="mb-8">
            <h1 className="text-3xl font-bold text-gray-900 dark:text-gray-100">Concessionária</h1>
            <p className="text-gray-600 dark:text-gray-400 text-sm mt-1">Local de retirada do veículo</p>
          </div>

          <div className="bg-white dark:bg-gray-900 rounded-3xl shadow-xl border border-gray-100 dark:border-gray-700 overflow-hidden">

            <div className="p-7 border-b border-gray-100 dark:border-gray-700">
              <h2 className="text-2xl font-bold text-gray-900 dark:text-gray-100">Ramires Motors Toyota</h2>
              <p className="text-sm text-gray-500 dark:text-gray-400 mt-1">Concessionária oficial</p>
            </div>

            <div className="p-7">
              <div className="mb-4">
                <div className="flex items-start gap-3 text-sm text-gray-800 dark:text-gray-200">
                  <MapPin className="w-5 h-5 mt-0.5 text-red-500" />
                  <p>Av. São Paulo, 1000 – Além Ponte, Sorocaba – SP</p>
                </div>
                <p className="text-xs text-gray-500 dark:text-gray-400 ml-8 mt-1">Sorocaba – SP, Brasil</p>
              </div>

              <div className="flex items-center gap-3 text-sm text-gray-800 dark:text-gray-200 mb-6">
                <Phone className="w-5 h-5 text-red-500" />
                <p className="font-medium">(15) 3212-9000</p>
              </div>

              <div className="rounded-2xl overflow-hidden shadow-sm mb-6">
                <iframe src="https://www.google.com/maps?q=Ramires+Motors+Toyota+Sorocaba&output=embed" className="w-full h-64 border-0" loading="lazy" />
              </div>

              <a href="https://www.google.com/maps/dir/?api=1&destination=Ramires+Motors+Toyota+Sorocaba" target="_blank"
                className="w-full bg-red-600 text-white py-3.5 rounded-xl text-center font-semibold flex items-center justify-center gap-2 hover:bg-red-700 hover:scale-[1.02] active:scale-[0.98] transition shadow-md">
                <MapPin size={18} /> Como chegar
              </a>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
