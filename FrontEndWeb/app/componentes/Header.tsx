"use client";

import { useEffect, useState } from "react";
import { api } from "@/app/lib/api";

export default function Header() {
  const [nome, setNome] = useState("");

  useEffect(() => {
    const cached = localStorage.getItem("user");
    if (cached) setNome(cached.split("@")[0]);

    api.getMe()
      .then((data) => { if (data.nome) setNome(data.nome); })
      .catch(() => {});
  }, []);

  return (
    <div className="mb-6 sm:mb-8">
      <p className="text-gray-500 dark:text-gray-400 text-xs sm:text-sm">Olá,</p>
      <h1 className="text-xl sm:text-2xl md:text-3xl font-bold text-gray-900 dark:text-white">
        {nome || "bem-vindo"} 👋
      </h1>
      <p className="text-gray-500 dark:text-gray-400 mt-1 text-sm sm:text-base">Novidades Toyota</p>
    </div>
  );
}
