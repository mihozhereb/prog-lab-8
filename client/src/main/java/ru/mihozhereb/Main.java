package ru.mihozhereb;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ru.mihozhereb.control.Handler;
import ru.mihozhereb.control.UDPClient;
import ru.mihozhereb.control.UserData;
import ru.mihozhereb.io.ConsoleWorker;

import java.io.IOException;
import java.net.SocketException;
import java.util.Objects;

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

    private void run(final String... args) {
        try {
            handler = new Handler(new UDPClient("localhost", 6666));
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }

//        try (ConsoleWorker consoleWorker = new ConsoleWorker()) {
//            String line;
//            while ((line = consoleWorker.read()) != null && !line.equals("exit")) {
//                try {
//                    consoleWorker.write(handler.handle(line, consoleWorker));
//                } catch (IOException e) {
//                    consoleWorker.writeLn("Connection error. Retry later...");
//                }
//
//            }
//        }

        launch(args);
    }

    public void showLogin() throws Exception {
        Parent root = FXMLLoader.load(Objects.requireNonNull(Main.class.getResource("login.fxml")));
        primaryStage.setTitle("Login");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void showMain() throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(Main.class.getResource("main.fxml")));
        primaryStage.setTitle("MusicBand collection app");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

        Parent graphRoot = FXMLLoader.load(
            Objects.requireNonNull(Main.class.getResource("graph.fxml"))
        );
        Stage graphStage = new Stage();
        graphStage.setTitle("Graph");
        graphStage.setScene(new Scene(graphRoot));
        graphStage.setX(primaryStage.getX() + primaryStage.getWidth() + 10);
        graphStage.setY(primaryStage.getY());
        graphStage.show();
    }
}