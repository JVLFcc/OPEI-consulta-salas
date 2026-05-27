package opei.cin.ufpe.br.consultasalas.service;

import opei.cin.ufpe.br.consultasalas.dto.LocalizacaoDTO;
import org.springframework.stereotype.Service;

/**
 * serviço responsável por interpretar sala do aluno e descobrir informações extras como bloco e andar
 *
 * a localização agora não vai depender apenas da sala, agora também vai depender do polo
 * isso tudo pq Recife, Caruaru, Palmares e Petrolina têm regras diferentes em suas alocações
 */
@Service
public class LocalizacaoService {

    /**
     * resolve bloco e andar a partir do polo, sala e modalidade
     *
     * ? a modalidade vai ser usada em apenas casos específicos, como em Palmares (pq todos da prática de lá vão fazer no Lab 9)
     */
    public LocalizacaoDTO resolverLocalizacao(String polo,String sala, String modalidade){
        if (sala == null || sala.isBlank()){
            return new LocalizacaoDTO("Não informado", "Não informado", null);
        }

        String poloNormalizado = normalizar(polo);
        String salaNormalizada = normalizar(sala);
        String modalidadeNormalizada = normalizar(modalidade);

        if (poloNormalizado.contains("RECIFE")) {
            return resolverRecife(salaNormalizada);
        }

        if (poloNormalizado.contains("CARUARU")) {
            return resolverCaruaru(salaNormalizada);
        }

        if (poloNormalizado.contains("PALMARES")) {
            return resolverPalmares(salaNormalizada, modalidadeNormalizada);
        }

        if (poloNormalizado.contains("PETROLINA")) {
            return resolverPetrolina(salaNormalizada);
        }

        return resolverPadrao(salaNormalizada);
    }

    /**
     * regras do polo Recife (Cin)
     *
     * sala E232 = Bloco E, 2° andar
     * sala A014 = Bloco A, térreo
     * G5 = bloco A, GRAD 5
     */
    private LocalizacaoDTO resolverRecife(String sala) {
        if (sala.matches("G[0-9]+")) {
            String numeroGrad = sala.substring(1);

            return new LocalizacaoDTO(
                    "GRAD " + numeroGrad,
                    "Bloco A",
                    "Térreo"
            );
        }

        if (sala.equals("ESPACO MAKER")) {
            return new LocalizacaoDTO(
                    "Espaço Maker",
                    "Bloco E",
                    "2° Andar");
        }

        return resolverPadrao(sala);
    }

    /**
     * regras do polo Caruaru (Diocesano)
     *
     * espaço maker Caruaru = térreo
     * salas como E101 seguem praticamente o msm padrão do Cin
     */
    private LocalizacaoDTO resolverCaruaru(String sala) {
        if (sala.equals("ESPACO MAKER")) {
            return new LocalizacaoDTO(
                    "Espaço Maker",
                    "Bloco E",
                    "Térreo");
        }

        return resolverPadrao(sala);
    }

    /**
     * regras do polo Palmares (IFPE)
     *
     * prática -> lab 9
     * teórica -> salas 1 a 6, todas no térreo
     */
    private LocalizacaoDTO resolverPalmares(String sala, String modalidade) {
        if (modalidade.contains("PRATICA")) {
            return new LocalizacaoDTO(
                    "Lab 9",
                    "Não informado",
                    "Térreo");
        }

        return new LocalizacaoDTO(
                formatarSalaPalmares(sala),
                "Não informado",
                "Térreo");
    }

    /**
     * padronizando a exibição das salas de Palmares
     *
     * Victor seboso
     */
    private String formatarSalaPalmares(String sala) {
        if (sala.startsWith("SALA")) {
            return capitalizarSala(sala);
        }

        if (sala.matches("[0-9]+")) {
            return "Sala " + sala;
        }

        return sala;
    }

    /**
     * ajusta a escrita de "SALA 3" para "Sala 3".
     */
    private String capitalizarSala(String sala) {
        return sala.substring(0, 1).toUpperCase()
                + sala.substring(1).toLowerCase();
    }

    /**
     * regras do polo Petrolina (GGE)
     *
     * todas as salas estão no 1° andar do bloco A e ponto
     */
    private LocalizacaoDTO resolverPetrolina(String sala) {
        return new LocalizacaoDTO(
                formatarSalaGenerica(sala),
                "Bloco A",
                "1° Andar");
    }

    /**
     * regra geral pra salas tipo E101, E232
     *
     * a primeira letra indica Bloco, o primeiro número indica o andar
     *
     * obs: se o primeiro número for 0, considera-se o térreo
     */
    private LocalizacaoDTO resolverPadrao(String sala) {
        if (sala.matches("[A-Z][0-9].*")) {
            char bloco = sala.charAt(0);
            char andar = sala.charAt(1);

            if (andar == '0') {
                return new LocalizacaoDTO(
                        sala,
                        "Bloco " + bloco,
                        "Térreo");
            }

            return new LocalizacaoDTO(
                    sala,
                    "Bloco " + bloco,
                    andar + "° Andar");
        }

        return new LocalizacaoDTO(
                formatarSalaGenerica(sala),
                "Local não mapeado",
                null);
    }

    /**
     * Formata nomes simples de sala para exibição.
     *
     * Ex: SALA 16 → Sala 16
     */
    private String formatarSalaGenerica(String sala) {
        if (sala.equals("ESPACO MAKER")) {
            return "Espaço Maker";
        }

        if (sala.startsWith("SALA")) {
            return capitalizarSala(sala);
        }

        return sala;
    }

    /**
     * achei uma boa normalizar textos pra evitar dor de cabeça (culpa de Victor)
     *
     * então basicamente é isso que essa função vai fazer, vai deixar no formato que eu coloquei anteriormente
     */
    private String normalizar(String texto) {
        if (texto == null) {
            return "";
        }

        // não vi uma forma melhor pra essa versão, e tbm não queria mais trabalho só pra tratar de texto
        return texto.trim()
                .toUpperCase()
                .replace("Á", "A")
                .replace("À", "A")
                .replace("Â", "A")
                .replace("Ã", "A")
                .replace("É", "E")
                .replace("Ê", "E")
                .replace("Í", "I")
                .replace("Ó", "O")
                .replace("Ô", "O")
                .replace("Õ", "O")
                .replace("Ú", "U")
                .replace("Ç", "C");
    }
}
