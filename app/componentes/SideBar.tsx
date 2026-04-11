"use client";

import NextLink from "next/link";
import { usePathname } from "next/navigation";
import { Home, User, Car, Store } from "lucide-react";

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
          : "text-gray-400 hover:bg-gray-100"
      }
    `;
  }

  return (
    <>
      {/* MOBILE */}
      <div className="fixed bottom-0 left-0 w-full bg-white shadow-md flex justify-around items-center py-3 md:hidden z-50">
        
        <NextLink href="/">
          <div className={getItemStyle("/")}>
            <Home size={22} />
          </div>
        </NextLink>

        <NextLink href="/Cliente/Acompanhamento">
          <div className={getItemStyle("/Cliente/Acompanhamento")}>
            <Car size={22} />
          </div>
        </NextLink>

        <NextLink href="/Cliente/Loja">
          <div className={getItemStyle("/Cliente/Loja")}>
            <Store size={22} />
          </div>
        </NextLink>

        <NextLink href="/Cliente/perfil">
          <div className={getItemStyle("/Cliente/perfil")}>
            <User size={22} />
          </div>
        </NextLink>

      </div>

      {/* DESKTOP */}
      <div className="hidden md:flex fixed top-0 left-0 h-screen w-20 bg-white shadow-md flex-col items-center py-6 gap-6 z-40">

        <NextLink href="/">
          <div className={getItemStyle("/")}>
            <Home size={22} />
          </div>
        </NextLink>

        <NextLink href="/Cliente/Acompanhamento">
          <div className={getItemStyle("/Cliente/Acompanhamento")}>
            <Car size={22} />
          </div>
        </NextLink>

        <NextLink href="/Cliente/Loja">
          <div className={getItemStyle("/Cliente/Loja")}>
            <Store size={22} />
          </div>
        </NextLink>

        <NextLink href="/Cliente/perfil">
          <div className={getItemStyle("/Cliente/perfil")}>
            <User size={22} />
          </div>
        </NextLink>

      </div>
    </>
  );
}