import { useState } from 'react'
import heroImg from './assets/hero.png'
import reactLogo from './assets/react.svg'
import viteLogo from './assets/vite.svg'
/* import './App.css' */

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
import Agendamento from "./pages/agendamento";

function App() {
  /* const [count, setCount] = useState(0) */

  return (
    <BrowserRouter>
      <nav>
        <Link to="/">Início</Link>{" | "}
        <Link to="/pacientes">Pacientes</Link>{" | "}
        <Link to="/proSaude">ProSaude</Link>{" | "}
        <Link to="/internacoes">Internacoes</Link>{" | "}
        <Link to="/agendamento">Agendamento</Link>{" | "}
      </nav>

      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/pacientes" element={<Pacientes />} />
        <Route path="/proSaude" element={<ProSaude />} />
        <Route path="/internacoes" element={<Internacoes />} />
        <Route path="/agendamento" element={<Agendamento />} />
      </Routes>
    </BrowserRouter>
  )
}

export default App
