package opei.cin.ufpe.br.consultasalas.model;

import jakarta.persistence.*;

/**
 * entidade que representa o aluno
 * essa classe é mapeada pra uma tabela no banco de dados
 * aqui ficam os dados brutos da planilha (nome, CPF, modalidade, polo, handle, sala e tals)
 */
@Entity
public class Aluno {

    /**
     * identificador único do aluno no banco, gerado automaticamente pelo JPA
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * nome completo que é usado na parte de requisiçã/busca por nome
     */
    private String nomeCompleto;

    /**
     * email do aluno que pode até ter no banco, mas não vai ser exibido no site
     */
    private String email;

    /**
     * cpf: é o original do aluno (NÃO É PRA RETORNAR COMPLETO PRA O FROTN)
     * cpfNormalizado: cpf contendo apenas números que facilita a busca
     */
    private String cpf;
    private String cpfNormalizado;

    /**
     * modalidade da prova, que é uma informação importante pra ser visualizada pois alunos podem estar em mais de uma modalidade, também serve para decidir se o handle deve ser mostrado ou não
     */
    private String modalidade;
    /**
     * usado pelos alunos da prática, quando existir
     */
    private String handle;

    /**
     * informação base onde o aluno fará a prova (Recife: CIN, Caruaru: Diocesano, Palmares: IFPE Palmares, Petrolina: GGE Petrolina)
     */
    private String polo;

    /**
     * sala onde o aluno fará a prova, informação de maior destaque a ser exibida, onde será derivada para descobrir bloco e andar
     */
    private String sala;

    // construtores, getters, setters, etc
    public Aluno() {
    }

    public Aluno(String nomeCompleto,String email, String cpf, String cpfNormalizado, String modalidade, String handle, String polo, String sala){
        this.nomeCompleto = nomeCompleto;
        this.email = email;
        this.cpf = cpf;
        this.cpfNormalizado = cpfNormalizado;
        this.modalidade = modalidade;
        this.handle = handle;
        this.polo = polo;
        this.sala = sala;
    }

    public Long getId() {
        return id;
    }

    public String getNomeCompleto() {
        return nomeCompleto;
    }

    public String getEmail() {
        return email;
    }

    public String getCpf() {
        return cpf;
    }

    public String getCpfNormalizado() {
        return cpfNormalizado;
    }

    public String getModalidade() {
        return modalidade;
    }

    public String getHandle() {
        return handle;
    }

    public String getPolo() {
        return polo;
    }

    public String getSala() {
        return sala;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setNomeCompleto(String nomeCompleto) {
        this.nomeCompleto = nomeCompleto;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public void setCpfNormalizado(String cpfNormalizado) {
        this.cpfNormalizado = cpfNormalizado;
    }

    public void setModalidade(String modalidade) {
        this.modalidade = modalidade;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }

    public void setPolo(String polo) {
        this.polo = polo;
    }

    public void setSala(String sala) {
        this.sala = sala;
    }
}
