"use client";

import { useState } from "react";
import Sidebar from "@/app/componentes/SideBar";
import { ChevronLeft, ChevronRight } from "lucide-react";

const etapas = [
  "Prensas",
  "Funilaria",
  "Pintura",
  "Montagem",
  "Inspeção de Qualidade",

];

const descricoes = [
  "O processo de prensa é uma das primeiras e mais importantes etapas na fabricação de um veículo. Nessa fase, chapas de aço de alta resistência são transformadas nas peças estruturais que darão forma ao carro, como portas, teto, capô e laterais da carroceria.",

  "A etapa de funilaria é responsável por dar forma e estrutura à carroceria do veículo. Após a prensagem das chapas de aço, as peças passam por um processo de montagem onde são alinhadas, ajustadas e unidas para formar o “esqueleto” do carro.",

  "A etapa de pintura é responsável por dar acabamento final ao veículo, garantindo não apenas sua aparência, mas também proteção contra corrosão e agentes externos. Após a montagem da carroceria, o veículo passa por um processo altamente controlado para receber as camadas de tinta.",

  "A etapa de montagem é o momento em que o veículo ganha vida e se torna um produto completo. Após passar pelas fases de estrutura e pintura, a carroceria segue para a linha de montagem final, onde todos os componentes são instalados.",

  "A etapa de inspeção é a fase final do processo de fabricação e representa a última garantia de qualidade antes do veículo ser entregue ao cliente. Nesse momento, o carro passa por uma verificação completa para assegurar que todos os sistemas estejam funcionando perfeitamente.",

  "A produção foi concluída com sucesso. O veículo está completamente montado, revisado e pronto para transporte.",

  "O veículo aguarda liberação logística. A equipe organiza o envio por caminhões cegonha com total segurança.",

  "Seu veículo está em trânsito. Ele está sendo transportado até a concessionária com todos os cuidados necessários.",

  "O veículo chegou à concessionária e passa pelos últimos ajustes, limpeza e preparação final antes da entrega.",

  "Tudo pronto! Seu veículo está disponível para retirada. Agora é só ir até a concessionária e aproveitar seu carro novo.",
];

const videos = [
  "https://www.youtube.com/embed/iSQJJ1z70l4",
  "https://www.youtube.com/embed/86nZPoKBBPw",
  "https://www.youtube.com/embed/AFHRTp5DGTA",
  "https://www.youtube.com/embed/_g-CQJsI3yA",
  "https://www.youtube.com/embed/7mq9PCMHNQw",
  
];

export default function SaibaMais() {
  const [etapa, setEtapa] = useState(0);

  function next() {
    if (etapa < etapas.length - 1) setEtapa(etapa + 1);
  }

  function prev() {
    if (etapa > 0) setEtapa(etapa - 1);
  }

  const progresso = Math.round(((etapa + 1) / etapas.length) * 100);

  return (
    <div className="flex min-h-screen bg-gray-100">
      <Sidebar />

      <div className="flex-1 flex justify-center">
        <div className="w-full max-w-5xl p-6 md:p-10">

          {/* HEADER */}
          <div className="mb-8 text-center">
            <h1 className="text-3xl font-bold text-gray-900">
              Conheça nossa fábrica
            </h1>
            <p className="text-gray-600 mt-2">
              Etapa {etapa + 1} de {etapas.length}
            </p>
          </div>

          {/* VIDEO MAIOR */}
          <div className="w-full h-[100px] md:h-[300px] rounded-2xl overflow-hidden shadow-lg mb-6">
            <iframe
              src={videos[etapa]}
              className="w-full h-full"
              allowFullScreen
            />
          </div>

          {/* SETAS */}
          <div className="flex items-center justify-between mb-6">

            <button
              onClick={prev}
              disabled={etapa === 0}
              className="flex items-center gap-2 px-4 py-2 rounded-xl bg-gray-400 shadow hover:bg-gray-300 disabled:opacity-40"
            >
              <ChevronLeft size={18} />
              Anterior
            </button>

            <button
              onClick={next}
              disabled={etapa === etapas.length - 1}
              className="flex items-center gap-2 px-4 py-2 rounded-xl bg-red-600 text-white shadow hover:bg-red-700 disabled:opacity-40"
            >
              Próximo
              <ChevronRight size={18} />
            </button>

          </div>

          {/* TÍTULO */}
          <h2 className="text-2xl font-bold text-gray-900 text-center mb-4">
            {etapas[etapa]}
          </h2>

          {/* DESCRIÇÃO */}
          <p className="text-base md:text-lg text-gray-700 leading-7 md:leading-8 text-center mb-8 px-2 md:px-10">
            {descricoes[etapa]}
          </p>

          {/* PROGRESSO */}
          <div>
            <div className="flex justify-between text-sm text-gray-600 mb-2">
              <span>Progresso</span>
              <span>{progresso}%</span>
            </div>

            <div className="w-full h-3 bg-gray-300 rounded-full">
              <div
                className="h-3 bg-red-600 rounded-full transition-all"
                style={{ width: `${progresso}%` }}
              />
            </div>
          </div>

        </div>
      </div>
    </div>
  );
}