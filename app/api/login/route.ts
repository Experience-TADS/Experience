import { NextResponse } from "next/server";
import { users } from "../users";

export async function POST(req: Request) {
  const { email, senha } = await req.json();

  const user = users.find(
    (u) => u.email === email && u.senha === senha
  );

  if (!user) {
    return NextResponse.json(
      { error: "Email ou senha inválidos" },
      { status: 401 }
    );
  }

  return NextResponse.json({ message: "Login realizado com sucesso" });
}