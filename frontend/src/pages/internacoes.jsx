import internacoesStylesheet from "./css/internacoes.css?url";
import usePageStyles from "../hooks/usePageStyles";

const internacoes = [
  {
    id: "#2001",
    paciente: "Ana Oliveira",
    profissional: "Dr. Carlos César",
    entrada: "05/10/2026",
    altaPrevista: "12/10/2026",
    altaEfetiva: "Ainda internada",
    observacoes: "Acompanhamento pós-operatório.",
    quarto: "Quarto 101",
  },
  {
    id: "#2002",
    paciente: "Carlos Santos",
    profissional: "Dra. Fernanda Lima",
    entrada: "01/10/2026",
    altaPrevista: "08/10/2026",
    altaEfetiva: "08/10/2026",
    observacoes: "Alta realizada sem intercorrências.",
    quarto: "Quarto 102",
  },
  {
    id: "#2003",
    paciente: "Mariana Costa",
    profissional: "Dr. João Mendes",
    entrada: "07/10/2026",
    altaPrevista: "15/10/2026",
    altaEfetiva: "Ainda internada",
    observacoes: "Em observação clínica.",
    quarto: "Quarto 103",
  },
];

function Internacoes() {
  usePageStyles(internacoesStylesheet);

  return (
    <main className="container">
      <header className="cabecalho">
        <div>
          <h1>Internações</h1>
          <p>Gerencie as internações e acompanhe as altas dos pacientes.</p>
        </div>

        <button type="button" className="btn-novo">
          + Nova internação
        </button>
      </header>

      <section className="pesquisa">
        <input
          type="text"
          placeholder="Pesquisar por paciente ou quarto..."
        />
        <span>{internacoes.length} internações</span>
      </section>

      <section className="lista-internacoes">
        {internacoes.map((internacao) => (
          <article className="card" key={internacao.id}>
            <div className="dados">
              <h2>{internacao.id}</h2>
              <p><strong>Paciente:</strong> {internacao.paciente}</p>
              <p><strong>Profissional de saúde:</strong> {internacao.profissional}</p>
              <p><strong>Data de entrada:</strong> {internacao.entrada}</p>
              <p><strong>Data prevista de alta:</strong> {internacao.altaPrevista}</p>
              <p><strong>Data efetiva de alta:</strong> {internacao.altaEfetiva}</p>
              <p><strong>Observações:</strong> {internacao.observacoes}</p>
              <p><strong>Quarto associado:</strong> {internacao.quarto}</p>
            </div>

            <div className="acoes">
              <button type="button" className="btn-editar">Editar</button>
              <button type="button" className="btn-remover">Remover</button>
            </div>
          </article>
        ))}
      </section>
    </main>
  );
}

export default Internacoes;