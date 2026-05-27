package opei.cin.ufpe.br.consultasalas.controller;

import opei.cin.ufpe.br.consultasalas.dto.ResultadoImportacaoResponse;
import opei.cin.ufpe.br.consultasalas.service.CsvImportService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * controller administrativo da aplicação, não é usada pelos alunos rs
 *
 * serve pra manter a organização ao importar a planilha real
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final CsvImportService csvImportService;

    @Value("${app.admin.token}")
    private String adminToken;

    public AdminController(CsvImportService csvImportService) {
        this.csvImportService = csvImportService;
    }

    /**
     * importa arquivo csv por upload
     *
     * tem que se ligar no header (esqueci em portugues) = X-Admin-Token: token_que_configurei
     *
     * o corpo é o multipart/from-data no campo file
     */
    @PostMapping(
            value = "/importar-csv",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> importarCsv(
            @RequestHeader("X-Admin-Token") String token,
            @RequestParam("file") MultipartFile file
    ) {

        if (!adminToken.trim().equals(token)) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED) // lembrei da minha época de técnico ;-;
                    .body("Token administrativo inválido.");
        }

        if (file.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body("Nenhum arquivo foi enviado.");
        }

        try {
            ResultadoImportacaoResponse resultado = csvImportService.importar(file.getInputStream());

            return ResponseEntity.ok(resultado);
        } catch (Exception exception) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao importar CSV: " + exception.getMessage());
        }
    }

}
