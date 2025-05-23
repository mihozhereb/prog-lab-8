package ru.mihozhereb.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import ru.mihozhereb.Main;
import ru.mihozhereb.collection.model.Coordinates;
import ru.mihozhereb.collection.model.MusicBand;
import ru.mihozhereb.collection.model.Person;
import ru.mihozhereb.control.Request;
import ru.mihozhereb.control.Response;
import ru.mihozhereb.control.UserData;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;
import java.util.Objects;

public class InfoBandController {
    @FXML private Button deleteButton;
    @FXML private Label titleLabel;
    @FXML private Label nameField, xField, yField, creationDateField, participantsField, genreField;
    @FXML private Label fmNameField, heightField, weightField, birthdayField, hairColorField;
    private MusicBand musicBand;

    public void setMusicBand(MusicBand band) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).withLocale(Locale.getDefault());
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).withLocale(Locale.getDefault());

        this.musicBand = band;

        // Если вложенные объекты отсутствуют — создаём пустые экземпляры
        if (band.getCoordinates() == null) {
            band.setCoordinates(new Coordinates());
        }
        if (band.getFrontMan() == null) {
            band.setFrontMan(new Person());
        }

        // Основные поля группы
        nameField.setText(String.valueOf(band.getName()));
        xField.setText(String.valueOf(band.getCoordinates().getX()));
        yField.setText(String.valueOf(band.getCoordinates().getY()));
        creationDateField.setText(band.getCreationDate().format(dateTimeFormatter));
        participantsField.setText(String.valueOf(band.getNumberOfParticipants()));
        genreField.setText(String.valueOf(band.getGenre()));

        // Поля фронтмена
        Person fm = band.getFrontMan();
        fmNameField.setText(String.valueOf(fm.getName()));
        birthdayField.setText(fm.getBirthday().format(dateFormatter));
        heightField.setText(String.valueOf(fm.getHeight()));
        weightField.setText(String.valueOf(fm.getWeight()));
        hairColorField.setText(String.valueOf(fm.getHairColor()));
    }

    @FXML
    private void onEdit() {
        try {
            Stage stage = (Stage) titleLabel.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("editBand.fxml"), Main.getLocale());
            Parent pane = loader.load();
            EditBandController ctrl = loader.getController();
            ctrl.setMusicBand(musicBand);
            ctrl.setArgs("update", musicBand.getId().toString(), Main.getLocale().getString("Update band"));
            stage.setScene(new Scene(pane));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onDelete() {
        Response res;
        try {
            res = Main.getHandler().getClient().sendRequest(
                    new Request("remove_by_id", musicBand.getId().toString(), null,
                            UserData.getUserLogin(), UserData.getUserPassword())
            );
            System.out.println(res);
            if (res.response().equals("Done.")) {
                Alerts.success(res);
                Stage stage = (Stage) deleteButton.getScene().getWindow();
                stage.close();
            } else {
                Alerts.ownershipError();
            }
        } catch (IOException e) {
            Alerts.connectionError();
        }
    }
}
