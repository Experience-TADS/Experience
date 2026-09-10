"use client";

import NextLink from "next/link";
import { usePathname } from "next/navigation";
import { Home, User, Car, Store, Smartphone, LogOut, MessageCircle } from "lucide-react";
import { removeToken } from "@/app/lib/api";

export default function Sidebar() {
  const pathname = usePathname();

  function isActive(path: string) {
    return pathname === path;
  }

  function getItemStyle(path: string) {
    return `p-3 rounded-xl cursor-pointer transition ${
      isActive(path)
        ? "bg-red-500 text-white"
        : "text-gray-400 hover:bg-gray-100 dark:hover:bg-gray-800"
    }`;
  }

  function logout() {
    removeToken();
    window.location.href = "/Login";
  }

  const links = [
    { href: "/", icon: <Home size={22} /> },
    { href: "/Cliente/Acompanhamento", icon: <Car size={22} /> },
    { href: "/Cliente/Loja", icon: <Store size={22} /> },
    { href: "/Cliente/Apps", icon: <Smartphone size={22} /> },
    { href: "/Cliente/Chat", icon: <MessageCircle size={22} /> },
    { href: "/Cliente/perfil", icon: <User size={22} /> },
  ];

  return (
    <>
      {/* MOBILE */}
      <div className="fixed bottom-0 left-0 w-full bg-white dark:bg-gray-900 border-t border-gray-200 dark:border-gray-800 shadow-md flex justify-around items-center py-3 md:hidden z-50">
        {links.map((l) => (
          <NextLink key={l.href} href={l.href}>
            <div className={getItemStyle(l.href)}>{l.icon}</div>
          </NextLink>
        ))}
      </div>

      {/* DESKTOP */}
      <div className="hidden md:flex fixed top-0 left-0 h-screen w-20 bg-white dark:bg-gray-900 border-r border-gray-200 dark:border-gray-800 shadow-md flex-col justify-between items-center py-6 z-40">
        <div className="flex flex-col items-center gap-6">
          {links.map((l) => (
            <NextLink key={l.href} href={l.href}>
              <div className={getItemStyle(l.href)}>{l.icon}</div>
            </NextLink>
          ))}
        </div>

        <div
          onClick={logout}
          className="p-3 rounded-xl cursor-pointer transition text-gray-400 hover:bg-red-100 dark:hover:bg-red-900/30 hover:text-red-600"
        >
          <LogOut size={22} />
        </div>
      </div>
    </>
  );
}
