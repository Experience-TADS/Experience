const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || "http://54.91.247.129";

export const api = {
  // Usuários
  getUsuarios: () => fetch(`${API_BASE_URL}/api/usuario`).then((r) => r.json()),
  getUsuario: (id: number) => fetch(`${API_BASE_URL}/api/usuario/${id}`).then((r) => r.json()),
  createUsuario: (data: object) =>
    fetch(`${API_BASE_URL}/api/usuario`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(data),
    }).then((r) => r.json()),
  updateUsuario: (id: number, data: object) =>
    fetch(`${API_BASE_URL}/api/usuario/${id}`, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(data),
    }).then((r) => r.json()),
  deleteUsuario: (id: number) =>
    fetch(`${API_BASE_URL}/api/usuario/${id}`, { method: "DELETE" }),

  // Pessoa Física
  getPessoasFisicas: () => fetch(`${API_BASE_URL}/api/pessoaFisica`).then((r) => r.json()),
  getPessoaFisica: (id: number) => fetch(`${API_BASE_URL}/api/pessoaFisica/${id}`).then((r) => r.json()),
  createPessoaFisica: (data: object) =>
    fetch(`${API_BASE_URL}/api/pessoaFisica`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(data),
    }).then((r) => r.json()),
  updatePessoaFisica: (id: number, data: object) =>
    fetch(`${API_BASE_URL}/api/pessoaFisica/${id}`, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(data),
    }).then((r) => r.json()),
  deletePessoaFisica: (id: number) =>
    fetch(`${API_BASE_URL}/api/pessoaFisica/${id}`, { method: "DELETE" }),

  // Pessoa Jurídica
  getPessoasJuridicas: () => fetch(`${API_BASE_URL}/api/pessoaJuridica`).then((r) => r.json()),
  createPessoaJuridica: (data: object) =>
    fetch(`${API_BASE_URL}/api/pessoaJuridica`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(data),
    }).then((r) => r.json()),
  updatePessoaJuridica: (id: number, data: object) =>
    fetch(`${API_BASE_URL}/api/pessoaJuridica/${id}`, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(data),
    }).then((r) => r.json()),
  deletePessoaJuridica: (id: number) =>
    fetch(`${API_BASE_URL}/api/pessoaJuridica/${id}`, { method: "DELETE" }),

  // Endereços
  getEnderecos: () => fetch(`${API_BASE_URL}/api/endereco`).then((r) => r.json()),
  createEndereco: (data: object) =>
    fetch(`${API_BASE_URL}/api/endereco`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(data),
    }).then((r) => r.json()),

  // Telefones
  getTelefones: () => fetch(`${API_BASE_URL}/api/telefones`).then((r) => r.json()),
  createTelefone: (data: object) =>
    fetch(`${API_BASE_URL}/api/telefones`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(data),
    }).then((r) => r.json()),
};
