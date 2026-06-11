"use client";

import NextLink from "next/link";
import { usePathname } from "next/navigation";
import {
  Home,
  User,
  Car,
  Store,
  Smartphone,
  MessageCircle,
  LogOut,
} from "lucide-react";

export default function Sidebar() {
  const pathname = usePathname();

  function isActive(path: string) {
    return pathname === path;
  }

  function getItemStyle(path: string) {
    return `
      p-3 rounded-xl cursor-pointer transition
      ${
        isActive(path)
          ? "bg-red-500 text-white"
          : "text-gray-400 hover:bg-gray-100 dark:hover:bg-gray-700 dark:text-gray-400"
      }
    `;
  }

  function logout() {
    localStorage.removeItem("user");
    window.location.href = "/Login";
  }

  return (
    <>
      {/* MOBILE */}
      <div className="fixed bottom-0 left-0 w-full bg-white dark:bg-gray-900 shadow-md border-t border-gray-100 dark:border-gray-800 flex justify-around items-center py-3 md:hidden z-50">
        <NextLink href="/"><div className={getItemStyle("/")}><Home size={22} /></div></NextLink>
        <NextLink href="/Cliente/Acompanhamento"><div className={getItemStyle("/Cliente/Acompanhamento")}><Car size={22} /></div></NextLink>
        <NextLink href="/Cliente/Loja"><div className={getItemStyle("/Cliente/Loja")}><Store size={22} /></div></NextLink>
        <NextLink href="/Cliente/Apps"><div className={getItemStyle("/Cliente/Apps")}><Smartphone size={22} /></div></NextLink>
        <NextLink href="/Cliente/Chat"><div className={getItemStyle("/Cliente/Chat")}><MessageCircle size={22} /></div></NextLink>
        <NextLink href="/Cliente/perfil"><div className={getItemStyle("/Cliente/perfil")}><User size={22} /></div></NextLink>
      </div>

      {/* DESKTOP */}
      <div className="hidden md:flex fixed top-0 left-0 h-screen w-20 bg-white dark:bg-gray-900 shadow-md border-r border-gray-100 dark:border-gray-800 flex-col justify-between items-center py-6 z-40">
        <div className="flex flex-col items-center gap-6">
          <NextLink href="/"><div className={getItemStyle("/")}><Home size={22} /></div></NextLink>
          <NextLink href="/Cliente/Acompanhamento"><div className={getItemStyle("/Cliente/Acompanhamento")}><Car size={22} /></div></NextLink>
          <NextLink href="/Cliente/Loja"><div className={getItemStyle("/Cliente/Loja")}><Store size={22} /></div></NextLink>
          <NextLink href="/Cliente/Apps"><div className={getItemStyle("/Cliente/Apps")}><Smartphone size={22} /></div></NextLink>
          <NextLink href="/Cliente/Chat"><div className={getItemStyle("/Cliente/Chat")}><MessageCircle size={22} /></div></NextLink>
          <NextLink href="/Cliente/perfil"><div className={getItemStyle("/Cliente/perfil")}><User size={22} /></div></NextLink>
        </div>
        <div>
          <div onClick={logout} className="p-3 rounded-xl cursor-pointer transition text-gray-400 hover:bg-red-100 dark:hover:bg-red-900/40 hover:text-red-600">
            <LogOut size={22} />
          </div>
        </div>
      </div>
    </>
  );
}
