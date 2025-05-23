package ru.mihozhereb.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import ru.mihozhereb.Main;
import ru.mihozhereb.collection.model.MusicGenre;
import ru.mihozhereb.control.Request;
import ru.mihozhereb.control.Response;
import ru.mihozhereb.control.UserData;
import ru.mihozhereb.controllers.Alerts;

import java.io.IOException;

public class SelectGenreController {
    @FXML private ComboBox<MusicGenre> genreComboBox;
    @FXML private Button submitButton;
    @FXML private Button cancelButton;

    private Stage dialogStage;
    private MusicGenre selectedGenre;

    @FXML
    public void initialize() {
        genreComboBox.getItems().setAll(MusicGenre.values());
        genreComboBox.setValue(MusicGenre.BLUES);
    }

    public void setDialogStage(Stage stage) {
        this.dialogStage = stage;
    }

    @FXML
    private void onSubmit() {
        selectedGenre = genreComboBox.getValue();

        Response res;
        try {
            res = Main.getHandler().getClient().sendRequest(
                    new Request("count_less_than_genre", selectedGenre.toString(), null,
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
        selectedGenre = null;
        dialogStage.close();
    }
}
