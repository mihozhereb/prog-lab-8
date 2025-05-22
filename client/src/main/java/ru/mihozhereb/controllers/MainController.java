package ru.mihozhereb.controllers;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import ru.mihozhereb.Main;
import ru.mihozhereb.collection.model.Color;
import ru.mihozhereb.collection.model.MusicBand;
import ru.mihozhereb.collection.model.MusicGenre;
import ru.mihozhereb.control.Request;
import ru.mihozhereb.control.Response;
import ru.mihozhereb.control.UserData;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Iterator;
import java.util.Locale;
import java.util.Objects;

public class MainController {
    @FXML private Label currentUserLabel;
    @FXML private ComboBox<String> languageComboBox;
    @FXML private TableView<MusicBand> tableView;
    @FXML private TableColumn<MusicBand, Void> actionColumn;

    @FXML
    public void initialize() {
        languageComboBox.setItems(FXCollections.observableArrayList("English", "Русский"));
        languageComboBox.setValue(Locale.getDefault().getLanguage().equals("ru") ? "Русский" : "English");
        languageComboBox.valueProperty().addListener((o, oldV, newV) -> {
            Locale.setDefault(newV.equals("Русский") ? new Locale("ru") : new Locale("en","US"));
            reloadMainScene();
        });

        currentUserLabel.setText("User: " + UserData.getUserLogin());

        buildTableColumns();
        initActionColumn();

        actionColumn.setText("Actions");
        actionColumn.setPrefWidth(70);
        tableView.getColumns().add(actionColumn);

        new Thread(new CollectionUpdater()).start();

//        // --- sample data ---
//        sampleBand = new MusicBand(
//                1, "Example", new Coordinates(100,150),
//                java.time.ZonedDateTime.now(), 20,
//                MusicGenre.POST_ROCK,
//                new Person("Bob",
//                        java.time.LocalDateTime.now().minusYears(25),
//                        180, 75L, Color.BLUE)
//        );
//        tableView.setItems(FXCollections.observableArrayList(sampleBand));
    }

