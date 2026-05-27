package opei.cin.ufpe.br.consultasalas.repository;

import opei.cin.ufpe.br.consultasalas.model.Aluno;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * repositório responsável por consultar os alunos/alocacoes no banco
 *
 * cada linha da planilha representa uma alocação, então um aluno pode aparecer mais de uma vez
 */
public interface AlunoRepository extends JpaRepository<Aluno, Long>{

    /**
     * busca alunos pelo nome e é isso
     * usa top20 agora pra n quebrar, e pq depois o service vai remover duplicatas por CPF
     */
    List<Aluno> findTop20ByNomeCompletoContainingIgnoreCase(String nome);

    /**
     * busca alunos pelo cpf normalizado e é isso
     * cpf normalizado contém apenas números!
     */
    List<Aluno> findTop20ByCpfNormalizadoContaining(String cpf);

    /**
     * busca todas alocações de um msm aluno pelo cpf
     *
     * esse método é o mais importante pra considerar os casos de que o aluno está em mais de uma modalidade
     */
    List<Aluno> findByCpfNormalizado(String cpfNormalizado);

}
