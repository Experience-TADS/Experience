"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { User, Mail, Phone, LogOut, Loader2, Pencil, X, Check } from "lucide-react";
import Sidebar from "@/app/componentes/SideBar";

const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || "http://54.91.247.129";

type UsuarioData = {
  id?: number;
  nome: string;
  email: string;
  telefone?: string;
  dataNascimento?: string;
  role?: string;
};

export default function Perfil() {
  const router = useRouter();
  const [usuario, setUsuario] = useState<UsuarioData | null>(null);
  const [loading, setLoading] = useState(true);

  // edição
  const [editando, setEditando] = useState(false);
  const [form, setForm] = useState({ nome: "", telefone: "" });
  const [salvando, setSalvando] = useState(false);
  const [erroEdicao, setErroEdicao] = useState("");
  const [sucessoEdicao, setSucessoEdicao] = useState(false);

  const [prefs, setPrefs] = useState({ emailNotif: false, smsNotif: true, promo: false });

  useEffect(() => {
    const token = localStorage.getItem("token");
    const emailLocal = localStorage.getItem("user");

    if (!token && !emailLocal) {
      router.push("/Login");
      return;
    }

    async function carregarPerfil() {
      try {
        const res = await fetch(`${API_BASE_URL}/api/usuario/me`, {
          headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
        });
        if (res.ok) {
          const data = await res.json();
          setUsuario({ id: data.id, nome: data.nome, email: data.email, telefone: data.telefone, dataNascimento: data.dataNascimento, role: data.role });
          setForm({ nome: data.nome || "", telefone: data.telefone || "" });
        } else {
          const fallback = { nome: "", email: emailLocal || "" };
          setUsuario(fallback);
          setForm({ nome: "", telefone: "" });
        }
      } catch {
        const fallback = { nome: "", email: emailLocal || "" };
        setUsuario(fallback);
        setForm({ nome: "", telefone: "" });
      } finally {
        setLoading(false);
      }
    }

    carregarPerfil();

    const savedPrefs = localStorage.getItem("prefs");
    if (savedPrefs) setPrefs(JSON.parse(savedPrefs));
  }, [router]);

  async function salvarEdicao() {
    setErroEdicao("");
    if (!form.nome.trim()) {
      setErroEdicao("O nome não pode ficar vazio.");
      return;
    }
    setSalvando(true);
    try {
      const token = localStorage.getItem("token");
      const id = usuario?.id;

      // Salva o telefone via POST /api/telefones (o PUT de usuário requer senha,
      // que não mantemos no frontend por segurança)
      const numeroLimpo = form.telefone.replace(/\D/g, "");
      if (numeroLimpo.length >= 10) {
        await fetch(`${API_BASE_URL}/api/telefones`, {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
          body: JSON.stringify({ numero: numeroLimpo, idUsuario: id }),
        });
      }

      // Atualiza exibição local com os dados editados
      setUsuario((prev) =>
        prev ? { ...prev, nome: form.nome, telefone: form.telefone || prev.telefone } : prev
      );

      setEditando(false);
      setSucessoEdicao(true);
      setTimeout(() => setSucessoEdicao(false), 3000);
    } catch {
      setErroEdicao("Erro de conexão. Tente novamente.");
    } finally {
      setSalvando(false);
    }
  }

  function cancelarEdicao() {
    setForm({ nome: usuario?.nome || "", telefone: usuario?.telefone || "" });
    setErroEdicao("");
    setEditando(false);
  }

  function updatePref(key: string) {
    const updated = { ...prefs, [key]: !prefs[key as keyof typeof prefs] };
    setPrefs(updated);
    localStorage.setItem("prefs", JSON.stringify(updated));
  }

  function sair() {
    localStorage.removeItem("user");
    localStorage.removeItem("token");
    localStorage.removeItem("userRole");
    router.push("/Login");
  }

  function Switch({ active }: { active: boolean }) {
    return (
      <div className={`w-12 h-6 flex items-center rounded-full p-1 transition-all duration-300 ${active ? "bg-red-500" : "bg-gray-300"}`}>
        <div className={`bg-white w-5 h-5 rounded-full shadow-md transform transition-all duration-300 ${active ? "translate-x-6" : ""}`} />
      </div>
    );
  }

  if (loading) {
    return (
      <div className="flex min-h-screen bg-gray-100">
        <Sidebar />
        <div className="flex-1 flex items-center justify-center md:ml-20">
          <Loader2 className="animate-spin text-red-600" size={32} />
        </div>
      </div>
    );
  }

  return (
    <div className="flex min-h-screen bg-gray-100">
      <Sidebar />

      <div className="flex-1 px-5 md:px-12 py-8 md:ml-20 pb-24 md:pb-8">
        <div className="max-w-3xl mx-auto">

          {/* HEADER */}
          <div className="flex items-center justify-between mb-8">
            <div>
              <h1 className="text-3xl font-bold text-gray-900">Perfil</h1>
              <p className="text-gray-600 text-sm mt-1">Seus dados pessoais e preferências</p>
            </div>
            {!editando && (
              <button
                onClick={() => setEditando(true)}
                className="flex items-center gap-2 px-4 py-2 rounded-xl border border-gray-300 text-gray-600 hover:bg-gray-50 transition text-sm"
              >
                <Pencil size={15} />
                Editar perfil
              </button>
            )}
          </div>

          {/* AVATAR */}
          <div className="flex justify-center mb-8">
            <div className="bg-red-100 p-6 rounded-full shadow-md">
              <User className="text-red-600 w-10 h-10" />
            </div>
          </div>

          {/* MENSAGEM DE SUCESSO */}
          {sucessoEdicao && (
            <div className="flex items-center gap-2 bg-green-50 border border-green-200 text-green-700 px-4 py-3 rounded-xl mb-4 text-sm">
              <Check size={16} /> Perfil atualizado com sucesso!
            </div>
          )}

          {/* DADOS — modo visualização */}
          {!editando && (
            <div className="bg-white rounded-3xl shadow-xl border border-gray-100 overflow-hidden mb-8">
              <div className="flex items-center gap-4 p-5 border-b">
                <div className="bg-gray-100 p-3 rounded-xl"><User className="text-gray-600 w-5 h-5" /></div>
                <div>
                  <p className="text-xs text-gray-500">Nome</p>
                  <p className="font-semibold text-gray-900">{usuario?.nome || "—"}</p>
                </div>
              </div>
              <div className="flex items-center gap-4 p-5 border-b">
                <div className="bg-gray-100 p-3 rounded-xl"><Mail className="text-gray-600 w-5 h-5" /></div>
                <div>
                  <p className="text-xs text-gray-500">Email</p>
                  <p className="font-semibold text-gray-900 break-all">{usuario?.email || "—"}</p>
                </div>
              </div>
              <div className="flex items-center gap-4 p-5">
                <div className="bg-gray-100 p-3 rounded-xl"><Phone className="text-gray-600 w-5 h-5" /></div>
                <div>
                  <p className="text-xs text-gray-500">Telefone</p>
                  <p className="font-semibold text-gray-900">{usuario?.telefone || "—"}</p>
                </div>
              </div>
            </div>
          )}

          {/* DADOS — modo edição */}
          {editando && (
            <div className="bg-white rounded-3xl shadow-xl border border-gray-100 overflow-hidden mb-8">
              <div className="p-6 space-y-4">
                <div>
                  <label className="text-xs text-gray-500 mb-1 block">Nome</label>
                  <input
                    type="text"
                    value={form.nome}
                    onChange={(e) => setForm({ ...form, nome: e.target.value })}
                    className="w-full p-3 rounded-xl border border-gray-200 text-gray-900 focus:outline-none focus:ring-2 focus:ring-red-500"
                    placeholder="Seu nome completo"
                  />
                </div>

                <div>
                  <label className="text-xs text-gray-500 mb-1 block">Email</label>
                  <input
                    type="email"
                    value={usuario?.email || ""}
                    disabled
                    className="w-full p-3 rounded-xl border border-gray-100 bg-gray-50 text-gray-400 cursor-not-allowed"
                  />
                  <p className="text-xs text-gray-400 mt-1">O email não pode ser alterado.</p>
                </div>

                <div>
                  <label className="text-xs text-gray-500 mb-1 block">Telefone</label>
                  <input
                    type="tel"
                    value={form.telefone}
                    onChange={(e) => {
                      const v = e.target.value.replace(/\D/g, "")
                        .replace(/(\d{2})(\d)/, "($1) $2")
                        .replace(/(\d{5})(\d)/, "$1-$2")
                        .slice(0, 15);
                      setForm({ ...form, telefone: v });
                    }}
                    className="w-full p-3 rounded-xl border border-gray-200 text-gray-900 focus:outline-none focus:ring-2 focus:ring-red-500"
                    placeholder="(11) 99999-9999"
                    maxLength={15}
                  />
                </div>

                {erroEdicao && (
                  <p className="text-red-500 text-sm">{erroEdicao}</p>
                )}

                <div className="flex gap-3 pt-2">
                  <button
                    onClick={cancelarEdicao}
                    className="flex-1 flex items-center justify-center gap-2 py-3 rounded-xl border border-gray-300 text-gray-600 hover:bg-gray-50 transition"
                  >
                    <X size={16} /> Cancelar
                  </button>
                  <button
                    onClick={salvarEdicao}
                    disabled={salvando}
                    className="flex-1 flex items-center justify-center gap-2 py-3 rounded-xl bg-red-600 text-white hover:bg-red-700 transition disabled:opacity-50"
                  >
                    {salvando ? <Loader2 className="animate-spin" size={16} /> : <Check size={16} />}
                    {salvando ? "Salvando..." : "Salvar"}
                  </button>
                </div>
              </div>
            </div>
          )}

          {/* PREFERÊNCIAS */}
          <div className="mb-4">
            <h2 className="text-lg font-semibold text-gray-900">Preferências</h2>
            <p className="text-sm text-gray-500">Controle como você recebe notificações</p>
          </div>

          <div className="bg-white rounded-3xl shadow-xl border border-gray-100 overflow-hidden mb-8">
            {[
              { key: "emailNotif", label: "Notificações por Email", desc: "Receber atualizações por email" },
              { key: "smsNotif", label: "Notificações por SMS", desc: "Alertas diretos no celular" },
              { key: "promo", label: "Emails Promocionais", desc: "Ofertas e novidades" },
            ].map(({ key, label, desc }, i, arr) => (
              <div
                key={key}
                onClick={() => updatePref(key)}
                className={`flex items-center justify-between p-5 cursor-pointer hover:bg-gray-50 transition ${i < arr.length - 1 ? "border-b" : ""}`}
              >
                <div>
                  <p className="text-sm font-medium text-gray-900">{label}</p>
                  <p className="text-xs text-gray-500">{desc}</p>
                </div>
                <Switch active={prefs[key as keyof typeof prefs]} />
              </div>
            ))}
          </div>

          <button
            onClick={sair}
            className="w-full bg-white border border-red-500 text-red-500 py-3 rounded-xl font-semibold flex items-center justify-center gap-2 hover:bg-red-50 transition shadow-sm"
          >
            <LogOut size={18} />
            Sair da conta
          </button>

        </div>
      </div>
    </div>
  );
}
