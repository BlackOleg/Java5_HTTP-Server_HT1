import java.io.OutputStream;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.File;
public class Response {
    private static final int BUFFER_SIZE = 1024;
    Request request;
    OutputStream output;
    public Response(OutputStream output) {
        this.output = output;
    }
    public void setRequest(Request request) {
        this.request = request;
    }
    public void sendStaticResource() throws IOException {
        byte[] bytes = new byte[BUFFER_SIZE];
        FileInputStream fis = null;
        try {
            File file = new File(HttpServer.WEB_ROOT, request.getUri());
            if (file.exists()) {
                fis = new FileInputStream(file);
                int ch = fis.read(bytes, 0, BUFFER_SIZE);
                while (ch!=-1) {
                    output.write(bytes, 0, ch);
                    ch = fis.read(bytes, 0, BUFFER_SIZE);
                }
            } else {
// file not found
                String errorMessage = new StringBuilder()
                        .append("HTTP/1.1 404 File Not Found\r\n")
                        .append("Content-Type: text/html\r\n")
                        .append("Content-Length: 0\r\n")
                        .append("\r\n")
                        .toString();
                output.write(errorMessage.getBytes());
            } }catch (Exception e) {
// thrown if cannot instantiate a File object
            System.out.println(e.toString() );
        } finally {
            if (fis!=null) fis.close();
        } }
}

