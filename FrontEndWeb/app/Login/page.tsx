"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { Car } from "lucide-react";
import { api, setToken } from "@/app/lib/api";

function detectarRole(email: string): string {
  const lower = email.toLowerCase();
  if (lower.includes("@admin")) return "admin";
  if (lower.includes("@toyota")) return "vendedor";
  return "cliente";
}

export default function LoginPage() {
  const router = useRouter();

  const [modo, setModo] = useState<"login" | "cadastro">("login");
  const [email, setEmail] = useState("");
  const [senha, setSenha] = useState("");
  const [nome, setNome] = useState("");
  const [confirmarSenha, setConfirmarSenha] = useState("");
  const [dataNascimento, setDataNascimento] = useState("");
  const [erro, setErro] = useState("");
  const [sucesso, setSucesso] = useState("");
  const [loading, setLoading] = useState(false);

  function trocarModo(novo: "login" | "cadastro") {
    setModo(novo);
    setErro("");
    setSucesso("");
    setEmail("");
    setSenha("");
    setNome("");
    setConfirmarSenha("");
    setDataNascimento("");
  }

  function redirecionar(role: string) {
    if (role === "admin") router.push("/Vendedor/Administracao");
    else if (role === "vendedor") router.push("/Vendedor/Dashbord");
    else router.push("/");
  }

  async function handleLogin(e: React.FormEvent) {
    e.preventDefault();
    setErro("");
    if (!email || !senha) { setErro("Preencha todos os campos."); return; }

    setLoading(true);
    try {
      const data = await api.login(email, senha);

      setToken(data.token);
      localStorage.setItem("user", data.email);

      // Usa a role do backend; se vier null/vazio, detecta pelo domínio do email
      const roleBackend = (data.role || "").toLowerCase();
      const role = roleBackend && roleBackend !== "null" ? roleBackend : detectarRole(email);
      localStorage.setItem("userRole", role);

      redirecionar(role);
    } catch (err: unknown) {
      const msg = err instanceof Error ? err.message : "";
      if (msg.includes("401") || msg.toLowerCase().includes("inválid")) {
        setErro("Email ou senha inválidos.");
      } else {
        setErro("Não foi possível conectar ao servidor.");
      }
    } finally {
      setLoading(false);
    }
  }

  async function handleCadastro(e: React.FormEvent) {
    e.preventDefault();
    setErro("");
    setSucesso("");

    if (!nome || !email || !senha || !confirmarSenha || !dataNascimento) {
      setErro("Preencha todos os campos.");
      return;
    }
    if (senha !== confirmarSenha) { setErro("As senhas não coincidem."); return; }
    if (senha.length < 6) { setErro("A senha deve ter pelo menos 6 caracteres."); return; }

    // Determina role pelo domínio do email no cadastro
    const role = detectarRole(email) === "vendedor" ? "VENDEDOR" : "CLIENTE";

    setLoading(true);
    try {
      await api.createUsuario({ nome, email, senha, dataNascimento, role });
      setSucesso("Conta criada com sucesso! Faça login para continuar.");
      trocarModo("login");
    } catch (err: unknown) {
      const msg = err instanceof Error ? err.message : "";
      if (msg.includes("409") || msg.toLowerCase().includes("já existe") || msg.toLowerCase().includes("duplicate")) {
        setErro("Este email já está cadastrado.");
      } else {
        setErro("Não foi possível criar a conta. Tente novamente.");
      }
    } finally {
      setLoading(false);
    }
  }

  const inputClass = "w-full mt-1 p-3 rounded-lg border border-gray-200 bg-gray-50 text-gray-900 placeholder:text-gray-400 focus:outline-none focus:ring-2 focus:ring-red-500";

  return (
    <div className="flex items-center justify-center min-h-screen bg-gray-100 dark:bg-gray-950">
      <div className="bg-white dark:bg-gray-900 p-8 rounded-2xl shadow-lg w-full max-w-sm border border-gray-100 dark:border-gray-800">

        <div className="flex flex-col items-center mb-6">
          <div className="bg-red-600 p-4 rounded-xl mb-4">
            <Car className="text-white w-8 h-8" />
          </div>
          <h1 className="text-2xl font-bold text-gray-900 dark:text-white">Toyota Experience</h1>
          <p className="text-gray-500 text-sm mt-1">
            {modo === "login" ? "Acompanhe seu veículo em tempo real" : "Crie sua conta"}
          </p>
        </div>

        {/* ABAS */}
        <div className="flex rounded-xl border border-gray-200 dark:border-gray-700 overflow-hidden mb-6">
          <button
            onClick={() => trocarModo("login")}
            className={`flex-1 py-2 text-sm font-medium transition ${
              modo === "login"
                ? "bg-red-600 text-white"
                : "bg-white dark:bg-gray-900 text-gray-500 dark:text-gray-400 hover:bg-gray-50 dark:hover:bg-gray-800"
            }`}
          >
            Login
          </button>
          <button
            onClick={() => trocarModo("cadastro")}
            className={`flex-1 py-2 text-sm font-medium transition ${
              modo === "cadastro"
                ? "bg-red-600 text-white"
                : "bg-white dark:bg-gray-900 text-gray-500 dark:text-gray-400 hover:bg-gray-50 dark:hover:bg-gray-800"
            }`}
          >
            Cadastro
          </button>
        </div>

        {sucesso && <p className="text-green-600 text-sm mb-4 text-center">{sucesso}</p>}

        {modo === "login" ? (
          <form onSubmit={handleLogin} className="space-y-4">
            <div>
              <label className="text-sm text-gray-600 dark:text-gray-400">Email</label>
              <input type="email" placeholder="seu@email.com" className={inputClass} value={email} onChange={(e) => setEmail(e.target.value)} required />
            </div>
            <div>
              <label className="text-sm text-gray-600 dark:text-gray-400">Senha</label>
              <input type="password" placeholder="••••••••" className={inputClass} value={senha} onChange={(e) => setSenha(e.target.value)} required />
            </div>

            {erro && <p className="text-red-500 text-sm">{erro}</p>}

            <button type="submit" disabled={loading} className="w-full bg-red-600 text-white py-3 rounded-lg font-semibold hover:bg-red-700 transition disabled:opacity-50">
              {loading ? "Entrando..." : "Entrar"}
            </button>

            <p className="text-xs text-center text-gray-400 pt-1">
              <span className="font-medium">@toyota.com</span> → vendedor &nbsp;|&nbsp; <span className="font-medium">@gmail.com</span> → cliente
            </p>
          </form>
        ) : (
          <form onSubmit={handleCadastro} className="space-y-4">
            <div>
              <label className="text-sm text-gray-600 dark:text-gray-400">Nome completo</label>
              <input type="text" placeholder="Seu nome" className={inputClass} value={nome} onChange={(e) => setNome(e.target.value)} required />
            </div>
            <div>
              <label className="text-sm text-gray-600 dark:text-gray-400">Email</label>
              <input type="email" placeholder="seu@email.com" className={inputClass} value={email} onChange={(e) => setEmail(e.target.value)} required />
            </div>
            <div>
              <label className="text-sm text-gray-600 dark:text-gray-400">Data de nascimento</label>
              <input type="date" className={inputClass} value={dataNascimento} onChange={(e) => setDataNascimento(e.target.value)} required />
            </div>
            <div>
              <label className="text-sm text-gray-600 dark:text-gray-400">Senha</label>
              <input type="password" placeholder="Mínimo 6 caracteres" className={inputClass} value={senha} onChange={(e) => setSenha(e.target.value)} required />
            </div>
            <div>
              <label className="text-sm text-gray-600 dark:text-gray-400">Confirmar senha</label>
              <input type="password" placeholder="Repita a senha" className={inputClass} value={confirmarSenha} onChange={(e) => setConfirmarSenha(e.target.value)} required />
            </div>

            {/* Dica de domínio */}
            {email && (
              <p className="text-xs text-gray-400">
                Tipo de conta:{" "}
                <span className="font-semibold text-red-600">
                  {detectarRole(email) === "vendedor" ? "Vendedor (Toyota)" : "Cliente"}
                </span>
              </p>
            )}

            {erro && <p className="text-red-500 text-sm">{erro}</p>}

            <button type="submit" disabled={loading} className="w-full bg-red-600 text-white py-3 rounded-lg font-semibold hover:bg-red-700 transition disabled:opacity-50">
              {loading ? "Criando conta..." : "Criar conta"}
            </button>
          </form>
        )}
      </div>
    </div>
  );
}
