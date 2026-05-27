package opei.cin.ufpe.br.consultasalas.service;

import opei.cin.ufpe.br.consultasalas.dto.ResultadoImportacaoResponse;
import opei.cin.ufpe.br.consultasalas.model.Aluno;
import opei.cin.ufpe.br.consultasalas.repository.AlunoRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
/**
 *  serviço reponsavel por importar a planilha de alocações em formato CSV
 *
 *  cada linha do CSV representa uma alocação, então se um aluno estiver inscrito em duas modalidades logicamente ele aparece em duas linhas com o memsmo cpf
 */
@Service
public class CsvImportService {

    private final AlunoRepository alunoRepository;

    public CsvImportService(AlunoRepository alunoRepository) {
        this.alunoRepository = alunoRepository;
    }

    /**
     * importa o csv recebido e substitui os dados atuais do banco
     *
     * essa função vai ser usada pelo endpoint admin, e pelo carregamento automático opcional no inicio da aplicação. pelo menos até agora esses são os usos
     */
    public ResultadoImportacaoResponse importar(InputStream inputStream) throws Exception {

        List<Aluno> alunos = new ArrayList<>();

        // tive que fazer isso pq vi alguns alunos repetidos na planilha, e essa vai ser a forma para tratar de erros de duplicação
        java.util.Set<String> chavesJaImportadas = new java.util.HashSet<>();

        int registrosLidos = 0;
        int duplicatasIgnoradas = 0;

        try (
                Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                CSVParser csvParser = CSVFormat.DEFAULT
                        .builder().setHeader().setSkipHeaderRecord(true)
                        .setTrim(true).build().parse(reader)
        ) {
            for (CSVRecord record : csvParser) {
                registrosLidos++;

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

                String chaveAlocacao = gerarChaveAlocacao(aluno);

                if (chavesJaImportadas.contains(chaveAlocacao)) {
                    duplicatasIgnoradas++;

                    continue;
                }

                chavesJaImportadas.add(chaveAlocacao);
                alunos.add(aluno);
            }
        }
        alunoRepository.deleteAll();
        alunoRepository.saveAll(alunos);

        return new ResultadoImportacaoResponse(
                registrosLidos,
                alunos.size(),
                "Importação concluída com sucesso! Duplicatas ignoradas: " + duplicatasIgnoradas + "."
        );
    }

    /**
     * lê o campo do csv com cuidado, e se a coluna n estiver vazia retorna null
     *
     * ? depois tenho que ver se essa é a melhor maneira
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
     * normalizar cpf pra conter apenas números
     *
     * se o cpf tiver menos de 11 digitos ent Victor salvou como número (merece uma pisa)
     * aí vai completar com zero à esquerda, ajuda pra n ter que lidar com erro de tratamento da planilha e nem de Victor
     */
    private String normalizarCpf(String cpf) {
        if (cpf == null) {
            return null;
        }

        String apenasNumeros = cpf.replaceAll("\\D", "");

        if (apenasNumeros.length() < 11) {
            return String.format("%11s", apenasNumeros).replace(' ', '0');
        }

        return apenasNumeros;
    }

    /**
     * padroniza a data de nascimento pra exibir no front bonitinho cuti cuti
     *
     * ex.: 2010-10-19 -> 19/10/2010
     * se a data estiver no formato correto (queria), é deixada do jeito que tá
     */
    private String formatarDataNascimento(String data) {
        if (data == null || data.isBlank()) {
            return null;
        }

        String dataLimpa = data.trim();

        if (dataLimpa.matches("\\d{4}-\\d{2}-\\d{2}")) {
            String[] partes = dataLimpa.split("-");

            return partes[2] + "/" + partes[1] + "/" + partes[0];
        }

        return dataLimpa;
    }

    /**
     * gera  chave única para identificar uma alocação, pq de vez em quando surgem erros (e eu prefiro tratar aqui doq na planilha)
     *
     * portannto, se duas linhas tiverem o mesmo cpf, modalidade, polo, sala e handle, é connsiderada uma duplicata
     */
    private String gerarChaveAlocacao(Aluno aluno) {
        return normalizarParaChave(aluno.getCpfNormalizado()) + "|" +
                normalizarParaChave(aluno.getModalidade()) + "|" +
                normalizarParaChave(aluno.getPolo()) + "|" +
                normalizarParaChave(aluno.getSala()) + "|" +
                normalizarParaChave(aluno.getHandle());
    }

    /**
     * normaliza valores pra comparação interna
     *
     * isso vai evitar que diferenças pequenas, como espaço extra ou letra maiúscula e tals, faça com que o sistema pese que duas linhas iguais são diferentes rs
     */
    private String normalizarParaChave(String valor) {
        if (valor == null) {
            return "";
        }

        return valor.trim().toUpperCase();
    }
}
