import java.io.InputStream;
import java.io.IOException;
public class Request {
    private String  input;
    private String uri;
    public Request(String  input) {
        this.input = input;
    }
    public void parse() {

        System.out.print(input);

        uri = parseUri(input);
    }

    private String parseUri(String requestString) {
        int index1, index2;
        index1 = requestString.indexOf(' ');
        if (index1 != -1) {
            index2 = requestString.indexOf(' ', index1 + 1); if (index2 > index1)
                return requestString.substring(index1 + 1, index2);
        }
        return null;
    }
    public String getUri() {
        return uri;
    }
}