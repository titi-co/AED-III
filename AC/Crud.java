import java.io.*;
import java.lang.reflect.Constructor;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

class Crud<T extends Registro> {
    private RandomAccessFile arq;
    private RandomAccessFile arq_s;
    private RandomAccessFile arq_g;
    private RandomAccessFile arq_c;
    private RandomAccessFile arq_p;
    private HashExtensivel indice_direto;
    private ArvoreBMais_String_Int indice_indireto;
    private HashExtensivel indice_direto_sugestoes;
    private HashExtensivel indice_direto_grupo;
    private HashExtensivel indice_direto_convite;
    private HashExtensivel indice_direto_participacao;
    private ArvoreBMais_ChaveComposta_String_Int lista_convites;
    private AB_sugestions indice_sugestions;
    private AB_grupos indice_grupos;
    private AB_convites indice_convites;
    private AB_participacao indice_participacao;
    private int[][] associacao_vetorid;
    private int associacao_n;
    private int[][] associacao_vetorid_grupo;
    private int associacao_n_grupo;
    private int[][] associacao_vetorid_convite;
    private int associacao_n_convite;
    private int[][] associacao_vetorid_participacao;
    private int associacao_n_participacao;

    Constructor<T> construtor;

    public Crud(Constructor<T> construtor) {
        this.construtor = construtor;
        associacao_n = 500; // Mudar conforme necessidade
        associacao_vetorid = new int[associacao_n][2];

        associacao_n_grupo = 500; // Mudar conforme necessidade
        associacao_vetorid_grupo = new int[associacao_n_grupo][2];

        associacao_n_convite = 500; // Mudar conforme necessidade
        associacao_vetorid_convite = new int[associacao_n_convite][2];

        associacao_n_participacao = 500; // Mudar conforme necessidade
        associacao_vetorid_participacao = new int[associacao_n_participacao][2];

        for (int i = 0; i < associacao_n; i++) {
            associacao_vetorid[i][0] = -1;
        }
        for (int i = 0; i < associacao_n; i++) {
            associacao_vetorid[i][1] = -1;
        }

        for (int i = 0; i < associacao_n_grupo; i++) {
            associacao_vetorid_grupo[i][0] = -1;
        }
        for (int i = 0; i < associacao_n_grupo; i++) {
            associacao_vetorid_grupo[i][1] = -1;
        }

        for (int i = 0; i < associacao_n_convite; i++) {
            associacao_vetorid_convite[i][0] = -1;
        }
        for (int i = 0; i < associacao_n_convite; i++) {
            associacao_vetorid_convite[i][1] = -1;
        }

        for (int i = 0; i < associacao_n_participacao; i++) {
            associacao_vetorid_participacao[i][0] = -1;
        }
        for (int i = 0; i < associacao_n_participacao; i++) {
            associacao_vetorid_participacao[i][1] = -1;
        }

        try {
            arq = new RandomAccessFile("dados/dados.db", "rw");
            arq_s = new RandomAccessFile("dados/dados_s.db", "rw");
            arq_g = new RandomAccessFile("dados/dados_g.db", "rw");
            arq_c = new RandomAccessFile("dados/dados_c.db", "rw");
            arq_p = new RandomAccessFile("dados/dados_p.db", "rw");

            // Verifico se o arquivo está vazio. Se estiver, trato de inicializar o
            // cabeçalho
            if (arq.length() == 0)
                arq.writeInt(0);
            if (arq_s.length() == 0)
                arq_s.writeInt(0);
            if (arq_g.length() == 0)
                arq_g.writeInt(0);
            if (arq_c.length() == 0)
                arq_c.writeInt(0);
            if (arq_p.length() == 0)
                arq_p.writeInt(0);

            indice_direto = new HashExtensivel(5, "dados/diretorio.db", "dados/bucket.db");
            indice_indireto = new ArvoreBMais_String_Int(5, "dados/AB.db");
            indice_sugestions = new AB_sugestions("dados/sugestions.db");
            indice_direto_sugestoes = new HashExtensivel(5, "dados/diretorio_sugestions.db",
                    "dados/bucket_sugestions.db");
            indice_grupos = new AB_grupos("dados/grupos.db");
            indice_direto_grupo = new HashExtensivel(5, "dados/diretorio_grupos.db", "dados/bucket_grupos.db");
            indice_convites = new AB_convites("dados/convites.db");
            lista_convites = new ArvoreBMais_ChaveComposta_String_Int(5, "dados/lista_convites.db");
            indice_direto_convite = new HashExtensivel(5, "dados/diretorio_convite.db", "dados/bucket_convite.db");
            indice_participacao = new AB_participacao("dados/participacao.db");
            indice_direto_participacao = new HashExtensivel(5, "dados/diretorio_participacao.db",
                    "dados/bucket_participacao.db");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Cria um usuario no arquivo de dados
    public int create(String nome, String email, String senha) {
        int last_id = 0;
        int current_id = 0;
        try {
            arq.seek(0);
            last_id = arq.readInt();
            current_id = last_id + 1;
            arq.seek(0);
            arq.writeInt(current_id);

            // Criando o objeto usuario
            Usuario user = new Usuario(current_id, nome, email, senha);

            // Movo o ponteiro pro fim do arquivo
            arq.seek(arq.length());
            long begin = arq.getFilePointer();
            // System.out.println(begin);
            arq.writeByte('*');
            arq.seek(arq.length());
            arq.writeShort(user.toByteArray().size());
            arq.seek(arq.length());
            arq.write(user.toByteArray().toByteArray());

            // Indices
            indice_direto.create(user.getId(), begin);
            indice_indireto.create(email, user.getId());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return current_id;
    }

    // Cria uma sugestao no arquivo de dados
    public int create(int id, String p, String l, float f, String o) {
        int last_id = 0;
        int current_id = 0;
        long begin = 0;
        try {
            arq_s.seek(0);
            last_id = arq_s.readInt();
            current_id = last_id + 1;
            arq_s.seek(0);
            arq_s.writeInt(current_id);

            // Criando o objeto usuario
            Sugestao sugestao = new Sugestao(current_id, id, p, l, f, o);

            // Movo o ponteiro pro fim do arquivo
            arq_s.seek(arq_s.length());
            begin = arq_s.getFilePointer();
            // System.out.println(begin);
            arq_s.writeByte('*');
            arq_s.seek(arq_s.length());
            arq_s.writeShort(sugestao.toByteArray().size());
            arq_s.seek(arq_s.length());
            arq_s.write(sugestao.toByteArray().toByteArray());

            indice_sugestions.insere(id, begin);
            indice_direto_sugestoes.create(current_id, begin);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return current_id;
    }

    // Cria uma grupo no arquivo de dados
    public int create(int idU, String n, long ms, float v, long me, String le, String o, boolean s, boolean a) {
        int last_id = 0;
        int current_id = 0;
        long begin = 0;
        try {
            arq_g.seek(0);
            last_id = arq_g.readInt();
            current_id = last_id + 1;
            arq_g.seek(0);
            arq_g.writeInt(current_id);

            // Criando o objeto usuario
            Grupo grupo = new Grupo(current_id, idU, n, ms, v, me, le, o, s, a);

            // Movo o ponteiro pro fim do arquivo
            arq_g.seek(arq_g.length());
            begin = arq_g.getFilePointer();
            // System.out.println(begin);
            arq_g.writeByte('*');
            arq_g.seek(arq_g.length());
            arq_g.writeShort(grupo.toByteArray().size());
            arq_g.seek(arq_g.length());
            arq_g.write(grupo.toByteArray().toByteArray());

            indice_grupos.insere(idU, begin);
            indice_direto_grupo.create(current_id, begin);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return current_id;
    }

    // Cria uma convite no arquivo de dados
    public int participar_grupo(int idUsuario, int idGrupo) {
        int last_id = 0;
        int current_id = 0;
        long begin = 0;
        try {
            arq_p.seek(0);
            last_id = arq_p.readInt();
            current_id = last_id + 1;
            arq_p.seek(0);
            arq_p.writeInt(current_id);

            // Criando o objeto usuario
            Participacao participacao = new Participacao(current_id, idUsuario, idGrupo, -1);

            // Movo o ponteiro pro fim do arquivo
            arq_p.seek(arq_p.length());
            begin = arq_p.getFilePointer();
            // System.out.println(begin);
            arq_p.writeByte('*');
            arq_p.seek(arq_p.length());
            arq_p.writeShort(participacao.toByteArray().size());
            arq_p.seek(arq_p.length());
            arq_p.write(participacao.toByteArray().toByteArray());

            indice_participacao.insere(idGrupo, begin);
            indice_participacao.insere_id(idUsuario, begin);
            indice_direto_participacao.create(current_id, begin);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return current_id;
    }

    // Cria uma participacao no arquivo de dados
    public int create(int idGrupo, String e, long mc, byte estado) {
        int last_id = 0;
        int current_id = 0;
        long begin = 0;
        try {
            arq_c.seek(0);
            last_id = arq_c.readInt();
            current_id = last_id + 1;
            arq_c.seek(0);
            arq_c.writeInt(current_id);

            // Criando o objeto usuario
            Convite convite = new Convite(current_id, idGrupo, e, mc, estado);

            // Movo o ponteiro pro fim do arquivo
            arq_c.seek(arq_c.length());
            begin = arq_c.getFilePointer();
            // System.out.println(begin);
            arq_c.writeByte('*');
            arq_c.seek(arq_c.length());
            arq_c.writeShort(convite.toByteArray().size());
            arq_c.seek(arq_c.length());
            arq_c.write(convite.toByteArray().toByteArray());

            indice_convites.insere(idGrupo, begin);
            indice_direto_convite.create(current_id, begin);
            lista_convites.create(e, current_id);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return current_id;
    }

    public void organiza_vetor(int id) {
        ArrayList<Long> sugestoes = null;

        try {
            sugestoes = indice_sugestions.getSugestions(id);
            for (int i = 0; i < sugestoes.size(); i++) {
                arq_s.seek(sugestoes.get(i));
                byte lapide = arq_s.readByte();
                short tamanho_reg = arq_s.readShort();
                int idSugestao = arq_s.readInt();
                int idUsuario = arq_s.readInt();
                String produto = arq_s.readUTF();
                String loja = arq_s.readUTF();
                Float valor = arq_s.readFloat();
                String obs = arq_s.readUTF();

                associacao_vetorid[i][0] = i + 1;
                associacao_vetorid[i][1] = idSugestao;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void organiza_vetor_grupos(int id) {
        ArrayList<Long> grupos = null;

        try {
            grupos = indice_grupos.getGrupos(id);
            for (int i = 0; i < grupos.size(); i++) {
                arq_g.seek(grupos.get(i));
                byte lapide = arq_g.readByte();
                short tamanho_reg = arq_g.readShort();
                int idGrupo = arq_g.readInt();
                int idUsuario = arq_g.readInt();
                String nome = arq_g.readUTF();
                long momentoSorteio = arq_g.readLong();
                float valor = arq_g.readFloat();
                long momentoEncontro = arq_g.readLong();
                String local = arq_g.readUTF();
                String obs = arq_g.readUTF();
                boolean sorteado = arq_g.readBoolean();
                boolean ativo = arq_g.readBoolean();
                associacao_vetorid_grupo[i][0] = i + 1;
                associacao_vetorid_grupo[i][1] = idGrupo;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void organiza_vetor_convites(int id) {
        ArrayList<Long> convites = null;

        try {
            convites = indice_convites.getConvites(id);
            for (int i = 0; i < convites.size(); i++) {
                arq_c.seek(convites.get(i));
                byte lapide = arq_c.readByte();
                short tamanho_reg = arq_c.readShort();
                int idConvite = arq_c.readInt();
                int idGrupo = arq_c.readInt();
                String email = arq_c.readUTF();
                long momentoConvite = arq_c.readLong();
                byte estado = arq_c.readByte();
                associacao_vetorid_convite[i][0] = i + 1;
                associacao_vetorid_convite[i][1] = idConvite;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Lista as sugestões de um dado usuario
    public void list(int idU) {
        ArrayList<Long> sugestoes = null;
        String p = "";
        try {
            sugestoes = indice_sugestions.getSugestions(idU);

            for (int i = 0; i < sugestoes.size(); i++) {
                arq_s.seek(sugestoes.get(i));
                byte lapide = arq_s.readByte();
                short tamanho_reg = arq_s.readShort();
                int idSugestao = arq_s.readInt();
                int idUsuario = arq_s.readInt();
                String produto = arq_s.readUTF();
                String loja = arq_s.readUTF();
                Float valor = arq_s.readFloat();
                String obs = arq_s.readUTF();

                if (lapide == '*') {

                    System.out.println((i + 1) + ". " + produto);
                    System.out.println(loja);
                    System.out.println("R$ " + valor);
                    System.out.println(obs);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Lista os grupos de um dado usuario
    public void list_grupo(int idU) {
        ArrayList<Long> grupos = null;
        String p = "";
        try {
            grupos = indice_grupos.getGrupos(idU);

            for (int i = 0; i < grupos.size(); i++) {
                // System.out.println(grupos.get(i));
                arq_g.seek(grupos.get(i));
                byte lapide = arq_g.readByte();
                short tamanho_reg = arq_g.readShort();
                int idGrupo = arq_g.readInt();
                int idUsuario = arq_g.readInt();
                String nome = arq_g.readUTF();
                long momentoSorteio = arq_g.readLong();
                float valor = arq_g.readFloat();
                long momentoEncontro = arq_g.readLong();
                String local = arq_g.readUTF();
                String obs = arq_g.readUTF();
                boolean sorteado = arq_g.readBoolean();
                boolean ativo = arq_g.readBoolean();

                if (ativo == true && lapide == '*') {

                    System.out.println((i + 1) + ". " + nome);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Lista os grupos de um dado usuario
    public void list_grupo(int idU, boolean sorteio) {
        ArrayList<Long> grupos = null;
        String p = "";
        try {
            grupos = indice_grupos.getGrupos(idU);

            for (int i = 0; i < grupos.size(); i++) {
                // System.out.println(grupos.get(i));
                arq_g.seek(grupos.get(i));
                byte lapide = arq_g.readByte();
                short tamanho_reg = arq_g.readShort();
                int idGrupo = arq_g.readInt();
                int idUsuario = arq_g.readInt();
                String nome = arq_g.readUTF();
                long momentoSorteio = arq_g.readLong();
                float valor = arq_g.readFloat();
                long momentoEncontro = arq_g.readLong();
                String local = arq_g.readUTF();
                String obs = arq_g.readUTF();
                boolean sorteado = arq_g.readBoolean();
                boolean ativo = arq_g.readBoolean();

                if (ativo == true && lapide == '*' && sorteado == false) {

                    System.out.println((i + 1) + ". " + nome);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Grupo> get_grupos(int idU) {
        ArrayList<Long> grupos = null;
        String p = "";
        ArrayList<Grupo> out = new ArrayList<Grupo>();
        Grupo g = null;
        try {
            grupos = indice_grupos.getGrupos(idU);

            for (int i = 0; i < grupos.size(); i++) {
                // System.out.println(grupos.get(i));
                arq_g.seek(grupos.get(i));
                byte lapide = arq_g.readByte();
                short tamanho_reg = arq_g.readShort();
                int idGrupo = arq_g.readInt();
                int idUsuario = arq_g.readInt();
                String nome = arq_g.readUTF();
                long momentoSorteio = arq_g.readLong();
                float valor = arq_g.readFloat();
                long momentoEncontro = arq_g.readLong();
                String local = arq_g.readUTF();
                String obs = arq_g.readUTF();
                boolean sorteado = arq_g.readBoolean();
                boolean ativo = arq_g.readBoolean();

                if (ativo == true && lapide == '*') {

                    g = new Grupo(idGrupo, idUsuario, nome, momentoSorteio, valor, momentoEncontro, local, obs,
                            sorteado, ativo);
                    out.add(g);
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return out;
    }

    // Lista os grupos de um dado usuario
    public ArrayList<Convite> list_convites(int idG, boolean flag) {
        ArrayList<Long> convites = null;
        ArrayList<Convite> out = new ArrayList<Convite>();
        Convite c = null;
        String p = "";
        try {
            convites = indice_convites.getConvites(idG);

            for (int i = 0; i < convites.size(); i++) {
                // System.out.println(grupos.get(i));
                arq_c.seek(convites.get(i));
                byte lapide = arq_c.readByte();
                short tamanho_reg = arq_c.readShort();
                int idConvite = arq_c.readInt();
                int idGrupo = arq_c.readInt();
                String email = arq_c.readUTF();
                long momentoConvite = arq_c.readLong();
                byte estado = arq_c.readByte();
                SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm");
                Date mc = new Date(momentoConvite);
                if (flag) {
                    System.out
                            .println(
                                    (i + 1) + ". " + email + " (" + df.format(mc) + " - "
                                            + (estado == 0 ? "pendente"
                                                    : estado == 1 ? "aceito" : estado == 2 ? "recusado" : "cancelado")
                                            + ")");
                }
                c = new Convite(idConvite, idGrupo, email, momentoConvite, estado);
                out.add(c);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return out;
    }

    // Lista os grupos de um dado usuario
    public ArrayList<Participacao> list_participantes(int idG, boolean flag) {
        ArrayList<Long> participantes = null;
        ArrayList<Participacao> out = new ArrayList<Participacao>();
        Participacao p = null;

        try {
            participantes = indice_participacao.getConvites(idG);

            for (int i = 0; i < participantes.size(); i++) {
                // System.out.println(grupos.get(i));
                arq_p.seek(participantes.get(i));
                byte lapide = arq_p.readByte();
                short tamanho_reg = arq_p.readShort();
                int idParticipacao = arq_p.readInt();
                int idUsuario = arq_p.readInt();
                int idGrupo = arq_p.readInt();
                int idAmigo = arq_p.readInt();

                p = new Participacao(idParticipacao, idUsuario, idGrupo, idAmigo);
                if (lapide == '*') {
                    out.add(p);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return out;
    }

    // Lista os grupos de um dado usuario
    public ArrayList<Participacao> list_part_user(int idU) {
        ArrayList<Long> participantes = null;
        ArrayList<Participacao> out = new ArrayList<Participacao>();
        Participacao p = null;

        try {
            participantes = indice_participacao.getUserGroups(idU);

            for (int i = 0; i < participantes.size(); i++) {
                // System.out.println(grupos.get(i));
                arq_p.seek(participantes.get(i));
                byte lapide = arq_p.readByte();
                short tamanho_reg = arq_p.readShort();
                int idParticipacao = arq_p.readInt();
                int idUsuario = arq_p.readInt();
                int idGrupo = arq_p.readInt();
                int idAmigo = arq_p.readInt();

                p = new Participacao(idParticipacao, idUsuario, idGrupo, idAmigo);
                if (lapide == '*') {
                    out.add(p);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return out;
    }

    // Altera uma sugestão
    public void update_sugestao(Sugestao s) throws Exception {
        long endereco = indice_direto_sugestoes.read(s.getId());
        arq_s.seek(endereco + 1);
        long old_size = arq_s.readShort();
        if (old_size == s.toByteArray().size()) {
            arq_s.seek(endereco);
            arq_s.writeByte('*');
            arq_s.writeShort(s.toByteArray().size());
            arq_s.write(s.toByteArray().toByteArray());

        } else {
            arq_s.seek(endereco);
            arq_s.writeByte('$');
            arq_s.seek(arq_s.length());
            long begin = arq_s.getFilePointer();
            arq_s.writeByte('*');
            arq_s.writeShort(s.toByteArray().size());
            arq_s.write(s.toByteArray().toByteArray());

            indice_sugestions.insere(s.getIdUsuario(), begin);
            indice_direto_sugestoes.update(s.getId(), begin);

        }
    }

    // Altera um grupo
    public void update_grupo(Grupo g) throws Exception {
        long endereco = indice_direto_grupo.read(g.getId());
        arq_g.seek(endereco + 1);
        long old_size = arq_g.readShort();
        if (old_size == g.toByteArray().size()) {
            arq_g.seek(endereco);
            arq_g.writeByte('*');
            arq_g.writeShort(g.toByteArray().size());
            arq_g.write(g.toByteArray().toByteArray());

        } else {
            arq_g.seek(endereco);
            arq_g.writeByte('$');
            arq_g.seek(arq_g.length());
            long begin = arq_g.getFilePointer();
            arq_g.writeByte('*');
            arq_g.writeShort(g.toByteArray().size());
            arq_g.write(g.toByteArray().toByteArray());

            indice_grupos.insere(g.getIdUsuario(), begin);
            indice_direto_grupo.update(g.getId(), begin);

        }
    }

    public void update_part(Participacao p) throws Exception {
        long endereco = indice_direto_participacao.read(p.getId());
        arq_p.seek(endereco + 1);

        arq_p.seek(endereco);
        arq_p.writeByte('*');
        arq_p.writeShort(p.toByteArray().size());
        arq_p.write(p.toByteArray().toByteArray());

    }

    // Altera um grupo
    public void update_convite(Convite c) throws Exception {
        long endereco = indice_direto_convite.read(c.getId());
        arq_c.seek(endereco + 1);
        long old_size = arq_c.readShort();

        arq_c.seek(endereco);
        arq_c.writeByte('*');
        arq_c.writeShort(c.toByteArray().size());
        arq_c.write(c.toByteArray().toByteArray());

    }

    public void delete_sugestao(int pseudo_id) {
        int idSugestao = 0;
        for (int i = 0; i < associacao_vetorid.length; i++) {
            // System.out.println(associacao_vetorid[i][1]);
            if (associacao_vetorid[i][0] == pseudo_id) {
                idSugestao = associacao_vetorid[i][1];
            }
        }
        try {
            long endereco = indice_direto_sugestoes.read(idSugestao);
            arq_s.seek(endereco);
            arq_s.writeByte('$');
            indice_direto_sugestoes.delete(idSugestao);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void delete_participacao(int id) {

        try {
            long endereco = indice_direto_participacao.read(id);
            arq_p.seek(endereco);
            arq_p.writeByte('$');
            indice_direto_participacao.delete(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Busca um usuario no arquivo de dados pela chave primaria id
    public Sugestao getSugestao_user(int id) {

        Sugestao s = new Sugestao();

        try {
            long endereco = indice_direto_sugestoes.read(id);
            arq_s.seek(endereco + 1);
            short reg_size = arq_s.readShort();
            // System.out.println(reg_size);
            arq_s.seek(endereco + 3);
            byte[] data = new byte[reg_size];
            arq_s.readFully(data);

            s.fromByteArray(data);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return s;
    }

    // Busca um usuario no arquivo de dados pela chave primaria id
    public Sugestao read_Sugestao(int pseudo_id) {
        Sugestao s = new Sugestao();
        int idSugestao = 0;
        for (int i = 0; i < associacao_vetorid.length; i++) {
            // System.out.println(associacao_vetorid[i][1]);
            if (associacao_vetorid[i][0] == pseudo_id) {
                idSugestao = associacao_vetorid[i][1];
            }
        }
        try {
            long endereco = indice_direto_sugestoes.read(idSugestao);
            arq_s.seek(endereco + 1);
            short reg_size = arq_s.readShort();
            // System.out.println(reg_size);
            arq_s.seek(endereco + 3);
            byte[] data = new byte[reg_size];
            arq_s.readFully(data);

            s.fromByteArray(data);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return s;
    }

    // Busca um grupo pelo pseudo id da interface
    public Grupo read_grupo(int pseudo_id) {
        Grupo g = new Grupo();
        int idGrupo = 0;
        for (int i = 0; i < associacao_vetorid_grupo.length; i++) {
            // System.out.println(associacao_vetorid[i][1]);
            if (associacao_vetorid_grupo[i][0] == pseudo_id) {
                idGrupo = associacao_vetorid_grupo[i][1];
            }
        }
        try {
            long endereco = indice_direto_grupo.read(idGrupo);

            arq_g.seek(endereco + 1);
            short reg_size = arq_g.readShort();
            // System.out.println(reg_size);
            arq_g.seek(endereco + 3);
            byte[] data = new byte[reg_size];
            arq_g.readFully(data);

            g.fromByteArray(data);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return g;
    }

    // Busca um grupo pelo pseudo id da interface
    public Grupo read_grupo(int idG, boolean flag) {
        Grupo g = new Grupo();
        try {
            long endereco = indice_direto_grupo.read(idG);

            arq_g.seek(endereco + 1);
            short reg_size = arq_g.readShort();
            // System.out.println(reg_size);
            arq_g.seek(endereco + 3);
            byte[] data = new byte[reg_size];
            arq_g.readFully(data);

            g.fromByteArray(data);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return g;
    }

    // Busca um grupo pelo pseudo id da interface
    public Convite read_convite(int idC) {
        Convite c = new Convite();

        try {
            long endereco = indice_direto_convite.read(idC);

            arq_c.seek(endereco + 1);
            short reg_size = arq_c.readShort();
            // System.out.println(reg_size);
            arq_c.seek(endereco + 3);
            byte[] data = new byte[reg_size];
            arq_c.readFully(data);

            c.fromByteArray(data);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return c;
    }

    public Convite convite_Exists(String email, int idG) {
        int[] lista = null;
        try {
            lista = lista_convites.read(email);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Convite c = null;
        boolean flag = false;
        for (int i = 0; (i < lista.length) && !flag; i++) {
            c = read_convite(lista[i]);
            if (c.getEmail().equals(email) && c.getIdGrupo() == idG)
                flag = true;

        }

        return c;

    }

    // Busca um usuario no arquivo de dados pela chave primaria id
    public Usuario read(int id) {
        Usuario user = new Usuario();
        try {
            long endereco = indice_direto.read(id);
            arq.seek(endereco + 1);
            short reg_size = arq.readShort();
            // System.out.println(reg_size);
            arq.seek(endereco + 3);
            byte[] data = new byte[reg_size];
            arq.readFully(data);

            user.fromByteArray(data);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return user;
    }

    // Busca um usuario no arquivo de dados pela chave secundaria email
    public Usuario read(String email) {
        Usuario user = new Usuario();
        try {
            int id = indice_indireto.read(email);
            user = read(id);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return user;
    }

    public int[] getConvites(String e) {
        int[] out = null;
        try {
            out = lista_convites.read(e);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return out;
    }

    public void removeLista_CV(String email, int idC) {
        try {
            lista_convites.delete(email, idC);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int emailExists(String email) throws Exception {
        return indice_indireto.read(email);
    }

}