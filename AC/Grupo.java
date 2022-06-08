import java.io.*;

// Entidade Usuario
public class Grupo implements Registro {

    protected int idGrupo;
    protected int idUsuario;
    protected String nome;
    protected long momentoSorteio;
    protected float valor;
    protected long momentoEncontro;
    protected String localEncontro;
    protected String observacoes;
    protected boolean sorteado;
    protected boolean ativo;

    // CONSTRUTORES

    // default
    public Grupo() {
        this.idGrupo = 0;
        this.idUsuario = 0; // TODO: DEFINIR COMO O ID SERÁ GERADO AQUI;
        this.nome = "";
        this.momentoSorteio = 0;
        this.valor = 0;
        this.momentoEncontro = 0;
        this.localEncontro = "";
        this.observacoes = "";
        this.sorteado = false;
        this.ativo = true;

    }

    // preenchido
    public Grupo(int idG, int idU, String n, long ms, float v, long me, String le, String o, boolean s, boolean a) {
        this.idGrupo = idG;
        this.idUsuario = idU;
        this.nome = n;
        this.momentoSorteio = ms;
        this.valor = v;
        this.momentoEncontro = me;
        this.localEncontro = le;
        this.observacoes = o;
        this.sorteado = s;
        this.ativo = a;
    }

    // GETTERS

    public int getId() {
        return this.idGrupo;
    }

    public int getIdUsuario() {
        return this.idUsuario;
    }

    public String getNome() {
        return this.nome;
    }

    public long getMomentoSorteio() {
        return this.momentoSorteio;
    }

    public float getValor() {
        return this.valor;
    }

    public long getMomentoEncontro() {
        return this.momentoEncontro;
    }

    public String getLocalEncontro() {
        return this.localEncontro;
    }

    public String getObservacoes() {
        return this.observacoes;
    }

    public boolean getSorteado() {
        return this.sorteado;
    }

    public boolean getAtivo() {
        return this.ativo;
    }

    // Recupera um segundo identificador no caso do não uso do id
    public String chaveSecundaria() {
        return this.idUsuario + "|" + this.nome;
    }

    // SETTERS

    public void setId(int id) {
        this.idGrupo = id;
    }

    public void setAtivo(boolean a) {
        this.ativo = a;
    }

    public void setSorteado(boolean a) {
        this.sorteado = a;
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
            saida.writeInt(this.idGrupo);
            saida.writeInt(this.idUsuario);
            saida.writeUTF(this.nome);
            saida.writeLong(this.momentoSorteio);
            saida.writeFloat(this.valor);
            saida.writeLong(this.momentoEncontro);
            saida.writeUTF(this.localEncontro);
            saida.writeUTF(this.observacoes);
            saida.writeBoolean(this.sorteado);
            saida.writeBoolean(this.ativo);
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

            this.idGrupo = entrada.readInt();
            this.idUsuario = entrada.readInt();
            this.nome = entrada.readUTF();
            this.momentoSorteio = entrada.readLong();
            this.valor = entrada.readFloat();
            this.momentoEncontro = entrada.readLong();
            this.localEncontro = entrada.readUTF();
            this.observacoes = entrada.readUTF();
            this.sorteado = entrada.readBoolean();
            this.ativo = entrada.readBoolean();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}