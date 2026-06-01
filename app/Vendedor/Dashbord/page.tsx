"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import { LayoutDashboard, Package, Users, User, LogOut, DollarSign, ShoppingCart, TrendingUp, Shield } from "lucide-react";
import { AreaChart, Area, BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, Line } from "recharts";
import api from "@/app/lib/api";

const chartData = [
  { mes: "Jan", vendas: 12000, meta: 10000 },{ mes: "Fev", vendas: 19000, meta: 15000 },
  { mes: "Mar", vendas: 15000, meta: 16000 },{ mes: "Abr", vendas: 22000, meta: 18000 },
  { mes: "Mai", vendas: 18000, meta: 20000 },{ mes: "Jun", vendas: 24000, meta: 21000 },
];

const topVehicles = [
  { modelo: "Corolla Cross", vendas: 18 },{ modelo: "Hilux", vendas: 14 },
  { modelo: "SW4", vendas: 11 },{ modelo: "RAV4", vendas: 8 },{ modelo: "Yaris", vendas: 6 },
];

function getStatusColor(s: string) {
  if (s === "Finalizado")        return "bg-green-100 dark:bg-green-900/30 text-green-700 dark:text-green-400";
  if (s === "Em produção")       return "bg-yellow-100 dark:bg-yellow-900/30 text-yellow-700 dark:text-yellow-400";
  if (s === "Em transporte")     return "bg-purple-100 dark:bg-purple-900/30 text-purple-700 dark:text-purple-400";
  if (s === "Pedido confirmado") return "bg-blue-100 dark:bg-blue-900/30 text-blue-700 dark:text-blue-400";
  return "bg-gray-100 dark:bg-gray-700 text-gray-600 dark:text-gray-300";
}

const navLinks = [
  { href: "/Vendedor/Dashbord",     label: "Dashboard",     icon: LayoutDashboard, active: true },
  { href: "/Vendedor/Pedidos",      label: "Pedidos",       icon: Package },
  { href: "/Vendedor/Clientes",     label: "Clientes",      icon: Users },
  { href: "/Vendedor/Perfil",       label: "Perfil",        icon: User },
  { href: "/Vendedor/Administracao",label: "Administração", icon: Shield },
];

type Pedido = {
  id: number;
  dataPedido: string;
  valorTotal: number;
  cliente: { id: number; nome: string; email: string };
  vendedor: { id: number; nome: string };
  itens: { id: number; quantidade: number; produto: { id: number; modelo: string; cor: string; versao: string; ano: number } }[];
};

function formatarData(iso: string) {
  if (!iso) return "—";
  return new Date(iso).toLocaleDateString("pt-BR");
}

