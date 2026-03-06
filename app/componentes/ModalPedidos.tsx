"use client";

import { X } from "lucide-react";

const etapas = [
  "Pedido confirmado",
  "Em produção",
  "Controle de qualidade",
  "Em transporte",
  "Chegou na concessionária",
  "Retirado",
  "Finalizado",
];

const pontoCor: any = {
  "Pedido confirmado": "bg-red-600",
  "Em produção": "bg-yellow-500",
  "Controle de qualidade": "bg-orange-500",
  "Em transporte": "bg-purple-500",
  "Chegou na concessionária": "bg-blue-500",
  "Retirado": "bg-gray-700",
  "Finalizado": "bg-green-600",
};

export default function ModalPedido({
  pedido,
  statusAtual,
  setStatusAtual,
  fecharModal,
}: any) {
  if (!pedido) return null;

  return (
    <div className="fixed inset-0 bg-black/60 flex items-center justify-center z-50">

      <div className="bg-white w-[500px] rounded-2xl p-8 relative shadow-2xl">

        {/* BOTÃO FECHAR */}
        <X
          onClick={fecharModal}
          className="absolute right-5 top-5 cursor-pointer text-gray-700 hover:text-red-600 transition"
        />

        {/* TÍTULO */}
        <h2 className="text-2xl font-bold text-gray-900 mb-6">
          Pedido {pedido.id}
        </h2>

        {/* INFORMAÇÕES */}
        <div className="grid grid-cols-2 gap-6 text-sm mb-6">

          <div>
            <p className="text-gray-600 font-medium">Cliente</p>
            <p className="text-gray-900 font-semibold">{pedido.cliente}</p>
          </div>

          <div>
            <p className="text-gray-600 font-medium">Email</p>
            <p className="text-gray-900 font-semibold">{pedido.email}</p>
          </div>

          <div>
            <p className="text-gray-600 font-medium">Veículo</p>
            <p className="text-gray-900 font-semibold">{pedido.veiculo}</p>
          </div>

          <div>
            <p className="text-gray-600 font-medium">Data</p>
            <p className="text-gray-900 font-semibold">{pedido.data}</p>
          </div>

        </div>

        {/* SELECT STATUS */}
        <p className="text-gray-700 font-semibold mb-2">
          Atualizar Status
        </p>

        <select
          value={statusAtual}
          onChange={(e) => setStatusAtual(e.target.value)}
          className="w-full border-2 border-red-500 rounded-xl p-3 mb-8 text-gray-900 font-medium bg-white focus:outline-none focus:ring-2 focus:ring-red-500"
        >
          {etapas.map((etapa) => (
            <option key={etapa}>{etapa}</option>
          ))}
        </select>

        {/* ACOMPANHAMENTO */}
        <h3 className="font-bold text-gray-800 mb-4">
          Acompanhamento
        </h3>

        <div className="space-y-3">

          {etapas.map((etapa) => {
            const ativo =
              etapas.indexOf(etapa) <= etapas.indexOf(statusAtual);

            return (
              <div key={etapa} className="flex items-center gap-3">

                <div
                  className={`w-3 h-3 rounded-full ${
                    ativo ? pontoCor[etapa] : "bg-gray-400"
                  }`}
                />

                <span
                  className={`text-sm ${
                    ativo
                      ? "text-gray-900 font-semibold"
                      : "text-gray-500"
                  }`}
                >
                  {etapa}
                </span>

              </div>
            );
          })}

        </div>

      </div>

    </div>
  );
}