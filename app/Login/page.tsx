"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { Car } from "lucide-react";

export default function LoginPage() {
  const router = useRouter();

  const [email, setEmail] = useState("");
  const [senha, setSenha] = useState("");
  const [erro, setErro] = useState("");
  const [isCadastro, setIsCadastro] = useState(false);
  const [loading, setLoading] = useState(false);

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    setErro("");
    setLoading(true);

    const endpoint = isCadastro ? "/api/register" : "/api/login";

    try {
      const res = await fetch(endpoint, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ email, senha }),
      });

      const data = await res.json();

      if (!res.ok) {
        setErro(data.error || "Erro inesperado");
        setLoading(false);
        return;
      }

      if (isCadastro) {
        alert("Conta criada com sucesso!");
        setIsCadastro(false);
      } else {
        localStorage.setItem("user", email);
        router.push("/");
      }
    } catch (err) {
      setErro("Erro ao conectar com o servidor");
    }

    setLoading(false);
  }

  return (
    <div className="flex items-center justify-center min-h-screen bg-gray-100">
      <div className="bg-white p-8 rounded-2xl shadow-md w-96">

        <div className="flex flex-col items-center mb-6">
          <div className="bg-red-600 p-4 rounded-xl mb-4">
            <Car className="text-white w-8 h-8" />
          </div>

          <h1 className="text-2xl font-bold text-gray-900">
            Toyota Experience
          </h1>
          <p className="text-gray-600 text-sm">
            {isCadastro
              ? "Crie sua conta"
              : "Acompanhe seu veículo em tempo real"}
          </p>
        </div>

        <form onSubmit={handleSubmit} className="space-y-4">

          <div>
            <label className="text-sm text-gray-600">Email</label>
            <input
              type="email"
              placeholder="seu@email.com"
              className="w-full mt-1 p-3 rounded-lg border bg-gray-50 text-gray-900 placeholder:text-gray-600 focus:outline-none focus:ring-2 focus:ring-red-500"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
            />
          </div>

          <div>
            <label className="text-sm text-gray-600">Senha</label>
            <input
              type="password"
              placeholder="••••••••"
              className="w-full mt-1 p-3 rounded-lg border bg-gray-50 text-gray-900 placeholder:text-gray-600 focus:outline-none focus:ring-2 focus:ring-red-500"
              value={senha}
              onChange={(e) => setSenha(e.target.value)}
              required
            />
          </div>

          {erro && (
            <p className="text-red-500 text-sm">{erro}</p>
          )}

          <button
            type="submit"
            disabled={loading}
            className="w-full bg-red-600 text-white py-3 rounded-lg font-semibold hover:bg-red-700 transition disabled:opacity-50"
          >
            {loading
              ? "Carregando..."
              : isCadastro
              ? "Criar Conta"
              : "Entrar"}
          </button>
        </form>

        <p
          onClick={() => {
            setIsCadastro(!isCadastro);
            setErro("");
          }}
          className="text-center text-sm text-red-600 mt-4 cursor-pointer hover:underline"
        >
          {isCadastro
            ? "Já tem conta? Fazer login"
            : "Não tem conta? Criar agora"}
        </p>

      </div>
    </div>
  );
}