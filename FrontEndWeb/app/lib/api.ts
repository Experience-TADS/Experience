const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080";

export function getToken(): string | null {
  if (typeof window === "undefined") return null;
  return localStorage.getItem("token");
}

export function setToken(token: string): void {
  localStorage.setItem("token", token);
}

export function removeToken(): void {
  localStorage.removeItem("token");
  localStorage.removeItem("user");
  localStorage.removeItem("userRole");
}

function authHeaders(): HeadersInit {
  const token = getToken();
  return {
    "Content-Type": "application/json",
    ...(token ? { Authorization: `Bearer ${token}` } : {}),
  };
}

async function handleResponse<T>(res: Response): Promise<T> {
  if (!res.ok) {
    const text = await res.text().catch(() => res.statusText);
    throw new Error(text || `Erro ${res.status}`);
  }
  if (res.status === 204) return undefined as unknown as T;
  return res.json();
}

export const api = {
  login: (email: string, senha: string) =>
    fetch(`${API_BASE_URL}/api/usuario/login`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ email, senha }),
    }).then((r) => handleResponse<{ token: string; email: string; role: string }>(r)),

  getMe: () =>
    fetch(`${API_BASE_URL}/api/usuario/me`, {
      headers: authHeaders(),
    }).then((r) => handleResponse<UsuarioResponse>(r)),

  getUsuarios: (page = 0, size = 50) =>
    fetch(`${API_BASE_URL}/api/usuario?page=${page}&size=${size}`, {
      headers: authHeaders(),
    }).then((r) => handleResponse<PageResponse<UsuarioResponse>>(r)),

  getUsuario: (id: number) =>
    fetch(`${API_BASE_URL}/api/usuario/${id}`, {
      headers: authHeaders(),
    }).then((r) => handleResponse<UsuarioResponse>(r)),

  createUsuario: (data: UsuarioRequest) =>
    fetch(`${API_BASE_URL}/api/usuario`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(data),
    }).then((r) => handleResponse<UsuarioResponse>(r)),

  updateUsuario: (id: number, data: Partial<UsuarioRequest>) =>
    fetch(`${API_BASE_URL}/api/usuario/${id}`, {
      method: "PUT",
      headers: authHeaders(),
      body: JSON.stringify(data),
    }).then((r) => handleResponse<UsuarioResponse>(r)),

  deleteUsuario: (id: number) =>
    fetch(`${API_BASE_URL}/api/usuario/${id}`, {
      method: "DELETE",
      headers: authHeaders(),
    }).then((r) => handleResponse<void>(r)),

  ativarUsuario: (id: number) =>
    fetch(`${API_BASE_URL}/api/usuario/${id}/ativar`, {
      method: "PATCH",
      headers: authHeaders(),
    }).then((r) => handleResponse<UsuarioResponse>(r)),

  desativarUsuario: (id: number) =>
    fetch(`${API_BASE_URL}/api/usuario/${id}/desativar`, {
      method: "PATCH",
      headers: authHeaders(),
    }).then((r) => handleResponse<UsuarioResponse>(r)),

  getPedidos: (page = 0, size = 50) =>
    fetch(`${API_BASE_URL}/api/pedido?page=${page}&size=${size}`, {
      headers: authHeaders(),
    }).then((r) => handleResponse<PageResponse<PedidoResponse>>(r)),

  getPedido: (id: number) =>
    fetch(`${API_BASE_URL}/api/pedido/${id}`, {
      headers: authHeaders(),
    }).then((r) => handleResponse<PedidoResponse>(r)),

  getMeusPedidos: () =>
    fetch(`${API_BASE_URL}/api/pedido/meus-pedidos`, {
      headers: authHeaders(),
    }).then((r) => handleResponse<PedidoResponse[]>(r)),

  createPedido: (data: PedidoRequest) =>
    fetch(`${API_BASE_URL}/api/pedido`, {
      method: "POST",
      headers: authHeaders(),
      body: JSON.stringify(data),
    }).then((r) => handleResponse<PedidoResponse>(r)),

  createItemPedido: (data: { idPedido: number; idProduto: number; quantidade: number }) =>
    fetch(`${API_BASE_URL}/api/itens-pedido`, {
      method: "POST",
      headers: authHeaders(),
      body: JSON.stringify(data),
    }).then((r) => handleResponse<unknown>(r)),

  updatePedido: (id: number, data: PedidoRequest) =>
    fetch(`${API_BASE_URL}/api/pedido/${id}`, {
      method: "PUT",
      headers: authHeaders(),
      body: JSON.stringify(data),
    }).then((r) => handleResponse<PedidoResponse>(r)),

  deletePedido: (id: number) =>
    fetch(`${API_BASE_URL}/api/pedido/${id}`, {
      method: "DELETE",
      headers: authHeaders(),
    }).then((r) => handleResponse<void>(r)),

  getVeiculos: (page = 0, size = 50) =>
    fetch(`${API_BASE_URL}/api/veiculo?page=${page}&size=${size}`, {
      headers: authHeaders(),
    }).then((r) => handleResponse<PageResponse<VeiculoResponse>>(r)),

  getVeiculo: (id: number) =>
    fetch(`${API_BASE_URL}/api/veiculo/${id}`, {
      headers: authHeaders(),
    }).then((r) => handleResponse<VeiculoResponse>(r)),

  getProdutos: (page = 0, size = 50) =>
    fetch(`${API_BASE_URL}/api/produto?page=${page}&size=${size}`, {
      headers: authHeaders(),
    }).then((r) => handleResponse<PageResponse<ProdutoResponse>>(r)),

  getPessoasFisicas: (page = 0, size = 50) =>
    fetch(`${API_BASE_URL}/api/pessoaFisica?page=${page}&size=${size}`, {
      headers: authHeaders(),
    }).then((r) => handleResponse<PageResponse<unknown>>(r)),

  getPessoaFisica: (id: number) =>
    fetch(`${API_BASE_URL}/api/pessoaFisica/${id}`, {
      headers: authHeaders(),
    }).then((r) => handleResponse<unknown>(r)),

  createPessoaFisica: (data: object) =>
    fetch(`${API_BASE_URL}/api/pessoaFisica`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(data),
    }).then((r) => handleResponse<unknown>(r)),

  updatePessoaFisica: (id: number, data: object) =>
    fetch(`${API_BASE_URL}/api/pessoaFisica/${id}`, {
      method: "PUT",
      headers: authHeaders(),
      body: JSON.stringify(data),
    }).then((r) => handleResponse<unknown>(r)),

  deletePessoaFisica: (id: number) =>
    fetch(`${API_BASE_URL}/api/pessoaFisica/${id}`, {
      method: "DELETE",
      headers: authHeaders(),
    }).then((r) => handleResponse<void>(r)),

  getPessoasJuridicas: (page = 0, size = 50) =>
    fetch(`${API_BASE_URL}/api/pessoaJuridica?page=${page}&size=${size}`, {
      headers: authHeaders(),
    }).then((r) => handleResponse<PageResponse<unknown>>(r)),

  getPessoaJuridica: (id: number) =>
    fetch(`${API_BASE_URL}/api/pessoaJuridica/${id}`, {
      headers: authHeaders(),
    }).then((r) => handleResponse<unknown>(r)),

  createPessoaJuridica: (data: object) =>
    fetch(`${API_BASE_URL}/api/pessoaJuridica`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(data),
    }).then((r) => handleResponse<unknown>(r)),

  updatePessoaJuridica: (id: number, data: object) =>
    fetch(`${API_BASE_URL}/api/pessoaJuridica/${id}`, {
      method: "PUT",
      headers: authHeaders(),
      body: JSON.stringify(data),
    }).then((r) => handleResponse<unknown>(r)),

  deletePessoaJuridica: (id: number) =>
    fetch(`${API_BASE_URL}/api/pessoaJuridica/${id}`, {
      method: "DELETE",
      headers: authHeaders(),
    }).then((r) => handleResponse<void>(r)),

  getEnderecos: () =>
    fetch(`${API_BASE_URL}/api/endereco`, {
      headers: authHeaders(),
    }).then((r) => handleResponse<unknown>(r)),

  createEndereco: (data: object) =>
    fetch(`${API_BASE_URL}/api/endereco`, {
      method: "POST",
      headers: authHeaders(),
      body: JSON.stringify(data),
    }).then((r) => handleResponse<unknown>(r)),

  getTelefone: (id: number) =>
    fetch(`${API_BASE_URL}/api/telefones/${id}`, {
      headers: authHeaders(),
    }).then((r) => handleResponse<unknown>(r)),

  createTelefone: (data: object) =>
    fetch(`${API_BASE_URL}/api/telefones`, {
      method: "POST",
      headers: authHeaders(),
      body: JSON.stringify(data),
    }).then((r) => handleResponse<unknown>(r)),
};

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}

export interface UsuarioResponse {
  id: number;
  nome: string;
  email: string;
  dataNascimento?: string;
  role: string;
  ativo: boolean;
}

export interface UsuarioRequest {
  nome: string;
  email: string;
  senha: string;
  dataNascimento?: string;
  role?: string;
}

export interface PedidoResponse {
  id: number;
  dataPedido: string;
  valorTotal: number;
  cliente: { id: number; nome: string; email: string };
  vendedor: { id: number; nome: string };
  itens: Array<{
    id: number;
    quantidade: number;
    produto: {
      id: number;
      modelo: string;
      cor: string;
      versao: string;
      ano: number;
    };
  }>;
}

export interface PedidoRequest {
  idCliente: number;
  idVendedor: number;
  dataPedido?: string;
  valorTotal: number;
}

export interface VeiculoResponse {
  id: number;
  produto: {
    id: number;
    modelo: string;
    cor: string;
    versao: string;
    ano: number;
  };
  chassi: number;
  statusVeiculo: string;
  idPedido: number;
}

export interface ProdutoResponse {
  id: number;
  idProduto?: number;
  modelo: string;
  cor: string;
  versao: string;
  ano: number;
}
