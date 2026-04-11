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

      <div className="flex-1 flex flex-col items-center p-5 md:p-10 md:ml-20">

        {/* HEADER */}
        <div className="w-full max-w-md sticky top-0 z-20 bg-gray-100 pb-4 mb-6">
          <div className="flex justify-between items-center pt-4">
            <div>
              <h1 className="text-xl font-bold text-gray-900">
                Seus Veículos
              </h1>
              <p className="text-sm text-gray-700">
                {pedidos.length} pedidos
              </p>
            </div>

            <button
              onClick={() => setModalAdd(true)}
              className="bg-red-600 text-white px-4 py-2 rounded-lg flex items-center gap-2 shadow"
            >
              <Plus size={16} />
              Adicionar
            </button>
          </div>
        </div>

        {/* LISTA */}
        {pedidos.map((p) => {
          const aberto = expandido === p.id;

          return (
            <div key={p.id} className="w-full max-w-md mb-6">

              {/* CARD */}
              <div
                onClick={() => setExpandido(aberto ? null : p.id)}
                className="bg-white p-5 rounded-xl shadow cursor-pointer"
              >
                <div className="flex justify-between">

                  <div>
                    <p className="text-xs text-gray-600">
                      Pedido #{p.id}
                    </p>

                    <h2 className="text-lg font-bold text-gray-900">
                      {p.modelo}
                    </h2>

                    <p className="text-sm text-gray-700">
                      {p.cor} • {p.ano}
                    </p>

                    <span className="mt-2 inline-block text-xs font-semibold text-red-700 bg-red-100 px-2 py-1 rounded-full">
                      {etapas[p.status]}
                    </span>
                  </div>

                  <div className="flex items-center gap-2">
                    <div className="w-10 h-10 rounded-xl bg-red-100 flex items-center justify-center">
                      <Car className="w-5 h-5 text-red-600" />
                    </div>

                    {aberto ? <ChevronUp /> : <ChevronDown />}
                  </div>

                </div>
              </div>

              {/* DETALHES */}
              {aberto && (
                <div className="mt-4 space-y-4">

                  {/* IMAGEM (MANTIDA 🔥) */}
                  <div className="w-full h-40 rounded-xl overflow-hidden">
                    <img
                      src={p.imagem}
                      className="w-full h-full object-cover"
                    />
                  </div>

                  {/* TIMELINE */}
                  <div className="bg-white p-5 rounded-xl shadow">

                    <h3 className="text-lg font-bold text-gray-900 mb-6">
                      Acompanhamento
                    </h3>

                    <div className="relative">

                      {/* LINHA */}
                      <div className="absolute left-[13px] top-0 bottom-0 w-[2px] bg-red-500" />

                      {etapas.map((etapa, i) => {
                        const concluida = i <= p.status;

                        return (
                          <div key={i} className="flex items-start gap-4 mb-8">

                            {/* BOLINHA */}
                            <div className="z-10">
                              <div
                                className={`w-7 h-7 flex items-center justify-center rounded-full
                                ${
                                  concluida
                                    ? "bg-red-600"
                                    : "bg-gray-300"
                                }`}
                              >
                                {concluida && (
                                  <span className="text-white text-xs">✓</span>
                                )}
                              </div>
                            </div>

                            {/* TEXTO */}
                            <div>
                              <p className="text-sm font-semibold text-gray-900">
                                {etapa}
                              </p>

                              <p className="text-xs text-gray-500">
                                15 de jan.
                              </p>

                              <button
                                onClick={(e) => {
                                  e.stopPropagation();
                                  router.push("/Cliente/SaibaMais");
                                }}
                                className="text-xs text-gray-500 mt-1 hover:text-red-600"
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
                      className="mt-4 w-full bg-red-600 text-white py-2 rounded-lg font-semibold"
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
        <div className="fixed inset-0 bg-black/60 flex items-center justify-center z-50">
          <div className="bg-white p-6 rounded-xl w-[90%] max-w-sm">
            <h2 className="font-bold mb-4">Adicionar Veículo</h2>

            <form onSubmit={adicionarPedido}>
              <input
                value={codigo}
                onChange={(e) => setCodigo(e.target.value)}
                className="w-full border p-2 mb-3"
                placeholder="Código"
              />
              <button className="w-full bg-red-600 text-white py-2 rounded">
                Adicionar
              </button>
            </form>
          </div>
        </div>
      )}

      {/* MODAL AGENDAR */}
      {modalAgendar && (
        <div className="fixed inset-0 bg-black/60 flex items-center justify-center z-50">
          <div className="bg-white p-6 rounded-xl w-[90%] max-w-sm">

            <h2 className="font-bold mb-4">Agendar Retirada</h2>

            <input
              type="date"
              className="w-full border p-2 mb-3"
              value={data}
              onChange={(e) => setData(e.target.value)}
            />

            <div className="grid grid-cols-3 gap-2 mb-4">
              {horarios.map((h) => (
                <button
                  key={h}
                  onClick={() => setHora(h)}
                  className={`py-2 border rounded ${
                    hora === h ? "bg-red-600 text-white" : ""
                  }`}
                >
                  {h}
                </button>
              ))}
            </div>

            <button
              onClick={confirmarAgendamento}
              className="w-full bg-red-600 text-white py-2 rounded"
            >
              Confirmar
            </button>

          </div>
        </div>
      )}

    </div>
  );
}