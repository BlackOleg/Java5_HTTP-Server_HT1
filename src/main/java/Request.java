import java.io.InputStream;
import java.io.IOException;
public class Request {
    private String  input;
    private String uri;
    public Request(String  input) {
        this.input = input;
    }
    public void parse() {
        uri = parseUri(input);
    }

    private String parseUri(String requestString) {
        final var parts = requestString.split(" ");

        if (parts.length == 3) {
           return parts[1];
        }

        return null;
    }
    public String getUri() {
        return uri;
    }
}