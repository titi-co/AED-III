import java.io.*;

// Entidade Usuario
public class Usuario implements Registro {

    protected int idUsuario;
    protected String nome;
    protected String email;
    protected String senha;

    // CONSTRUTORES

    // default
    public Usuario() {
        this.idUsuario = 0; // TODO: DEFINIR COMO O ID SERÁ GERADO AQUI;
        this.nome = " ";
        this.email = " ";
        this.senha = " ";

    }

    // preenchido
    public Usuario(int id, String n, String e, String s) {
        this.idUsuario = id;
        this.nome = n;
        this.email = e;
        this.senha = s;
    }

    // GETTERS

    public int getId() {
        return this.idUsuario;
    }

    public String getNome() {
        return this.nome;
    }

    public String getEmail() {
        return this.email;
    }

    public String getSenha() {
        return this.senha;
    }

    // Recupera um segundo identificador no caso do não uso do id, neste caso ele
    // será o EMAIL.
    public String chaveSecundaria() {
        return this.email;
    }

    // SETTERS

    public void setId(int id) {
        this.idUsuario = id;
    }

    public void setNome(String n) {
        this.nome = n;
    }

    public void setEmail(String e) {
        this.email = e;
    }

    public void setSenha(String s) {
        this.senha = s;
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
            saida.writeInt(this.idUsuario);
            saida.writeUTF(this.nome);
            saida.writeUTF(this.email);
            saida.writeUTF(this.senha);
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
            this.idUsuario = entrada.readInt();

            this.nome = entrada.readUTF();

            this.email = entrada.readUTF();

            this.senha = entrada.readUTF();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}