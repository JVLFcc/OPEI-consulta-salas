package opei.cin.ufpe.br.consultasalas.dto;

/**
 * DTO pra montar o card final que vai ser exibido para o aluno
 *
 * contém informações principais da consulta com destaque para o local de prova (sala, bloco e andar)
 */
public record AlunoResponseDetalhada(Long id,
                                     String nomeCompleto,
                                     String cpfMascarado,
                                     String modalidade,
                                     String polo,
                                     String handle,
                                     String sala,
                                     String bloco,
                                     String andar) {
}
