package ru.mihozhereb.controllers;

import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import ru.mihozhereb.Main;
import ru.mihozhereb.collection.model.Color;
import ru.mihozhereb.collection.model.MusicBand;
import ru.mihozhereb.collection.model.MusicGenre;
import ru.mihozhereb.command.CommandType;
import ru.mihozhereb.control.Request;
import ru.mihozhereb.control.Response;
import ru.mihozhereb.control.UserData;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;

public class MainController {
    @FXML private Button printAscendingButton;
    @FXML private Button countLessButton;
    @FXML private Button clearButton;
    @FXML private Button addIfButton;
    @FXML private Button removeGreaterButton;
    @FXML private Button removeLoverButton;
    @FXML private Button filterButton;
    @FXML private Button infoButton;
    @FXML private Button helpButton;
    @FXML private Button executeScriptButton;
    @FXML private Button addButton;
    @FXML private Label currentUserLabel;
    @FXML private ComboBox<String> languageComboBox;
    @FXML private TableView<MusicBand> tableView;
    @FXML private TableColumn<MusicBand, Void> actionColumn;

    private static final Map<String, Locale> langMap = new LinkedHashMap<>();
    static {
        langMap.put("Русский",              new Locale("ru", "RU"));
        langMap.put("English",              new Locale("en", "US"));
        langMap.put("Türkçe",               new Locale("tr", "TR"));
        langMap.put("Italiano",             new Locale("it", "IT"));
        langMap.put("Español (Honduras)",   new Locale("es", "HN"));
    }

    public TableView<MusicBand> getTableView() {
        return tableView;
    }

    @FXML
    public void initialize() {
        setupLangBox();
        setupUserLabel();
        buildTableColumns();
        initActionColumn();
        setLocalization();
    }

    private void setupLangBox() {
        languageComboBox.setItems(FXCollections.observableArrayList(langMap.keySet()));
        Locale cur = Locale.getDefault();

        String def = langMap.entrySet().stream()
                .filter(e -> e.getValue().equals(cur))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElseGet(() -> langMap.entrySet().stream()
                        .filter(e -> e.getValue().getLanguage().equals(cur.getLanguage()))
                        .map(Map.Entry::getKey)
                        .findFirst()
                        .orElse("English")
                );
        languageComboBox.setValue(def);

        languageComboBox.valueProperty().addListener((obs, oldV, newV) -> {
            Locale selected = langMap.get(newV);
            if (selected != null && !selected.equals(Locale.getDefault())) {
                Locale.setDefault(selected);
                reloadMainScene();
            }
        });
    }

    private void setupUserLabel() {
        String userColor;
        try {
            userColor = GraphController.colorFromId(UserData.getUserId(
                    Main.getHandler().getClient()
            )).toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        currentUserLabel.setStyle(
                "-fx-background-color: #" + userColor.substring(2, userColor.length() - 2) + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 4 12;" +
                        "-fx-background-radius: 4;"
        );
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
        actionColumn.setPrefWidth(70);
        tableView.getColumns().add(actionColumn);
    }

    private void setLocalization() {
        ResourceBundle bundle = Main.getLocale();

        addButton.setText(bundle.getString("addButton"));
        printAscendingButton.setText(bundle.getString("printAscendingButton"));
        countLessButton.setText(bundle.getString("countLessButton"));
        clearButton.setText(bundle.getString("clearButton"));
        addIfButton.setText(bundle.getString("addIfButton"));
        removeGreaterButton.setText(bundle.getString("removeGreaterButton"));
        removeLoverButton.setText(bundle.getString("removeLoverButton"));
        filterButton.setText(bundle.getString("filterButton"));
        infoButton.setText(bundle.getString("infoButton"));
        helpButton.setText(bundle.getString("helpButton"));
        executeScriptButton.setText(bundle.getString("executeScriptButton"));
        actionColumn.setText(bundle.getString("actionColumn"));
        currentUserLabel.setText(bundle.getString("currentUserLabel") + UserData.getUserLogin());
        Platform.runLater(() -> {
            Stage stage = (Stage) currentUserLabel.getScene().getWindow();
            stage.setTitle(bundle.getString("appTitle"));
        });
    }

    private void onEditBand(MusicBand band) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("editBand.fxml"), Main.getLocale());
            Parent pane = loader.load();
            EditBandController ctrl = loader.getController();
            ctrl.setMusicBand(band);
            ctrl.setArgs("update", band.getId().toString(), Main.getLocale().getString("Update band"));
            Stage owner = (Stage) currentUserLabel
                    .getScene()
                    .getWindow();
            Stage dialog = new Stage();
            dialog.initOwner(owner);
            dialog.initModality(Modality.WINDOW_MODAL);
            dialog.setScene(new Scene(pane));
            dialog.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void onDeleteBand(MusicBand band) {
        Response res;
        try {
            res = Main.getHandler().getClient().sendRequest(
                    new Request("remove_by_id", band.getId().toString(), null,
                            UserData.getUserLogin(), UserData.getUserPassword())
            );
            System.out.println(res);
            if (res.response().equals("Done.")) {
                Alerts.success(res);
            } else {
                Alerts.ownershipError();
            }
        } catch (IOException e) {
            Alerts.connectionError();
        }
    }

