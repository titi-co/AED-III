import java.io.*;

public class Sugestao implements Registro {
    protected int idSugestao;
    protected int idUsuario;
    protected String produto;
    protected String loja;
    protected float valor;
    protected String observacoes;

    // CONSTRUTORES

    // default
    public Sugestao() {
        this.idSugestao = 0;
        this.idUsuario = 0; // TODO: DEFINIR COMO O ID SERÁ GERADO AQUI;
        this.produto = " ";
        this.loja = " ";
        this.valor = 0;
        this.observacoes = " ";

    }

    // preenchido
    public Sugestao(int idS, int idU, String p, String l, float v, String o) {
        this.idSugestao = idS;
        this.idUsuario = idU;
        this.produto = p;
        this.loja = l;
        this.valor = v;
        this.observacoes = o;
    }

    // GETTERS

    public int getId() {
        return this.idSugestao;
    }

    public int getIdUsuario() {
        return this.idUsuario;
    }

    public String getProduto() {
        return this.produto;
    }

    public String getLoja() {
        return this.loja;
    }

    public float getValor() {
        return this.valor;
    }

    public String getObservacoes() {
        return this.observacoes;
    }

    // Recupera um segundo identificador no caso do não uso do id, neste caso ele
    // será o EMAIL.
    public String chaveSecundaria() {
        return this.idUsuario + "|" + this.produto;
    }

    // SETTERS

    public void setId(int id) {
        this.idSugestao = id;
    }

    public void setIdUsuario(int idU) {
        this.idUsuario = idU;
    }

    public void setProduto(String produto) {
        this.produto = produto;
    }

    public void setLoja(String loja) {
        this.loja = loja;
    }

    public void setValor(float valor) {
        this.valor = valor;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
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
            saida.writeInt(this.idSugestao);
            saida.writeInt(this.idUsuario);
            saida.writeUTF(this.produto);
            saida.writeUTF(this.loja);
            saida.writeFloat(this.valor);
            saida.writeUTF(this.observacoes);
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
            this.idSugestao = entrada.readInt();

            this.idUsuario = entrada.readInt();

            this.produto = entrada.readUTF();

            this.loja = entrada.readUTF();

            this.valor = entrada.readFloat();

            this.observacoes = entrada.readUTF();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}