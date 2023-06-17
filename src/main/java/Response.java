import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

public class Response {
    final List<String> validPaths = List.of(
            "/index.html",
            "/spring.svg",
            "/spring.png",
            "/resources.html",
            "/styles.css",
            "/app.js",
            "/links.html",
            "/forms.html",
            "/classic.html",
            "/events.html",
            "/events.js");
    private static final int BUFFER_SIZE = 1024;
    Request request;
    OutputStream output;

    public Response(BufferedOutputStream output) {
        this.output = output;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public void sendError404() throws IOException {
        String errorMessage = new StringBuilder()
                .append("HTTP/1.1 404 File Not Found\r\n")
                .append("Content-Type: text/html\r\n")
                .append("Content-Length: 0\r\n")
                .append("\r\n")
                .toString();
        output.write(errorMessage.getBytes());
        output.flush();
    }

    public void sendGood200(String type, String length) throws IOException {
        String goodMessage = new StringBuilder()
                .append("HTTP/1.1 200 OK\r\n")
                .append("Content-Type: " + type + "\r\n")
                .append("Content-Length: " + length + "\r\n")
                .append("Connection: close\r\n")
                .append("\r\n")
                .toString();
        output.write(goodMessage.getBytes());
        output.flush();
    }

    public void sendStaticResource() throws IOException {
        byte[] bytes = new byte[BUFFER_SIZE];
        FileInputStream fis = null;
        if (!validPaths.contains(request.getUri())) {
            sendError404();
            return;
        }

        try {
            File file = new File(HttpServer.WEB_ROOT, request.getUri());

            if (file.exists()) {
                final var mimeType = Files.probeContentType(file.toPath());
                fis = new FileInputStream(file);
                int ch = fis.read(bytes, 0, BUFFER_SIZE);
                while (ch != -1) {
                    output.write(bytes, 0, ch);
                    ch = fis.read(bytes, 0, BUFFER_SIZE);
                    output.flush();
                }
            } else {
                sendError404();
            }
        } catch (Exception e) {
// thrown if cannot instantiate a File object
            System.out.println(e.toString());
        } finally {
            if (fis != null) fis.close();
        }
    }

    public void handler() throws IOException {
        if (!validPaths.contains(request.getUri())) {
            sendError404();
            return;
        }

        try {
            File file = new File(HttpServer.WEB_ROOT, request.getUri());

            if (file.exists()) {
                final var mimeType = Files.probeContentType(file.toPath());

                // special case for classic
                if (request.getUri().equals("/classic.html")) {
                    final var template = Files.readString(file.toPath());
                    final var content = template.replace(
                            "{time}",
                            LocalDateTime.now().toString()
                    ).getBytes();
                    sendGood200(mimeType, String.valueOf(content.length));
                    output.write(content);
                    output.flush();

                }
                final var length = Files.size(file.toPath());
                sendGood200(mimeType, String.valueOf(length));
                Files.copy(file.toPath(), output);
                output.flush();


            } else {
                // file not found
                sendError404();
            }
        } catch (Exception e) {
            // thrown if cannot instantiate a File object
            System.out.println(e.toString());
        }
    }

}

