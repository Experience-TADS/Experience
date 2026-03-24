"use client";

import { useState } from "react";

interface CardCarProps {
  image: string[];
  title: string;
  summary: string;
  onClick: () => void;
}

export default function CardCar({
  image,
  title,
  summary,
  onClick,
}: CardCarProps) {
  const [currentImage, setCurrentImage] = useState(0);

  return (
    <div
      onClick={onClick}
      className="
        group
        bg-white 
        rounded-xl 
        shadow-md 
        overflow-hidden 
        cursor-pointer 
        hover:shadow-xl 
        transition 
        duration-300
        w-full
      "
    >
      <div
        onMouseEnter={() => image.length > 1 && setCurrentImage(1)}
        onMouseLeave={() => setCurrentImage(0)}
        className="relative overflow-hidden"
      >
        <img
          src={image[currentImage]}
          alt={title}
          className="
            w-full 
            h-32 sm:h-36 md:h-45
            object-cover 
            transition 
            duration-500 
            group-hover:scale-105
          "
        />

        {/* OVERLAY SUAVE */}
        <div className="
          absolute inset-0 
          bg-black/30 
          opacity-0 
          group-hover:opacity-100 
          transition
        " />

        {/* BOTÃO */}
        <div className="
          absolute inset-0 
          flex items-center justify-center 
          opacity-0 
          group-hover:opacity-100 
          transition
        ">
          <span className="
            bg-white 
            text-gray-900 
            px-3 py-1.5 
            rounded-lg 
            text-xs sm:text-sm 
            font-semibold 
            shadow
          ">
            Ver detalhes
          </span>
        </div>
      </div>

      <div className="p-3">

        <h2 className="text-sm sm:text-base font-bold text-gray-900">
          {title}
        </h2>

        <p className="text-gray-600 text-xs sm:text-sm mt-1">
          {summary}
        </p>

      </div>
    </div>
  );
}