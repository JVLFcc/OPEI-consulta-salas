package opei.cin.ufpe.br.consultasalas.service;

import opei.cin.ufpe.br.consultasalas.dto.LocalizacaoDTO;
import org.springframework.stereotype.Service;

/**
 * serviço responsável por interpretar sala do aluno e descobrir informações extras como bloco e andar
 *
 * a planilha a qual eu estava trabalhando não possuía bloco e andar separados, por isso as informações estão sendo separadas dessa forma, mas pode ser que mude
 */
@Service
public class LocalizacaoService {

    /**
     * recebe o nome da sala e retorna bloco e andar correspondente
     *
     * por enquanto apliquei apenas as regras do Cin, onde:
     * - salas tipo E112 ou E233
     *   primeira letra indica bloco, primeiro numero indica andar
     *
     * Grads ficam no bloco A térreo, e o Espaço Maker fica no bloco E 2° andar
     */
    public LocalizacaoDTO resolverLocalizacao(String sala){
        if (sala == null || sala.isBlank()){
            return new LocalizacaoDTO("Não informado", null);
        }

        String salaNormalizada = sala.trim().toUpperCase();

        // Grads no bloco A térreo
        if (salaNormalizada.startsWith("GRAD")){
            return new LocalizacaoDTO("Bloco A", "Térreo");
        }

        // Espaço Maker fica no bloco E 2° andar
        if (salaNormalizada.equals("ESPAÇO MAKER") || salaNormalizada.equals("ESPACO MAKER")){
            return new LocalizacaoDTO("Bloco E", "2° andar");
        }

        // regra de formatação a partir de salas no formato tradicional
        if (salaNormalizada.matches("[A-Z][0-9].*")){
            char bloco = salaNormalizada.charAt(0);
            char andar = salaNormalizada.charAt(1);

            return new LocalizacaoDTO("Bloco " + bloco, andar + "° Andar");
        }

        return new LocalizacaoDTO("Local não mapeado", null);
    }
}
