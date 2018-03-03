import javax.lang.model.type.PrimitiveType;
import java.io.BufferedReader;
import java.io.PrintWriter;

public class ClientInfo {
    PrintWriter writer;
    String name;

    public ClientInfo(String name, PrintWriter writer) {
        this.writer = writer;
        this.name = name;
    }

    public PrintWriter getWriter() {
        return writer;
    }

    public String getName() {
        return name;
    }
}
