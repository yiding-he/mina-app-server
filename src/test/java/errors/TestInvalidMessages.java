package errors;

import java.net.Socket;

/**
 * todo: description
 *
 * @author yiding.he
 */
public class TestInvalidMessages {


    public static void main(String[] args) throws Exception {
        Socket socket = new Socket("localhost", 8765);
        socket.getOutputStream().write("{\"functionName\":\"Hello\"}\n{\"functionName\":\"Hello\"}\n".getBytes());

        byte[] buffer = new byte[2048];
        int count = socket.getInputStream().read(buffer);

        if (count > 0) {
            System.out.println(new String(buffer, 0, count));
        } else {
            System.out.println("No data received.");
        }

        socket.close();
    }
}
