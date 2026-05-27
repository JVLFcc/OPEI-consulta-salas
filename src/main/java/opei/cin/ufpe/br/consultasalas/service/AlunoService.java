package opei.cin.ufpe.br.consultasalas.service;

import opei.cin.ufpe.br.consultasalas.dto.AlocacaoResponse;
import opei.cin.ufpe.br.consultasalas.dto.AlunoResponseDetalhada;
import opei.cin.ufpe.br.consultasalas.dto.AlunoSugestaoResponse;
import opei.cin.ufpe.br.consultasalas.dto.LocalizacaoDTO;
import opei.cin.ufpe.br.consultasalas.model.Aluno;
import opei.cin.ufpe.br.consultasalas.repository.AlunoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;

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
     * busca sugestões com base no termo digitado
     *
     * a busca começa com pelo menos 3 caracteres pra evitar gargalo e requisições desnecessárias
     * se o termo possui apenas numeros, o sistema busca por cpf, caso o contrário busca por nome
     *
     * aqui removemos duplicatas pelo CPF antes de retornar
     */
    public List<AlunoSugestaoResponse> buscarSugestoes(String termo) {
        if (termo == null || termo.trim().length() < 3){
            return List.of();
        }

        String termoLimpo = termo.trim();
        String apenasNumeros = termoLimpo.replaceAll("\\D", "");

        List<Aluno> alunos;

        if (pareceCpf(termoLimpo, apenasNumeros)) {
            alunos = alunoRepository.findTop20ByCpfNormalizadoContaining(apenasNumeros);
        } else {
            alunos = alunoRepository.findTop20ByNomeNormalizadoContaining(normalizarTexto(termoLimpo));
        }

        /**
         * a resposta da sugestão não traz todos os dados do aluno
         * ela retorna apenas o necessário para o aluno se identificar: Id, nome completo, cpf mascarado e data de nascimento
         */
        return removerDuplicatasPorCpf(alunos).stream().map(aluno -> new AlunoSugestaoResponse(aluno.getId(), aluno.getNomeCompleto(), mascararCpf(aluno.getCpfNormalizado()), aluno.getDataNascimento())).toList();

    }

    /**
     * já aqui nós buscamos os detalhes completos de um aluno específico
     *
     * a partir do ID selecionado na sugestão, vai ser encontrado o cpf do aluno aí sim buscamos todas as alocações vinculadas a esse cpf
     */
    public AlunoResponseDetalhada buscarDetalhes(Long id){
        Aluno alunoBase = alunoRepository.findById(id).orElseThrow(() -> new RuntimeException("Aluno não encontrado"));

        List<Aluno> alocacoesDoAluno = alunoRepository.findByCpfNormalizado(alunoBase.getCpfNormalizado());

        java.util.Set<String> chavesAlocacoes = new java.util.HashSet<>();

        List<AlocacaoResponse> alocacoes = alocacoesDoAluno.stream()
                .filter(aluno -> { // tive que fazer isso por causa das duplicatas, é feio mas funciona
                    String chave = gerarChaveAlocacao(aluno);

                    if (chavesAlocacoes.contains(chave)) {
                        return false;
                    }

                    chavesAlocacoes.add(chave);
                    return true;
                }).map(aluno -> {
            LocalizacaoDTO localizacao = localizacaoService.resolverLocalizacao(
                    aluno.getPolo(),
                    aluno.getSala(),
                    aluno.getModalidade()
            );

            return new AlocacaoResponse(
                    aluno.getModalidade(),
                    aluno.getPolo(),
                    aluno.getHandle(),
                    localizacao.salaFormatada(),
                    localizacao.bloco(),
                    localizacao.andar()
            );

        }).toList();

        return new AlunoResponseDetalhada(
                alunoBase.getId(),
                alunoBase.getNomeCompleto(),
                mascararCpf(alunoBase.getCpfNormalizado()),
                alunoBase.getDataNascimento(),
                alunoBase.getInstituicao(),
                alocacoes
        );
    }

    /**
     * remove alunos duplicados pelo cpf
     *
     * isso vai evitar que um aluno inscrito em duas modalidades apareça duas vezes no autocomplete
     */
    private List<Aluno> removerDuplicatasPorCpf(List<Aluno> alunos) {
        Map<String, Aluno> alunosPorCpf = new LinkedHashMap<>();

        for (Aluno aluno : alunos) {
            String chave = aluno.getCpfNormalizado();

            if (chave == null || chave.isBlank()) {
                chave = "ID_" + aluno.getId();
            }

            alunosPorCpf.putIfAbsent(chave, aluno);
        }

        return alunosPorCpf.values().stream().toList();
    }

    /**
     * vê se o termo digitado é realmente cpf, como o nome já diz
     *
     * ele vê tanto xxxxxxxxxxx quanto xxx.xxx.xxx-xx (além das buscas parciais, é claro)
     */
    private boolean pareceCpf(String termoOriginal, String apenasNumeros) {
        if (apenasNumeros.isBlank()) {
            return false;
        }

        return termoOriginal.matches("[0-9.\\-\\s]+");
    }

    /**
     * mascara o cpf antes de enviá-lo ao front
     *
     * ex.: 12345678900 --> 123******00 e depois retorna no formato de cpf porém mascarado (123.***.***-00)
     * isso ajuda a diferenciar homônimos sem expor o cpf completo (LGPD ajudando aó olha só)
     */
    private String mascararCpf(String cpf) {
        if (cpf == null) {
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

    /**
     * gera chave única para identificar uma alocação do aluno
     *
     * a diferença desses métodos para os que estão no CsvImportService é que lá é pra evitar salvar duplicatas, e aqui é pra evitar exibir duplicatas
     * ou seja, essa parte aqui é mais pra uma camada de proteção
     *
     * além de que aqui a chave ão inclui o cpf pq lá em cima já teve a busca
     */
    private String gerarChaveAlocacao(Aluno aluno) {
        return normalizarParaChave(aluno.getModalidade()) + "|" +
                normalizarParaChave(aluno.getPolo()) + "|" +
                normalizarParaChave(aluno.getSala()) + "|" +
                normalizarParaChave(aluno.getHandle());
    }

    /**
     * normaliza texto pra comparação interna
     *
     * consistência rs
     */
    private String normalizarParaChave(String valor) {
        if (valor == null) {
            return "";
        }

        return valor.trim().toUpperCase();
    }

    // tem o mesmo intuito do método que foi aplicado no CsvImportService
    private String normalizarTexto(String texto) {
        if (texto == null) {
            return null;
        }

        return java.text.Normalizer.normalize(texto, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .trim()
                .toUpperCase();
    }
}
