"use client";

import { useState } from "react";
import Sidebar from "@/app/componentes/SideBar";
import {
  Car,
  Plus,
  ChevronDown,
  ChevronUp,
} from "lucide-react";
import { useRouter } from "next/navigation";

const etapas = [
  "Compra realizada",
  "Pedido encaminhado",
  "Início de produção",
  "Qualidade",
  "Vistoria",
  "Pedido pronto",
];

const horarios = [
  "08:00","09:00","10:00","11:00",
  "13:00","14:00","15:00","16:00",
];

export default function Veiculo() {
  const router = useRouter();

  const [pedidos, setPedidos] = useState([
    {
      id: "1",
      modelo: "Hilux",
      cor: "Prata Nívea",
      ano: "2026",
      status: 3,
      imagem: "/toyota_yaris.jpg",
    },
  ]);

  const [expandido, setExpandido] = useState<string | null>(null);
  const [modalAdd, setModalAdd] = useState(false);
  const [codigo, setCodigo] = useState("");

  const [modalAgendar, setModalAgendar] = useState(false);
  const [data, setData] = useState("");
  const [hora, setHora] = useState("");

  function adicionarPedido(e?: any) {
    if (e) e.preventDefault();
    if (!codigo) return;

    setPedidos([
      ...pedidos,
      {
        id: Date.now().toString(),
        modelo: "Novo Veículo",
        cor: "Preto",
        ano: "2025",
        status: 1,
        imagem: "/toyota_yaris.jpg",
      },
    ]);

    setCodigo("");
    setModalAdd(false);
  }

  function confirmarAgendamento() {
    if (!data || !hora) return;
    alert(`Retirada agendada para ${data} às ${hora}`);
    setModalAgendar(false);
  }

  return (
    <div className="flex min-h-screen bg-gray-100">

      <Sidebar />

      <div className="flex-1 flex flex-col items-center px-5 md:px-12 py-8 md:ml-20">

        {/* HEADER */}
        <div className="w-full max-w-3xl sticky top-0 z-20 bg-gray-100 pb-5 mb-8">
          <div className="flex justify-between items-center pt-4">
            <div>
              <h1 className="text-2xl font-bold text-gray-900">
                Seus Veículos
              </h1>
              <p className="text-sm text-gray-600 mt-1">
                {pedidos.length} pedidos
              </p>
            </div>

            <button
              onClick={() => setModalAdd(true)}
              className="bg-red-600 hover:bg-red-700 transition text-white px-5 py-2.5 rounded-xl flex items-center gap-2 shadow-md"
            >
              <Plus size={18} />
              Adicionar
            </button>
          </div>
        </div>

        {/* LISTA */}
        {pedidos.map((p) => {
          const aberto = expandido === p.id;

          return (
            <div key={p.id} className="w-full max-w-3xl mb-8">

              {/* CARD */}
              <div
                onClick={() => setExpandido(aberto ? null : p.id)}
                className="bg-white p-7 rounded-3xl shadow-md hover:shadow-xl transition duration-300 cursor-pointer border border-gray-100"
              >
                <div className="flex justify-between items-center gap-6">

                  <div>
                    <p className="text-xs text-gray-500">
                      Pedido #{p.id}
                    </p>

                    <h2 className="text-2xl font-bold text-gray-900 mt-1">
                      {p.modelo}
                    </h2>

                    <p className="text-sm text-gray-600 mt-1">
                      {p.cor} • {p.ano}
                    </p>

                    <span className="mt-4 inline-block text-xs font-semibold text-red-700 bg-red-100 px-4 py-1.5 rounded-full">
                      {etapas[p.status]}
                    </span>
                  </div>

                  <div className="flex items-center gap-4">
                    <div className="w-14 h-14 rounded-2xl bg-red-100 flex items-center justify-center">
                      <Car className="w-7 h-7 text-red-600" />
                    </div>

                    {aberto ? <ChevronUp size={22}/> : <ChevronDown size={22}/>}
                  </div>

                </div>
              </div>

              {/* DETALHES */}
              {aberto && (
                <div className="mt-6 space-y-6">

                  {/* IMAGEM */}
                  <div className="w-full h-64 rounded-3xl overflow-hidden shadow">
                    <img
                      src={p.imagem}
                      className="w-full h-full object-cover"
                    />
                  </div>

                  {/* TIMELINE */}
                  <div className="bg-white p-7 rounded-3xl shadow-md border border-gray-100">

                    <h3 className="text-xl font-bold text-gray-900 mb-7">
                      Acompanhamento
                    </h3>

                    <div className="relative">
                      <div className="absolute left-[14px] top-0 bottom-0 w-[2px] bg-red-500" />

                      {etapas.map((etapa, i) => {
                        const concluida = i <= p.status;

                        return (
                          <div key={i} className="flex items-start gap-5 mb-10">

                            <div className="z-10">
                              <div
                                className={`w-8 h-8 flex items-center justify-center rounded-full
                                ${concluida ? "bg-red-600" : "bg-gray-300"}`}
                              >
                                {concluida && (
                                  <span className="text-white text-xs">✓</span>
                                )}
                              </div>
                            </div>

                            <div>
                              <p className="text-sm font-semibold text-gray-900">
                                {etapa}
                              </p>

                              <p className="text-xs text-gray-400 mt-1">
                                15 de jan.
                              </p>

                              <button
                                onClick={(e) => {
                                  e.stopPropagation();
                                  router.push("/Cliente/SaibaMais");
                                }}
                                className="text-xs text-gray-500 mt-2 hover:text-red-600 transition"
                              >
                                ⓘ Mais detalhes
                              </button>
                            </div>

                          </div>
                        );
                      })}
                    </div>

                    <button
                      onClick={(e) => {
                        e.stopPropagation();
                        setModalAgendar(true);
                      }}
                      className="mt-6 w-full bg-red-600 text-white py-3.5 rounded-xl font-semibold hover:bg-red-700 transition shadow"
                    >
                      Agendar retirada
                    </button>

                  </div>
                </div>
              )}
            </div>
          );
        })}

      </div>

      {/* MODAL ADD */}
      {modalAdd && (
        <div className="fixed inset-0 bg-black/40 backdrop-blur-sm flex items-center justify-center z-50">

          <div className="bg-white p-7 rounded-3xl w-[90%] max-w-sm shadow-2xl relative animate-in fade-in zoom-in-95 duration-200">

            <button
              onClick={() => setModalAdd(false)}
              className="absolute top-4 right-4 text-gray-400 hover:text-gray-700 transition"
            >
              ✕
            </button>

            <div className="mb-5">
              <h2 className="text-xl font-bold text-gray-900">
                Adicionar veículo
              </h2>
              <p className="text-sm text-gray-500 mt-1">
                Insira o código do seu pedido
              </p>
            </div>

            <div className="mb-5">
              <label className="text-sm text-gray-600">
                Código do veículo
              </label>
              <input
                value={codigo}
                onChange={(e) => setCodigo(e.target.value)}
                placeholder="Ex: 123ABC"
                className="w-full border border-gray-200 p-3 mt-1 rounded-xl focus:outline-none focus:ring-2 focus:ring-red-500"
              />
            </div>

            <button
              onClick={adicionarPedido}
              className="w-full bg-red-600 text-white py-3 rounded-xl font-semibold hover:bg-red-700 transition shadow-md"
            >
              Adicionar veículo
            </button>

          </div>
        </div>
      )}

      {/* MODAL AGENDAR */}
      {modalAgendar && (
        <div className="fixed inset-0 bg-black/40 backdrop-blur-sm flex items-center justify-center z-50">

          <div className="bg-white p-7 rounded-3xl w-[90%] max-w-sm shadow-2xl relative animate-in fade-in zoom-in-95 duration-200">

            <button
              onClick={() => setModalAgendar(false)}
              className="absolute top-4 right-4 text-gray-400 hover:text-gray-700 transition"
            >
              ✕
            </button>

            <div className="mb-5">
              <h2 className="text-xl font-bold text-gray-900">
                Agendar retirada
              </h2>
              <p className="text-sm text-gray-500 mt-1">
                Escolha a data e o horário disponível
              </p>
            </div>

            <div className="mb-4">
              <label className="text-sm text-gray-600">Data</label>
              <input
                type="date"
                className="w-full border border-gray-200 p-3 mt-1 rounded-xl focus:outline-none focus:ring-2 focus:ring-red-500"
                value={data}
                onChange={(e) => setData(e.target.value)}
              />
            </div>

            <div className="mb-5">
              <label className="text-sm text-gray-600 mb-2 block">
                Horário
              </label>

              <div className="grid grid-cols-3 gap-3">
                {horarios.map((h) => (
                  <button
                    key={h}
                    onClick={() => setHora(h)}
                    className={`py-2.5 rounded-xl text-sm font-medium border transition
                      ${
                        hora === h
                          ? "bg-red-600 text-white border-red-600 shadow-md"
                          : "bg-white text-gray-700 border-gray-200 hover:bg-gray-100"
                      }
                    `}
                  >
                    {h}
                  </button>
                ))}
              </div>
            </div>

            <button
              onClick={confirmarAgendamento}
              className="w-full bg-red-600 text-white py-3 rounded-xl font-semibold hover:bg-red-700 transition shadow-md"
            >
              Confirmar agendamento
            </button>

          </div>
        </div>
      )}

    </div>
  );
}