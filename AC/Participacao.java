import java.io.*;

public class Participacao implements Registro {
    protected int idParticipacao;
    protected int idUsuario;
    protected int idGrupo;
    protected int idAmigo;

    // CONSTRUTORES

    // default
    public Participacao() {
        this.idParticipacao = 0;
        this.idUsuario = 0; // TODO: DEFINIR COMO O ID SERÁ GERADO AQUI;
        this.idGrupo = 0;
        this.idAmigo = 0;

    }

    // preenchido
    public Participacao(int idP, int idU, int idG, int idA) {
        this.idParticipacao = idP;
        this.idUsuario = idU;
        this.idGrupo = idG;
        this.idAmigo = idA;
    }

    // GETTERS

    public int getId() {
        return this.idParticipacao;
    }

    public int getIdUsuario() {
        return this.idUsuario;
    }

    public int getIdGrupo() {
        return this.idGrupo;
    }

    public int getAmigo() {
        return this.idAmigo;
    }

    // Recupera um segundo identificador no caso do não uso do id, neste caso ele
    // será o EMAIL.
    public String chaveSecundaria() {
        return this.idUsuario + "|" + this.idGrupo;
    }

    // SETTERS

    public void setId(int id) {
        this.idParticipacao = id;
    }

    public void setIdUsuario(int idU) {
        this.idUsuario = idU;
    }

    public void setIdGrupo(int idG) {
        this.idGrupo = idG;
    }

    public void setIdAmigo(int idA) {
        this.idAmigo = idA;
    }

    // METODOS PARA REPRESENTAÇÃO DA CLASSE EM UM VETOR DE BYTES

    // Escreve e devolve os atributos da classe em um vetor de bytes
    public ByteArrayOutputStream toByteArray() {
        ByteArrayOutputStream dados = null;
        DataOutputStream saida = null;
        try {
            // crio um fluxo de saida de dados para preencher meu output de bytes
            dados = new ByteArrayOutputStream();
            saida = new DataOutputStream(dados);

            // escrevo no fluxo de saida os meus dados
            saida.writeInt(this.idParticipacao);
            saida.writeInt(this.idUsuario);
            saida.writeInt(this.idGrupo);
            saida.writeInt(this.idAmigo);

        } catch (Exception e) {
            e.printStackTrace();
        }
        // retorno meu vetor de bytes preenchido
        return dados;
    }

    // Leio de um vetor de bytes e armazeno na classe
    public void fromByteArray(byte[] bytes) {
        try {
            // com o vetor de bytes recebido por parametro, crio um fluxo de entrada que
            // sera manuseado pelo meu fluxo de dados
            ByteArrayInputStream dados = new ByteArrayInputStream(bytes);
            DataInputStream entrada = new DataInputStream(dados);

            // leio do meu fluxo de dados e armazeno nos atributos da classe
            this.idParticipacao = entrada.readInt();

            this.idUsuario = entrada.readInt();

            this.idGrupo = entrada.readInt();

            this.idAmigo = entrada.readInt();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}