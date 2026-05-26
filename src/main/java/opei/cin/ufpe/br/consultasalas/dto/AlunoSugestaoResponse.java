package opei.cin.ufpe.br.consultasalas.dto;

/**
 * DTO usado na lista de sugestões do autocomplete
 *
 * retorna apenas dados necessários para o aluno se identificar sem informações sensíveis
 */
public record AlunoSugestaoResponse(Long id,
                                    String nomeCompleto,
                                    String cpfMascarado) {

}