    private void buildTableColumns() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).withLocale(Locale.getDefault());
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).withLocale(Locale.getDefault());

        TableColumn<MusicBand,Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(c-> new SimpleIntegerProperty(c.getValue().getId()).asObject());
        TableColumn<MusicBand,String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(c-> new SimpleStringProperty(c.getValue().getName()));
        TableColumn<MusicBand,Double> xCol = new TableColumn<>("X");
        xCol.setCellValueFactory(c-> new SimpleDoubleProperty(c.getValue().getCoordinates().getX()).asObject());
        TableColumn<MusicBand,Float> yCol = new TableColumn<>("Y");
        yCol.setCellValueFactory(c-> new SimpleFloatProperty(c.getValue().getCoordinates().getY()).asObject());
        TableColumn<MusicBand,String> dateCol = new TableColumn<>("Created");
        dateCol.setCellValueFactory(c-> new SimpleStringProperty(
                c.getValue().getCreationDate().format(dateTimeFormatter)));
        TableColumn<MusicBand,Long> numCol = new TableColumn<>("Participants");
        numCol.setCellValueFactory(c-> new SimpleLongProperty(c.getValue().getNumberOfParticipants()).asObject());
        TableColumn<MusicBand,MusicGenre> genreCol = new TableColumn<>("Genre");
        genreCol.setCellValueFactory(c-> new SimpleObjectProperty<>(c.getValue().getGenre()));

        TableColumn<MusicBand,String> fmName = new TableColumn<>("Frontman");
        fmName.setCellValueFactory(c-> new SimpleStringProperty(c.getValue().getFrontMan().getName()));
        TableColumn<MusicBand,String> fmBday = new TableColumn<>("Birthday");
        fmBday.setCellValueFactory(c-> new SimpleStringProperty(c.getValue().getFrontMan().getBirthday().format(dateFormatter)));
        TableColumn<MusicBand,Double> fmHeight = new TableColumn<>("Height");
        fmHeight.setCellValueFactory(c-> new SimpleObjectProperty<>(c.getValue().getFrontMan().getHeight()));
        TableColumn<MusicBand,Long> fmWeight = new TableColumn<>("Weight");
        fmWeight.setCellValueFactory(c-> new SimpleLongProperty(c.getValue().getFrontMan().getWeight()).asObject());
        TableColumn<MusicBand,Color> fmColor = new TableColumn<>("Hair");
        fmColor.setCellValueFactory(c-> new SimpleObjectProperty<>(c.getValue().getFrontMan().getHairColor()));

        tableView.getColumns().setAll(
                idCol,nameCol,xCol,yCol,dateCol,numCol,genreCol,
                fmName,fmBday,fmHeight,fmWeight,fmColor
        );
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void initActionColumn() {
        Callback<TableColumn<MusicBand, Void>, TableCell<MusicBand, Void>> cellFactory =
                param -> new TableCell<>() {
                    private final Button editBtn = new Button();
                    private final Button delBtn  = new Button();

                    {
                        // Edit icon
                        editBtn.setGraphic(new ImageView(new Image(Objects.requireNonNull(Main.class.getResourceAsStream("icons/edit.png")),16,16,true,true)));
                        editBtn.setOnAction((ActionEvent evt) -> {
                            MusicBand band = getTableView().getItems().get(getIndex());
                            onEditBand(band);
                        });

                        // Delete icon
                        delBtn.setGraphic(new ImageView(new Image(Objects.requireNonNull(Main.class.getResourceAsStream("icons/delete.png")),16,16,true,true)));
                        delBtn.setOnAction((ActionEvent evt) -> {
                            MusicBand band = getTableView().getItems().get(getIndex());
                            onDeleteBand(band);
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            HBox box = new HBox(5, editBtn, delBtn);
                            setGraphic(box);
                        }
                    }
                };

        actionColumn.setCellFactory(cellFactory);
    }

    private class CollectionUpdater implements Runnable {
        @Override
        public void run() {
            Response res = null;

            while (true) {
                try {
                    res = Main.getHandler().getClient().sendRequest(
                            new Request("show", null, null, UserData.getUserLogin(), UserData.getUserPassword())
                    );
                } catch (IOException e) {
                    sleep();
                    continue;
                }

                if (res == null || res.elements() == null) {
                    sleep();
                    continue;
                }

                for (Iterator<MusicBand> it = tableView.getItems().iterator(); it.hasNext(); ) {
                    MusicBand i = it.next();
                    if (!res.elements().contains(i)) {
                        // remove object
                        it.remove();
                    }
                }

                for (MusicBand i : res.elements()) {
                    if (!tableView.getItems().contains(i)) {
                        // add object
                        tableView.getItems().add(i);
                    }
                }

                sleep();
            }
        }
    }

    private void sleep() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ex) {}
    }

    private void onEditBand(MusicBand band) {
//        try {
//            FXMLLoader loader = new FXMLLoader(Main.class.getResource("editBand.fxml"));
//            var pane = loader.load();
//            BandController ctrl = loader.getController();
//            ctrl.setEditMode(true);
//            ctrl.setMusicBand(band);
//
//            Stage dialog = new Stage();
//            dialog.initModality(Modality.APPLICATION_MODAL);
//            dialog.setTitle("Edit MusicBand");
//            dialog.setScene(new Scene((Parent) pane));
//            dialog.showAndWait();
//
//            if (ctrl.isSaved()) {
//                tableView.refresh();
//                drawCanvas();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    /** Удаляет объект из таблицы (и должен слать команду на сервер) */
    private void onDeleteBand(MusicBand band) {
//        // TODO: добавить удаление на сервере
//        tableView.getItems().remove(band);
//        drawCanvas();
    }

    private void reloadMainScene() {
//        TODO
    }
}
