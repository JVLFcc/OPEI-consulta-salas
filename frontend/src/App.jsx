import { useState } from "react";
import { RiSearchLine } from "react-icons/ri";
import "./App.css";

//! url base da api vai ser chamada a partir de uma var de ambiente pra facilitar a configuração em diferentes ambientes
const API_URL = `${import.meta.env.VITE_API_URL}/api/alunos`;

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
                    <span>
                      CPF: {aluno.cpfMascarado}
                      {aluno.dataNascimento && ` . Nasc.: ${aluno.dataNascimento}`}
                    </span>
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

              <p>
                CPF: {alunoSelecionado.cpfMascarado}
                {alunoSelecionado.dataNascimento && 
                ` . Nasc.: ${alunoSelecionado.dataNascimento}`}
                </p>

                {alunoSelecionado.instituicao && (
                  <p className="student-school">{alunoSelecionado.instituicao}</p>
                )}
            </div>

            <div className="allocations-list">
  {alunoSelecionado.alocacoes?.map((alocacao, index) => (
    <section className="allocation-card" key={index}>
      <div className="allocation-header">
        <span>Modalidade</span>
        <strong>{alocacao.modalidade}</strong>
      </div>

            <div className="room-box">
              <span>Sala</span>
              <strong>{alocacao.sala}</strong>
            </div>

            <div className="location">
              <strong>{alocacao.bloco}</strong>

              {alocacao.andar && (
                <>
                  <span>•</span>
                  <strong>{alocacao.andar}</strong>
                </>
              )}
            </div>

            <div className="details">
              <div>
                <span>Polo</span>
                <strong>{alocacao.polo}</strong>
              </div>

              {alocacao.handle && (
                <div>
                  <span>Handle</span>
                  <strong>{alocacao.handle}</strong>
                </div>
              )}
            </div>
          </section>
        ))}
      </div>
          </section>
        )}
      </section>

      <section className="realization-section">
        <h2>Realização</h2>

        <div className="realization-logos">
          <img
            src="/pet-primary-signature-light 1.svg"
            alt="PET Informática"
            className="realization-logo pet-logo"
          />

          <img
            src="/logo-cin-removebg-preview 1.svg"
            alt="Centro de Informática da UFPE"
            className="realization-logo cin-logo"
          />

          <img
            src="/ufpe-logo-removebg-preview 1.svg"
            alt="Universidade Federal de Pernambuco"
            className="realization-logo ufpe-logo"
          />
        </div>
      </section>

      <footer className="footer">
        <div className="footer-content">
          <div className="footer-column">
            <strong>OPEI</strong>
            <span>Olimpíada Pernambucana de Informática</span>
          </div>

          <div className="footer-column">
            <strong>Contato</strong>
            <span>Av. Jorn. Aníbal Fernandes - Cidade Universitária, Recife - PE, 50740-560</span>
            <span>opei@cin.ufpe.br</span>
          </div>

          <div className="footer-column">
            <strong>Informações</strong>
            <span>Consulta de salas para participantes da OPEI.</span>
            <span>© 2026 OPEI. Todos os direitos reservados.</span>
          </div>
        </div>
      </footer>
    </main>
  );
}

export default App;