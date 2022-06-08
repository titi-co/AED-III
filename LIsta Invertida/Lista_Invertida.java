import java.io.*;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Lista_Invertida {
    // ATRIBUTOS DA CLASSE
    String nomeArqTermos;
    String nomeArqLista;
    String nomeArqDados;
    RandomAccessFile arqTermos;
    RandomAccessFile arqLista;
    RandomAccessFile arqDados;

    public Lista_Invertida(String nT, String nL, String nD) throws Exception {
        nomeArqLista = nL;
        nomeArqTermos = nT;
        nomeArqDados = nD;

        // INICIALIZAÇÃO DOS ARQUIVOS DE DADOS
        arqLista = new RandomAccessFile("dados/" + nL, "rw");
        arqTermos = new RandomAccessFile("dados/" + nT, "rw");
        arqDados = new RandomAccessFile("dados/" + nD, "rw");

        // INICIALIZAÇÃO DOS CABEÇALHOS NOS ARQUIVOS CASO AINDA NÃO EXISTAM
        if (arqDados.length() == 0)
            arqDados.writeInt(0);

        if (arqTermos.length() == 0)
            arqTermos.writeInt(0);

    }

    /**
     * Cria um registro no arquivo principal de dados, bem como ja prepara os dados
     * de entrada para a lista invertida
     * 
     * @param n String a ser inserida
     * @throws Exception
     */
    void create(String n) throws Exception {
        int last_id = 0;
        int current_id = 0;

        // Insere no arquivo de dados principal
        arqDados.seek(0);
        last_id = arqDados.readInt();
        current_id = last_id + 1;
        arqDados.seek(0);
        arqDados.writeInt(current_id);

        arqDados.seek(arqDados.length());
        arqDados.writeInt(current_id);
        arqDados.writeUTF(n);

        // Prepara para inserçao na lista de termos e de ids

        // remove acentos e torna lower case
        String handled_string = Normalizer.normalize(n, Form.NFD);
        handled_string = handled_string.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        handled_string = handled_string.toLowerCase();

        String[] broken_string = handled_string.split(" ");

        // Faço uma lista de elementos para fazer a remoção das stop words da mesma
        List<String> allWords = new ArrayList<String>(Arrays.asList(broken_string));
        // Completar com mais stop words caso seja necessario
        List<String> stopWords = Arrays.asList("de", "das", "da");
        allWords.removeAll(stopWords);

        for (String termo : allWords) {
            /*
             * Para cada palavra quebrada do meu dado, preciso verificar se ele ja existe na
             * lista de termos
             */

            // Percorre Sequencialmente a lista de termos, verificando se o termo ja existe
            // Caso não exista (-1) insere-se o termo na base de termos
            // Cria-se tambem o bloco respectivo a esse termo na lista de ids
            if (buscaTermo(termo) == -1) {
                long endereco = insereTermo(termo, arqTermos.length());
                criaBloco(endereco, 1, current_id, -1);
            }
            // Caso o termo ja tenha sido capturado previamente, apenas insere-se seu id no
            // respectivo bloco
            else {
                insereBloco(current_id, buscaTermo(termo), termo);
            }

        }

    }

    /**
     * Busca um termo qualquer com pesquisa na lista invertida
     * 
     * @param termo String a ser buscada
     * @return Uma lista de endereços de registros que contem o termo de pesquisa
     * @throws Exception
     */
    ArrayList<Long> buscar(String termo) throws Exception {
        // remove acentos e torna lower case
        String handled_string = Normalizer.normalize(termo, Form.NFD);
        handled_string = handled_string.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        handled_string = handled_string.toLowerCase();
        String[] broken_string = handled_string.split(" ");

        // Listas de interseçao
        ArrayList<Integer> out = new ArrayList<Integer>();
        ArrayList<Integer> aux = new ArrayList<Integer>();

        // Busco o endereço de um termo na lista de ids
        long e = buscaTermo(broken_string[0]);

        // Se o termo existe
        if (e != -1) {
            arqLista.seek(e); // Aponto o arquivo no endereço do bloco
            int n = arqLista.readInt(); // Faço a leitura da quantidade de ids existente nesse bloco
            // Para cada id existente, armazeno na lista de saida
            for (int i = 0; i < n; i++) {
                out.add(arqLista.readInt());
            }

            // Percorro os demais blocos se ouverem mais termos a serem pesquisados
            // Mesmo processo descrito acima
            for (int i = 1; i < broken_string.length; i++) {
                aux.clear();
                e = buscaTermo(broken_string[i]);
                arqLista.seek(e);
                n = arqLista.readInt();
                for (int j = 0; j < n; j++) {
                    aux.add(arqLista.readInt());
                }

                // Para cada termo pesquisado, devo fazer a interseção com a lista de saida
                // Assim capturo apenas os ids que apresentam todos os termos chave
                out.retainAll(aux);
            }
        }
        // Crio uma lista de endereços que será preenchida com os endereços dos
        // respectivos ids da lista de saida
        ArrayList<Long> out_endereco = new ArrayList<Long>();
        for (int i = 0; i < out.size(); i++) {
            out_endereco.add(busca_id(out.get(i)));
        }

        // retorno os endereços chave
        return out_endereco;

    }

    /**
     * Insiro um termo inexistente na lista de termos
     * 
     * @param termo    String a ser inserida
     * @param endereco endereço a ser usado na inserçao do termo
     * @return
     * @throws Exception
     */
    long insereTermo(String termo, long endereco) throws Exception {
        arqTermos.seek(0);
        int n = arqTermos.readInt(); // leio o cabeçalho para descobrir quantos termos tenho cadastrados
        /*
         * esse cabeçalho me é de extrema importancia alem de me auxiliar nas leituras,
         * serve para calcular o offset de endereços na lista de ids exemplo: Se tenho 2
         * termos e vou inserir o terceiro, sei que o local de criação do bloco desse
         * termo será o 3 * quantidade de bytes de um bloco
         */

        long current_offset = (n + 1) * 50;

        // Insiro o termo
        arqTermos.seek(0);
        arqTermos.writeInt(n + 1);

        arqTermos.seek(arqTermos.length());
        arqTermos.writeUTF(termo);
        arqTermos.writeLong(current_offset);

        // endereço a ser usado na lista de ids
        return current_offset;

    }

    /**
     * Recebendo um termo, retorno, caso exista, o endereço do mesmo
     * 
     * @param termo
     * @return Endereço do termo no arquivo de lista de ids
     * @throws Exception
     */
    long buscaTermo(String termo) throws Exception {
        arqTermos.seek(0);
        int n = arqTermos.readInt();
        String t = "";
        long e = 0;

        long out = -1;

        if (n != 0) {
            for (int i = 0; i < n; i++) {
                t = arqTermos.readUTF();
                e = arqTermos.readLong();
                if (t.equals(termo)) {
                    out = e;
                    break;
                }

            }
        }

        return out;
    }

    /**
     * Crio o bloco de termo no arquivo de lista de ids
     * 
     * @param e    Endereço de inclusão do bloco
     * @param n    Numero de ids cadastrados no bloco
     * @param id   O primeiro id recebido na criação do bloco
     * @param prox Ponteiro para o proximo bloco encadeado, padrão -1
     * @return Endereço do bloco
     * @throws Exception
     */
    long criaBloco(long e, int n, int id, long prox) throws Exception {
        arqLista.seek(e);
        arqLista.writeInt(n);
        arqLista.writeInt(id);
        // inicializo meu bloco sempre ja com as 10 posiçoes de id possiveis
        for (int i = 0; i < 9; i++) {
            arqLista.writeInt(-1);
        }
        arqLista.writeLong(prox);

        return e;
    }

    /**
     * Insere um id no bloco
     * 
     * @param id    Id a ser inserido
     * @param e     Endereço do bloco
     * @param termo Termo referente ao id
     * @throws Exception
     */
    void insereBloco(int id, long e, String termo) throws Exception {
        arqLista.seek(e); // Aponto para o endereço requisitado
        int n = arqLista.readInt(); // Leio n
        // Caso ainda exista espaço no bloco
        if (n <= 10) {
            arqLista.seek(e);
            arqLista.writeInt(n + 1); // somo um em n

            int offset = n * 4; // Calculo o endereço do proximo id, n * 4 bytes de um inteiro

            arqLista.seek(e + 4 + offset); // Aponto para meu offset
            arqLista.writeInt(id); // escrevo o inteiro
        }
        // Caso não caiba um novo id
        else {
            // Crio um novo bloco e resgato o seu endereço
            long new_bloco = criaBloco(e * 10, 1, id, -1);
            arqLista.seek(e + 4 + n * 4); // Aponto para o local de escrita do ponteiro prox para atualiza-lo com o novo
            arqLista.writeLong(new_bloco);
        }
    }

    /**
     * Recebendo um id, retorno o endereço da chave no arquivo principal
     * 
     * @param chave ID a ser buscado
     * @return Endereço do registro
     * @throws Exception
     */
    long busca_id(int chave) throws Exception {
        arqDados.seek(0);
        int n = arqDados.readInt(); // Leio o cabeçalho para saber quantos registros tenho
        int id = 0;
        String nome = "";

        long out = -1; // Valor padrão para caso o id não exista

        // Para cada registro, sequencialmente testo se o id bate com a chave
        for (int i = 0; i < n; i++) {
            long pointer = arqDados.getFilePointer();
            id = arqDados.readInt();
            nome = arqDados.readUTF();
            // Caso a chave case com o id do registro
            if (id == chave) {
                out = pointer;
                break;
            }
        }

        return out;
    }

    /**
     * Busco um registro no arquivo principal com base no seu endereço
     * 
     * @param e Endereço do registro
     * @return Nome no respectivo registro
     * @throws Exception
     */
    String busca_endereco(long e) throws Exception {
        arqDados.seek(e);
        arqDados.readInt();
        String nome = arqDados.readUTF();

        return nome;
    }
}