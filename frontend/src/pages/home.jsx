import homeStylesheet from "./css/home.css?url";
import imageHome from "../assets/hospital.jpg";
import usePageStyles from "../hooks/usePageStyles";

const atalhos = [
  { titulo: "Pacientes", texto: "Cadastre e gerencie os pacientes." },
  { titulo: "Quartos", texto: "Veja os quartos disponíveis e ocupados." },
  { titulo: "Internações", texto: "Acompanhe as internações em andamento." },
  { titulo: "Agendamento", texto: "Organize consultas e procedimentos." },
];

export default function Home() {
  usePageStyles(homeStylesheet);

  return (
    <main className="container">
      <section className="home-hero">
        <div className="home-texto">
          <h1>Bem-vindo ao sistema hospitalar</h1>
          <p>
            Gerencie pacientes, internações, quartos e agendamentos em um só lugar.
          </p>
          <button type="button" className="btn-novo">Ver pacientes</button>
        </div>

        <div className="home-imagem">
          {<img src={imageHome} alt="Imagem garimpada no pinterest" />}
        </div>
      </section>

      <h2 className="home-subtitulo">Acesso rápido</h2>
      <div className="home-atalhos">
        {atalhos.map((a) => (
          <div className="home-atalho" key={a.titulo}>
            <h3>{a.titulo}</h3>
            <p>{a.texto}</p>
          </div>
        ))}
      </div>
    </main>
  );
}