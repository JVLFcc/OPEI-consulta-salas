package opei.cin.ufpe.br.consultasalas.dto;

import java.util.List;

/**
 * DTO pra montar o card final que vai ser exibido para o aluno
 *
 * contém informações principais da consulta com destaque para o local de prova (sala, bloco e andar)
 *
 * ? agora ele rretorna uma lista de alocações, pois o mesmo aluno pode estar em mais de uma modalidade
 */
public record AlunoResponseDetalhada(Long id,
                                     String nomeCompleto,
                                     String cpfMascarado,
                                     String dataNascimento,
                                     String instituicao,
                                     List<AlocacaoResponse> alocacoes
                                     ) {
}
