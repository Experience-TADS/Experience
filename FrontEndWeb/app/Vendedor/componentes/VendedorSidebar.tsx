"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";
import { LayoutDashboard, Package, Users, User, LogOut, Shield } from "lucide-react";
import { removeToken } from "@/app/lib/api";

const links = [
  { href: "/Vendedor/Dashbord", icon: LayoutDashboard, label: "Dashboard" },
  { href: "/Vendedor/Pedidos", icon: Package, label: "Pedidos" },
  { href: "/Vendedor/Clientes", icon: Users, label: "Clientes" },
  { href: "/Vendedor/Perfil", icon: User, label: "Perfil" },
  { href: "/Vendedor/Administracao", icon: Shield, label: "Administração" },
];

export default function VendedorSidebar() {
  const pathname = usePathname();

  return (
    <div className="hidden md:flex w-64 bg-white dark:bg-gray-900 border-r border-gray-200 dark:border-gray-800 flex-col justify-between shrink-0 sticky top-0 h-screen overflow-y-auto">
      <div>
        <div className="flex items-center gap-3 p-6">
          <div className="bg-red-600 text-white p-3 rounded-lg">
            <Package size={20} />
          </div>
          <div>
            <p className="font-bold text-gray-900 dark:text-white">Toyota</p>
            <p className="text-sm text-gray-500">Painel do Vendedor</p>
          </div>
        </div>

        <nav className="flex flex-col gap-1 px-4">
          {links.map(({ href, icon: Icon, label }) => {
            const active = pathname === href;
            return (
              <Link
                key={href}
                href={href}
                className={`flex items-center gap-3 p-3 rounded-xl transition text-sm font-medium ${
                  active
                    ? "bg-red-600 text-white"
                    : "text-gray-600 dark:text-gray-400 hover:bg-gray-100 dark:hover:bg-gray-800"
                }`}
              >
                <Icon size={18} />
                {label}
              </Link>
            );
          })}
        </nav>
      </div>

      <div className="p-4 border-t border-gray-200 dark:border-gray-800">
        <button
          onClick={() => { removeToken(); window.location.href = "/Login"; }}
          className="flex items-center gap-2 text-gray-500 dark:text-gray-400 hover:text-red-600 dark:hover:text-red-500 transition text-sm w-full"
        >
          <LogOut size={18} /> Sair
        </button>
      </div>
    </div>
  );
}
