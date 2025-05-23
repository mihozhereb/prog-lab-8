package ru.mihozhereb.controllers;

import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import ru.mihozhereb.Main;
import ru.mihozhereb.collection.model.MusicBand;

import java.io.IOException;
import java.util.*;

public class GraphController {
    private class DrawableBand {
        private final MusicBand band;
        private final DoubleProperty alpha = new SimpleDoubleProperty(0);

        public DrawableBand(MusicBand band) { this.band = band; }
        public MusicBand getBand() { return band; }
        public DoubleProperty alphaProperty() { return alpha; }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            DrawableBand that = (DrawableBand) o;
            return Objects.equals(band, that.band);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(band);
        }
    }

    @FXML private Canvas canvas;
    private final List<DrawableBand> items = new ArrayList<>();
    private Timeline tl;

    @FXML
    public void initialize() {
        new AnimationTimer() {
            @Override public void handle(long now) {
                GraphicsContext gc = canvas.getGraphicsContext2D();
                gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
                drawCanvas();
                for (DrawableBand db : items) {
                    double a = db.alphaProperty().get();
                    gc.setGlobalAlpha(a);
                    drawBand(gc, db.getBand());
                }
                gc.setGlobalAlpha(1);
            }
        }.start();

        canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, this::onCanvasClick);
    }

    private void drawCanvas() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        double w = canvas.getWidth(), h = canvas.getHeight();
        double cx = w/2, cy = h/2;

        gc.clearRect(0,0,w,h);
        // оси через центр
        gc.strokeLine(0, cy, w, cy);      // горизонтальная
        gc.strokeLine(cx, 0, cx, h);      // вертикальная

        // стрелки
        gc.strokeLine(w-10, cy, w, cy);
        gc.strokeLine(w-10, cy-5, w, cy);
        gc.strokeLine(w-10, cy+5, w, cy);
        gc.strokeLine(cx, 10, cx, 0);
        gc.strokeLine(cx-5, 10, cx, 0);
        gc.strokeLine(cx+5, 10, cx, 0);
    }

    public static Color colorFromId(int id) {
        // Золотой угол в градусах (~360° × (1–1/φ)), φ = (1+√5)/2
        final double GOLDEN_ANGLE_DEG = 137.508;
        // Считаем угол оттенка в пределах [0,360)
        double hue = (id * GOLDEN_ANGLE_DEG) % 360;
        if (hue < 0) hue += 360;

        // Фиксированная насыщенность и яркость
        double saturation = 0.65;  // 0 = серый … 1 = полная насыщенность
        double brightness = 0.85;  // 0 = чёрный … 1 = полный свет

        // Возвращаем готовый цвет
        return Color.hsb(hue, saturation, brightness);
    }

    private void drawBand(GraphicsContext gc, MusicBand mb) {
        double x = 250 + mb.getCoordinates().getX() * 0.25;
        double y = 250 - mb.getCoordinates().getY() * 0.25;
        double r = mb.getNumberOfParticipants() * 0.25;
        Color color = colorFromId(mb.getOwnerId());
        gc.setFill(color);
        gc.fillOval(x - r, y - r, r*2, r*2);
    }

    public void drawObject(MusicBand mb) {
        DrawableBand db = new DrawableBand(mb);
        items.add(db);

        double durationSec = 3.0;

        tl = new Timeline(
                new KeyFrame(Duration.ZERO,   new KeyValue(db.alphaProperty(), 0)),
                new KeyFrame(Duration.seconds(durationSec), new KeyValue(db.alphaProperty(), 1))
        );
        tl.play();
    }

    public void eraseObject(MusicBand mb) {
        DrawableBand db = new DrawableBand(mb);
        int DbIndex = items.indexOf(db);

        if (DbIndex == -1) {return;}

        DrawableBand foundDb = items.get(DbIndex);

        double durationSec = 3.0;

        Timeline tl = new Timeline(
                new KeyFrame(Duration.ZERO,   new KeyValue(foundDb.alphaProperty(), 1)),
                new KeyFrame(Duration.seconds(durationSec), new KeyValue(foundDb.alphaProperty(), 0))
        );
        tl.play();

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        items.remove(DbIndex);
    }

    private void onCanvasClick(MouseEvent ev) {
        for (Iterator<DrawableBand> it = items.iterator(); it.hasNext(); ) {
            MusicBand mb = it.next().getBand();
            double sampleCanvasX = 250 + mb.getCoordinates().getX() * 0.25;
            double sampleCanvasY = 250 - mb.getCoordinates().getY() * 0.25;
            double sampleRadius = mb.getNumberOfParticipants() * 0.25;
            double dx = ev.getX() - sampleCanvasX;
            double dy = ev.getY() - sampleCanvasY;
            if (Math.hypot(dx, dy) <= sampleRadius) {
                FXMLLoader loader = new FXMLLoader(Main.class.getResource("infoBand.fxml"), Main.getLocale());
                Parent root;
                try {
                    root = loader.load();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                InfoBandController infoCtrl = loader.getController();
                infoCtrl.setMusicBand(mb);
                Stage owner = (Stage) canvas
                        .getScene()
                        .getWindow();
                Stage dialog = new Stage();
                dialog.initOwner(owner);
                dialog.initModality(Modality.WINDOW_MODAL);
                dialog.setScene(new Scene(root));
                dialog.showAndWait();
                return;
            }
        }
    }
}
