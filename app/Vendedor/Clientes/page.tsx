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
  Plus
} from "lucide-react";

export default function Clientes() {

  const [clientes, setClientes] = useState([
    {
      id: 1,
      nome: "Lauren Silva",
      email: "lauren@gmail.com",
      telefone: "11 98888-1111",
      veiculo: "Corolla Cross"
    },
    {
      id: 2,
      nome: "Julia Harumi",
      email: "julia@gmail.com",
      telefone: "11 97777-2222",
      veiculo: "Hilux SRV"
    },
    {
      id: 3,
      nome: "Bianca Nunes",
      email: "bia@gmail.com",
      telefone: "11 96666-3333",
      veiculo: "Yaris Sedan"
    },
    {
      id: 4,
      nome: "Paola Costa",
      email: "paola@gmail.com",
      telefone: "11 95555-4444",
      veiculo: "Corolla"
    },
    {
      id: 5,
      nome: "Vitor Fogaça",
      email: "vitor@gmail.com",
      telefone: "11 94444-5555",
      veiculo: "Yaris Cross"
    }
  ]);

  const [modalCadastro, setModalCadastro] = useState(false);
  const [modalEditar, setModalEditar] = useState(false);
  const [clienteEditando, setClienteEditando] = useState<any>(null);

  const [novoCliente, setNovoCliente] = useState({
    nome: "",
    email: "",
    telefone: "",
    veiculo: ""
  });

  function abrirEditar(cliente:any){
    setClienteEditando(cliente);
    setModalEditar(true);
  }

  function salvarEdicao(){

    const listaAtualizada = clientes.map((c)=>
      c.id === clienteEditando.id ? clienteEditando : c
    );

    setClientes(listaAtualizada);
    setModalEditar(false);
  }

  function cadastrarCliente(){

    const novo = {
      ...novoCliente,
      id: clientes.length + 1
    };

    setClientes([...clientes, novo]);

    setNovoCliente({
      nome:"",
      email:"",
      telefone:"",
      veiculo:""
    });

    setModalCadastro(false);
  }

  return (

    <div className="flex min-h-screen bg-gray-100">

{/* SIDEBAR */}
<div className="w-64 bg-white border-r flex flex-col justify-between">

  <div>

    <div className="flex items-center gap-3 p-6">
      <div className="bg-red-600 text-white p-3 rounded-lg">
        <Package size={20} />
      </div>

      <div>
        <p className="font-bold text-gray-900">Toyota</p>
        <p className="text-sm text-gray-500">Painel do Vendedor</p>
      </div>
    </div>

    <nav className="flex flex-col gap-2 px-4">

      <Link
        href="/Vendedor/Dashboard"
        className="flex items-center gap-3 text-gray-600 p-3 rounded-xl hover:bg-gray-100"
      >
        <LayoutDashboard size={18} />
        Dashboard
      </Link>

      <Link
        href="/Vendedor/Pedidos"
        className="flex items-center gap-3 text-gray-600 p-3 rounded-xl hover:bg-gray-100"
      >
        <Package size={18} />
        Pedidos
      </Link>

      <Link
        href="/Vendedor/Clientes"
        className="flex items-center gap-3 bg-red-600 text-white p-3 rounded-xl"
      >
        <Users size={18} />
        Clientes
      </Link>

      <Link
        href="/Vendedor/Perfil"
        className="flex items-center gap-3 text-gray-600 p-3 rounded-xl hover:bg-gray-100"
      >
        <User size={18} />
        Perfil
      </Link>

    </nav>

  </div>

  <div className="p-4 border-t">
    <Link
      href="/Login"
      className="flex items-center gap-2 text-gray-600 hover:text-red-600"
    >
      <LogOut size={18} />
      Sair
    </Link>
  </div>

</div>


{/* CONTEÚDO */}

<div className="flex-1 p-10">

<h1 className="text-3xl font-bold text-gray-900">
Clientes
</h1>

<p className="text-gray-600 mt-1">
Gerencie os clientes da concessionária
</p>


<button
onClick={()=>setModalCadastro(true)}
className="mt-6 flex items-center gap-2 bg-red-600 text-white px-5 py-2 rounded-lg hover:bg-red-700 transition"
>
<Plus size={18}/>
Cadastrar Cliente
</button>



<div className="mt-8 bg-white rounded-2xl shadow border overflow-hidden">

<div className="grid grid-cols-5 px-8 py-4 text-gray-600 text-sm border-b font-semibold">
<span>Nome</span>
<span>Email</span>
<span>Telefone</span>
<span>Veículo</span>
<span>Ações</span>
</div>


{clientes.map((cliente)=>(
<div
key={cliente.id}
className="grid grid-cols-5 px-8 py-6 items-center border-b hover:bg-gray-50 transition"
>

<span className="font-semibold text-gray-900">
{cliente.nome}
</span>

<span className="text-gray-700">
{cliente.email}
</span>

<span className="text-gray-700">
{cliente.telefone}
</span>

<span className="text-gray-700">
{cliente.veiculo}
</span>

<button
onClick={()=>abrirEditar(cliente)}
className="flex items-center gap-1 text-blue-600 hover:text-blue-800"
>
<Edit size={16}/>
Editar
</button>

</div>
))}

</div>

</div>



{/* MODAL CADASTRAR */}

{modalCadastro && (

<div className="fixed inset-0 bg-black/50 backdrop-blur-sm flex items-center justify-center z-50">

<div className="bg-white w-[420px] rounded-2xl p-6 shadow-xl">

<h2 className="text-xl font-bold text-gray-900 mb-4">
Cadastrar Cliente
</h2>

<div className="space-y-3">

<input
placeholder="Nome"
className="w-full border border-gray-300 text-gray-900 p-3 rounded-lg outline-none focus:border-red-500"
value={novoCliente.nome}
onChange={(e)=>setNovoCliente({...novoCliente,nome:e.target.value})}
/>

<input
placeholder="Email"
className="w-full border border-gray-300 text-gray-900 p-3 rounded-lg outline-none focus:border-red-500"
value={novoCliente.email}
onChange={(e)=>setNovoCliente({...novoCliente,email:e.target.value})}
/>

<input
placeholder="Telefone"
className="w-full border border-gray-300 text-gray-900 p-3 rounded-lg outline-none focus:border-red-500"
value={novoCliente.telefone}
onChange={(e)=>setNovoCliente({...novoCliente,telefone:e.target.value})}
/>

<input
placeholder="Veículo"
className="w-full border border-gray-300 text-gray-900 p-3 rounded-lg outline-none focus:border-red-500"
value={novoCliente.veiculo}
onChange={(e)=>setNovoCliente({...novoCliente,veiculo:e.target.value})}
/>

</div>

<div className="flex justify-end gap-3 mt-6">

<button
onClick={()=>setModalCadastro(false)}
className="px-4 py-2 bg-gray-200 text-gray-800 rounded-lg hover:bg-gray-300"
>
Cancelar
</button>

<button
onClick={cadastrarCliente}
className="px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700"
>
Cadastrar
</button>

</div>

</div>
</div>

)}



{/* MODAL EDITAR */}

{modalEditar && clienteEditando && (

<div className="fixed inset-0 bg-black/60 backdrop-blur-sm flex items-center justify-center z-50">

<div className="bg-white w-[450px] rounded-2xl shadow-2xl p-8">

<h2 className="text-xl font-bold text-gray-900 mb-6">
Editar Cliente
</h2>

<div className="space-y-4">

<input
className="w-full border border-gray-300 text-gray-900 p-3 rounded-lg outline-none focus:border-red-500"
value={clienteEditando.nome}
onChange={(e)=>setClienteEditando({...clienteEditando,nome:e.target.value})}
/>

<input
className="w-full border border-gray-300 text-gray-900 p-3 rounded-lg outline-none focus:border-red-500"
value={clienteEditando.email}
onChange={(e)=>setClienteEditando({...clienteEditando,email:e.target.value})}
/>

<input
className="w-full border border-gray-300 text-gray-900 p-3 rounded-lg outline-none focus:border-red-500"
value={clienteEditando.telefone}
onChange={(e)=>setClienteEditando({...clienteEditando,telefone:e.target.value})}
/>

<input
className="w-full border border-gray-300 text-gray-900 p-3 rounded-lg outline-none focus:border-red-500"
value={clienteEditando.veiculo}
onChange={(e)=>setClienteEditando({...clienteEditando,veiculo:e.target.value})}
/>

</div>

<div className="flex justify-end gap-3 mt-8">

<button
onClick={()=>setModalEditar(false)}
className="px-5 py-2 rounded-lg bg-gray-200 text-gray-800 hover:bg-gray-300"
>
Cancelar
</button>

<button
onClick={salvarEdicao}
className="px-5 py-2 rounded-lg bg-red-600 text-white hover:bg-red-700"
>
Salvar
</button>

</div>

</div>
</div>

)}

</div>
  );
}