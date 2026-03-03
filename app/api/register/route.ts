import { NextResponse } from "next/server";
import { users } from "../users";

export async function POST(req: Request) {
  const { email, senha } = await req.json();

  const userExists = users.find((u) => u.email === email);

  if (userExists) {
    return NextResponse.json(
      { error: "Email já cadastrado" },
      { status: 400 }
    );
  }

  users.push({ email, senha });

  return NextResponse.json({ message: "Usuário criado com sucesso" });
}