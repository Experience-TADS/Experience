"use client";

import NextLink from "next/link";
import { usePathname } from "next/navigation";
import {
  Home,
  User,
  Car,
  Store,
  Smartphone,
  LogOut,
} from "lucide-react";

const navItems = [
  { href: "/", icon: Home, label: "Início" },
  { href: "/Cliente/Acompanhamento", icon: Car, label: "Meu Veículo" },
  { href: "/Cliente/Loja", icon: Store, label: "Loja" },
  { href: "/Cliente/Apps", icon: Smartphone, label: "Conecte-se" },
  { href: "/Cliente/perfil", icon: User, label: "Perfil" },
];

export default function Sidebar() {
  const pathname = usePathname();

  function isActive(path: string) {
    return pathname === path;
  }

  function getItemStyle(path: string) {
    return `p-3 rounded-xl cursor-pointer transition ${
      isActive(path) ? "bg-red-500 text-white" : "text-gray-400 hover:bg-gray-100"
    }`;
  }

  function logout() {
    localStorage.removeItem("user");
    localStorage.removeItem("token");
    localStorage.removeItem("userRole");
    window.location.href = "/Login";
  }

  return (
    <>
      {/* MOBILE — barra inferior com labels */}
      <div className="fixed bottom-0 left-0 w-full bg-white shadow-md flex justify-around items-center py-2 md:hidden z-50">
        {navItems.map(({ href, icon: Icon, label }) => (
          <NextLink key={href} href={href}>
            <div className={`flex flex-col items-center gap-0.5 px-1 py-1 rounded-xl transition ${
              isActive(href) ? "text-red-500" : "text-gray-400"
            }`}>
              <Icon size={20} />
              <span className="text-[9px]">{label}</span>
            </div>
          </NextLink>
        ))}

        {/* BOTÃO SAIR — mobile */}
        <button
          onClick={logout}
          className="flex flex-col items-center gap-0.5 px-1 py-1 rounded-xl transition text-gray-400 hover:text-red-500"
        >
          <LogOut size={20} />
          <span className="text-[9px]">Sair</span>
        </button>
      </div>

      {/* DESKTOP — barra lateral com tooltips */}
      <div className="hidden md:flex fixed top-0 left-0 h-screen w-20 bg-white shadow-md flex-col justify-between items-center py-6 z-40">

        <div className="flex flex-col items-center gap-4">
          {navItems.map(({ href, icon: Icon, label }) => (
            <NextLink key={href} href={href}>
              <div className="relative group">
                <div className={getItemStyle(href)}>
                  <Icon size={22} />
                </div>
                {/* TOOLTIP */}
                <div className="absolute left-full ml-3 top-1/2 -translate-y-1/2 bg-gray-800 text-white text-xs px-2 py-1 rounded-lg whitespace-nowrap opacity-0 group-hover:opacity-100 transition-opacity pointer-events-none z-50">
                  {label}
                  <div className="absolute right-full top-1/2 -translate-y-1/2 border-4 border-transparent border-r-gray-800" />
                </div>
              </div>
            </NextLink>
          ))}
        </div>

        {/* LOGOUT com tooltip */}
        <div className="relative group">
          <div
            onClick={logout}
            className="p-3 rounded-xl cursor-pointer transition text-gray-400 hover:bg-red-100 hover:text-red-600"
          >
            <LogOut size={22} />
          </div>
          <div className="absolute left-full ml-3 top-1/2 -translate-y-1/2 bg-gray-800 text-white text-xs px-2 py-1 rounded-lg whitespace-nowrap opacity-0 group-hover:opacity-100 transition-opacity pointer-events-none z-50">
            Sair
            <div className="absolute right-full top-1/2 -translate-y-1/2 border-4 border-transparent border-r-gray-800" />
          </div>
        </div>

      </div>
    </>
  );
}
