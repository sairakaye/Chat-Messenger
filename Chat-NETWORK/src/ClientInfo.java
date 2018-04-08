package src;

import javax.lang.model.type.PrimitiveType;
import java.io.BufferedReader;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;

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
