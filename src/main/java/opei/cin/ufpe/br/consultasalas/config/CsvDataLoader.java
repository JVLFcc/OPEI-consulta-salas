package opei.cin.ufpe.br.consultasalas.config;

import opei.cin.ufpe.br.consultasalas.dto.ResultadoImportacaoResponse;
import opei.cin.ufpe.br.consultasalas.service.CsvImportService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

/**
 * carregador opcional de CSV pra o inicio da aplicação
 *
 * em produção vai ficar desativada por default, o uso principal será pelo endpoint admin de upload
 */
@Component
public class CsvDataLoader implements CommandLineRunner{

    private final CsvImportService csvImportService;

    @Value("${app.import.csv.enabled:false}")
    private boolean importCsvEnabled;

    public CsvDataLoader(CsvImportService csvImportService) {
        this.csvImportService = csvImportService;
    }

    @Override
    public void run(String... args) throws Exception {

        if (!importCsvEnabled) {
            System.out.println("Importação automática de CSV desativada.");
            return;
        }

        ClassPathResource resource = new ClassPathResource("data/alocacoesAlunos.csv");

        if (!resource.exists()) {
            System.out.println("Arquivo CSV não encontrado. Importação ignorada.");
            return;
        }

        ResultadoImportacaoResponse resultado = csvImportService.importar(resource.getInputStream());

        System.out.println("Alocações carregadas do CSV: " + resultado.registrosSalvos());
    }

}
