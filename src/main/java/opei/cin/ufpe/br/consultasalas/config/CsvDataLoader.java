package opei.cin.ufpe.br.consultasalas.config;

import opei.cin.ufpe.br.consultasalas.model.Aluno;
import opei.cin.ufpe.br.consultasalas.repository.AlunoRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

/**
 * carrega os dados reais da planilha de alocações a partir do CSV
 *
 * cada linha do CSV é uma alocação, se um aluno estiver em duas modalidades logicamente ele aparece em duas linhas
 */
@Component
public class CsvDataLoader implements CommandLineRunner{

    private final AlunoRepository alunoRepository;

    public CsvDataLoader(AlunoRepository alunoRepository) {
        this.alunoRepository = alunoRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        alunoRepository.deleteAll();

        ClassPathResource resource = new ClassPathResource("data/alocacoesAlunos.csv");

        try (
             Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);

             CSVParser csvParser = CSVFormat.DEFAULT
                     .builder().setHeader().setSkipHeaderRecord(true)
                     .setTrim(true).build().parse(reader)
        ) {
            for (CSVRecord record : csvParser) {
                Aluno aluno = new Aluno(
                        lerCampo(record, "Nome"),
                        lerCampo(record, "Email"),
                        lerCampo(record, "CPF"),
                        normalizarCpf(lerCampo(record, "CPF")),
                        formatarDataNascimento(lerCampo(record, "Data de Nascimento")),
                        lerCampo(record, "Instituição"),
                        lerCampo(record, "Modalidade"),
                        lerCampo(record, "Handle"),
                        lerCampo(record, "Local"),
                        lerCampo(record, "Sala")
                );

                alunoRepository.save(aluno);
            }
        }

        System.out.println("Alocações carregadas do CSV: " + alunoRepository.count());
    }

    /**
     * lê um campo do CSV com cuidado pq se o campo n existir ou estiver vazio, ele retorna null
     */
    private String lerCampo(CSVRecord record, String nomeColuna) {
        if (!record.isMapped(nomeColuna)) {
            return null;
        }

        String valor = record.get(nomeColuna);

        if (valor == null || valor.isBlank()) {
            return null;
        }

        return valor.trim();
    }

    /**
     * normaiza cpf pra conter apenas números, e tbm pra preservar zeros à esquerda se o cpf vier com mennos de 11 digitos
     *
     * Victor não colocou como texto, aí alguns vieram com 10 só
     */
    private String normalizarCpf(String cpf) {
        if (cpf == null) {
            return null;
        }

        String apenasNumeros = cpf.replaceAll("\\D", "");

        if (apenasNumeros.length() <11) {
            return String.format("%11s", apenasNumeros).replace(' ', '0');
        }

        return apenasNumeros;
    }

    /**
     * converte a data vinda da planilha
     *
     * já que a planilha está como xxxx-xx-xx mas na exibição vamos usar xx/xx/xxxx
     *
     * eu tennho que dar uma surra em Victor
     */
    private String formatarDataNascimento(String data) {
        if (data == null || data.isBlank()) {
            return null;
        }

        if (data.matches("\\d{4}-\\d{2}-\\d{2}")) {
            String[] partes = data.split("-");

            return partes[2] + "/" + partes[1] + "/" + partes[0];
        }

        return data;
    }
}
