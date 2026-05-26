import { useState } from "react";
import { RiSearchLine } from "react-icons/ri";
import "./App.css";

const API_URL = "http://localhost:8080/api/alunos";

function App() {
  const [termo, setTermo] = useState("");
  const [sugestoes, setSugestoes] = useState([]);
  const [alunoSelecionado, setAlunoSelecionado] = useState(null);
  const [carregando, setCarregando] = useState(false);
  const [erro, setErro] = useState("");

  async function buscarSugestoes(valor) {
    setTermo(valor);
    setAlunoSelecionado(null);
    setErro("");

    if (valor.trim().length < 3) {
      setSugestoes([]);
      return;
    }

    try {
      setCarregando(true);

      const resposta = await fetch(
        `${API_URL}/search?q=${encodeURIComponent(valor)}`
      );

      if (!resposta.ok) {
        throw new Error("Erro ao buscar alunos.");
      }

      const dados = await resposta.json();
      setSugestoes(dados);
    } catch (error) {
      setErro("Não foi possível buscar os alunos.");
      setSugestoes([]);
    } finally {
      setCarregando(false);
    }
  }

  async function selecionarAluno(id) {
    try {
      setCarregando(true);
      setErro("");

      const resposta = await fetch(`${API_URL}/${id}`);

      if (!resposta.ok) {
        throw new Error("Erro ao buscar detalhes do aluno.");
      }

      const dados = await resposta.json();

      setAlunoSelecionado(dados);
      setSugestoes([]);
      setTermo(dados.nomeCompleto);
    } catch (error) {
      setErro("Não foi possível carregar os dados do aluno.");
    } finally {
      setCarregando(false);
    }
  }

  function limparBusca() {
    setTermo("");
    setSugestoes([]);
    setAlunoSelecionado(null);
    setErro("");
  }

  return (
    <main className="page">
      <section className="content">
        <header className="header">
          <img src="/Logo OPEI Olimpiada.svg" alt="Logo da OPEI" className="logo" />

          <div className="color-bar">
            <span className="bar-o"></span>
            <span className="bar-p"></span>
            <span className="bar-e"></span>
            <span className="bar-i"></span>
          </div>

          <h1>Consulta de salas</h1>

          <p>
            Digite seu nome ou CPF para encontrar o local da sua prova da OPEI.
          </p>
        </header>

        <section className="search-card">
          <label htmlFor="busca">Nome ou CPF</label>

          <div className="input-area">
            <RiSearchLine className="search-icon" />

            <input
              id="busca"
              type="text"
              value={termo}
              onChange={(event) => buscarSugestoes(event.target.value)}
              placeholder="Ex: Victor Luiz ou 123..."
              autoComplete="off"
            />

            {termo && (
              <button type="button" onClick={limparBusca} className="clear-btn">
                ×
              </button>
            )}
          </div>

          <p className="hint">
            O CPF aparecerá apenas mascarado por questões de privacidade.
          </p>

          {carregando && <p className="status">Buscando...</p>}

          {erro && <p className="error">{erro}</p>}

          {sugestoes.length > 0 && (
            <ul className="suggestions">
              {sugestoes.map((aluno) => (
                <li key={aluno.id}>
                  <button type="button" onClick={() => selecionarAluno(aluno.id)}>
                    <strong>{aluno.nomeCompleto}</strong>
                    <span>CPF: {aluno.cpfMascarado}</span>
                  </button>
                </li>
              ))}
            </ul>
          )}
        </section>

        {alunoSelecionado && (
          <section className="result-card">
            <div className="result-top">
              <span>Resultado da consulta</span>
              <button type="button" onClick={limparBusca}>
                Nova busca
              </button>
            </div>

            <div className="student-info">
              <h2>{alunoSelecionado.nomeCompleto}</h2>
              <p>CPF: {alunoSelecionado.cpfMascarado}</p>
            </div>

            <div className="room-box">
              <span>Sala</span>
              <strong>{alunoSelecionado.sala}</strong>
            </div>

            <div className="location">
              <strong>{alunoSelecionado.bloco}</strong>

              {alunoSelecionado.andar && (
                <>
                  <span>•</span>
                  <strong>{alunoSelecionado.andar}</strong>
                </>
              )}
            </div>

            <div className="details">
              <div>
                <span>Modalidade</span>
                <strong>{alunoSelecionado.modalidade}</strong>
              </div>

              <div>
                <span>Polo</span>
                <strong>{alunoSelecionado.polo}</strong>
              </div>

              {alunoSelecionado.handle && (
                <div>
                  <span>Handle</span>
                  <strong>{alunoSelecionado.handle}</strong>
                </div>
              )}
            </div>
          </section>
        )}
      </section>

      <footer className="footer">
        <strong>OPEI</strong>
        <span>Olimpíada Pernambucana de Informática</span>
      </footer>
    </main>
  );
}

export default App;