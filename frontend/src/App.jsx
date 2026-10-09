import { useState } from 'react'
import heroImg from './assets/hero.png'
import reactLogo from './assets/react.svg'
import viteLogo from './assets/vite.svg'
import './App.css'

import {
  BrowserRouter,
  Routes,
  Route,
  Link
} from "react-router-dom";

import Home from "./pages/home";
import Pacientes from "./pages/pacientes";
import ProSaude from "./pages/proSaude";
import Internacoes from "./pages/internacoes";
import Consultas from "./pages/consultas";
import Quartos from "./pages/quartos";
import Login from "./pages/login";

function App() {
  /* const [count, setCount] = useState(0) */

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

      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/pacientes" element={<Pacientes />} />
        <Route path="/proSaude" element={<ProSaude />} />
        <Route path="/internacoes" element={<Internacoes />} />
        <Route path="/consultas" element={<Consultas />} />
        <Route path="/quartos" element={<Quartos />} />
        <Route path="/login" element={<Login />} />
      </Routes>
    </BrowserRouter>
  )
}

export default App