export default function Dashboard() {
  const hora = new Date().getHours();
  const saudacao = hora < 12 ? "Bom dia" : hora < 18 ? "Boa tarde" : "Boa noite";

  const [pedidos, setPedidos] = useState<Pedido[]>([]);
  const [totalPedidos, setTotalPedidos] = useState(0);
  const [totalClientes, setTotalClientes] = useState(0);
  const [nomeUsuario, setNomeUsuario] = useState("Vendedor");
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // Busca dados do usuário logado
    api.get("/api/usuario/me")
      .then(({ data }) => setNomeUsuario(data.nome?.split(" ")[0] ?? "Vendedor"))
      .catch(() => {});

    // Busca pedidos recentes
    api.get("/api/pedido?size=5&sort=dataPedido,desc")
      .then(({ data }) => {
        setPedidos(data.content ?? []);
        setTotalPedidos(data.totalElements ?? 0);
      })
      .catch(() => {})
      .finally(() => setLoading(false));

    // Busca total de clientes
    api.get("/api/usuario?size=1")
      .then(({ data }) => setTotalClientes(data.totalElements ?? 0))
      .catch(() => {});
  }, []);

  const metrics = [
    { title: "Pedidos Ativos", value: String(totalPedidos), change: "total cadastrados", icon: ShoppingCart },
    { title: "Clientes",       value: String(totalClientes), change: "total cadastrados", icon: Users },
    { title: "Crescimento",    value: "+12%", change: "vs mês anterior", icon: TrendingUp },
    { title: "Receita",        value: pedidos.length > 0
        ? `R$ ${pedidos.reduce((acc, p) => acc + Number(p.valorTotal ?? 0), 0).toLocaleString("pt-BR")}`
        : "—",
      change: "últimos 5 pedidos", icon: DollarSign },
  ];

  return (
    <div className="flex min-h-screen bg-gray-100 dark:bg-gray-950">

      {/* SIDEBAR */}
      <div className="hidden md:flex w-64 bg-white dark:bg-gray-900 border-r border-gray-100 dark:border-gray-800 flex-col justify-between">
        <div>
          <div className="flex items-center gap-3 p-6">
            <div className="bg-red-600 text-white p-3 rounded-lg"><Package size={20} /></div>
            <div>
              <p className="font-bold text-gray-900 dark:text-gray-100">Toyota</p>
              <p className="text-sm text-gray-500 dark:text-gray-400">Painel do Vendedor</p>
            </div>
          </div>
          <nav className="flex flex-col gap-2 px-4">
            {navLinks.map((l) => (
              <Link key={l.href} href={l.href} className={`flex items-center gap-3 p-3 rounded-xl transition ${l.active ? "bg-red-600 text-white" : "text-gray-700 dark:text-gray-400 hover:bg-gray-100 dark:hover:bg-gray-800"}`}>
                <l.icon size={18} /> {l.label}
              </Link>
            ))}
          </nav>
        </div>
        <div className="p-4 border-t border-gray-100 dark:border-gray-800">
          <Link href="/Login" onClick={() => {
            localStorage.removeItem("token");
            localStorage.removeItem("user");
            localStorage.removeItem("userRole");
          }} className="flex items-center gap-2 text-gray-600 dark:text-gray-400 hover:text-red-600">
            <LogOut size={18} /> Sair
          </Link>
        </div>
      </div>

      {/* CONTEÚDO */}
      <div className="flex-1 p-6 md:p-10 space-y-6">

        <div>
          <h1 className="text-2xl font-bold text-gray-900 dark:text-gray-100">{saudacao}, {nomeUsuario} 👋</h1>
          <p className="text-gray-500 dark:text-gray-400">Aqui está o resumo das suas vendas de hoje.</p>
        </div>

        {/* MÉTRICAS */}
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
          {metrics.map((m) => (
            <div key={m.title} className="bg-white dark:bg-gray-900 border border-transparent dark:border-gray-700 p-5 rounded-xl shadow-sm hover:shadow-md transition">
              <div className="flex justify-between items-center">
                <p className="text-sm text-gray-500 dark:text-gray-400">{m.title}</p>
                <m.icon className="text-red-600" size={20} />
              </div>
              <h2 className="text-2xl font-bold text-gray-900 dark:text-gray-100 mt-2">{m.value}</h2>
              <p className="text-sm text-green-600 dark:text-green-400 mt-1">{m.change}</p>
            </div>
          ))}
        </div>

        {/* GRÁFICOS */}
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-4">
          <div className="bg-white dark:bg-gray-900 border border-transparent dark:border-gray-700 p-6 rounded-xl shadow lg:col-span-2">
            <h2 className="text-lg font-bold text-gray-900 dark:text-gray-100 mb-4">Vendas Mensais</h2>
            <ResponsiveContainer width="100%" height={300}>
              <AreaChart data={chartData}>
                <CartesianGrid strokeDasharray="3 3" stroke="#374151" />
                <XAxis dataKey="mes" stroke="#9ca3af" />
                <YAxis stroke="#9ca3af" />
                <Tooltip contentStyle={{ backgroundColor: "#1f2937", border: "none", color: "#f9fafb" }} />
                <Area type="monotone" dataKey="vendas" stroke="#dc2626" fill="#fecaca" />
                <Line type="monotone" dataKey="meta" stroke="#9ca3af" strokeDasharray="5 5" />
              </AreaChart>
            </ResponsiveContainer>
          </div>
          <div className="bg-white dark:bg-gray-900 border border-transparent dark:border-gray-700 p-6 rounded-xl shadow">
            <h2 className="text-lg font-bold text-gray-900 dark:text-gray-100 mb-4">Top Veículos</h2>
            <ResponsiveContainer width="100%" height={300}>
              <BarChart data={topVehicles} layout="vertical">
                <XAxis type="number" stroke="#9ca3af" />
                <YAxis dataKey="modelo" type="category" stroke="#9ca3af" />
                <Tooltip contentStyle={{ backgroundColor: "#1f2937", border: "none", color: "#f9fafb" }} />
                <Bar dataKey="vendas" fill="#dc2626" radius={[0, 6, 6, 0]} />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </div>

        {/* PEDIDOS RECENTES */}
        <div className="bg-white dark:bg-gray-900 border border-transparent dark:border-gray-700 rounded-xl shadow p-6">
          <h2 className="text-lg font-bold text-gray-900 dark:text-gray-100 mb-4">Pedidos Recentes</h2>
          {loading ? (
            <p className="text-sm text-gray-500 dark:text-gray-400">Carregando...</p>
          ) : (
            <table className="w-full text-sm">
              <thead>
                <tr className="text-left text-gray-500 dark:text-gray-400 border-b border-gray-100 dark:border-gray-700">
                  <th className="pb-3">Pedido</th><th className="pb-3">Cliente</th>
                  <th className="pb-3">Veículo</th><th className="pb-3">Valor</th><th className="pb-3">Data</th>
                </tr>
              </thead>
              <tbody>
                {pedidos.map((p) => (
                  <tr key={p.id} className="border-b border-gray-100 dark:border-gray-700 hover:bg-gray-50 dark:hover:bg-gray-800 transition">
                    <td className="py-4 font-semibold text-gray-900 dark:text-gray-100">#{p.id}</td>
                    <td className="text-gray-800 dark:text-gray-200">{p.cliente?.nome ?? "—"}</td>
                    <td className="text-gray-600 dark:text-gray-400">{p.itens?.[0]?.produto?.modelo ?? "—"}</td>
                    <td className="text-gray-600 dark:text-gray-400">
                      {p.valorTotal != null ? `R$ ${Number(p.valorTotal).toLocaleString("pt-BR")}` : "—"}
                    </td>
                    <td className="text-gray-500 dark:text-gray-400">{formatarData(p.dataPedido)}</td>
                  </tr>
                ))}
                {pedidos.length === 0 && (
                  <tr><td colSpan={5} className="py-4 text-gray-400 dark:text-gray-500 text-center">Nenhum pedido encontrado.</td></tr>
                )}
              </tbody>
            </table>
          )}
        </div>

      </div>
    </div>
  );
}
