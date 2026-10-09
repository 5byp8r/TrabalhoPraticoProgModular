
import "./css/ProSaude.css";

export default function ProSaude() {
  return (
    <main className="container-profissionais">
        <header className="cabecalho-profissionais">
            <div>
            <h1>Profissionais de Saúde</h1>
            <p>Gerencie os profissionais cadastrados no sistema.</p>
            </div>

            <button className="btn-novo-profissional">
            + Novo profissional
            </button>
        </header>

        <section className="pesquisa-profissionais">
            <input
            type="text"
            placeholder="Pesquisar por nome, registro ou especialidade..."
            />
            <span>3 profissionais</span>
        </section>

        <section className="lista-profissionais">
            <article className="card-profissional">
            <div className="avatar-profissional">M</div>

            <div className="dados-profissional">
                <h2>Mariana Oliveira</h2>
                <p><strong>Registro:</strong> CRM-MG 123456</p>
                <p><strong>Especialidade:</strong> Cardiologia</p>
                <p><strong>Telefone:</strong> (31) 99999-1111</p>
                <p><strong>E-mail:</strong> mariana@email.com</p>
            </div>

            <div className="acoes-profissional">
                <button className="btn-editar-profissional">Editar</button>
                <button className="btn-remover-profissional">Remover</button>
            </div>
            </article>

            <article className="card-profissional">
            <div className="avatar-profissional">R</div>

            <div className="dados-profissional">
                <h2>Rafael Santos</h2>
                <p><strong>Registro:</strong> CRM-MG 234567</p>
                <p><strong>Especialidade:</strong> Pediatria</p>
                <p><strong>Telefone:</strong> (31) 98888-2222</p>
                <p><strong>E-mail:</strong> rafael@email.com</p>
            </div>

            <div className="acoes-profissional">
                <button className="btn-editar-profissional">Editar</button>
                <button className="btn-remover-profissional">Remover</button>
            </div>
            </article>

            <article className="card-profissional">
            <div className="avatar-profissional">A</div>

            <div className="dados-profissional">
                <h2>Ana Costa</h2>
                <p><strong>Registro:</strong> COREN-MG 345678</p>
                <p><strong>Especialidade:</strong> Enfermagem</p>
                <p><strong>Telefone:</strong> (31) 97777-3333</p>
                <p><strong>E-mail:</strong> ana@email.com</p>
            </div>

            <div className="acoes-profissional">
                <button className="btn-editar-profissional">Editar</button>
                <button className="btn-remover-profissional">Remover</button>
            </div>
            </article>
        </section>
    </main>
  );
}