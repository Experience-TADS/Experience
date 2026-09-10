"use client";

import { useEffect, useState } from "react";
import {
  DollarSign, ShoppingCart, TrendingUp, Users, Loader2,
} from "lucide-react";
import {
  AreaChart, Area, BarChart, Bar,
  XAxis, YAxis, CartesianGrid, Tooltip,
  ResponsiveContainer,
} from "recharts";
import { api, type PedidoResponse } from "@/app/lib/api";
import VendedorSidebar from "@/app/Vendedor/componentes/VendedorSidebar";

function formatarData(dataPedido: any): string {
  if (!dataPedido) return "—";
  try {
    if (Array.isArray(dataPedido)) {
      const [ano, mes, dia] = dataPedido;
      return new Date(ano, mes - 1, dia).toLocaleDateString("pt-BR");
    }
    return new Date(dataPedido).toLocaleDateString("pt-BR");
  } catch { return "—"; }
}

const chartVendas = [
  { mes: "Jan", vendas: 12000, meta: 10000 },
  { mes: "Fev", vendas: 19000, meta: 15000 },
  { mes: "Mar", vendas: 15000, meta: 16000 },
  { mes: "Abr", vendas: 22000, meta: 18000 },
  { mes: "Mai", vendas: 18000, meta: 20000 },
  { mes: "Jun", vendas: 24000, meta: 21000 },
];

const chartVeiculos = [
  { modelo: "Corolla Cross", vendas: 18 },
  { modelo: "Hilux", vendas: 14 },
  { modelo: "SW4", vendas: 11 },
  { modelo: "RAV4", vendas: 8 },
  { modelo: "Yaris", vendas: 6 },
];

