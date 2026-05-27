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
//@Component
public class DataLoader implements CommandLineRunner {

    private final AlunoRepository alunoRepository;

    public DataLoader(AlunoRepository alunoRepository) {
        this.alunoRepository = alunoRepository;
    }

    @Override
    public void run(String... args) {
        alunoRepository.save(new Aluno(
                "Arthur Santos da Silva",
                "arthur@email.com",
                "98765432100",
                "98765432100",
                "10/05/2008",
                "Escola Exemplo Recife",
                "Teórica Médio",
                null,
                "Recife",
                "E233"
        ));

        alunoRepository.save(new Aluno(
                "Arthur Santos da Silva",
                "arthur@email.com",
                "98765432100",
                "98765432100",
                "10/05/2008",
                "Escola Exemplo Recife",
                "Prática Sênior",
                "sabrina_lover_123",
                "Recife",
                "G5"
        ));

        alunoRepository.save(new Aluno(
                "Maria Clara Souza",
                "maria@email.com",
                "11122233344",
                "11122233344",
                "22/09/2009",
                "Escola Exemplo Caruaru",
                "Prática Júnior",
                "filha_de_Kanye",
                "Caruaru",
                "Espaço Maker"
        ));

        alunoRepository.save(new Aluno(
                "Lucas Henrique Alves",
                "lucas@email.com",
                "55566677788",
                "55566677788",
                "03/02/2007",
                "Escola Exemplo Palmares",
                "Teórica Médio",
                null,
                "Palmares",
                "Sala 3"
        ));

        alunoRepository.save(new Aluno(
                "Ana Beatriz Lima",
                "ana@email.com",
                "22233344455",
                "22233344455",
                "15/08/2008",
                "Escola Exemplo Petrolina",
                "Teórica Médio",
                null,
                "Petrolina",
                "Sala 16"
        ));
    }
}
