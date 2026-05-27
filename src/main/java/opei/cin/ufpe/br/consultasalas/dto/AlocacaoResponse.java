package opei.cin.ufpe.br.consultasalas.dto;

/**
* vai servir pra representar a alocação de prova do alunno, vi que precisaria pela planilha de 2026
 *
 * me lembrei do detalhe que um msm aluno pode ter mais de uma alocação (prática e teórica)
*/

public record AlocacaoResponse(
        String modalidade,
        String polo,
        String handle,
        String sala,
        String bloco,
        String andar
) {
}
