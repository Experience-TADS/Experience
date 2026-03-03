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
      className="bg-white rounded-xl shadow-md overflow-hidden cursor-pointer hover:shadow-xl transition duration-300"
    >
      <div
        onMouseEnter={() => image.length > 1 && setCurrentImage(1)}
        onMouseLeave={() => setCurrentImage(0)}
        className="overflow-hidden"
      >
        <img
          src={image[currentImage]}
          alt={title}
          className="w-full h-34 object-cover transition duration-500 hover:scale-105"
        />
      </div>

      <div className="p-4">
        <h2 className="text-lg font-bold text-gray-900">
          {title}
        </h2>

        <p className="text-gray-700 text-sm mt-2">
          {summary}
        </p>
      </div>
    </div>
  );
}