package sample;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Controller {
    @FXML
    private Label statusLabel;
    @FXML
    private ProgressBar progressBar;
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    @FXML
    private void handleButtonAction(ActionEvent actionEvent) {

        File file = new FileChooser().showOpenDialog(null);

        if (file != null) {
            Task<Void> sendFileTask = new SendFileTask(file); //klasa zadania
            statusLabel.textProperty().bind(sendFileTask.messageProperty());
            progressBar.progressProperty().bind(sendFileTask.progressProperty());
            executor.submit(sendFileTask); //uruchomienie zadania w tle
        }
    }
}

