import java.io.*;
import java.util.ArrayList;

public class AB_sugestions {
    private RandomAccessFile arq;
    private String nomeArq;

    public AB_sugestions(String na) {
        try {
            nomeArq = na;
            arq = new RandomAccessFile(nomeArq, "rw");

            if (arq.length() == 0)
                arq.writeInt(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void insere(int idUsuario, long endereco) {
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
            arq.writeInt(idUsuario);
            arq.seek(arq.length());
            arq.writeLong(endereco);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public ArrayList<Long> getSugestions(int idUsuario) throws IOException {
        arq.seek(0);
        int n = arq.readInt();
        arq.seek(4);
        int id = 0;
        long endereco;
        ArrayList<Long> sugestions = new ArrayList<Long>();

        while (true) {
            try {
                id = arq.readInt();
                endereco = arq.readLong();
                if (id == idUsuario) {

                    sugestions.add(endereco);

                }
            } catch (IOException e) {
                break;
            }
        }

        return sugestions;
    }
}