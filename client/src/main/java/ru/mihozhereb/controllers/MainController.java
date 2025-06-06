package ru.mihozhereb.controllers;

import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
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
import java.util.function.Function;
import java.util.function.Predicate;

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

    private final ObservableList<MusicBand> masterData = FXCollections.observableArrayList();
    private FilteredList<MusicBand> filteredData;
    private SortedList<MusicBand> sortedData;
    private final Map<TableColumn<MusicBand, ?>, Predicate<MusicBand>> columnFilters = new HashMap<>();

    private static final Map<String, Locale> langMap = new LinkedHashMap<>();
    static {
        langMap.put("Русский",              new Locale("ru", "RU"));
        langMap.put("English",              new Locale("en", "US"));
        langMap.put("Türkçe",               new Locale("tr", "TR"));
        langMap.put("Italiano",             new Locale("it", "IT"));
        langMap.put("Español (Honduras)",   new Locale("es", "HN"));
    }

    public ObservableList<MusicBand> getTableData() {
        return masterData;
    }

    @FXML
    public void initialize() {
        setupLangBox();
        setupUserLabel();
        initTableData();
        buildFilterableColumns();
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

    private void initTableData() {
        filteredData = new FilteredList<>(masterData, b -> true);

        sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tableView.comparatorProperty());
        tableView.setItems(sortedData);
    }

    private <T> TableColumn<MusicBand, T> makeFilterableColumn(
            String title,
            Function<MusicBand, T> valueExtractor,
            Function<T, String> toStringConverter,
            Callback<TableColumn.CellDataFeatures<MusicBand, T>, ObservableValue<T>> cellValueFactory
    ) {
        // 1) Создаём пустую колонку (без текста)
        TableColumn<MusicBand, T> col = new TableColumn<>();
        col.setCellValueFactory(cellValueFactory);

        // 2) Label с названием: берем дефолтный класс "column-header", но убираем у него фон
        Label lbl = new Label(title);
        lbl.getStyleClass().add("column-header");
        lbl.setStyle(
                "-fx-background-color: transparent; " + // фон делаем прозрачным
                        "-fx-border-color: transparent; " +     // рамку тоже убираем
                        "-fx-padding: 0 0 0 0;"                  // обнуляем внутренние отступы
        );
        // При желании можно выставить фиксированную высоту, но обычно достаточно CSS-класса:
        lbl.setMinHeight(Region.USE_PREF_SIZE);
        lbl.setPrefHeight(Region.USE_COMPUTED_SIZE);
        lbl.setMaxHeight(Region.USE_PREF_SIZE);

        // 3) TextField для фильтрации, сразу под Label
        TextField filterField = new TextField();
        filterField.setPromptText("…");
        filterField.setMaxWidth(Double.MAX_VALUE);
        filterField.setStyle(
                "-fx-background-color: Snow; " +            // обычный белый фон
                        "-fx-border-color: transparent; " +                 // тонкая нижняя граница
                        "-fx-border-width: 0 0 0 0; " +               // только снизу
                        "-fx-padding: 2 4 2 4;"                       // немного padding внутри
        );

        // 4) Listener для фильтрации через Stream API
        filterField.textProperty().addListener((obs, oldV, newV) -> {
            if (newV == null || newV.isEmpty()) {
                columnFilters.remove(col);
            } else {
                String lower = newV.toLowerCase();
                columnFilters.put(col, band -> {
                    T cellValue = valueExtractor.apply(band);
                    String cellText = toStringConverter.apply(cellValue);
                    return cellText != null && cellText.toLowerCase().contains(lower);
                });
            }
            // Пересобираем Predicate сразу по всем полям
            filteredData.setPredicate(band ->
                    columnFilters.values().stream()
                            .allMatch(pred -> pred.test(band))
            );
        });

        // 5) Собираем VBox: сначала Label (прозрачный, но с дефолтным фоном шапки), затем TextField
        VBox headerBox = new VBox();
        headerBox.setSpacing(0);
        headerBox.setFillWidth(true);
        headerBox.setPadding(new Insets(2, 8, 2, 2));
        headerBox.getChildren().addAll(lbl, filterField);
        VBox.setVgrow(filterField, Priority.ALWAYS);

        // 6) Подменяем стандартный текст колонки на нашу графику
        col.setText(null);
        col.setGraphic(headerBox);

        return col;
    }

    private void buildFilterableColumns() {
        // 1) Форматтеры с учётом текущей локали
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter
                .ofLocalizedDateTime(FormatStyle.SHORT)
                .withLocale(Locale.getDefault());
        DateTimeFormatter dateFormatter = DateTimeFormatter
                .ofLocalizedDate(FormatStyle.SHORT)
                .withLocale(Locale.getDefault());

        // 2) Каждая колонка создаётся через makeFilterableColumn:

        // ID (Integer → String)
        TableColumn<MusicBand, Integer> idCol = makeFilterableColumn(
                "ID",
                MusicBand::getId,
                i -> i == null ? "" : i.toString(),
                cd -> new SimpleIntegerProperty(cd.getValue().getId()).asObject()
        );

        // Name (String → String)
        TableColumn<MusicBand, String> nameCol = makeFilterableColumn(
                "Name",
                MusicBand::getName,
                s -> s == null ? "" : s,
                cd -> new SimpleStringProperty(cd.getValue().getName())
        );

        // X (Double → String)
        TableColumn<MusicBand, Double> xCol = makeFilterableColumn(
                "X",
                mb -> mb.getCoordinates().getX(),
                d -> d == null ? "" : d.toString(),
                cd -> new SimpleDoubleProperty(cd.getValue().getCoordinates().getX()).asObject()
        );

        // Y (Float → String) — у вас это Integer, но в примере Float
        TableColumn<MusicBand, Float> yCol = makeFilterableColumn(
                "Y",
                mb -> mb.getCoordinates().getY(),
                f -> f == null ? "" : f.toString(),
                cd -> new SimpleFloatProperty(cd.getValue().getCoordinates().getY()).asObject()
        );

        // Created (String уже отформатированный)
        TableColumn<MusicBand, String> dateCol = makeFilterableColumn(
                "Created",
                mb -> mb.getCreationDate().format(dateTimeFormatter),
                s -> s,
                cd -> new SimpleStringProperty(
                        cd.getValue().getCreationDate().format(dateTimeFormatter)
                )
        );

        // Participants (Long → String)
        TableColumn<MusicBand, Long> numCol = makeFilterableColumn(
                "Participants",
                MusicBand::getNumberOfParticipants,
                l -> l == null ? "" : l.toString(),
                cd -> new SimpleLongProperty(cd.getValue().getNumberOfParticipants()).asObject()
        );

        // Genre (MusicGenre → String)
        TableColumn<MusicBand, MusicGenre> genreCol = makeFilterableColumn(
                "Genre",
                MusicBand::getGenre,
                mg -> mg == null ? "" : mg.name(),
                cd -> new SimpleObjectProperty<>(cd.getValue().getGenre())
        );

        // Frontman Name (String → String)
        TableColumn<MusicBand, String> fmName = makeFilterableColumn(
                "Frontman",
                mb -> mb.getFrontMan().getName(),
                s -> s == null ? "" : s,
                cd -> new SimpleStringProperty(cd.getValue().getFrontMan().getName())
        );

        // Frontman Birthday (String отформатированный)
        TableColumn<MusicBand, String> fmBday = makeFilterableColumn(
                "Birthday",
                mb -> mb.getFrontMan().getBirthday().format(dateFormatter),
                s -> s,
                cd -> new SimpleStringProperty(
                        cd.getValue().getFrontMan().getBirthday().format(dateFormatter)
                )
        );

        // Frontman Height (Double → String)
        TableColumn<MusicBand, Double> fmHeight = makeFilterableColumn(
                "Height",
                mb -> mb.getFrontMan().getHeight(),
                l -> l == null ? "" : l.toString(),
                cd -> new SimpleObjectProperty<>(cd.getValue().getFrontMan().getHeight())
        );

        // Frontman Weight (Integer → String)
        TableColumn<MusicBand, Integer> fmWeight = makeFilterableColumn(
                "Weight",
                mb -> mb.getFrontMan().getWeight(),
                l -> l == null ? "" : l.toString(),
                cd -> new SimpleIntegerProperty(cd.getValue().getFrontMan().getWeight()).asObject()
        );

        // Frontman Hair (Color → String)
        TableColumn<MusicBand, Color> fmColor = makeFilterableColumn(
                "Hair",
                mb -> mb.getFrontMan().getHairColor(),
                c -> c == null ? "" : c.name(),
                cd -> new SimpleObjectProperty<>(cd.getValue().getFrontMan().getHairColor())
        );

        // 3) Подставляем все колонки в таблицу:
        tableView.getColumns().setAll(
                idCol, nameCol, xCol, yCol, dateCol, numCol, genreCol,
                fmName, fmBday, fmHeight, fmWeight, fmColor
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
