package ru.mihozhereb.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ru.mihozhereb.Main;
import ru.mihozhereb.control.Request;
import ru.mihozhereb.control.Response;
import ru.mihozhereb.control.UserData;

import java.io.IOException;

public class EnterNameController {
    @FXML private TextField nameField;
    @FXML private Button submitButton;
    @FXML private Button cancelButton;

    private Stage dialogStage;
    private String enteredName;

    @FXML
    public void initialize() {
        submitButton.disableProperty().bind(
                nameField.textProperty().isEmpty()
        );
    }

    public void setDialogStage(Stage stage) {
        this.dialogStage = stage;
    }

    @FXML
    private void onSubmit() {
        enteredName = nameField.getText().trim();

        Response res;
        try {
            res = Main.getHandler().getClient().sendRequest(
                    new Request("filter_contains_name", enteredName, null,
                            UserData.getUserLogin(), UserData.getUserPassword())
            );
        } catch (IOException e) {
            Alerts.connectionError();
            return;
        }
        System.out.println(res);
        Alerts.success(res);

        dialogStage.close();
    }

    @FXML
    private void onCancel() {
        enteredName = null;
        dialogStage.close();
    }
}
