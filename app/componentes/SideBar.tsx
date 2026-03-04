"use client";

import NextLink from "next/link"; 
import { Home, Clock, Link, MapPin, User } from "lucide-react";

export default function Sidebar() {
  return (
    <div className="w-20 h-screen bg-white shadow-md flex flex-col items-center py-6 gap-6">
      
      <NextLink href="/">
        <div className="bg-red-500 p-3 rounded-xl text-white cursor-pointer">
          <Home size={22} />
        </div>
      </NextLink>

      <NextLink href="/historico">
        <Clock className="text-gray-400 cursor-pointer" />
      </NextLink>

      <NextLink href="/perfil">
        <User className="text-gray-400 cursor-pointer" />
      </NextLink>
      
    </div>
  );
}