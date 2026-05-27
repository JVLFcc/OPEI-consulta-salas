package opei.cin.ufpe.br.consultasalas.controller;

import opei.cin.ufpe.br.consultasalas.dto.AlunoResponseDetalhada;
import opei.cin.ufpe.br.consultasalas.dto.AlunoSugestaoResponse;
import opei.cin.ufpe.br.consultasalas.service.AlunoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * controller responsável por expor os endpoints de consulta dos alunos
 *
 * essa é a camada que recebe as requisições HTTP vindas do frontend (acho que vou fazer em react)
 * repassa para o AlunoService
 */
@RestController
@RequestMapping("/api/alunos")
@CrossOrigin(origins = {
        "http://localhost:5173",
        "https://opei-consulta-salas.vercel.app"
})
public class AlunoController {

    private final AlunoService alunoService;

    public AlunoController(AlunoService alunoService) {
        this.alunoService = alunoService;
    }

    // endpoint do autocomplete
    @GetMapping("/search")
    public List<AlunoSugestaoResponse> buscarSugestoes(@RequestParam String q) {
        return alunoService.buscarSugestoes(q);
    }

    // endpoint dos detalhes de um aluno específico para o card final
    @GetMapping("/{id}")
    public AlunoResponseDetalhada buscarDetalhes(@PathVariable Long id) {
        return alunoService.buscarDetalhes(id);
    }
}
