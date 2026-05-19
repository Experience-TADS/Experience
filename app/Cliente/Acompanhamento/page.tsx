"use client";

import { useState } from "react";
import Sidebar from "@/app/componentes/SideBar";
import { Car, Plus, ChevronDown, ChevronUp } from "lucide-react";
import { useRouter } from "next/navigation";

const etapas = ["Compra realizada","Pedido encaminhado","Início de produção","Qualidade","Vistoria","Pedido pronto"];
const horarios = ["08:00","09:00","10:00","11:00","13:00","14:00","15:00","16:00"];

export default function Veiculo() {
  const router = useRouter();
  const [pedidos, setPedidos] = useState([{ id: "1", modelo: "Hilux", cor: "Prata Nívea", ano: "2026", status: 3, imagem: "/toyota_yaris.jpg" }]);
  const [expandido, setExpandido] = useState<string | null>(null);
  const [modalAdd, setModalAdd] = useState(false);
  const [codigo, setCodigo] = useState("");
  const [modalAgendar, setModalAgendar] = useState(false);
  const [data, setData] = useState("");
  const [hora, setHora] = useState("");

  function adicionarPedido() {
    if (!codigo) return;
    setPedidos([...pedidos, { id: Date.now().toString(), modelo: "Novo Veículo", cor: "Preto", ano: "2025", status: 1, imagem: "/toyota_yaris.jpg" }]);
    setCodigo(""); setModalAdd(false);
  }

  function confirmarAgendamento() {
    if (!data || !hora) return;
    alert(`Retirada agendada para ${data} às ${hora}`);
    setModalAgendar(false);
  }

  return (
    <div className="flex min-h-screen bg-gray-100 dark:bg-gray-950">
      <Sidebar />

      <div className="flex-1 flex flex-col items-center px-5 md:px-12 py-8 md:ml-20 pb-24 md:pb-8">

        {/* HEADER */}
        <div className="w-full max-w-3xl sticky top-0 z-20 bg-gray-100 dark:bg-gray-950 pb-5 mb-8">
          <div className="flex justify-between items-center pt-4">
            <div>
              <h1 className="text-2xl font-bold text-gray-900 dark:text-gray-100">Seus Veículos</h1>
              <p className="text-sm text-gray-600 dark:text-gray-400 mt-1">{pedidos.length} pedidos</p>
            </div>
            <button onClick={() => setModalAdd(true)} className="bg-red-600 hover:bg-red-700 transition text-white px-5 py-2.5 rounded-xl flex items-center gap-2 shadow-md">
              <Plus size={18} /> Adicionar
            </button>
          </div>
        </div>

        {/* LISTA */}
        {pedidos.map((p) => {
          const aberto = expandido === p.id;
          return (
            <div key={p.id} className="w-full max-w-3xl mb-8">
              <div onClick={() => setExpandido(aberto ? null : p.id)} className="bg-white dark:bg-gray-900 p-7 rounded-3xl shadow-md hover:shadow-xl transition duration-300 cursor-pointer border border-gray-100 dark:border-gray-700">
                <div className="flex justify-between items-center gap-6">
                  <div>
                    <p className="text-xs text-gray-500 dark:text-gray-400">Pedido #{p.id}</p>
                    <h2 className="text-2xl font-bold text-gray-900 dark:text-gray-100 mt-1">{p.modelo}</h2>
                    <p className="text-sm text-gray-600 dark:text-gray-400 mt-1">{p.cor} • {p.ano}</p>
                    <span className="mt-4 inline-block text-xs font-semibold text-red-700 dark:text-red-400 bg-red-100 dark:bg-red-900/30 px-4 py-1.5 rounded-full">
                      {etapas[p.status]}
                    </span>
                  </div>
                  <div className="flex items-center gap-4">
                    <div className="w-14 h-14 rounded-2xl bg-red-100 dark:bg-red-900/30 flex items-center justify-center">
                      <Car className="w-7 h-7 text-red-600" />
                    </div>
                    {aberto ? <ChevronUp size={22} className="text-gray-600 dark:text-gray-400" /> : <ChevronDown size={22} className="text-gray-600 dark:text-gray-400" />}
                  </div>
                </div>
              </div>

              {aberto && (
                <div className="mt-6 space-y-6">
                  <div className="w-full h-64 rounded-3xl overflow-hidden shadow">
                    <img src={p.imagem} className="w-full h-full object-cover" />
                  </div>
                  <div className="bg-white dark:bg-gray-900 p-7 rounded-3xl shadow-md border border-gray-100 dark:border-gray-700">
                    <h3 className="text-xl font-bold text-gray-900 dark:text-gray-100 mb-7">Acompanhamento</h3>
                    <div className="relative">
                      <div className="absolute left-[14px] top-0 bottom-0 w-[2px] bg-red-500" />
                      {etapas.map((etapa, i) => {
                        const concluida = i <= p.status;
                        return (
                          <div key={i} className="flex items-start gap-5 mb-10">
                            <div className="z-10">
                              <div className={`w-8 h-8 flex items-center justify-center rounded-full ${concluida ? "bg-red-600" : "bg-gray-300 dark:bg-gray-600"}`}>
                                {concluida && <span className="text-white text-xs">✓</span>}
                              </div>
                            </div>
                            <div>
                              <p className="text-sm font-semibold text-gray-900 dark:text-gray-100">{etapa}</p>
                              <p className="text-xs text-gray-400 dark:text-gray-500 mt-1">15 de jan.</p>
                              <button onClick={(e) => { e.stopPropagation(); router.push("/Cliente/SaibaMais"); }} className="text-xs text-gray-500 dark:text-gray-400 mt-2 hover:text-red-600 transition">
                                ⓘ Mais detalhes
                              </button>
                            </div>
                          </div>
                        );
                      })}
                    </div>
                    <button onClick={(e) => { e.stopPropagation(); setModalAgendar(true); }} className="mt-6 w-full bg-red-600 text-white py-3.5 rounded-xl font-semibold hover:bg-red-700 transition shadow">
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
          <div className="bg-white dark:bg-gray-900 p-7 rounded-3xl w-[90%] max-w-sm shadow-2xl relative border border-transparent dark:border-gray-700">
            <button onClick={() => setModalAdd(false)} className="absolute top-4 right-4 text-gray-400 hover:text-gray-700 dark:hover:text-gray-200 transition">✕</button>
            <h2 className="text-xl font-bold text-gray-900 dark:text-gray-100 mb-1">Adicionar veículo</h2>
            <p className="text-sm text-gray-500 dark:text-gray-400 mb-5">Insira o código do seu pedido</p>
            <label className="text-sm text-gray-600 dark:text-gray-400">Código do veículo</label>
            <input value={codigo} onChange={(e) => setCodigo(e.target.value)} placeholder="Ex: 123ABC"
              className="w-full border border-gray-200 dark:border-gray-700 bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100 p-3 mt-1 mb-5 rounded-xl focus:outline-none focus:ring-2 focus:ring-red-500" />
            <button onClick={adicionarPedido} className="w-full bg-red-600 text-white py-3 rounded-xl font-semibold hover:bg-red-700 transition shadow-md">Adicionar veículo</button>
          </div>
        </div>
      )}

      {/* MODAL AGENDAR */}
      {modalAgendar && (
        <div className="fixed inset-0 bg-black/40 backdrop-blur-sm flex items-center justify-center z-50">
          <div className="bg-white dark:bg-gray-900 p-7 rounded-3xl w-[90%] max-w-sm shadow-2xl relative border border-transparent dark:border-gray-700">
            <button onClick={() => setModalAgendar(false)} className="absolute top-4 right-4 text-gray-400 hover:text-gray-700 dark:hover:text-gray-200 transition">✕</button>
            <h2 className="text-xl font-bold text-gray-900 dark:text-gray-100 mb-1">Agendar retirada</h2>
            <p className="text-sm text-gray-500 dark:text-gray-400 mb-4">Escolha a data e o horário disponível</p>
            <label className="text-sm text-gray-600 dark:text-gray-400">Data</label>
            <input type="date" value={data} onChange={(e) => setData(e.target.value)}
              className="w-full border border-gray-200 dark:border-gray-700 bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100 p-3 mt-1 mb-4 rounded-xl focus:outline-none focus:ring-2 focus:ring-red-500" />
            <label className="text-sm text-gray-600 dark:text-gray-400 mb-2 block">Horário</label>
            <div className="grid grid-cols-3 gap-3 mb-5">
              {horarios.map((h) => (
                <button key={h} onClick={() => setHora(h)}
                  className={`py-2.5 rounded-xl text-sm font-medium border transition ${hora === h ? "bg-red-600 text-white border-red-600 shadow-md" : "bg-white dark:bg-gray-800 text-gray-700 dark:text-gray-300 border-gray-200 dark:border-gray-600 hover:bg-gray-100 dark:hover:bg-gray-700"}`}>
                  {h}
                </button>
              ))}
            </div>
            <button onClick={confirmarAgendamento} className="w-full bg-red-600 text-white py-3 rounded-xl font-semibold hover:bg-red-700 transition shadow-md">Confirmar agendamento</button>
          </div>
        </div>
      )}
    </div>
  );
}
