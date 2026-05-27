package opei.cin.ufpe.br.consultasalas.dto;

/**
 * resposta que vai retornar após importação do CSV
 *
 * vai possuir registrosLidos, registrosSalvos e mensagem (resultado da operação)
 */
public record ResultadoImportacaoResponse(
        int registrosLidos,
        int registrosSalvos,
        String mensagem
) {
}
