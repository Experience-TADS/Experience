"use client";

import { MapPin, Phone } from "lucide-react";
import Sidebar from "@/app/componentes/SideBar";

export default function Loja() {
  return (
    <div className="flex min-h-screen bg-gray-100">

      <Sidebar />

      <div className="flex-1 p-6 md:p-10">

        <div className="max-w-lg mx-auto">

          {/* HEADER */}
          <h1 className="text-2xl font-bold text-gray-900">
            Concessionária
          </h1>
          <p className="text-gray-600 text-sm mb-6">
            Local de retirada do veículo
          </p>

          {/* CARD */}
          <div className="bg-white rounded-2xl shadow-xl border border-gray-200 p-6">

            {/* NOME */}
            <h2 className="text-xl font-bold text-gray-900 mb-4">
              Ramires Motors Toyota
            </h2>

            {/* ENDEREÇO */}
            <div className="flex items-start gap-2 text-sm text-gray-800 mb-1">
              <MapPin className="w-4 h-4 mt-0.5 text-red-500" />
              <p>
                Av. São Paulo, 1000 – Além Ponte, Sorocaba – SP
              </p>
            </div>

            <div className="flex items-start gap-2 text-sm text-gray-500 mb-3">
              <MapPin className="w-4 h-4 mt-0.5" />
              <p>
                Sorocaba – SP, Brasil
              </p>
            </div>

            {/* TELEFONE */}
            <div className="flex items-center gap-2 text-sm text-red-500 mb-5">
              <Phone className="w-4 h-4" />
              <p>(15) 3212-9000</p>
            </div>

            {/* MAPA */}
            <div className="rounded-xl overflow-hidden mb-5">
              <iframe
                src="https://www.google.com/maps?q=Ramires+Motors+Toyota+Sorocaba&output=embed"
                className="w-full h-52 border-0"
                loading="lazy"
              ></iframe>
            </div>

            {/* BOTÃO */}
            <a
              href="https://www.google.com/maps/dir/?api=1&destination=Ramires+Motors+Toyota+Sorocaba"
              target="_blank"
              className="
                w-full 
                bg-red-500 
                text-white 
                py-3.5 
                rounded-xl 
                text-center 
                font-semibold
                flex items-center justify-center gap-2
                hover:bg-red-600 
                hover:scale-[1.02]
                active:scale-[0.98]
                transition
              "
            >
              <MapPin size={18} />
              Como chegar
            </a>

          </div>

        </div>

      </div>
    </div>
  );
}