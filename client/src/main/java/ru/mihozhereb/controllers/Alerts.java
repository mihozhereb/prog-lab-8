package ru.mihozhereb.controllers;

import javafx.scene.control.Alert;
import ru.mihozhereb.Main;
import ru.mihozhereb.control.Response;

public class Alerts {
    public static void connectionError() {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setHeaderText(Main.getLocale().getString("Alerts.Connection error"));
        a.setContentText(Main.getLocale().getString("Alerts.Try again later..."));
        a.showAndWait();
    }

    public static void success(Response res) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(Main.getLocale().getString("Alerts.Successfully"));
        a.setContentText(
                res.response() +
                        (res.elements() != null ? "\n" + res.elements() : "")
        );
        a.showAndWait();
    }

    public static void ownershipError() {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setHeaderText(Main.getLocale().getString("Alerts.Error"));
        a.setContentText(Main.getLocale().getString("Alerts.Insufficient permissions to delete the object"));
        a.showAndWait();
    }

    public static void illegalArgumentError() {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setHeaderText(Main.getLocale().getString("Alerts.Wrong argument"));
        a.setContentText(Main.getLocale().getString("Alerts.Check the data!"));
        a.showAndWait();
    }

    public static void loginError() {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setHeaderText(Main.getLocale().getString("Alerts.Invalid login or password"));
        a.setContentText(Main.getLocale().getString("Alerts.Try again later..."));
        a.showAndWait();
    }

    public static void registerError() {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setHeaderText(Main.getLocale().getString("Alerts.Failed to register a user"));
        a.setContentText(Main.getLocale().getString("Alerts.Try again later..."));
        a.showAndWait();
    }
}
