import java.io.Serializable;

public class FileToTransfer implements Serializable{

    FileToTransfer(byte[] content, String name, String extension) {
        this.content = content;
        this.name = name;
        this.extension = "." + extension;
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

    private byte[] content;
    private String name;
    private String extension;
}
