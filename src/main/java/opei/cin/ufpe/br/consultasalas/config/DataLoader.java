package opei.cin.ufpe.br.consultasalas.config;

import opei.cin.ufpe.br.consultasalas.model.Aluno;
import opei.cin.ufpe.br.consultasalas.repository.AlunoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * classe pra inserir dados de testes automaticamente quando a aplicação inicia
 *
 * fiz apenas pra testar durante o desenvolvimento pra ver se tá tudo ok e tals rsrs
 */
@Component
public class DataLoader implements CommandLineRunner {

    private final AlunoRepository alunoRepository;

    public DataLoader(AlunoRepository alunoRepository) {
        this.alunoRepository = alunoRepository;
    }

    @Override
    public void run(String... args) {
        alunoRepository.save(new Aluno(
                "Arthur Santos de Lima",
                "arthur.lima@email.com",
                "12345678900",
                "12345678900",
                "Teórica Médio",
                null,
                "Recife",
                "E112"
        ));

        alunoRepository.save(new Aluno(
                "Arthur Santos da Silva",
                "arthur.silva@email.com",
                "98765432100",
                "98765432100",
                "Teórica Médio",
                null,
                "Recife",
                "E233"
        ));

        alunoRepository.save(new Aluno(
                "Maria Clara Souza",
                "maria@email.com",
                "11122233344",
                "11122233344",
                "Prática Júnior",
                "maria_clara",
                "Recife",
                "GRAD 2"
        ));

        alunoRepository.save(new Aluno(
                "Lucas Henrique Alves",
                "lucas@email.com",
                "55566677788",
                "55566677788",
                "Prática Sênior",
                "lucas_h",
                "Recife",
                "Espaço Maker"
        ));
    }
}
