import "./css/pacientes.css";

export default function Pacientes() {
  return (
    <main className="container">
        <header className="cabecalho">
        <div>
            <h1>Pacientes</h1>
            <p>Gerencie os pacientes cadastrados.</p>
        </div>

        <button className="btn-novo">
            + Novo paciente
        </button>
        </header>

        <section className="pesquisa">
        <input
            type="text"
            placeholder="Pesquisar paciente por nome ou CPF..."
        />
        <span>3 pacientes</span>
        </section>

        <section className="lista-pacientes">

        <article className="card">
            <div className="avatar">A</div>

            <div className="dados">
            <h2>Ana Oliveira</h2>
            <p><strong>CPF:</strong> 123.456.789-00</p>
            <p><strong>Data de nascimento:</strong> 15/03/1995</p>
            <p><strong>Telefone:</strong> (31) 99999-1111</p>
            <p><strong>Endereço:</strong> Rua das Flores, 123 - Belo Horizonte/MG</p>
            <p><strong>E-mail:</strong> ana@email.com</p>
            </div>

            <div className="acoes">
            <button className="btn-editar">Editar</button>
            <button className="btn-remover">Remover</button>
            </div>
        </article>

        <article className="card">
            <div className="avatar">C</div>

            <div className="dados">
            <h2>Carlos Santos</h2>
            <p><strong>CPF:</strong> 987.654.321-00</p>
            <p><strong>Data de nascimento:</strong> 22/08/1988</p>
            <p><strong>Telefone:</strong> (31) 98888-2222</p>
            <p><strong>Endereço:</strong> Av. Brasil, 450 - Contagem/MG</p>
            <p><strong>E-mail:</strong> carlos@email.com</p>
            </div>

            <div className="acoes">
            <button className="btn-editar">Editar</button>
            <button className="btn-remover">Remover</button>
            </div>
        </article>

        <article className="card">
            <div className="avatar">M</div>

            <div className="dados">
            <h2>Mariana Costa</h2>
            <p><strong>CPF:</strong> 456.789.123-00</p>
            <p><strong>Data de nascimento:</strong> 10/12/2001</p>
            <p><strong>Telefone:</strong> (31) 97777-3333</p>
            <p><strong>Endereço:</strong> Rua Central, 75 - Betim/MG</p>
            <p><strong>E-mail:</strong> mariana@email.com</p>
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