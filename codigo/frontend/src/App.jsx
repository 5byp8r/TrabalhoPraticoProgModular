import { lazy, Suspense } from 'react'
import './App.css'

import {
  BrowserRouter,
  Routes,
  Route,
  Link
} from "react-router-dom";

const Home = lazy(() => import("./pages/home"));
const Pacientes = lazy(() => import("./pages/pacientes"));
const ProSaude = lazy(() => import("./pages/proSaude"));
const Internacoes = lazy(() => import("./pages/internacoes"));
const Consultas = lazy(() => import("./pages/consultas"));
const Quartos = lazy(() => import("./pages/quartos"));
const Login = lazy(() => import("./pages/login"));

function App() {
  return (
    <BrowserRouter>
      <nav>
        <Link to="/">Início</Link>
        <Link to="/pacientes">Pacientes</Link>
        <Link to="/proSaude">ProSaude</Link>
        <Link to="/internacoes">Internações</Link>
        <Link to="/consultas">Consultas</Link>
        <Link to="/quartos">Quartos</Link>
        <Link to="/login">Login</Link>
      </nav>

      <Suspense fallback={<p>Carregando página...</p>}>
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/pacientes" element={<Pacientes />} />
          <Route path="/proSaude" element={<ProSaude />} />
          <Route path="/internacoes" element={<Internacoes />} />
          <Route path="/consultas" element={<Consultas />} />
          <Route path="/quartos" element={<Quartos />} />
          <Route path="/login" element={<Login />} />
        </Routes>
      </Suspense>
    </BrowserRouter>
  )
}

export default App
