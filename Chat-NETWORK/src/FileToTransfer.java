import java.io.Serializable;

public class FileToTransfer implements Serializable{

    FileToTransfer(byte[] content, String name, String extension, String toWhere, String specific, String recipient) {
        this.content = content;
        this.name = name;
        this.extension = "." + extension;
        this.toWhere = toWhere;
        this.specific = specific;
        this.recipient = recipient;
    }

    public byte[] getContent() {
        return content;
    }

    public String getName() {
        return name;
    }

    public String getExtension() {
        return extension;
    }

    public String getToWhere() { return toWhere; }

    public String getSpecific() { return specific; }

    public String getRecipient() { return recipient; }

    private byte[] content;
    private String name;
    private String extension;
    private String toWhere;
    private String specific;
    private String recipient;
}
