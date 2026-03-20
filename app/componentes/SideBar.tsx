"use client";

import NextLink from "next/link";
import { Home, Clock, User } from "lucide-react";

export default function Sidebar() {
  return (
    <>
      {/* MOBILE - barra inferior */}
      <div className="fixed bottom-0 left-0 w-full bg-white shadow-md flex justify-around items-center py-3 md:hidden z-50">
        
        <NextLink href="/">
          <Home className="text-red-500" size={24} />
        </NextLink>

        <NextLink href="/historico">
          <Clock className="text-gray-400" size={24} />
        </NextLink>

        <NextLink href="/perfil">
          <User className="text-gray-400" size={24} />
        </NextLink>

      </div>

      {/* DESKTOP - sidebar lateral */}
      <div className="hidden md:flex w-20 h-screen bg-white shadow-md flex-col items-center py-6 gap-6">

        <NextLink href="/">
          <div className="bg-red-500 p-3 rounded-xl text-white cursor-pointer">
            <Home size={22} />
          </div>
        </NextLink>

        <NextLink href="/historico">
          <Clock className="text-gray-400 cursor-pointer" size={22} />
        </NextLink>

        <NextLink href="/perfil">
          <User className="text-gray-400 cursor-pointer" size={22} />
        </NextLink>

      </div>
    </>
  );
}