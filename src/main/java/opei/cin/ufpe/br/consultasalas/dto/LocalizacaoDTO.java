package opei.cin.ufpe.br.consultasalas.dto;

/**
 * DTO bem simples apenas pra representar a localização derivada da sala
 *
 * salaFormatada é a sala já tratada pra exibição (por causa do G5 que tenho que transformar em GRAD 5)
 *
 * VC ME PAGA VICTOR SEU LIXO
 */
public record LocalizacaoDTO(String salaFormatada,
                             String bloco,
                             String andar) {
}
