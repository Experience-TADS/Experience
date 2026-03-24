"use client";

import { useState } from "react";
import Sidebar from "@/app/componentes/SideBar";
import {
  Car,
  Plus,
  ChevronDown,
  ChevronUp,
  X,
} from "lucide-react";

const etapas = [
  "Compra realizada",
  "Pedido encaminhado",
  "Produção",
  "Qualidade",
  "Transporte",
  "Concessionária",
  "Retirada",
];

const horarios = [
  "08:00","09:00","10:00","11:00",
  "13:00","14:00","15:00","16:00",
];

export default function Veiculo() {

  const [pedidos, setPedidos] = useState([
    {
      id: "1",
      modelo: "Corolla Cross",
      cor: "Branco",
      ano: "2026",
      status: 3,
      imagem: "/toyota_yaris.jpg", // 🔥 imagem do carro
    },
  ]);

  const [expandido, setExpandido] = useState<string | null>(null);

  const [modalAdd, setModalAdd] = useState(false);
  const [codigo, setCodigo] = useState("");

  const [modalAgendar, setModalAgendar] = useState(false);
  const [data, setData] = useState("");
  const [hora, setHora] = useState("");

  function adicionarPedido(e: any) {
    e.preventDefault();

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

      <div className="flex-1 p-5 md:p-10">

        {/* HEADER */}
        <div className="flex justify-between items-center mb-6">
          <h1 className="text-xl font-bold text-gray-900">
            Seus Veículos
          </h1>

          <button
            onClick={() => setModalAdd(true)}
            className="bg-red-500 text-white px-4 py-2 rounded-lg flex items-center gap-2"
          >
            <Plus size={16} />
            Adicionar
          </button>
        </div>

        {/* LISTA */}
        {pedidos.map((p) => {
          const aberto = expandido === p.id;

          return (
            <div key={p.id} className="mb-4">

              {/* CARD */}
              <div
                onClick={() => setExpandido(aberto ? null : p.id)}
                className="bg-white p-5 rounded-xl shadow cursor-pointer hover:shadow-xl transition-shadow max-w-sm mx-auto"
              >
                <div className="flex items-start justify-between">
                  <div>
                    <p className="text-xs text-gray-800 font-medium">
                      Pedido #{p.id}
                    </p>

                    <h2 className="text-lg font-bold text-gray-900">
                      {p.modelo}
                    </h2>

                    <p className="text-sm text-gray-800">
                      {p.cor} • {p.ano}
                    </p>
                  </div>

                  <div className="flex items-center gap-2">
                    <div className="w-10 h-10 rounded-xl bg-red-100 flex items-center justify-center">
                      <Car className="w-5 h-5 text-red-500" />
                    </div>

                    {aberto ? (
                      <ChevronUp className="w-4 h-4 text-gray-800" />
                    ) : (
                      <ChevronDown className="w-4 h-4 text-gray-800" />
                    )}
                  </div>
                </div>

                <div className="mt-3">
                  <span className="text-xs font-semibold text-red-600 bg-red-100 px-2 py-1 rounded-full">
                    {etapas[p.status]}
                  </span>
                </div>
              </div>

              {/* DETALHES */}
              {aberto && (
                <div className="space-y-4 animate-in fade-in slide-in-from-top-2 duration-300 max-w-sm mx-auto">

                  {/* 🔥 IMAGEM DO CARRO */}
                  <div className="w-full h-40 rounded-xl overflow-hidden">
                    <img
                      src={p.imagem}
                      className="w-full h-full object-cover"
                    />
                  </div>

                  <div className="bg-white p-4 rounded-xl shadow">

                    <h3 className="font-bold text-gray-900 mb-4">
                      Acompanhamento
                    </h3>

                    {etapas.map((etapa, i) => (
                      <div key={i} className="flex items-center gap-3 mb-2">

                        <div
                          className={`w-3 h-3 rounded-full ${
                            i <= p.status
                              ? "bg-red-600"
                              : "bg-gray-400"
                          }`}
                        />

                        <span
                          className={`text-sm ${
                            i <= p.status
                              ? "text-gray-900 font-semibold"
                              : "text-gray-600"
                          }`}
                        >
                          {etapa}
                        </span>

                      </div>
                    ))}

                    <button
                      onClick={(e) => {
                        e.stopPropagation();
                        setModalAgendar(true);
                      }}
                      className="mt-4 w-full bg-red-500 text-white py-2 rounded-lg font-semibold"
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
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">

          <div className="bg-white p-6 rounded-xl w-[90%] max-w-sm relative">

            <X
              onClick={() => setModalAdd(false)}
              className="absolute right-4 top-4 cursor-pointer text-gray-800"
            />

            <h2 className="font-bold mb-4 text-gray-900">
              Adicionar Veículo
            </h2>

            <form onSubmit={adicionarPedido} className="space-y-3">

              <input
                value={codigo}
                onChange={(e) => setCodigo(e.target.value)}
                placeholder="Código"
                className="w-full border p-2 rounded text-gray-900"
              />

              <button className="w-full bg-red-500 text-white py-2 rounded font-semibold">
                Adicionar
              </button>

            </form>

          </div>

        </div>
      )}

      {/* MODAL AGENDAR */}
      {modalAgendar && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">

          <div className="bg-white p-6 rounded-xl w-[90%] max-w-sm relative">

            <X
              onClick={() => setModalAgendar(false)}
              className="absolute right-4 top-4 cursor-pointer text-gray-800"
            />

            <h2 className="font-bold mb-4 text-gray-900">
              Agendar Retirada
            </h2>

            <input
              type="date"
              className="w-full border p-2 rounded mb-3 text-gray-900 font-medium"
              value={data}
              onChange={(e) => setData(e.target.value)}
            />

            <div className="grid grid-cols-3 gap-2 mb-4">
              {horarios.map((h) => (
                <button
                  key={h}
                  onClick={() => setHora(h)}
                  className={`py-2 rounded border text-sm font-medium ${
                    hora === h
                      ? "bg-red-500 text-white"
                      : "text-gray-800"
                  }`}
                >
                  {h}
                </button>
              ))}
            </div>

            <button
              onClick={confirmarAgendamento}
              className="w-full bg-red-500 text-white py-2 rounded font-semibold"
            >
              Confirmar
            </button>

          </div>

        </div>
      )}

    </div>
  );
}