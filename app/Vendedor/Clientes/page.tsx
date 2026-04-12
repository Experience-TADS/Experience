"use client";

import { useState } from "react";
import Link from "next/link";

import {
  LayoutDashboard,
  Package,
  Users,
  User,
  LogOut,
  Edit,
  Plus,
  Search
} from "lucide-react";

export default function Clientes() {

  const [busca, setBusca] = useState("");

  const [clientes, setClientes] = useState([
    {
      id: 1,
      nome: "Lauren Silva",
      email: "lauren@gmail.com",
      telefone: "11 98888-1111",
      veiculo: "Corolla Cross",
      status: "Ativo"
    },
    {
      id: 2,
      nome: "Julia Harumi",
      email: "julia@gmail.com",
      telefone: "11 97777-2222",
      veiculo: "Hilux SRV",
      status: "Inativo"
    }
  ]);

  const [modalCadastro, setModalCadastro] = useState(false);
  const [modalEditar, setModalEditar] = useState(false);
  const [clienteEditando, setClienteEditando] = useState<any>(null);

  const [novoCliente, setNovoCliente] = useState({
    nome: "",
    email: "",
    telefone: "",
    veiculo: "",
    status: "Ativo"
  });

  const clientesFiltrados = clientes.filter((c) =>
    c.nome.toLowerCase().includes(busca.toLowerCase())
  );

  function getStatusColor(status: string) {
    return status === "Ativo"
      ? "bg-green-100 text-green-700"
      : "bg-red-100 text-red-700";
  }

  function abrirEditar(cliente:any){
    setClienteEditando({ ...cliente });
    setModalEditar(true);
  }

  function salvarEdicao(){
    const atualizados = clientes.map((c)=>
      c.id === clienteEditando.id ? clienteEditando : c
    );

    setClientes(atualizados);
    setModalEditar(false);
  }

  function cadastrarCliente(){
    const novo = {
      ...novoCliente,
      id: Date.now()
    };

    setClientes([...clientes, novo]);

    setNovoCliente({
      nome:"",
      email:"",
      telefone:"",
      veiculo:"",
      status:"Ativo"
    });

    setModalCadastro(false);
  }

  return (

<div className="flex min-h-screen bg-gray-100">

{/* SIDEBAR */}
<div className="hidden md:flex w-64 bg-white border-r flex-col justify-between">

  <div>

    <div className="flex items-center gap-3 p-6">
      <div className="bg-red-600 text-white p-3 rounded-lg">
        <Package size={20} />
      </div>

      <div>
        <p className="font-bold text-black">Toyota</p>
        <p className="text-sm text-black">Painel do Vendedor</p>
      </div>
    </div>

    <nav className="flex flex-col gap-2 px-4">

      <Link href="/Vendedor/Dashbord" className="flex items-center gap-3 text-black p-3 rounded-xl hover:bg-gray-100">
        <LayoutDashboard size={18} />
        Dashboard
      </Link>

      <Link href="/Vendedor/Pedidos" className="flex items-center gap-3 text-black p-3 rounded-xl hover:bg-gray-100">
        <Package size={18} />
        Pedidos
      </Link>

      <Link href="/Vendedor/Clientes" className="flex items-center gap-3 bg-red-600 text-white p-3 rounded-xl">
        <Users size={18} />
        Clientes
      </Link>

      <Link href="/Vendedor/Perfil" className="flex items-center gap-3 text-black p-3 rounded-xl hover:bg-gray-100">
        <User size={18} />
        Perfil
      </Link>

    </nav>

  </div>

  <div className="p-4 border-t">
    <Link href="/Login" className="flex items-center gap-2 text-black hover:text-red-600">
      <LogOut size={18} />
      Sair
    </Link>
  </div>

</div>

{/* CONTEÚDO */}
<div className="flex-1 p-5 md:p-10">

<h1 className="text-2xl md:text-3xl font-bold text-black">
Clientes
</h1>

<p className="text-black mt-1">
Gerencie os clientes da concessionária
</p>

{/* BUSCA + BOTÃO */}
<div className="flex justify-between items-center mt-6">

  <div className="relative max-w-lg w-full">
    <Search className="absolute left-4 top-3 text-gray-400" size={18} />
    <input
      type="text"
      placeholder="Buscar cliente..."
      value={busca}
      onChange={(e)=>setBusca(e.target.value)}
      className="w-full pl-10 pr-4 py-3 rounded-xl border bg-white text-black outline-none focus:ring-2 focus:ring-red-500"
    />
  </div>

  <button
    onClick={()=>setModalCadastro(true)}
    className="ml-4 flex items-center gap-2 bg-red-600 text-white px-5 py-3 rounded-xl hover:bg-red-700"
  >
    <Plus size={18}/>
    Cadastrar Cliente
  </button>

</div>

{/* TABELA */}
<div className="mt-8 bg-white rounded-2xl shadow border overflow-x-auto">

<div className="min-w-[700px]">

<div className="grid grid-cols-6 px-8 py-4 text-black text-sm border-b">
<span>Nome</span>
<span>Email</span>
<span>Telefone</span>
<span>Veículo</span>
<span>Status</span>
<span>Ações</span>
</div>

{clientesFiltrados.map((cliente)=>(
<div key={cliente.id} className="grid grid-cols-6 px-8 py-6 items-center border-b hover:bg-gray-50">

<span className="font-semibold text-black">{cliente.nome}</span>
<span className="text-black">{cliente.email}</span>
<span className="text-black">{cliente.telefone}</span>
<span className="text-black">{cliente.veiculo}</span>

<span className={`px-3 py-1 text-xs rounded-full w-fit ${getStatusColor(cliente.status)}`}>
{cliente.status}
</span>

<button onClick={()=>abrirEditar(cliente)} className="text-blue-600 hover:text-blue-800">
<Edit size={18}/>
</button>

</div>
))}

</div>
</div>

</div>

{/* 🔴 MODAL CADASTRO */}
{modalCadastro && (
<div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">

  <div className="bg-white p-6 rounded-xl w-96">

    <h2 className="text-xl font-bold text-black mb-4">Cadastrar Cliente</h2>

    <div className="space-y-3">

      <input placeholder="Nome" className="w-full border p-2 text-black"
        value={novoCliente.nome}
        onChange={(e)=>setNovoCliente({...novoCliente,nome:e.target.value})}/>

      <input placeholder="Email" className="w-full border p-2 text-black"
        value={novoCliente.email}
        onChange={(e)=>setNovoCliente({...novoCliente,email:e.target.value})}/>

      <input placeholder="Telefone" className="w-full border p-2 text-black"
        value={novoCliente.telefone}
        onChange={(e)=>setNovoCliente({...novoCliente,telefone:e.target.value})}/>

      <input placeholder="Veículo" className="w-full border p-2 text-black"
        value={novoCliente.veiculo}
        onChange={(e)=>setNovoCliente({...novoCliente,veiculo:e.target.value})}/>

      <select className="w-full border p-2 text-black"
        value={novoCliente.status}
        onChange={(e)=>setNovoCliente({...novoCliente,status:e.target.value})}>
        <option>Ativo</option>
        <option>Inativo</option>
      </select>

    </div>

    <div className="flex justify-end gap-2 mt-4">
      <button onClick={()=>setModalCadastro(false)} className="text-black">Cancelar</button>
      <button onClick={cadastrarCliente} className="bg-red-600 text-white px-4 py-2 rounded">Salvar</button>
    </div>

  </div>
</div>
)}

{/* 🔵 MODAL EDITAR */}
{modalEditar && clienteEditando && (
<div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">

  <div className="bg-white p-6 rounded-xl w-96">

    <h2 className="text-xl font-bold text-black mb-4">Editar Cliente</h2>

    <div className="space-y-3">

      <input className="w-full border p-2 text-black"
        value={clienteEditando.nome}
        onChange={(e)=>setClienteEditando({...clienteEditando,nome:e.target.value})}/>

      <input className="w-full border p-2 text-black"
        value={clienteEditando.email}
        onChange={(e)=>setClienteEditando({...clienteEditando,email:e.target.value})}/>

      <input className="w-full border p-2 text-black"
        value={clienteEditando.telefone}
        onChange={(e)=>setClienteEditando({...clienteEditando,telefone:e.target.value})}/>

      <input className="w-full border p-2 text-black"
        value={clienteEditando.veiculo}
        onChange={(e)=>setClienteEditando({...clienteEditando,veiculo:e.target.value})}/>

      <select className="w-full border p-2 text-black"
        value={clienteEditando.status}
        onChange={(e)=>setClienteEditando({...clienteEditando,status:e.target.value})}>
        <option>Ativo</option>
        <option>Inativo</option>
      </select>

    </div>

    <div className="flex justify-end gap-2 mt-4">
      <button onClick={()=>setModalEditar(false)} className="text-black">Cancelar</button>
      <button onClick={salvarEdicao} className="bg-red-600 text-white px-4 py-2 rounded">Salvar</button>
    </div>

  </div>
</div>
)}

</div>
  );
}