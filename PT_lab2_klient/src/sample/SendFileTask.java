package sample;

import javafx.concurrent.Task;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

public class SendFileTask extends Task<Void> {
    private File file; //plik do wysłania

    public SendFileTask(File file) {
        this.file = file;
    }

    @Override
    protected Void call() throws Exception {
        this.updateMessage("Initiating...");
        this.updateProgress(0, file.length());

        try (Socket client = new Socket("localhost", 9797)) { //Nawiązywanie połączenia z serwerem

            try (FileInputStream fileInputStream = new FileInputStream(file)) {
                byte[] buffer = new byte[4096]; //bufor 4KB

                try (DataOutputStream dataOutputStream = new DataOutputStream(client.getOutputStream())) {  //output
                    dataOutputStream.writeUTF(file.getName());
                    dataOutputStream.writeLong(file.length());
                    long numberOfBytesToUpload = file.length();
                    long numberOfBytesAlreadyUploaded = 0;
                    this.updateMessage("Uploading...");

                    while (numberOfBytesAlreadyUploaded != numberOfBytesToUpload) {  //wczytywanie
                        int readSize = fileInputStream.read(buffer);
                        dataOutputStream.write(buffer, 0, readSize);
                        numberOfBytesAlreadyUploaded += readSize;
                        this.updateProgress(numberOfBytesAlreadyUploaded, numberOfBytesToUpload);
                    }
                } catch (IOException ex) {
                    this.updateMessage("Failure: " + ex.getMessage());
                }
                this.updateMessage("File uploaded with success");
                this.updateProgress(file.length(), file.length());

            } catch (IOException ex) {
                this.updateMessage("Failure: " + ex.getMessage());
            }
        } catch (SocketException ex) {
            this.updateMessage("Connection failed " + ex.getMessage());
        }
        return null;
    }
}

