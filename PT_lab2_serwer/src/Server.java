import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private static void getFile(Socket socket) throws IOException {
        String fileName;
        long numberOfBytes;

        try (DataInputStream dataInputStream = new DataInputStream(socket.getInputStream())) { //input

            fileName = dataInputStream.readUTF();
            numberOfBytes = dataInputStream.readLong();

            try (FileOutputStream fileOutputStream = new FileOutputStream(fileName)) {
                System.out.println("Getting file..." + fileName);

                long numberOfBytesAlreadyUploaded = 0;
                byte[] buffer = new byte[4096]; //bufor 4KB

                while (numberOfBytesAlreadyUploaded != numberOfBytes) {   //wczytywanie
                    int readSize = dataInputStream.read(buffer);
                    fileOutputStream.write(buffer, 0, readSize);
                    numberOfBytesAlreadyUploaded += readSize;
                }
                System.out.println("Getting " + fileName + " finished with success");

            } catch (IOException ex) {
                System.out.println("Failure: " + ex.getMessage());
            }
        } catch (IOException ex) {
            System.out.println("Failure: " + ex.getMessage());
        } finally {
            socket.close();
        }
    }

    public static void main(String[] args) {
        Integer numberOfThreads = 4;

        System.out.println("Server is waiting... ");

        ExecutorService threadPoll = Executors.newFixedThreadPool(numberOfThreads);   //pula wątków z ograniczeniem

        try (ServerSocket serverSocket = new ServerSocket(9797)) {  //port      //nasłuchiwanie na połączenia
            while (true) {
                final Socket socket = serverSocket.accept();                     //przyjecie polaczenia

                threadPoll.submit(() -> {                                       //przydzielanie zadan
                    try {
                        Server.getFile(socket);
                    } catch (IOException ex) {
                        System.out.println("Failure: " + ex.getMessage());
                    }
                });
            }
        } catch (IOException ex) {
            System.out.println("Connection failed " + ex.getMessage());
            System.exit(1);
        }
    }
}

