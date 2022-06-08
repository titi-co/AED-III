import java.io.*;
import java.util.ArrayList;

public class AB_participacao {
    private RandomAccessFile arq;
    private RandomAccessFile arq1;
    private String nomeArq;

    public AB_participacao(String na) {
        try {
            nomeArq = na;
            arq = new RandomAccessFile(nomeArq, "rw");
            arq1 = new RandomAccessFile(nomeArq + "id", "rw");

            if (arq.length() == 0)
                arq.writeInt(0);
            if (arq1.length() == 0)
                arq1.writeInt(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void insere(int idGrupo, long endereco) {
        int last_id = 0;
        int current_id = 0;
        try {
            arq.seek(0);
            last_id = arq.readInt();
            current_id = last_id + 1;
            arq.seek(0);
            arq.writeInt(current_id);

            // Criando o objeto usuario

            // Movo o ponteiro pro fim do arquivo
            arq.seek(arq.length());
            // System.out.println(begin);
            arq.writeInt(idGrupo);
            arq.seek(arq.length());
            arq.writeLong(endereco);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void insere_id(int idU, long endereco) {
        int last_id = 0;
        int current_id = 0;
        try {
            arq1.seek(0);
            last_id = arq1.readInt();
            current_id = last_id + 1;
            arq1.seek(0);
            arq1.writeInt(current_id);

            // Criando o objeto usuario

            // Movo o ponteiro pro fim do arq1uivo
            arq1.seek(arq1.length());
            // System.out.println(begin);
            arq1.writeInt(idU);
            arq1.seek(arq1.length());
            arq1.writeLong(endereco);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public ArrayList<Long> getConvites(int idGrupo) throws IOException {
        arq.seek(0);
        int n = arq.readInt();
        arq.seek(4);
        int id = 0;
        long endereco;
        ArrayList<Long> convites = new ArrayList<Long>();

        while (true) {
            try {
                id = arq.readInt();
                endereco = arq.readLong();
                if (id == idGrupo) {

                    convites.add(endereco);

                }
            } catch (IOException e) {
                break;
            }
        }

        return convites;
    }

    public ArrayList<Long> getUserGroups(int idU) throws IOException {
        arq1.seek(0);
        int n = arq1.readInt();
        arq1.seek(4);
        int id = 0;
        long endereco;
        ArrayList<Long> participacao = new ArrayList<Long>();

        while (true) {
            try {
                id = arq1.readInt();
                endereco = arq1.readLong();
                if (id == idU) {

                    participacao.add(endereco);

                }
            } catch (IOException e) {
                break;
            }
        }

        return participacao;
    }
}