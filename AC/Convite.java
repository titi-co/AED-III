import java.io.*;

public class Convite implements Registro {
    protected int idConvite;
    protected int idGrupo;
    protected String email;
    protected long momentoConvite;
    protected byte estado;

    // CONSTRUTORES

    // default
    public Convite() {
        this.idConvite = 0;
        this.idGrupo = 0; // TODO: DEFINIR COMO O ID SERÁ GERADO AQUI;
        this.email = " ";
        this.momentoConvite = 0;
        this.estado = 0;

    }

    // preenchido
    public Convite(int idC, int idG, String e, long mc, byte es) {
        this.idConvite = idC;
        this.idGrupo = idG;
        this.email = e;
        this.momentoConvite = mc;
        this.estado = es;

    }

    // GETTERS

    public int getId() {
        return this.idConvite;
    }

    public int getIdGrupo() {
        return this.idGrupo;
    }

    public String getEmail() {
        return this.email;
    }

    public long getMomentoConvite() {
        return this.momentoConvite;
    }

    public byte getEstado() {
        return this.estado;
    }

    // Recupera um segundo identificador no caso do não uso do id, neste caso ele
    // será o EMAIL.
    public String chaveSecundaria() {
        return this.idGrupo + "|" + this.email;
    }

    // SETTERS

    public void setEstado(byte e) {
        this.estado = e;
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
            saida.writeInt(this.idConvite);
            saida.writeInt(this.idGrupo);
            saida.writeUTF(this.email);
            saida.writeLong(this.momentoConvite);
            saida.writeByte(this.estado);
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
            this.idConvite = entrada.readInt();

            this.idGrupo = entrada.readInt();

            this.email = entrada.readUTF();

            this.momentoConvite = entrada.readLong();

            this.estado = entrada.readByte();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}