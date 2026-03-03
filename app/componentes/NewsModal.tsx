"use client";

import Image from "next/image";

interface ModalProps {
  news: {
    image: string[]; // ✅ agora é array
    title: string;
    content: string;
  };
  onClose: () => void;
}

export default function NewsModal({ news, onClose }: ModalProps) {
  return (
    <div className="fixed inset-0 bg-black/60 flex items-center justify-center z-50">
      
      <div className="relative bg-white rounded-xl max-w-2xl w-full overflow-hidden shadow-2xl">

        <button
          onClick={onClose}
          className="absolute top-4 right-4 z-50 bg-white hover:bg-red-600 hover:text-white transition rounded-full w-10 h-10 flex items-center justify-center shadow-md"
        >
          ✕
        </button>

        {/* 👇 usa a primeira imagem do array */}
        <div className="relative w-full h-56">
          <Image
            src={news.image[0]}
            alt={news.title}
            fill
            className="object-cover"
          />
        </div>

        <div className="p-6">
          <h2 className="text-2xl font-bold text-gray-900 mb-4">
            {news.title}
          </h2>

          <p className="text-gray-800 leading-relaxed text-base">
            {news.content}
          </p>

          <p className="text-gray-600 text-sm mt-6 font-medium">
            Toyota • 25 Fev 2026
          </p>
        </div>
      </div>
    </div>
  );
}