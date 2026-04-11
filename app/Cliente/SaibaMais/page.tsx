"use client";

import { useState } from "react";
import Sidebar from "@/app/componentes/SideBar";

const etapas = [
  "Compra realizada",
  "Pedido encaminhado",
  "Início de produção",
  "Qualidade",
  "Vistoria",
  "Pedido pronto",
  "Aguardando cegonha",
  "Em trânsito",
  "Concessionária",
  "Retirada",
];

const descricoes = [
  "Parabéns! Sua compra foi confirmada com sucesso.",
  "Seu pedido foi encaminhado à fábrica.",
  "Seu veículo começou a produção.",
  "O carro está passando por testes.",
  "Inspeção final em andamento.",
  "Seu veículo está pronto.",
  "Aguardando transporte.",
  "Seu veículo está a caminho.",
  "Chegou na concessionária.",
  "Pronto para retirada.",
];

export default function SaibaMais() {
  const [etapa, setEtapa] = useState(1);

  // 🔥 POSIÇÕES CORRETAS
const posicoes = [
  { top: "93%", left: "48%" },
  { top: "80%", left: "95%" },
  { top: "60%", left: "62%" },
  { top: "47%", left: "85%" },
  { top: "20%", left: "65%" },
  { top: "70%", left: "45%" },
  { top: "8%", left: "20%" },
  { top: "49%", left: "25%" },
  { top: "70%", left: "6%" },
  { top: "93%", left: "25%" },
];
  // 🔥 PROGRESSO (tava faltando)
  const progresso = Math.round((etapa / etapas.length) * 100);

  return (
    <div className="flex min-h-screen bg-gray-100">

      <Sidebar />

      <div className="flex-1 flex justify-center">

        <div className="w-full max-w-lg p-5">

          {/* HEADER */}
          <div className="mb-6 text-center">
            <h1 className="text-2xl font-bold text-gray-900">
              Acompanhamento
            </h1>
            <p className="text-sm text-gray-600">
              Etapa {etapa} de {etapas.length}
            </p>
          </div>

          {/* 🛣️ MAPA */}
          <div className="relative w-full h-[320px] rounded-xl overflow-hidden shadow mb-6">

            <img
              src="/pista.png"
              className="w-full h-full object-cover"
            />

            {/* 🔴 PONTOS */}
            {posicoes.map((p, i) => {
              const n = i + 1;

              return (
                <button
                  key={n}
                  onClick={() => setEtapa(n)}
                  className={`absolute w-7 h-7 rounded-full text-xs flex items-center justify-center font-bold text-white shadow-md border-2 z-10
                    ${
                      n <= etapa
                        ? "bg-red-600 border-white"
                        : "bg-gray-400 border-white"
                    }
                  `}
                  style={{
                    left: p.left,
                    top: p.top,
                    transform: "translate(-50%, -50%)",
                  }}
                >
                  {n}
                </button>
              );
            })}

            {/* 🚗 CARRO */}
            <div
              className="absolute transition-all duration-700 z-50"
              style={{
                left: posicoes[etapa - 1]?.left,
                top: posicoes[etapa - 1]?.top,
                transform: "translate(-50%, -120%)", // 🔥 sobe o carro acima do ponto
              }}
            >
              <div className="text-3xl drop-shadow-2xl">
                🚗
              </div>
            </div>

          </div>

          {/* ETAPA ATUAL */}
          <div className="flex items-center gap-4 justify-center mb-4">

            <div className="w-12 h-12 rounded-full bg-red-600 text-white flex items-center justify-center font-bold">
              {etapa}
            </div>

            <div>
              <h2 className="font-bold text-gray-900 text-lg">
                {etapas[etapa - 1]}
              </h2>
            </div>

          </div>

          {/* DESCRIÇÃO */}
          <div className="bg-white p-4 rounded-xl shadow mb-4 text-center">
            <p className="text-sm text-gray-800">
              {descricoes[etapa - 1]}
            </p>
          </div>

          {/* PROGRESSO */}
          <div className="mb-4">
            <div className="flex justify-between text-sm text-gray-600 mb-1">
              <span>Progresso</span>
              <span>{progresso}%</span>
            </div>

            <div className="w-full h-2 bg-gray-300 rounded-full">
              <div
                className="h-2 bg-red-600 rounded-full transition-all"
                style={{ width: `${progresso}%` }}
              />
            </div>
          </div>

          {/* LISTA */}
          <div className="grid grid-cols-2 gap-2">

            {etapas.map((e, i) => {
              const n = i + 1;

              return (
                <button
                  key={n}
                  onClick={() => setEtapa(n)}
                  className={`p-3 rounded-lg border text-xs text-center transition
                    ${
                      n === etapa
                        ? "bg-red-100 border-red-600 text-red-700 font-semibold"
                        : "bg-white border-gray-200 text-gray-700"
                    }
                  `}
                >
                  {n}. {e}
                </button>
              );
            })}

          </div>

        </div>
      </div>
    </div>
  );
}