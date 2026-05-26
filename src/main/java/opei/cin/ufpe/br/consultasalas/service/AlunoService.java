package opei.cin.ufpe.br.consultasalas.service;

import opei.cin.ufpe.br.consultasalas.dto.AlunoResponseDetalhada;
import opei.cin.ufpe.br.consultasalas.dto.AlunoSugestaoResponse;
import opei.cin.ufpe.br.consultasalas.dto.LocalizacaoDTO;
import opei.cin.ufpe.br.consultasalas.model.Aluno;
import opei.cin.ufpe.br.consultasalas.repository.AlunoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * serviço principal da aplicação onde contém as regras relacionadas à busca dos alunos, exibição das sugestões e montagem do card final com detalhes
 */
@Service
public class AlunoService {
    private final AlunoRepository alunoRepository;
    private final LocalizacaoService localizacaoService;

    // o spring injeta automaticamente as dependências necessárias pelo construtor
    public AlunoService(AlunoRepository alunoRepository, LocalizacaoService localizacaoService){
        this.alunoRepository = alunoRepository;
        this.localizacaoService = localizacaoService;
    }

    /**
     * busca sugestões com base num termo digitado
     *
     * a busca começa com pelo menos 3 caracteres pra evitar gargalo e requisições desnecessárias
     * se o termo possui apenas numeros, o sistema busca por cpf, caso o contrário busca por nome
     */
    public List<AlunoSugestaoResponse> buscarSugestoes(String termo) {
        if (termo == null || termo.trim().length() < 3){
            return List.of();
        }

        String termoLimpo = termo.trim();
        String apenasNumeros = termoLimpo.replaceAll("\\D", "");

        List<Aluno> alunos;

        if (termoLimpo.matches("\\d+")) {
            alunos = alunoRepository.findTop10ByCpfNormalizadoContaining(apenasNumeros);
        } else {
            alunos = alunoRepository.findTop10ByNomeCompletoContainingIgnoreCase(termoLimpo);
        }

        /**
         * a resposta da sugestão não traz todos os dados do aluno
         * ela retorna apenas o necessário para o aluno se identificar: Id, nome completo e cpf mascarado
         */
        return alunos.stream().map(aluno -> new AlunoSugestaoResponse(aluno.getId(), aluno.getNomeCompleto(), mascararCpf(aluno.getCpfNormalizado()))).toList();
    }

    /**
     * já aqui nós buscamos os detalhes completos de um aluno específico
     * esse método vai ser chamado depois de um aluno clicar em uma sugestão, pq só assim o sistema vai montar o card final
     * card final: sala, bloco, andar, modalidade, polo e outras informações de apoio
     */
    public AlunoResponseDetalhada buscarDetalhes(Long id){
        Aluno aluno = alunoRepository.findById(id).orElseThrow(() -> new RuntimeException("Aluno não encontrado"));

        LocalizacaoDTO localizacao = localizacaoService.resolverLocalizacao(aluno.getSala());

        return new AlunoResponseDetalhada(
                aluno.getId(),
                aluno.getNomeCompleto(),
                mascararCpf(aluno.getCpfNormalizado()),
                aluno.getModalidade(),
                aluno.getPolo(),
                aluno.getHandle(),
                aluno.getSala(),
                localizacao.bloco(),
                localizacao.andar()
        );
    }

    /**
     * mascara o cpf antes de enviá-lo ao front
     *
     * ex.: 12345678900 --> 123******00 e depois retorna no formato de cpf porém mascarado (123.***.***-00)
     * isso ajuda a diferenciar homônimos sem expor o cpf completo (LGPD ajudando aó olha só)
     */
    private String mascararCpf(String cpf) {
        if (cpf == null || cpf.length() < 5) {
            return "***";
        }

        String cpfLimpo = cpf.replaceAll("\\D", "");

        if (cpfLimpo.length() < 5) {
            return "***";
        }

        String tresPrimeiros = cpfLimpo.substring(0, 3);
        String doisUltimos = cpfLimpo.substring(cpfLimpo.length() - 2);

        return tresPrimeiros + ".***.***-" + doisUltimos;
    }
}
