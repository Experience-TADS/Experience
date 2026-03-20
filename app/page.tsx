"use client";

import { useState } from "react";
import Sidebar from "./componentes/SideBar";
import Header from "./componentes/Header";
import CardCar from "./componentes/Card";
import NewsModal from "./componentes/NewsModal";

type News = {
  image: string[];
  title: string;
  summary: string;
  content: string;
};

export default function Home() {
  const [selectedNews, setSelectedNews] = useState<News | null>(null);

  const newsList: News[] = [
    {
      image: ["/toyota_yaris.jpg"],
      title: "Toyota lança novo carro híbrido Yaris Cross 2026.",
      summary:
        "O Toyota Yaris Cross 2026 — o novo SUV compacto da marca com versões flex e híbridas — já começou a ser lançado no Brasil.",
      content:
        "O modelo está sendo apresentado em diversas cidades brasileiras, marcando a chegada oficial às concessionárias em 2026. Com design moderno, tecnologia embarcada e foco em eficiência energética, o Yaris Cross reforça a estratégia da Toyota em ampliar sua linha de veículos eletrificados no Brasil.",
    },
    {
      image: ["/tech1.jpeg"],
      title: "TechMob 4.0 completa um ano formando jovens para a Indústria 4.0",
      summary:
        "Programa completa 12 meses unindo formação técnica e prática industrial.",
      content:
        "O TechMob 4.0 vai além de um curso convencional: ele prepara os jovens para os desafios da Indústria 4.0, com foco em inovação, empregabilidade e desenvolvimento de soluções tecnológicas que atendam às necessidades do mercado atual.",
    },
    {
      image: ["/10anos.jpeg"],
      title: "Toyota amplia confiança no cliente com Garantia de até 10 anos",
      summary:
        "A Toyota reforça seu compromisso com qualidade e durabilidade ao oferecer garantia de até 10 anos para seus veículos no Brasil.",
      content:
        "Todo veículo zero km da marca já sai de fábrica com 5 anos de garantia. Após esse período, o cliente pode estender a cobertura por mais 5 anos adicionais, totalizando até 10 anos de proteção.",
    },
    {
      image: ["/novaFa.jpeg"],
      title: "Toyota anuncia nova fábrica no Brasil em Sorocaba",
      summary:
        "Expansão em Sorocaba marca nova fase de crescimento industrial da montadora.",
      content:
        "A Toyota confirmou a construção de uma nova fábrica no Brasil, como parte de um plano de investimentos de R$ 11 bilhões até 2030 para expandir a produção e fortalecer a presença no país.",
    },
    {
      image: ["/Carro-dos-Sonhos.jpg"],
      title: "Toyota abre inscrições para o concurso Carro dos Sonhos 2026",
      summary:
        "Iniciativa estimula a criatividade de crianças e adolescentes por meio de desenhos.",
      content:
        "O concurso convida jovens de 6 a 16 anos a desenharem o carro dos seus sonhos, incentivando criatividade e interesse pela indústria automotiva.",
    },
    {
      image: ["/ararinha.jpeg"],
      title: "Fundação Toyota destaca o Projeto Arara Azul",
      summary:
        "Projeto protege a arara-azul e fortalece o equilíbrio do Pantanal.",
      content:
        "Idealizado pela bióloga Neiva Guedes em 1989, o projeto promove a conservação da arara-azul e da biodiversidade do Pantanal com apoio da Toyota.",
    },
  ];

  return (
    <div className="flex bg-gray-100 min-h-screen">

      <Sidebar />

      <div className="flex-1 p-5 md:p-10">

        <Header />

        {/* GRID DE NOTÍCIAS */}
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">

          {newsList.map((news, index) => (
            <CardCar
              key={index}
              image={news.image}
              title={news.title}
              summary={news.summary}
              onClick={() => setSelectedNews(news)}
            />
          ))}

        </div>

      </div>

      {/* MODAL */}
      {selectedNews && (
        <NewsModal
          news={selectedNews}
          onClose={() => setSelectedNews(null)}
        />
      )}

    </div>
  );
}