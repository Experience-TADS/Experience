"use client";

import { Home, Clock, Link, MapPin, User } from "lucide-react";

export default function Sidebar() {
  return (
    <div className="w-20 h-screen bg-white shadow-md flex flex-col items-center py-6 gap-6">
      
      <div className="bg-red-500 p-3 rounded-xl text-white">
        <Home size={22} />
      </div>

      <Clock className="text-gray-400" />
      <Link className="text-gray-400" />
      <MapPin className="text-gray-400" />
      <User className="text-gray-400" />
      
    </div>
  );
}