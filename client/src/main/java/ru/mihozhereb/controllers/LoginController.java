package ru.mihozhereb.controllers;

import javafx.scene.control.Alert;
import ru.mihozhereb.Main;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import ru.mihozhereb.control.UserData;

import java.io.IOException;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    @FXML
    private void onLogin() {
        String login = usernameField.getText();
        String pass = passwordField.getText();
        boolean success;
        try {
            success = UserData.checkUser(login, pass, Main.getHandler().getClient());
        } catch (IOException e) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setHeaderText("Ошибка соединения");
            a.setContentText("Попробуйте позже...");
            a.showAndWait();
            return;
        }
        if (success) {
            UserData.setUserLogin(login);
            UserData.setUserPassword(pass);
            try {
                Main.showMain();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setHeaderText("Неверный логин или пароль");
            a.setContentText("Попробуйте снова...");
            a.showAndWait();
        }
        System.out.println("Login: " + login);
        System.out.println("Password: " + pass);
    }

    @FXML
    private void onRegister() {
        String login = usernameField.getText();
        String pass = passwordField.getText();
        boolean success;
        try {
            success = UserData.register(login, pass, Main.getHandler().getClient());
        } catch (IOException e) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setHeaderText("Ошибка соединения");
            a.setContentText("Попробуйте позже...");
            a.showAndWait();
            return;
        }
        if (success) {
            UserData.setUserLogin(login);
            UserData.setUserPassword(pass);
            try {
                Main.showMain();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle("Error");
            a.setHeaderText("Не удалось зарегистрировать пользователя");
            a.setContentText("Попробуйте снова...");
            a.showAndWait();
        }
        System.out.println("Login: " + login);
        System.out.println("Password: " + pass);
    }
}
