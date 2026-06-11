"use client";

import { useMemo } from "react";

const roadPoints = [
  { x: 8, y: 95 },
  { x: 28, y: 82 },
  { x: 55, y: 75 },
  { x: 82, y: 68 },
  { x: 92, y: 55 },
  { x: 65, y: 48 },
  { x: 35, y: 40 },
  { x: 12, y: 30 },
  { x: 40, y: 18 },
  { x: 78, y: 8 },
];

const generatePath = () => {
  let d = `M ${roadPoints[0].x} ${roadPoints[0].y}`;

  for (let i = 1; i < roadPoints.length; i++) {
    const prev = roadPoints[i - 1];
    const curr = roadPoints[i];

    const dx = curr.x - prev.x;

    const cp1x = prev.x + dx * 0.55;
    const cp1y = prev.y;

    const cp2x = prev.x + dx * 0.45;
    const cp2y = curr.y;

    d += ` C ${cp1x} ${cp1y}, ${cp2x} ${cp2y}, ${curr.x} ${curr.y}`;
  }

  return d;
};

const getPointOnPath = (t: number) => {
  const total = roadPoints.length - 1;
  const i = Math.floor(t * total);
  const next = Math.min(i + 1, total);

  const p0 = roadPoints[i];
  const p1 = roadPoints[next];

  const localT = t * total - i;

  return {
    x: p0.x + (p1.x - p0.x) * localT,
    y: p0.y + (p1.y - p0.y) * localT,
  };
};

interface Props {
  currentStageNum: number;
  activeStage: number;
  getStageStatus: (num: number) => string;
  stageTitle?: string;
}

const RoadVisualization = ({
  currentStageNum,
  activeStage,
  getStageStatus,
  stageTitle,
}: Props) => {

  const pathD = useMemo(() => generatePath(), []);

  const stagePositions = useMemo(
    () =>
      Array.from({ length: 10 }, (_, i) => ({
        ...getPointOnPath(i / 9),
        number: i + 1,
      })),
    []
  );

  // 🔥 posição do carro
  const carT = (currentStageNum - 1) / 9;
  const carPos = getPointOnPath(carT);

  return (
    <div className="w-full bg-gray-100 rounded-xl p-3 border">

      <svg viewBox="0 0 100 100" className="w-full h-[300px]">

        {/* 🛣️ estrada */}
        <path
          d={pathD}
          fill="none"
          stroke="#ccc"
          strokeWidth="6"
          strokeLinecap="round"
        />

        {/* progresso */}
        <path
          d={pathD}
          fill="none"
          stroke="#ef4444"
          strokeWidth="6"
          strokeLinecap="round"
          strokeDasharray={`${(activeStage / 10) * 200} 999`}
        />

        {/* 🟢 pontos */}
        {stagePositions.map((p) => {
          const status = getStageStatus(p.number);
          const isActive = p.number === currentStageNum;

          return (
            <g key={p.number}>

              <circle
                cx={p.x}
                cy={p.y}
                r={isActive ? 3 : 2}
                fill={
                  status === "completed"
                    ? "#ef4444"
                    : status === "in_progress"
                    ? "#f87171"
                    : "#d1d5db"
                }
              />

              <text
                x={p.x}
                y={p.y + 0.5}
                textAnchor="middle"
                fontSize="2"
                fill="white"
                fontWeight="bold"
              >
                {p.number}
              </text>

            </g>
          );
        })}

        {/* 🚗 carro */}
        <g transform={`translate(${carPos.x}, ${carPos.y})`}>
          <rect x="-3" y="-2" width="6" height="4" rx="1" fill="#111" />
          <circle cx="-2" cy="2" r="1" fill="black" />
          <circle cx="2" cy="2" r="1" fill="black" />
        </g>

      </svg>

      {/* título */}
      {stageTitle && (
        <p className="text-center text-sm mt-2 font-semibold text-gray-700">
          {stageTitle}
        </p>
      )}

    </div>
  );
};

export default RoadVisualization;