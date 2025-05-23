package ru.mihozhereb.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ru.mihozhereb.command.ExecuteScriptCommand;
import ru.mihozhereb.control.Request;
import ru.mihozhereb.control.Response;
import ru.mihozhereb.control.UserData;

import java.io.File;

public class SelectFileController {
    @FXML private TextField pathField;
    @FXML private Button browseButton;
    @FXML private Button submitButton;
    @FXML private Button cancelButton;

    private Stage dialogStage;
    private File selectedFile;

    @FXML
    public void initialize() {
        submitButton.disableProperty().bind(
                pathField.textProperty().isEmpty()
        );
    }

    public void setDialogStage(Stage stage) {
        this.dialogStage = stage;
    }

    @FXML
    private void onBrowse() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Выбор файла");
        File file = chooser.showOpenDialog(dialogStage);
        if (file != null) {
            selectedFile = file;
            pathField.setText(file.getAbsolutePath());
        }
    }

    @FXML
    private void onSubmit() {
        // просто закрываем, путь хранится в selectedFile
        Response res = new ExecuteScriptCommand().execute(
                new Request("execute_script", selectedFile.getAbsolutePath(), null,
                        UserData.getUserLogin(), UserData.getUserPassword())
        );

        System.out.println(res);
        Alerts.success(res);

        dialogStage.close();
    }

    @FXML
    private void onCancel() {
        selectedFile = null;
        dialogStage.close();
    }
}
