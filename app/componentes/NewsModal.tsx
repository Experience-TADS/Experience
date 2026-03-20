"use client";

import Image from "next/image";

interface ModalProps {
  news: {
    image: string[];
    title: string;
    content: string;
  };
  onClose: () => void;
}

export default function NewsModal({ news, onClose }: ModalProps) {
  return (
    <div className="fixed inset-0 bg-black/60 flex items-center justify-center z-50 p-4">

      <div className="relative bg-white rounded-xl w-full max-w-2xl overflow-hidden shadow-2xl max-h-[90vh] overflow-y-auto">

        <button
          onClick={onClose}
          className="absolute top-3 right-3 sm:top-4 sm:right-4 z-50 bg-white hover:bg-red-600 hover:text-white transition rounded-full w-9 h-9 sm:w-10 sm:h-10 flex items-center justify-center shadow-md"
        >
          ✕
        </button>

        {/* IMAGEM */}
        <div className="relative w-full h-44 sm:h-56 md:h-64">
          <Image
            src={news.image[0]}
            alt={news.title}
            fill
            className="object-cover"
          />
        </div>

        <div className="p-4 sm:p-6">
          <h2 className="text-xl sm:text-2xl md:text-3xl font-bold text-gray-900 mb-4">
            {news.title}
          </h2>

          <p className="text-gray-800 leading-relaxed text-sm sm:text-base">
            {news.content}
          </p>

          <p className="text-gray-600 text-xs sm:text-sm mt-6 font-medium">
            Toyota • 25 Fev 2026
          </p>
        </div>

      </div>

    </div>
  );
}