export default function Dashboard() {
  const hora = new Date().getHours();
  const saudacao = hora < 12 ? "Bom dia" : hora < 18 ? "Boa tarde" : "Boa noite";

  const [pedidos, setPedidos] = useState<PedidoResponse[]>([]);
  const [totalClientes, setTotalClientes] = useState(0);
  const [loading, setLoading] = useState(true);
  const [userName, setUserName] = useState("Vendedor");

  useEffect(() => {
    const user = localStorage.getItem("user");
    if (user) setUserName(user.split("@")[0]);

    async function carregar() {
      try {
        const [p, c] = await Promise.all([api.getPedidos(0, 5), api.getUsuarios(0, 1)]);
        setPedidos(p.content);
        setTotalClientes(c.totalElements);
      } finally { setLoading(false); }
    }
    carregar();
  }, []);

  const metrics = [
    { title: "Pedidos", value: loading ? "…" : String(pedidos.length), icon: ShoppingCart, color: "text-blue-500" },
    { title: "Clientes", value: loading ? "…" : String(totalClientes), icon: Users, color: "text-green-500" },
    { title: "Crescimento", value: "+12%", icon: TrendingUp, color: "text-purple-500" },
    { title: "Vendas", value: "R$ 24k", icon: DollarSign, color: "text-red-500" },
  ];

  return (
    <div className="flex min-h-screen bg-gray-100 dark:bg-gray-950">
      <VendedorSidebar />

      <div className="flex-1 p-6 md:p-10 space-y-6 overflow-auto">

        <div>
          <h1 className="text-2xl font-bold text-gray-900 dark:text-white">{saudacao}, {userName}</h1>
          <p className="text-gray-500">Resumo das atividades recentes.</p>
        </div>

        {/* MÉTRICAS */}
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
          {metrics.map((m) => (
            <div key={m.title} className="bg-white dark:bg-gray-900 p-5 rounded-xl border border-gray-100 dark:border-gray-800 shadow-sm hover:shadow-md transition">
              <div className="flex justify-between items-center">
                <p className="text-sm text-gray-500">{m.title}</p>
                <m.icon className={m.color} size={20} />
              </div>
              <h2 className="text-2xl font-bold text-gray-900 dark:text-white mt-2">{m.value}</h2>
            </div>
          ))}
        </div>

        {/* GRÁFICOS */}
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-4">

          <div className="bg-white dark:bg-gray-900 p-6 rounded-xl border border-gray-100 dark:border-gray-800 shadow lg:col-span-2">
            <h2 className="text-base font-bold text-gray-900 dark:text-white mb-4">Vendas Mensais</h2>
            <ResponsiveContainer width="100%" height={260}>
              <AreaChart data={chartVendas}>
                <defs>
                  <linearGradient id="gradVendas" x1="0" y1="0" x2="0" y2="1">
                    <stop offset="5%" stopColor="#dc2626" stopOpacity={0.3} />
                    <stop offset="95%" stopColor="#dc2626" stopOpacity={0} />
                  </linearGradient>
                </defs>
                <CartesianGrid strokeDasharray="3 3" stroke="#e5e7eb" className="dark:[stroke:#374151]" />
                <XAxis dataKey="mes" tick={{ fontSize: 12, fill: "#9ca3af" }} axisLine={false} tickLine={false} />
                <YAxis tick={{ fontSize: 12, fill: "#9ca3af" }} axisLine={false} tickLine={false} />
                <Tooltip
                  contentStyle={{ background: "#1f2937", border: "none", borderRadius: "8px", color: "#f9fafb" }}
                  formatter={(v: any) => [`R$ ${Number(v).toLocaleString("pt-BR")}`, ""]}
                />
                <Area type="monotone" dataKey="vendas" stroke="#dc2626" strokeWidth={2} fill="url(#gradVendas)" />
                <Area type="monotone" dataKey="meta" stroke="#9ca3af" strokeWidth={1.5} strokeDasharray="5 5" fill="none" />
              </AreaChart>
            </ResponsiveContainer>
          </div>

          <div className="bg-white dark:bg-gray-900 p-6 rounded-xl border border-gray-100 dark:border-gray-800 shadow">
            <h2 className="text-base font-bold text-gray-900 dark:text-white mb-4">Top Veículos</h2>
            <ResponsiveContainer width="100%" height={260}>
              <BarChart data={chartVeiculos} layout="vertical">
                <XAxis type="number" tick={{ fontSize: 11, fill: "#9ca3af" }} axisLine={false} tickLine={false} />
                <YAxis dataKey="modelo" type="category" tick={{ fontSize: 11, fill: "#9ca3af" }} axisLine={false} tickLine={false} width={100} />
                <Tooltip
                  contentStyle={{ background: "#1f2937", border: "none", borderRadius: "8px", color: "#f9fafb" }}
                />
                <Bar dataKey="vendas" fill="#dc2626" radius={[0, 6, 6, 0]} />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </div>

        {/* PEDIDOS RECENTES */}
        <div className="bg-white dark:bg-gray-900 rounded-xl border border-gray-100 dark:border-gray-800 shadow p-6">
          <h2 className="text-base font-bold text-gray-900 dark:text-white mb-4">Pedidos Recentes</h2>

          {loading ? (
            <div className="flex items-center gap-2 text-gray-500 py-8 justify-center">
              <Loader2 className="animate-spin" size={20} /> Carregando...
            </div>
          ) : pedidos.length === 0 ? (
            <p className="text-center text-gray-400 py-8">Nenhum pedido encontrado.</p>
          ) : (
            <table className="w-full text-sm">
              <thead>
                <tr className="text-left text-gray-400 border-b border-gray-100 dark:border-gray-800">
                  <th className="pb-3">Pedido</th>
                  <th className="pb-3">Cliente</th>
                  <th className="pb-3">Veículo</th>
                  <th className="pb-3">Valor</th>
                  <th className="pb-3">Data</th>
                </tr>
              </thead>
              <tbody>
                {pedidos.map((p) => (
                  <tr key={p.id} className="border-b border-gray-100 dark:border-gray-800 hover:bg-gray-50 dark:hover:bg-gray-800 transition">
                    <td className="py-4 font-semibold text-gray-900 dark:text-white">#{p.id}</td>
                    <td className="text-gray-700 dark:text-gray-300">{p.cliente?.nome ?? "—"}</td>
                    <td className="text-gray-500">{p.itens?.[0]?.produto?.modelo ?? "—"}</td>
                    <td className="text-gray-500">
                      {p.valorTotal != null ? `R$ ${Number(p.valorTotal).toLocaleString("pt-BR", { minimumFractionDigits: 2 })}` : "—"}
                    </td>
                    <td className="text-gray-400">{formatarData(p.dataPedido)}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>
      </div>
    </div>
  );
}
