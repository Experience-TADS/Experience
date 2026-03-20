"use client";

import Link from "next/link";

import {
  LayoutDashboard,
  Package,
  Users,
  User,
  LogOut,
  DollarSign,
  ShoppingCart,
  TrendingUp
} from "lucide-react";

import {
  LineChart,
  Line,
  XAxis,
  YAxis,
  Tooltip,
  ResponsiveContainer
} from "recharts";

const data = [
  { name: "Jan", vendas: 400 },
  { name: "Fev", vendas: 700 },
  { name: "Mar", vendas: 500 },
  { name: "Abr", vendas: 900 },
  { name: "Mai", vendas: 1200 },
];

export default function Dashbord() {
  return (
    <div className="flex bg-gray-100 min-h-screen">

      {/* SIDEBAR */}
      <div className="hidden md:flex w-64 bg-white border-r flex-col justify-between">

        <div>

          <div className="flex items-center gap-3 p-6">
            <div className="bg-red-600 text-white p-3 rounded-lg">
              <Package size={20} />
            </div>

            <div>
              <p className="font-bold text-gray-900">Toyota</p>
              <p className="text-sm text-gray-500">Painel do Vendedor</p>
            </div>
          </div>

          <nav className="flex flex-col gap-2 px-4">

            <Link
              href="/Vendedor/Dashbord"
              className="flex items-center gap-3 bg-red-600 text-white p-3 rounded-xl"
            >
              <LayoutDashboard size={18} />
              Dashboard
            </Link>

            <Link
              href="/Vendedor/Pedidos"
              className="flex items-center gap-3 text-gray-600 p-3 rounded-xl hover:bg-gray-100 transition"
            >
              <Package size={18} />
              Pedidos
            </Link>

            <Link
              href="/Vendedor/Clientes"
              className="flex items-center gap-3 text-gray-600 p-3 rounded-xl hover:bg-gray-100 transition"
            >
              <Users size={18} />
              Clientes
            </Link>

            <Link
              href="/Vendedor/Perfil"
              className="flex items-center gap-3 text-gray-600 p-3 rounded-xl hover:bg-gray-100 transition"
            >
              <User size={18} />
              Perfil
            </Link>

          </nav>

        </div>

        <div className="p-4 border-t">
          <Link
            href="/Login"
            className="flex items-center gap-2 text-gray-600 hover:text-red-600 transition"
          >
            <LogOut size={18} />
            Sair
          </Link>
        </div>

      </div>


      {/* CONTEÚDO */}
      <div className="flex-1 p-5 md:p-8">

        <h1 className="text-xl md:text-2xl font-bold text-gray-900 mb-6">
          Dashboard do Vendedor
        </h1>


        {/* CARDS */}
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6 mb-10">

          <div className="bg-white p-5 rounded-xl shadow-sm hover:shadow-md hover:-translate-y-1 transition">

            <div className="flex justify-between items-center">
              <p className="text-sm text-gray-600">Vendas Hoje</p>
              <DollarSign className="text-red-500" size={20}/>
            </div>

            <h2 className="text-2xl font-bold text-gray-900 mt-2">
              R$ 2.540
            </h2>

          </div>


          <div className="bg-white p-5 rounded-xl shadow-sm hover:shadow-md hover:-translate-y-1 transition">

            <div className="flex justify-between items-center">
              <p className="text-sm text-gray-600">Pedidos</p>
              <ShoppingCart className="text-red-500" size={20}/>
            </div>

            <h2 className="text-2xl font-bold text-gray-900 mt-2">
              38
            </h2>

          </div>


          <div className="bg-white p-5 rounded-xl shadow-sm hover:shadow-md hover:-translate-y-1 transition">

            <div className="flex justify-between items-center">
              <p className="text-sm text-gray-600">Clientes</p>
              <Users className="text-red-500" size={20}/>
            </div>

            <h2 className="text-2xl font-bold text-gray-900 mt-2">
              124
            </h2>

          </div>


          <div className="bg-white p-5 rounded-xl shadow-sm hover:shadow-md hover:-translate-y-1 transition">

            <div className="flex justify-between items-center">
              <p className="text-sm text-gray-600">Crescimento</p>
              <TrendingUp className="text-green-500" size={20}/>
            </div>

            <h2 className="text-2xl font-bold text-green-600 mt-2">
              +12%
            </h2>

            <p className="text-xs text-gray-500">
              em relação ao mês anterior
            </p>

          </div>

        </div>


        {/* GRÁFICO */}
        <div className="bg-white p-4 md:p-6 rounded-xl shadow-sm">

          <h2 className="text-lg font-semibold text-gray-900 mb-4">
            Vendas Mensais
          </h2>

          <ResponsiveContainer width="100%" height={300}>

            <LineChart data={data}>

              <XAxis
                dataKey="name"
                stroke="#6b7280"
                tick={{ fill: "#374151", fontSize: 12 }}
              />

              <YAxis
                stroke="#6b7280"
                tick={{ fill: "#374151", fontSize: 12 }}
              />

              <Tooltip
                contentStyle={{
                  backgroundColor: "#fff",
                  borderRadius: "10px",
                  border: "1px solid #e5e7eb"
                }}
              />

              <Line
                type="monotone"
                dataKey="vendas"
                stroke="#ef4444"
                strokeWidth={3}
                dot={{ r: 5 }}
                activeDot={{ r: 8 }}
              />

            </LineChart>

          </ResponsiveContainer>

        </div>

      </div>
    </div>
  );
}