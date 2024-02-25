package de.uniks.pmws2324.ludo.controller;

import de.uniks.pmws2324.ludo.App;
import de.uniks.pmws2324.ludo.service.GameService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.util.List;
import java.util.function.Predicate;

import static de.uniks.pmws2324.ludo.Constants.*;

public class SetupController extends Controller {
    private int playerAmount;
    private List<PlayerSignSubController> playerSigns;
    @FXML
    Pane setupControl;
    @FXML
    ImageView zoomImageView;
    @FXML
    ImageView backgroundImageView;
    @FXML
    ImageView whosplayingImageView;
    @FXML
    ImageView playerAmountImageView;
    @FXML
    Button leftButton;
    @FXML
    Button rightButton;
    @FXML
    Button beginButton;
    @FXML
    GridPane playerSignsGrid;

    public SetupController(App app, GameService gameService) {
        super(app, gameService);
    }

    @Override
    public void init() {
        final FXMLLoader loader = new FXMLLoader(App.class.getResource("view/Setup.fxml"));
        loader.setControllerFactory(c -> this);
        try {
            this.parent = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.playerAmount = 2;
        this.playerSigns = List.of(
                new PlayerSignSubController(this.app, this.gameService, 1),
                new PlayerSignSubController(this.app, this.gameService, 2),
                new PlayerSignSubController(this.app, this.gameService, 3),
                new PlayerSignSubController(this.app, this.gameService, 4)
        );
        for (PlayerSignSubController pssc : this.playerSigns) {
            this.subControllers.add(pssc);
            pssc.init();
        }
        this.playerSignsGrid.add(this.playerSigns.get(0).render(), 0, 0);
        this.playerSignsGrid.add(this.playerSigns.get(1).render(), 1, 0);
        this.playerSignsGrid.add(this.playerSigns.get(2).render(), 0, 1);
        this.playerSignsGrid.add(this.playerSigns.get(3).render(), 1, 1);
        this.playerSigns.get(2).setVisible(false);
        this.playerSigns.get(3).setVisible(false);

        this.leftButton.setVisible(false);

        this.leftButton.setOnMouseClicked(mouseEvent -> {
            this.playerAmount -= 1;
            this.setPlayerAmountUI();
        });
        this.rightButton.setOnMouseClicked(mouseEvent -> {
            this.playerAmount += 1;
            this.setPlayerAmountUI();
        });

        this.setPlayerAmountUI();
        this.beginButton.setGraphic(SETUP_BEGIN_BUTTON_INACTIVE);
        this.beginButton.setOnMouseClicked(mouseEvent -> {
            if (!this.beginButton.isDisabled()) {
                this.gameService.setPlayerNames(this.getPlayerNicknames());
                this.app.initializeGame(this.playerAmount);
            }
        });
        this.beginButton.setDisable(true);

        Thread conditionsThread = new Thread(new ConditionsThread());
        conditionsThread.start();
    }

    private void setPlayerAmountUI() {
        this.leftButton.setVisible(this.playerAmount != 2);
        this.rightButton.setVisible(this.playerAmount != 4);
        for (PlayerSignSubController pssc : this.playerSigns) { pssc.setVisible(false); }
        for (int i = 0; i < this.playerAmount; i++) {
            this.playerSigns.get(i).setVisible(true);
        }

        this.playerAmountImageView.setImage(new Image(SETUP_PLAYER_DICE_VALUE_URL + this.playerAmount + ".png"));
    }

    private List<String> getPlayerNicknames() {
        return playerSigns.stream()
                .filter(PlayerSignSubController::isVisible)
                .map(PlayerSignSubController::getPlayerName)
                .filter(Predicate.not(String::isBlank)).toList();
    }

    private class ConditionsThread implements Runnable {
        @Override
        public void run() {
            while (app.getSceneTitle().equals("Setup")) {
                List<String> enteredNicknames = getPlayerNicknames();
                Platform.runLater(() -> {
                    if (enteredNicknames.size() == playerAmount) {
                        beginButton.setGraphic(SETUP_BEGIN_BUTTON);
                        beginButton.setDisable(false);
                    } else {
                        beginButton.setGraphic(SETUP_BEGIN_BUTTON_INACTIVE);
                        beginButton.setDisable(true);
                    }
                });
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
