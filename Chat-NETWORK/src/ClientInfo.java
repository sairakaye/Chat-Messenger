import java.io.ObjectOutputStream;

public class ClientInfo {
    ObjectOutputStream writer;
    String name;

    public ClientInfo(String name, ObjectOutputStream writer) {
        this.writer = writer;
        this.name = name;
    }

    public ObjectOutputStream getWriter() {
        return writer;
    }

    public String getName() {
        return name;
    }
}
