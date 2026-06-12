"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { Car, Eye, EyeOff } from "lucide-react";

const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || "http://54.91.247.129";

function validarCPF(cpf: string) {
  const c = cpf.replace(/\D/g, "");
  if (c.length !== 11 || /^(\d)\1+$/.test(c)) return false;
  let sum = 0;
  for (let i = 0; i < 9; i++) sum += parseInt(c[i]) * (10 - i);
  let r = (sum * 10) % 11;
  if (r === 10 || r === 11) r = 0;
  if (r !== parseInt(c[9])) return false;
  sum = 0;
  for (let i = 0; i < 10; i++) sum += parseInt(c[i]) * (11 - i);
  r = (sum * 10) % 11;
  if (r === 10 || r === 11) r = 0;
  return r === parseInt(c[10]);
}

function calcularIdade(dataNascimento: string): number {
  const hoje = new Date();
  const nasc = new Date(dataNascimento);
  let idade = hoje.getFullYear() - nasc.getFullYear();
  const m = hoje.getMonth() - nasc.getMonth();
  if (m < 0 || (m === 0 && hoje.getDate() < nasc.getDate())) idade--;
  return idade;
}

export default function LoginPage() {
  const router = useRouter();

  // LOGIN
  const [email, setEmail] = useState("");
  const [senha, setSenha] = useState("");
  const [erro, setErro] = useState("");
  const [loading, setLoading] = useState(false);
  const [mostrarSenha, setMostrarSenha] = useState(false);

  // CADASTRO
  const [isCadastro, setIsCadastro] = useState(false);
  const [nome, setNome] = useState("");
  const [cpf, setCpf] = useState("");
  const [dataNasc, setDataNasc] = useState("");
  const [emailCad, setEmailCad] = useState("");
  const [senhaCad, setSenhaCad] = useState("");
  const [mostrarSenhaCad, setMostrarSenhaCad] = useState(false);
  const [erroCad, setErroCad] = useState("");
  const [loadingCad, setLoadingCad] = useState(false);

  // ── LOGIN ──────────────────────────────────────────────
  async function handleLogin(e: React.FormEvent) {
    e.preventDefault();
    setErro("");
    setLoading(true);

    try {
      const res = await fetch(`${API_BASE_URL}/api/usuario/login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email, senha }),
      });

      if (res.status === 403) {
        setErro("Conta inativa. Entre em contato com a concessionária.");
        setLoading(false);
        return;
      }

      if (!res.ok) {
        setErro("Email ou senha incorretos.");
        setLoading(false);
        return;
      }

      const data = await res.json();
      const token = data.token;
      const role: string = (data.role || "CLIENTE").toUpperCase();

      localStorage.setItem("token", token);
      localStorage.setItem("user", email);
      localStorage.setItem("userRole", role);

      if (role === "ADMIN") router.push("/Vendedor/Administracao");
      else if (role === "VENDEDOR") router.push("/Vendedor/Dashbord");
      else router.push("/");
    } catch {
      setErro("Não foi possível conectar ao servidor.");
    } finally {
      setLoading(false);
    }
  }

  // ── CADASTRO ───────────────────────────────────────────
  async function handleCadastro(e: React.FormEvent) {
    e.preventDefault();
    setErroCad("");

    // Validação de CPF
    if (!validarCPF(cpf)) {
      setErroCad("CPF inválido. Verifique os dígitos informados.");
      return;
    }

    // Validação de idade
    if (!dataNasc) {
      setErroCad("Informe sua data de nascimento.");
      return;
    }
    if (calcularIdade(dataNasc) < 18) {
      setErroCad("Você precisa ter pelo menos 18 anos para se cadastrar.");
      return;
    }

    setLoadingCad(true);

    try {
      const res = await fetch(`${API_BASE_URL}/api/pessoaFisica`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          nome,
          email: emailCad,
          senha: senhaCad,
          cpf: cpf.replace(/\D/g, ""),
          dataNascimento: dataNasc,
        }),
      });

      if (res.status === 409 || res.status === 400) {
        const body = await res.json().catch(() => null);
        const msg: string = body?.message || body?.error || "";

        if (msg.toLowerCase().includes("cpf") || msg.toLowerCase().includes("duplicate") && msg.toLowerCase().includes("cpf")) {
          setErroCad("CPF já cadastrado. Utilize outro CPF ou faça login.");
        } else if (msg.toLowerCase().includes("email")) {
          setErroCad("E-mail já cadastrado. Tente fazer login.");
        } else if (msg.toLowerCase().includes("cpf")) {
          setErroCad("CPF inválido ou já cadastrado.");
        } else {
          setErroCad("Dados inválidos. Verifique as informações e tente novamente.");
        }
        return;
      }

      if (!res.ok) {
        const body = await res.json().catch(() => null);
        const msg: string = body?.message || body?.error || "";
        if (msg.toLowerCase().includes("email")) {
          setErroCad("E-mail já cadastrado. Tente fazer login.");
        } else if (msg.toLowerCase().includes("cpf")) {
          setErroCad("CPF já cadastrado ou inválido.");
        } else {
          setErroCad("Erro ao criar conta. Tente novamente.");
        }
        return;
      }

      // Sucesso — faz login automático
      const loginRes = await fetch(`${API_BASE_URL}/api/usuario/login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email: emailCad, senha: senhaCad }),
      });

      if (loginRes.ok) {
        const loginData = await loginRes.json();
        localStorage.setItem("token", loginData.token);
        localStorage.setItem("user", emailCad);
        localStorage.setItem("userRole", "CLIENTE");
        router.push("/");
      } else {
        setIsCadastro(false);
        setErro("Cadastro realizado! Faça login para continuar.");
      }
    } catch {
      setErroCad("Não foi possível conectar ao servidor.");
    } finally {
      setLoadingCad(false);
    }
  }

  return (
    <div className="flex items-center justify-center min-h-screen bg-gray-100">
      <div className="bg-white p-8 rounded-2xl shadow-md w-96">

        <div className="flex flex-col items-center mb-6">
          <div className="bg-red-600 p-4 rounded-xl mb-4">
            <Car className="text-white w-8 h-8" />
          </div>
          <h1 className="text-2xl font-bold text-gray-900">Toyota Experience</h1>
          <p className="text-gray-600 text-sm">
            {isCadastro ? "Crie sua conta" : "Acompanhe seu veículo em tempo real"}
          </p>
        </div>

        {/* ── FORMULÁRIO DE LOGIN ── */}
        {!isCadastro && (
          <form onSubmit={handleLogin} className="space-y-4">
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
              <div className="relative mt-1">
                <input
                  type={mostrarSenha ? "text" : "password"}
                  placeholder="••••••••"
                  className="w-full p-3 pr-10 rounded-lg border bg-gray-50 text-gray-900 placeholder:text-gray-600 focus:outline-none focus:ring-2 focus:ring-red-500"
                  value={senha}
                  onChange={(e) => setSenha(e.target.value)}
                  required
                />
                <button
                  type="button"
                  onClick={() => setMostrarSenha(!mostrarSenha)}
                  className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600 transition"
                >
                  {mostrarSenha ? <EyeOff size={18} /> : <Eye size={18} />}
                </button>
              </div>
            </div>

            {erro && <p className="text-red-500 text-sm">{erro}</p>}

            <button
              type="submit"
              disabled={loading}
              className="w-full bg-red-600 text-white py-3 rounded-lg font-semibold hover:bg-red-700 transition disabled:opacity-50"
            >
              {loading ? "Carregando..." : "Entrar"}
            </button>
          </form>
        )}

        {/* ── FORMULÁRIO DE CADASTRO ── */}
        {isCadastro && (
          <form onSubmit={handleCadastro} className="space-y-3">
            <div>
              <label className="text-sm text-gray-600">Nome completo</label>
              <input
                type="text"
                placeholder="Seu nome"
                className="w-full mt-1 p-3 rounded-lg border bg-gray-50 text-gray-900 focus:outline-none focus:ring-2 focus:ring-red-500"
                value={nome}
                onChange={(e) => setNome(e.target.value)}
                required
              />
            </div>

            <div>
              <label className="text-sm text-gray-600">CPF</label>
              <input
                type="text"
                placeholder="000.000.000-00"
                maxLength={14}
                className="w-full mt-1 p-3 rounded-lg border bg-gray-50 text-gray-900 focus:outline-none focus:ring-2 focus:ring-red-500"
                value={cpf}
                onChange={(e) => {
                  const v = e.target.value.replace(/\D/g, "")
                    .replace(/(\d{3})(\d)/, "$1.$2")
                    .replace(/(\d{3})(\d)/, "$1.$2")
                    .replace(/(\d{3})(\d{1,2})$/, "$1-$2");
                  setCpf(v);
                }}
                required
              />
            </div>

            <div>
              <label className="text-sm text-gray-600">Data de nascimento</label>
              <input
                type="date"
                className="w-full mt-1 p-3 rounded-lg border bg-gray-50 text-gray-900 focus:outline-none focus:ring-2 focus:ring-red-500"
                value={dataNasc}
                onChange={(e) => setDataNasc(e.target.value)}
                max={new Date(new Date().setFullYear(new Date().getFullYear() - 18))
                  .toISOString().split("T")[0]}
                required
              />
            </div>

            <div>
              <label className="text-sm text-gray-600">Email</label>
              <input
                type="email"
                placeholder="seu@email.com"
                className="w-full mt-1 p-3 rounded-lg border bg-gray-50 text-gray-900 focus:outline-none focus:ring-2 focus:ring-red-500"
                value={emailCad}
                onChange={(e) => setEmailCad(e.target.value)}
                required
              />
            </div>

            <div>
              <label className="text-sm text-gray-600">Senha</label>
              <div className="relative mt-1">
                <input
                  type={mostrarSenhaCad ? "text" : "password"}
                  placeholder="••••••••"
                  className="w-full p-3 pr-10 rounded-lg border bg-gray-50 text-gray-900 focus:outline-none focus:ring-2 focus:ring-red-500"
                  value={senhaCad}
                  onChange={(e) => setSenhaCad(e.target.value)}
                  required
                  minLength={6}
                />
                <button
                  type="button"
                  onClick={() => setMostrarSenhaCad(!mostrarSenhaCad)}
                  className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600 transition"
                >
                  {mostrarSenhaCad ? <EyeOff size={18} /> : <Eye size={18} />}
                </button>
              </div>
            </div>

            {erroCad && <p className="text-red-500 text-sm">{erroCad}</p>}

            <button
              type="submit"
              disabled={loadingCad}
              className="w-full bg-red-600 text-white py-3 rounded-lg font-semibold hover:bg-red-700 transition disabled:opacity-50"
            >
              {loadingCad ? "Criando conta..." : "Criar Conta"}
            </button>
          </form>
        )}

        <p
          onClick={() => {
            setIsCadastro(!isCadastro);
            setErro("");
            setErroCad("");
          }}
          className="text-center text-sm text-red-600 mt-4 cursor-pointer hover:underline"
        >
          {isCadastro ? "Já tem conta? Fazer login" : "Não tem conta? Criar agora"}
        </p>

      </div>
    </div>
  );
}
