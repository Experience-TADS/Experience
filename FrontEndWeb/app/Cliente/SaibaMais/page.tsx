"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { ChevronLeft, ChevronRight, ArrowLeft } from "lucide-react";

const etapas = [
  "Prensas",
  "Funilaria",
  "Pintura",
  "Montagem",
  "Inspeção de Qualidade",
];

const descricoes = [
  "O processo de prensa é uma das primeiras e mais importantes etapas na fabricação de um veículo. Nessa fase, chapas de aço de alta resistência são transformadas nas peças estruturais que darão forma ao carro, como portas, teto, capô e laterais da carroceria.",
  "A etapa de funilaria é responsável por dar forma e estrutura à carroceria do veículo. Após a prensagem das chapas de aço, as peças passam por um processo de montagem onde são alinhadas, ajustadas e unidas para formar o esqueleto do carro.",
  "A etapa de pintura é responsável por dar acabamento final ao veículo, garantindo não apenas sua aparência, mas também proteção contra corrosão e agentes externos. Após a montagem da carroceria, o veículo passa por um processo altamente controlado para receber as camadas de tinta.",
  "A etapa de montagem é o momento em que o veículo ganha vida e se torna um produto completo. Após passar pelas fases de estrutura e pintura, a carroceria segue para a linha de montagem final, onde todos os componentes são instalados.",
  "A etapa de inspeção é a fase final do processo de fabricação e representa a última garantia de qualidade antes do veículo ser entregue ao cliente. Nesse momento, o carro passa por uma verificação completa para assegurar que todos os sistemas estejam funcionando perfeitamente.",
];

const videos = [
  "https://www.youtube.com/embed/iSQJJ1z70l4",
  "https://www.youtube.com/embed/86nZPoKBBPw",
  "https://www.youtube.com/embed/AFHRTp5DGTA",
  "https://www.youtube.com/embed/_g-CQJsI3yA",
  "https://www.youtube.com/embed/7mq9PCMHNQw",
];

export default function SaibaMais() {
  const router = useRouter();
  const [etapa, setEtapa] = useState(0);

  const progresso = Math.round(((etapa + 1) / etapas.length) * 100);

  return (
    <div className="min-h-screen bg-gray-100 dark:bg-gray-950">

      {/* TOP BAR */}
      <div className="sticky top-0 z-10 bg-white dark:bg-gray-900 border-b border-gray-200 dark:border-gray-800 px-6 py-4 flex items-center justify-between shadow-sm">
        <button
          onClick={() => router.push("/Cliente/Acompanhamento")}
          className="flex items-center gap-2 text-sm font-medium text-gray-500 dark:text-gray-400 hover:text-red-600 dark:hover:text-red-500 transition"
        >
          <ArrowLeft size={16} />
          Voltar
        </button>

        <p className="text-xs text-gray-400 font-medium tracking-widest uppercase hidden sm:block">
          Processo de Fabricação
        </p>

        <span className="text-sm text-gray-400 font-medium">
          {etapa + 1} / {etapas.length}
        </span>
      </div>

      <div className="max-w-4xl mx-auto px-5 py-8">

        {/* BADGE + TÍTULO */}
        <div className="text-center mb-8">
          <span className="inline-block text-xs font-semibold tracking-widest uppercase text-red-600 bg-red-50 dark:bg-red-900/30 dark:text-red-400 px-3 py-1 rounded-full mb-4">
            Etapa {etapa + 1} de {etapas.length}
          </span>
          <h1 className="text-3xl md:text-4xl font-bold text-gray-900 dark:text-white">
            {etapas[etapa]}
          </h1>
        </div>

        {/* VÍDEO */}
        <div className="w-full aspect-video rounded-2xl overflow-hidden shadow-xl border border-gray-200 dark:border-gray-800 mb-8">
          <iframe
            key={etapa}
            src={videos[etapa]}
            className="w-full h-full"
            allowFullScreen
            allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
          />
        </div>

        {/* DESCRIÇÃO */}
        <div className="bg-white dark:bg-gray-900 rounded-2xl shadow-sm border border-gray-100 dark:border-gray-800 p-6 md:p-8 mb-8">
          <p className="text-gray-700 dark:text-gray-300 text-base md:text-lg leading-relaxed text-center">
            {descricoes[etapa]}
          </p>
        </div>

        {/* BARRA DE PROGRESSO — única */}
        <div className="bg-white dark:bg-gray-900 rounded-2xl shadow-sm border border-gray-100 dark:border-gray-800 px-6 py-5 mb-6">
          <div className="flex justify-between text-sm text-gray-500 mb-3">
            <span className="dark:text-gray-400">Progresso</span>
            <span className="font-semibold text-red-600">{progresso}%</span>
          </div>
          <div className="w-full h-2 bg-gray-100 dark:bg-gray-800 rounded-full">
            <div
              className="h-2 bg-red-600 rounded-full transition-all duration-500"
              style={{ width: `${progresso}%` }}
            />
          </div>
        </div>

        {/* NAVEGAÇÃO */}
        <div className="flex items-center justify-between gap-4">
          <button
            onClick={() => setEtapa((e) => Math.max(e - 1, 0))}
            disabled={etapa === 0}
            className="flex items-center gap-2 px-5 py-3 rounded-xl bg-white dark:bg-gray-900 border border-gray-200 dark:border-gray-700 shadow-sm hover:bg-gray-50 dark:hover:bg-gray-800 disabled:opacity-30 disabled:cursor-not-allowed transition text-sm font-medium text-gray-700 dark:text-gray-300"
          >
            <ChevronLeft size={16} />
            Anterior
          </button>

          <span className="text-sm text-gray-400 hidden sm:block">{etapas[etapa]}</span>

          <button
            onClick={() => setEtapa((e) => Math.min(e + 1, etapas.length - 1))}
            disabled={etapa === etapas.length - 1}
            className="flex items-center gap-2 px-5 py-3 rounded-xl bg-red-600 text-white shadow-sm hover:bg-red-700 disabled:opacity-30 disabled:cursor-not-allowed transition text-sm font-medium"
          >
            Próximo
            <ChevronRight size={16} />
          </button>
        </div>

      </div>
    </div>
  );
}