    private void countLessCommand() {
        FXMLLoader loader = new FXMLLoader(
                Main.class.getResource("selectGenre.fxml"),
                Main.getLocale()
        );
        Parent pane;
        try {
            pane = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Stage dialog = new Stage();
        dialog.initOwner((Stage) currentUserLabel
                .getScene()
                .getWindow());
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.setScene(new Scene(pane));

        SelectGenreController ctrl = loader.getController();
        ctrl.setDialogStage(dialog);

        dialog.showAndWait();
    }

    private void filterNameCommand() {
        FXMLLoader loader = new FXMLLoader(
                Main.class.getResource("enterName.fxml"),
                Main.getLocale()
        );
        Parent pane;
        try {
            pane = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Stage dialog = new Stage();
        dialog.initOwner((Stage) currentUserLabel
                .getScene()
                .getWindow());
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.setScene(new Scene(pane));

        EnterNameController ctrl = loader.getController();
        ctrl.setDialogStage(dialog);

        dialog.showAndWait();
    }

    private void executeScriptCommand() {
        FXMLLoader loader = new FXMLLoader(
                Main.class.getResource("selectFile.fxml"),
                Main.getLocale()
        );
        Parent pane;
        try {
            pane = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Stage dialog = new Stage();
        dialog.initOwner((Stage) currentUserLabel
                .getScene()
                .getWindow());
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.setScene(new Scene(pane));

        SelectFileController ctrl = loader.getController();
        ctrl.setDialogStage(dialog);

        dialog.showAndWait();
    }

    public void onCommand(ActionEvent actionEvent) {
        Button b = (Button) actionEvent.getSource();
        String command = String.valueOf(b.getUserData()).replace(" ", "_");

        switch (command) {
            case "count_less_than_genre" -> {
                countLessCommand();
                return;
            }
            case "filter_contains_name" -> {
                filterNameCommand();
                return;
            }
            case "execute_script" -> {
                executeScriptCommand();
                return;
            }
        }

        try {
            if (Main.getHandler().getCommandType(command) == CommandType.ENTER) {
                FXMLLoader loader = new FXMLLoader(Main.class.getResource("editBand.fxml"), Main.getLocale());
                Parent pane = loader.load();
                EditBandController ctrl = loader.getController();
                ctrl.setMusicBand(new MusicBand());
                ctrl.setArgs(command, "", b.getText());
                Stage owner = (Stage) currentUserLabel
                        .getScene()
                        .getWindow();
                Stage dialog = new Stage();
                dialog.initOwner(owner);
                dialog.initModality(Modality.WINDOW_MODAL);
                dialog.setScene(new Scene(pane));
                dialog.showAndWait();
                return;
            }
        } catch (IOException e) {
            Alerts.connectionError();
            return;
        }

        Response res;
        try {
            res = Main.getHandler().getClient().sendRequest(
                    new Request(command, "", null,
                            UserData.getUserLogin(), UserData.getUserPassword())
            );
        } catch (IOException e) {
            Alerts.connectionError();
            return;
        }
        System.out.println(res);
        Alerts.success(res);
    }

    @FXML
    private void reloadMainScene() {
        initialize();
    }
}
