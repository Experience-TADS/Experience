"use client";

import { useEffect, useRef, useState } from "react";
import { Send, ShieldCheck } from "lucide-react";
import Sidebar from "./SideBar";

const API = "http://localhost:5000/api";

type Message = {
  from: "bot" | "user";
  text: string;
};

type BotState = {
  menu: string;
  ultimo_menu?: string;
};

function getInitials(email: string): string {
  if (!email) return "EU";
  const name = email.split("@")[0];
  const parts = name.split(/[._-]/);
  if (parts.length >= 2) return (parts[0][0] + parts[1][0]).toUpperCase();
  return name.slice(0, 2).toUpperCase();
}

function getFirstName(email: string): string {
  if (!email) return "Você";
  const name = email.split("@")[0];
  const parts = name.split(/[._-]/);
  const first = parts[0];
  return first.charAt(0).toUpperCase() + first.slice(1).toLowerCase();
}

export default function ToyotaChat() {
  const [messages, setMessages] = useState<Message[]>([]);
  const [input, setInput]       = useState("");
  const [state, setState]       = useState<BotState | null>(null);
  const [typing, setTyping]     = useState(false);
  const [userName, setUserName] = useState("");
  const bottomRef               = useRef<HTMLDivElement>(null);
  const inputRef                = useRef<HTMLInputElement>(null);
  const fetchedRef              = useRef(false);

  useEffect(() => {
    const stored = localStorage.getItem("user") || "";
    setUserName(stored);
  }, []);

  useEffect(() => {
    if (fetchedRef.current) return;
    fetchedRef.current = true;
    fetch(`${API}/start`)
      .then((r) => r.json())
      .then((data) => {
        setMessages([{ from: "bot", text: data.reply }]);
        setState(data.state);
      })
      .catch(() => {
        setMessages([{
          from: "bot",
          text: "⚠️ Não foi possível conectar ao servidor.\nCertifique-se de que o api.py está rodando:\n\npython api.py",
        }]);
      });
  }, []);

  useEffect(() => {
    bottomRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages, typing]);

  function addBotMessage(text: string) {
    setMessages((prev) => [...prev, { from: "bot", text }]);
  }

  function addUserMessage(text: string) {
    setMessages((prev) => [...prev, { from: "user", text }]);
  }

  async function sendMessage() {
    const text = input.trim();
    if (!text) return;
    setInput("");
    addUserMessage(text);
    setTyping(true);

    try {
      const res = await fetch(`${API}/chat`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ message: text, state }),
      });
      const data = await res.json();
      setState(data.state);
      await new Promise((r) => setTimeout(r, 600));
      setTyping(false);
      if (data.reply === "__SAIR__") {
        addBotMessage("Até mais! Obrigado por usar o Toyota Experience 🚗");
        return;
      }
      addBotMessage(data.reply);
    } catch {
      setTyping(false);
      addBotMessage("⚠️ Não consegui me conectar ao servidor. Tente novamente.");
    }
  }

  function handleKey(e: React.KeyboardEvent<HTMLInputElement>) {
    if (e.key === "Enter") sendMessage();
  }

  const initials  = getInitials(userName);
  const firstName = getFirstName(userName);

  return (
    <div className="flex h-screen bg-gray-100 dark:bg-gray-950 overflow-hidden">

      <Sidebar />

      <div className="flex-1 flex flex-col md:ml-20 overflow-hidden">

        {/* HEADER */}
        <div className="px-5 md:px-10 pt-8 pb-4 shrink-0">
          <p className="text-gray-500 dark:text-gray-400 text-xs sm:text-sm">Assistente Virtual</p>
          <h1 className="text-2xl sm:text-3xl font-bold text-gray-800 dark:text-gray-100 mt-0.5">
            Assistente Totoya 🛡️
          </h1>
          <p className="text-gray-500 dark:text-gray-400 mt-1 text-sm">
            Consulte os códigos do manual de segurança do seu veículo
          </p>
        </div>

        {/* CARD DO CHAT */}
        <div className="flex-1 flex flex-col mx-5 md:mx-10 mb-6 bg-white dark:bg-gray-900 rounded-3xl shadow-md border border-gray-100 dark:border-gray-700 overflow-hidden">

          {/* Topo do card */}
          <div className="flex items-center gap-3 px-5 py-4 border-b border-gray-100 dark:border-gray-700 shrink-0 bg-white dark:bg-gray-900">
            <div className="w-10 h-10 rounded-2xl bg-red-600 flex items-center justify-center shadow-sm shrink-0">
              <ShieldCheck className="text-white w-5 h-5" />
            </div>
            <div>
              <p className="text-sm font-semibold text-gray-900 dark:text-gray-100 leading-tight">Totoya</p>
              <div className="flex items-center gap-1.5 mt-0.5">
                <span className="w-1.5 h-1.5 rounded-full bg-green-400" />
                <p className="text-xs text-gray-400 dark:text-gray-500">Assistente de Segurança · Online</p>
              </div>
            </div>

            {userName && (
              <div className="ml-auto flex items-center gap-2">
                <p className="text-xs text-gray-400 dark:text-gray-500 hidden sm:block">{firstName}</p>
                <div className="w-8 h-8 rounded-full bg-gray-100 dark:bg-gray-700 border border-gray-200 dark:border-gray-600 flex items-center justify-center text-gray-600 dark:text-gray-300 text-[10px] font-bold">
                  {initials}
                </div>
              </div>
            )}
          </div>

          {/* Área de mensagens */}
          <div className="flex-1 overflow-y-auto px-5 md:px-8 py-5 space-y-3 bg-gray-50/60 dark:bg-gray-950/60">

            {messages.map((msg, i) => (
              <div
                key={i}
                className={`flex items-end gap-2 ${msg.from === "user" ? "justify-end" : "justify-start"}`}
              >
                {/* Avatar Totoya */}
                {msg.from === "bot" && (
                  <div className="w-7 h-7 rounded-full bg-red-600 flex items-center justify-center text-white text-[10px] font-bold shrink-0 mb-0.5 shadow-sm">
                    T
                  </div>
                )}

                <div
                  className={`
                    max-w-[70%] rounded-2xl px-4 py-3 text-sm whitespace-pre-wrap leading-relaxed
                    ${msg.from === "user"
                      ? "bg-red-600 text-white rounded-br-sm shadow-sm"
                      : "bg-white dark:bg-gray-800 text-gray-800 dark:text-gray-100 rounded-bl-sm border border-gray-100 dark:border-gray-700 shadow-sm"
                    }
                  `}
                >
                  {msg.text}
                </div>

                {/* Avatar usuário */}
                {msg.from === "user" && (
                  <div className="w-7 h-7 rounded-full bg-gray-200 dark:bg-gray-700 border border-gray-300 dark:border-gray-600 flex items-center justify-center text-gray-600 dark:text-gray-300 text-[10px] font-bold shrink-0 mb-0.5">
                    {initials}
                  </div>
                )}
              </div>
            ))}

            {/* Indicador de digitação */}
            {typing && (
              <div className="flex items-end gap-2 justify-start">
                <div className="w-7 h-7 rounded-full bg-red-600 flex items-center justify-center text-white text-[10px] font-bold shrink-0 mb-0.5 shadow-sm">
                  T
                </div>
                <div className="bg-white dark:bg-gray-800 border border-gray-100 dark:border-gray-700 rounded-2xl rounded-bl-sm px-4 py-3 flex gap-1 items-center shadow-sm">
                  <span className="w-2 h-2 bg-red-300 rounded-full animate-bounce [animation-delay:0ms]" />
                  <span className="w-2 h-2 bg-red-400 rounded-full animate-bounce [animation-delay:150ms]" />
                  <span className="w-2 h-2 bg-red-500 rounded-full animate-bounce [animation-delay:300ms]" />
                </div>
              </div>
            )}

            <div ref={bottomRef} />
          </div>

          {/* Input */}
          <div className="px-5 md:px-8 py-4 border-t border-gray-100 dark:border-gray-700 bg-white dark:bg-gray-900 flex gap-3 items-center shrink-0">
            <input
              ref={inputRef}
              type="text"
              value={input}
              onChange={(e) => setInput(e.target.value)}
              onKeyDown={handleKey}
              placeholder="Digite um número ou código (ex: P0300, C1201)..."
              className="
                flex-1 bg-gray-100 dark:bg-gray-800
                text-gray-900 dark:text-gray-100
                placeholder-gray-400 dark:placeholder-gray-500
                rounded-xl px-4 py-3 text-sm
                outline-none focus:ring-2 focus:ring-red-500
                focus:bg-white dark:focus:bg-gray-700
                border border-transparent focus:border-red-200 dark:focus:border-red-800
                transition-all duration-200
              "
            />
            <button
              onClick={sendMessage}
              disabled={!input.trim()}
              className="
                bg-red-600 hover:bg-red-700
                disabled:bg-gray-200 dark:disabled:bg-gray-700
                disabled:text-gray-400 dark:disabled:text-gray-500
                text-white p-3 rounded-xl transition-all duration-200
                active:scale-95 shadow-sm hover:shadow-md
                disabled:shadow-none disabled:cursor-not-allowed
              "
              aria-label="Enviar"
            >
              <Send size={18} />
            </button>
          </div>

        </div>
      </div>
    </div>
  );
}
