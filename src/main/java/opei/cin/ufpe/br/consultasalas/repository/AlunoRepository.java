package opei.cin.ufpe.br.consultasalas.repository;

import opei.cin.ufpe.br.consultasalas.model.Aluno;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * repositório responsável por acessar dados de alunos no banco
 * o spring data JPA cria automaticamente as consultas com base nos métodos declarados na interface
 */
public interface AlunoRepository extends JpaRepository<Aluno, Long>{

    /**
     * busca até 10 alunos os quais o nome contenha o termo informado, e ignora diferença entre maiúsculas e minúsculas
     * ex.: "arthur" pode encontrar o "Arthur Santos de Lima"
     */
    List<Aluno> findTop10ByNomeCompletoContainingIgnoreCase(String nome);

    /**
     * busca até 10 alunos os quais o cpf contenha o trecho informado
     * cpf normalizado contém apenas números
     */
    List<Aluno> findTop10ByCpfNormalizadoContaining(String cpf);

}
