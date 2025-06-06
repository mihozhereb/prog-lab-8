package ru.mihozhereb;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ru.mihozhereb.control.Handler;
import ru.mihozhereb.control.UDPClient;
import ru.mihozhereb.control.UserData;
import ru.mihozhereb.controllers.CollectionUpdater;
import ru.mihozhereb.io.ConsoleWorker;

import java.io.IOException;
import java.net.SocketException;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

public final class Main extends Application {
    private static Stage primaryStage;
    private static Handler handler;

    public static void main(final String... args) {
        new Main().run(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        showLogin();
    }

    public static Handler getHandler() {return handler;}

    public static ResourceBundle getLocale() {
        return ResourceBundle.getBundle(
                "ru.mihozhereb.localization.Locale",  // Messages_en_US → base name = Messages
                Locale.getDefault()
        );
    }

    private void run(final String... args) {
        try {
            handler = new Handler(new UDPClient("localhost", 6666));
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }

        launch(args);
    }

    public void showLogin() throws Exception {
        Parent root = FXMLLoader.load(Objects.requireNonNull(Main.class.getResource("login.fxml")));
        primaryStage.setTitle("Login");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void showMain() throws IOException {
        FXMLLoader mainLoader = new FXMLLoader(Main.class.getResource("main.fxml"), getLocale());
        Parent root = mainLoader.load();
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

        FXMLLoader graphLoader = new FXMLLoader(Main.class.getResource("graph.fxml"));
        Parent graphRoot = graphLoader.load();
        Stage graphStage = new Stage();
        graphStage.setScene(new Scene(graphRoot));
        graphStage.setX(primaryStage.getX() + primaryStage.getWidth() + 10);
        graphStage.setY(primaryStage.getY());
        graphStage.show();

        new Thread(new CollectionUpdater(mainLoader.getController(), graphLoader.getController())).start();
    }
}