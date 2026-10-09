import quartosStylesheet from "./css/quartos.css?url";
import usePageStyles from "../hooks/usePageStyles";

// exemplo dos pacientes aí
const quartos = [
  { numero: 101, status: "Disponível", ala: "Clínica geral", leitos: 2, pacientes: [] },
  { numero: 102, status: "Ocupado", ala: "Clínica geral", leitos: 2, pacientes: ["Maria Souza", "João Pereira"] },
  { numero: 103, status: "Ocupado", ala: "Pediatria", leitos: 2, pacientes: ["Mariana Costa"] },
  { numero: 104, status: "Indisponível", ala: "UTI", leitos: 1, pacientes: [], motivo: "Em manutenção" },
];
const pacientesCadastrados = ["Ana Oliveira", "Carlos Santos", "Gabriel Assis", "Davi Sollar"];

// cor indicando a disponibilidade dos quartos
const corDoStatus = {
  "Disponível": "verde",
  "Ocupado": "azul",
  "Indisponível": "laranja",
};

export default function Quartos() {
  usePageStyles(quartosStylesheet);

  return (
    <>
      

      <main className="container">
        <div className="cabecalho">
          <div>
            <h1>Quartos</h1>
            <p className="subtitulo">Gerencie os quartos e os pacientes de cada um.</p>
          </div>
          <a href="#form-quarto" className="btn-principal">+ Novo quarto</a>
        </div>

        <div className="busca">
          <input type="text" placeholder="Pesquisar quarto por número ou paciente..." />
          <select defaultValue="">
            <option value="">Todos os status</option>
            <option>Disponível</option>
            <option>Ocupado</option>
            <option>Indisponível</option>
          </select>
          <span className="contador">{quartos.length} quartos</span>
        </div>

        <div className="lista">
          {quartos.map((q) => (
            <div className="card" key={q.numero}>
              <div className="card-topo">
                <div className="avatar">{q.numero}</div>

                <div className="info">
                  <h3>Quarto {q.numero}</h3>
                  <p>
                    <b>Status:</b>{" "}
                    <span className={`status ${corDoStatus[q.status]}`}>{q.status}</span>
                  </p>
                  <p><b>Ala:</b> {q.ala}</p>
                  {q.motivo ? (
                    <p><b>Motivo:</b> {q.motivo}</p>
                  ) : (
                    <p><b>Leitos:</b> {q.pacientes.length}/{q.leitos} ocupados</p>
                  )}
                </div>

                <div className="botoes">
                  <a href="#form-quarto" className="btn-editar">Editar</a>
                  <a href="#" className="btn-remover">Remover</a>
                </div>
              </div>

              <div className="pacientes">
                {q.status === "Indisponível" ? (
                  <p className="vazio">Quarto indisponível para novos pacientes.</p>
                ) : (
                  <>
                    {q.pacientes.length === 0 ? (
                      <p className="vazio">Nenhum paciente neste quarto.</p>
                    ) : (
                      <ul>
                        {q.pacientes.map((nome) => (
                          <li key={nome}>
                            {nome} <a href="#">Retirar</a>
                          </li>
                        ))}
                      </ul>
                    )}

                    {q.pacientes.length < q.leitos && (
                      <form>
                        <select defaultValue="">
                          <option value="">Selecione um paciente</option>
                          {pacientesCadastrados.map((nome) => (
                            <option key={nome}>{nome}</option>
                          ))}
                        </select>
                        <button type="button" className="btn-editar">Adicionar</button>
                      </form>
                    )}
                  </>
                )}
              </div>
            </div>
          ))}
        </div>

        <div className="card form" id="form-quarto">
          <h2>Novo quarto</h2>
          <form>
            <label>
              Número
              <input type="text" placeholder="Ex.: 105" />
            </label>
            <label>
              Ala
              <select defaultValue="Clínica geral">
                <option>Clínica geral</option>
                <option>Pediatria</option>
                <option>UTI</option>
              </select>
            </label>
            <label>
              Quantidade de leitos
              <input type="number" min="1" defaultValue="2" />
            </label>
            <label>
              Status
              <select defaultValue="Disponível">
                <option>Disponível</option>
                <option>Indisponível (manutenção)</option>
                <option>Indisponível (limpeza)</option>
              </select>
            </label>
            <button type="button" className="btn-principal">Salvar quarto</button>
          </form>
        </div>
      </main>
    </>
    );
}