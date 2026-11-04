import { useState } from "react";
import loginStylesheet from "./css/login.css?url";
import usePageStyles from "../hooks/usePageStyles";

function Login() {
  usePageStyles(loginStylesheet);

  const [email, setEmail] = useState("");
  const [senha, setSenha] = useState("");

  function handleSubmit(event) {
    event.preventDefault();
  }

  return (
    <main className="login-page">
      <section className="login-card">
        <div className="login-header">
          <h1>Entrar</h1>
          <p>Acesse o sistema hospitalar.</p>
        </div>

        <form className="login-form" onSubmit={handleSubmit}>
          <label htmlFor="email">Email</label>
          <input
            id="email"
            name="email"
            type="email"
            placeholder="seu@email.com"
            value={email}
            onChange={(event) => setEmail(event.target.value)}
            required
          />

          <label htmlFor="senha">Senha</label>
          <input
            id="senha"
            name="senha"
            type="password"
            placeholder="Digite sua senha"
            value={senha}
            onChange={(event) => setSenha(event.target.value)}
            required
          />

          <button type="submit" className="btn-login">
            Entrar
          </button>
        </form>
      </section>
    </main>
  );
}

export default Login;