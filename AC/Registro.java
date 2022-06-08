import java.io.*;

public interface Registro {

    public ByteArrayOutputStream toByteArray();

    public void fromByteArray(byte[] bytes);

    public int getId();

}