package ru.mihozhereb.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import ru.mihozhereb.Main;
import ru.mihozhereb.collection.model.*;
import ru.mihozhereb.control.Request;
import ru.mihozhereb.control.Response;
import ru.mihozhereb.control.UserData;

import java.io.IOException;

public class EditBandController {
    @FXML private Label titleLabel;
    @FXML private TextField nameField, xField, yField, participantsField;
    @FXML private ComboBox<MusicGenre> genreBox;

    @FXML private TextField fmNameField, heightField, weightField;
    @FXML private DatePicker birthdayPicker;
    @FXML private ComboBox<Color> hairColorBox;

    private String command;
    private String argument;

    @FXML
    public void initialize() {
        // Заполняем выпадающие списки
        ObservableList<MusicGenre> genres = FXCollections.observableArrayList();
        genres.add(null);
        genres.addAll(MusicGenre.values());
        genreBox.setItems(genres);
        genreBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(MusicGenre genre) {
                return genre == null ? "" : genre.name();
            }
            @Override
            public MusicGenre fromString(String string) {
                try {
                    return MusicGenre.valueOf(string);
                } catch (IllegalArgumentException e) {
                    return null;
                }
            }
        });
        genreBox.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(MusicGenre item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("");
                } else {
                    setText(item.name());
                }
            }
        });

        ObservableList<Color> colors = FXCollections.observableArrayList();
        colors.add(null);
        colors.addAll(Color.values());
        hairColorBox.setItems(colors);
        hairColorBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Color genre) {
                return genre == null ? "" : genre.name();
            }
            @Override
            public Color fromString(String string) {
                try {
                    return Color.valueOf(string);
                } catch (IllegalArgumentException e) {
                    return null;
                }
            }
        });
        hairColorBox.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Color item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("");
                } else {
                    setText(item.name());
                }
            }
        });
    }

    public void setArgs(String command, String argument, String title) {
        this.command = command;
        this.argument = argument;
        titleLabel.setText(title);
    }

    public void setMusicBand(MusicBand band) {
        // Если вложенные объекты отсутствуют — создаём пустые экземпляры
        if (band.getCoordinates() == null) {
            band.setCoordinates(new Coordinates());
        }
        if (band.getFrontMan() == null) {
            band.setFrontMan(new Person());
        }

        // Основные поля группы
        nameField.setText(band.getName() != null ? band.getName() : "");
        xField.setText(band.getCoordinates().getX() != null ? band.getCoordinates().getX().toString() : "");
        yField.setText(band.getCoordinates().getY() != null ? band.getCoordinates().getY().toString() : "");
        participantsField.setText(band.getNumberOfParticipants() != 0 ? String.valueOf(band.getNumberOfParticipants()) : "");
        genreBox.setValue(band.getGenre());

        // Поля фронтмена
        Person fm = band.getFrontMan();
        fmNameField.setText(fm.getName() != null ? fm.getName() : "");
        if (fm.getBirthday() != null) {
            birthdayPicker.setValue(fm.getBirthday());
        } else {
            birthdayPicker.setValue(null);
        }
        heightField.setText(fm.getHeight() != null ? fm.getHeight().toString() : "");
        weightField.setText(fm.getWeight() != 0 ? String.valueOf(fm.getWeight()) : "");
        hairColorBox.setValue(fm.getHairColor());
    }

    private MusicBand getAndValidateBand() throws IllegalArgumentException {
        MusicBand mb = new MusicBand();
        mb.setCoordinates(new Coordinates());
        mb.setFrontMan(new Person());

        mb.setName(nameField.getText());
        mb.getCoordinates().setX(Double.valueOf(xField.getText()));
        mb.getCoordinates().setY(Float.valueOf(yField.getText()));
        mb.setNumberOfParticipants(Long.parseLong(participantsField.getText()));
        if (genreBox.getValue() != null) {
            mb.setGenre(genreBox.getValue());
        }
        mb.getFrontMan().setName(fmNameField.getText());
        mb.getFrontMan().setBirthday(birthdayPicker.getValue());
        if (!heightField.getText().isEmpty()) {
            mb.getFrontMan().setHeight(Double.valueOf(heightField.getText()));
        }
        mb.getFrontMan().setWeight(Integer.parseInt(weightField.getText()));
        if (hairColorBox.getValue() != null) {
            mb.getFrontMan().setHairColor(hairColorBox.getValue());
        }

        return mb;
    }

    @FXML
    private void onSend() {
        MusicBand band;
        try {
            band = getAndValidateBand();
        } catch (IllegalArgumentException e) {
            Alerts.illegalArgumentError();
            return;
        }

        Response res;
        try {
            res = Main.getHandler().getClient().sendRequest(
                    new Request(command, argument, band,
                            UserData.getUserLogin(), UserData.getUserPassword())
            );
        } catch (IOException e) {
            Alerts.connectionError();
            return;
        }
        System.out.println(res);
        if (res.response().equals("Done.")) {
            Alerts.success(res);
            Stage stage = (Stage) titleLabel.getScene().getWindow();
            stage.close();
        } else {
            Alerts.ownershipError();
        }
    }

    @FXML
    private void onCancel() {
        Stage stage = (Stage) titleLabel.getScene().getWindow();
        stage.close();
    }
}
