import "./css/consultas.css";

function Consultas() {
  return (
    <main className="container">
      <header className="cabecalho">
        <div>
          <h1>Consultas</h1>
          <p>Gerencie as consultas cadastradas.</p>
        </div>

        <button className="btn-novo">
          + Nova consulta
        </button>
      </header>

      <section className="pesquisa">
        <input
          type="text"
          placeholder="Pesquisar consultas..."
        />
        <span>3 consultas</span>
      </section>

      <section className="lista-consultas">
        <article className="card">
          <div className="dados">
            <h2>#1001</h2>
            <p><strong>Paciente:</strong> Ana Oliveira</p>
            <p><strong>Profissional Responsável:</strong> Dr. Carlos César</p>
            <p><strong>Data:</strong> 10/10/2026</p>
            <p><strong>Horário:</strong> 10:10</p>
            <p><strong>Motivo da consulta:</strong> Consulta de rotina</p>
            <p><strong>Observações médicas:</strong> Nenhuma</p>
          </div>

          <div className="acoes">
            <button className="btn-editar">Editar</button>
            <button className="btn-remover">Remover</button>
          </div>
        </article>

        <article className="card">
          <div className="dados">
            <h2 className="codigo">#1002</h2>
            <p><strong>Paciente:</strong> Carlos Santos</p>
            <p><strong>Profissional Responsável:</strong> Dra. Fernanda Lima</p>
            <p><strong>Data:</strong> 15/10/2026</p>
            <p><strong>Horário:</strong> 14:30</p>
            <p><strong>Motivo da consulta:</strong> Avaliação clínica</p>
            <p><strong>Observações médicas:</strong> Retorno em 30 dias</p>
          </div>

          <div className="acoes">
            <button className="btn-editar">Editar</button>
            <button className="btn-remover">Remover</button>
          </div>
        </article>

        <article className="card">
          <div className="dados">
            <h2 className="codigo">#1003</h2>
            <p><strong>Paciente:</strong> Mariana Costa</p>
            <p><strong>Profissional Responsável:</strong> Dr. João Mendes</p>
            <p><strong>Data:</strong> 20/10/2026</p>
            <p><strong>Horário:</strong> 09:00</p>
            <p><strong>Motivo da consulta:</strong> Acompanhamento médico</p>
            <p><strong>Observações médicas:</strong> Trazer exames anteriores</p>
          </div>

          <div className="acoes">
            <button className="btn-editar">Editar</button>
            <button className="btn-remover">Remover</button>
          </div>
        </article>
      </section>
    </main>
  );
}

export default Consultas;