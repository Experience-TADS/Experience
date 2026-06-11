"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { Car, User, Mail, Lock, IdCard, Calendar } from "lucide-react";
import api from "@/app/lib/api";

type Tab = "login" | "cadastro";

export default function LoginPage() {
  const router = useRouter();
  const [tab, setTab] = useState<Tab>("login");
  const [loading, setLoading] = useState(false);
  const [erro, setErro] = useState("");
  const [sucesso, setSucesso] = useState("");

  // Login
  const [email, setEmail] = useState("");
  const [senha, setSenha] = useState("");

  // Cadastro
  const [nome, setNome] = useState("");
  const [emailCad, setEmailCad] = useState("");
  const [senhaCad, setSenhaCad] = useState("");
  const [cpf, setCpf] = useState("");
  const [dataNasc, setDataNasc] = useState("");

  // ── Login ───────────────────────────────────────────────────
  async function handleLogin(e: React.FormEvent) {
    e.preventDefault();
    setErro("");
    setLoading(true);
    try {
      const { data } = await api.post("/api/usuario/login", { email, senha });

      localStorage.setItem("token", data.token);
      localStorage.setItem("user", data.email);
      localStorage.setItem("userRole", data.role.toLowerCase());

      const role = data.role.toLowerCase();
      const emailLower = data.email.toLowerCase();

      if (emailLower.includes("@toyotaadmin") || role === "admin") {
        router.push("/Vendedor/Administracao");
      } else if (emailLower.includes("@toyota") || role === "vendedor") {
        router.push("/Vendedor/Dashbord");
      } else {
        router.push("/");
      }
    } catch {
      setErro("Email ou senha inválidos.");
    } finally {
      setLoading(false);
    }
  }

  // ── Cadastro via POST /api/pessoaFisica ─────────────────────
  // O backend recebe a entidade diretamente:
  //   senhaHash (não "senha"), cpf sem máscara, dataNascimento obrigatório
  async function handleCadastro(e: React.FormEvent) {
    e.preventDefault();
    setErro("");
    setSucesso("");
    setLoading(true);

    if (!dataNasc) {
      setErro("Data de nascimento é obrigatória.");
      setLoading(false);
      return;
    }

    try {
      await api.post("/api/pessoaFisica", {
        nome,
        email: emailCad,
        senhaHash: senhaCad,          // backend usa senhaHash, não senha
        cpf: cpf.replace(/\D/g, ""),  // apenas números
        dataNascimento: dataNasc,
        role: "CLIENTE",
        ativo: true,
      });

      setSucesso("Conta criada com sucesso! Faça login para continuar.");
      setTab("login");
      setEmail(emailCad);
      setNome(""); setEmailCad(""); setSenhaCad(""); setCpf(""); setDataNasc("");
    } catch (err: any) {
      const msg = String(err?.response?.data?.message ?? err?.response?.data ?? "");
      if (msg.toLowerCase().includes("cpf")) setErro("CPF já cadastrado ou inválido.");
      else if (msg.toLowerCase().includes("email")) setErro("Email já cadastrado.");
      else setErro("Erro ao criar conta. Verifique os dados e tente novamente.");
    } finally {
      setLoading(false);
    }
  }

  // ── Máscara CPF ─────────────────────────────────────────────
  function mascaraCpf(v: string) {
    return v.replace(/\D/g, "")
      .replace(/(\d{3})(\d)/, "$1.$2")
      .replace(/(\d{3})(\d)/, "$1.$2")
      .replace(/(\d{3})(\d{1,2})$/, "$1-$2")
      .slice(0, 14);
  }

  const inputClass =
    "w-full p-3 rounded-lg border border-gray-200 dark:border-gray-700 bg-gray-50 dark:bg-gray-800 text-gray-900 dark:text-gray-100 placeholder:text-gray-400 dark:placeholder:text-gray-500 focus:outline-none focus:ring-2 focus:ring-red-500";

  return (
    <div className="flex items-center justify-center min-h-screen bg-gray-100 dark:bg-gray-950 px-4">
      <div className="bg-white dark:bg-gray-900 rounded-2xl shadow-md dark:shadow-gray-800 w-full max-w-md border border-transparent dark:border-gray-700 overflow-hidden">

        {/* HEADER */}
        <div className="flex flex-col items-center pt-8 pb-4 px-8">
          <div className="bg-red-600 p-4 rounded-xl mb-4">
            <Car className="text-white w-8 h-8" />
          </div>
          <h1 className="text-2xl font-bold text-gray-900 dark:text-gray-100">Toyota Experience</h1>
          <p className="text-gray-500 dark:text-gray-400 text-sm mt-1">
            {tab === "login" ? "Acompanhe seu veículo em tempo real" : "Crie sua conta de cliente"}
          </p>
        </div>

        {/* TABS */}
        <div className="flex mx-8 mb-6 bg-gray-100 dark:bg-gray-800 rounded-xl p-1">
          {(["login", "cadastro"] as Tab[]).map((t) => (
            <button
              key={t}
              onClick={() => { setTab(t); setErro(""); setSucesso(""); }}
              className={`flex-1 py-2 rounded-lg text-sm font-semibold transition ${
                tab === t
                  ? "bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100 shadow"
                  : "text-gray-500 dark:text-gray-400 hover:text-gray-700 dark:hover:text-gray-300"
              }`}
            >
              {t === "login" ? "Entrar" : "Criar conta"}
            </button>
          ))}
        </div>

        <div className="px-8 pb-8 space-y-4">

          {/* MENSAGENS */}
          {erro    && <p className="text-red-500 text-sm bg-red-50 dark:bg-red-900/20 px-3 py-2 rounded-lg">{erro}</p>}
          {sucesso && <p className="text-green-600 text-sm bg-green-50 dark:bg-green-900/20 px-3 py-2 rounded-lg">{sucesso}</p>}

          {/* ── FORMULÁRIO LOGIN ── */}
          {tab === "login" && (
            <form onSubmit={handleLogin} className="space-y-4">
              <div>
                <label className="text-sm text-gray-600 dark:text-gray-400 flex items-center gap-1.5 mb-1">
                  <Mail size={13} /> Email
                </label>
                <input
                  type="email"
                  placeholder="seu@email.com"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  required
                  className={inputClass}
                />
              </div>
              <div>
                <label className="text-sm text-gray-600 dark:text-gray-400 flex items-center gap-1.5 mb-1">
                  <Lock size={13} /> Senha
                </label>
                <input
                  type="password"
                  placeholder="••••••••"
                  value={senha}
                  onChange={(e) => setSenha(e.target.value)}
                  required
                  className={inputClass}
                />
              </div>
              <button
                type="submit"
                disabled={loading}
                className="w-full bg-red-600 text-white py-3 rounded-lg font-semibold hover:bg-red-700 transition disabled:opacity-50"
              >
                {loading ? "Entrando..." : "Entrar"}
              </button>
            </form>
          )}

          {/* ── FORMULÁRIO CADASTRO ── */}
          {tab === "cadastro" && (
            <form onSubmit={handleCadastro} className="space-y-4">
              <div>
                <label className="text-sm text-gray-600 dark:text-gray-400 flex items-center gap-1.5 mb-1">
                  <User size={13} /> Nome completo
                </label>
                <input
                  type="text"
                  placeholder="Seu nome"
                  value={nome}
                  onChange={(e) => setNome(e.target.value)}
                  required
                  className={inputClass}
                />
              </div>
              <div>
                <label className="text-sm text-gray-600 dark:text-gray-400 flex items-center gap-1.5 mb-1">
                  <Mail size={13} /> Email
                </label>
                <input
                  type="email"
                  placeholder="seu@email.com"
                  value={emailCad}
                  onChange={(e) => setEmailCad(e.target.value)}
                  required
                  className={inputClass}
                />
              </div>
              <div>
                <label className="text-sm text-gray-600 dark:text-gray-400 flex items-center gap-1.5 mb-1">
                  <Lock size={13} /> Senha
                </label>
                <input
                  type="password"
                  placeholder="Mínimo 6 caracteres"
                  value={senhaCad}
                  onChange={(e) => setSenhaCad(e.target.value)}
                  required
                  className={inputClass}
                />
              </div>
              <div>
                <label className="text-sm text-gray-600 dark:text-gray-400 flex items-center gap-1.5 mb-1">
                  <IdCard size={13} /> CPF
                </label>
                <input
                  type="text"
                  placeholder="000.000.000-00"
                  value={cpf}
                  onChange={(e) => setCpf(mascaraCpf(e.target.value))}
                  required
                  maxLength={14}
                  className={inputClass}
                />
              </div>
              <div>
                <label className="text-sm text-gray-600 dark:text-gray-400 flex items-center gap-1.5 mb-1">
                  <Calendar size={13} /> Data de nascimento
                </label>
                <input
                  type="date"
                  value={dataNasc}
                  onChange={(e) => setDataNasc(e.target.value)}
                  required
                  className={inputClass}
                />
              </div>
              <button
                type="submit"
                disabled={loading}
                className="w-full bg-red-600 text-white py-3 rounded-lg font-semibold hover:bg-red-700 transition disabled:opacity-50"
              >
                {loading ? "Criando conta..." : "Criar conta"}
              </button>
            </form>
          )}

        </div>
      </div>
    </div>
  );
}
