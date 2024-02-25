package de.uniks.pmws2324.ludo.controller;

import de.uniks.pmws2324.ludo.App;
import de.uniks.pmws2324.ludo.model.Cone;
import de.uniks.pmws2324.ludo.model.Player;
import de.uniks.pmws2324.ludo.model.Position;
import de.uniks.pmws2324.ludo.service.GameService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Iterator;
import java.util.Objects;

import static de.uniks.pmws2324.ludo.Constants.*;
import static de.uniks.pmws2324.ludo.Constants.GAME_STATE.*;

public class IngameController extends Controller {
    private Instant instant;

    private GraphicsContext backgroundContext;
    private GraphicsContext conesContext;
    private GraphicsContext diceContext;
    private GraphicsContext uiContext;
    @FXML
    Canvas backgroundCanvas;
    @FXML
    Canvas conesCanvas;
    @FXML
    Canvas diceCanvas;
    @FXML
    Canvas mouseInteractionCanvas;
    @FXML
    Canvas uiCanvas;
    @FXML
    ImageView animationsImageView;
    @FXML
    ImageView zoomImageView;
    @FXML
    ImageView backgroundAnimationsImageView;
    @FXML
    Label infoLabel;
    @FXML
    TextArea informationTextArea;
    @FXML
    Button diceThrowButton;

    public IngameController(App app, GameService gameService) {
        super(app, gameService);
    }
    @Override
    public void init() {
        final FXMLLoader loader = new FXMLLoader(App.class.getResource("view/Ingame.fxml"));
        loader.setControllerFactory(c -> this);
        try {
            this.parent = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.instant = Instant.now();

        this.backgroundContext = this.backgroundCanvas.getGraphicsContext2D();
        this.conesContext = this.conesCanvas.getGraphicsContext2D();
        this.diceContext = this.diceCanvas.getGraphicsContext2D();
        this.uiContext = this.uiCanvas.getGraphicsContext2D();
        this.mouseInteractionCanvas.setOnMouseMoved(this::mouseMovedEvent);
        this.mouseInteractionCanvas.setOnMouseClicked(this::mouseClickedEvent);
        this.diceThrowButton.setOnMouseClicked(this::throwButtonClickedEvent);
        this.diceThrowButton.setVisible(false);

        this.drawBackground();
        this.drawDices();
        this.drawPlayerRotationUI();

        Thread drawConesThread = new Thread(new DrawConesThread());
        drawConesThread.start();
    }

    private void throwButtonClickedEvent(MouseEvent mouseEvent) {
        int outcome = this.gameService.calculateTrowOutcome();
        int diceVal = this.gameService.getLastDiceValue();

        switch (outcome) {
            case 0 -> this.informationTextArea.setText("Player throws " + diceVal + "!");
            case 1 -> this.informationTextArea.setText("One of the players already has " + diceVal + "!");
            case 2 -> this.informationTextArea.setText("Player throws " + diceVal + " and moves again!");
        }
        this.drawDices();
        this.drawPlayerRotationUI();
        this.diceThrowButton.setVisible(false);
    }

    private void showThrowButtonForCurrentPlayer() {
        this.diceThrowButton.setGraphic(
                new ImageView(DICE_THROW_BUTTON_URL + this.gameService.getCurrentPlayer().getPlayerColor() + ".png"));
        this.diceThrowButton.setVisible(true);
    }

    /**
     * Sets up the logic for clicking on the cones.
     */
    private void mouseClickedEvent(MouseEvent mouseEvent) {
        Player current = this.gameService.getCurrentPlayer();
        switch (this.gameService.getGameState()) {
            case CHOOSE_ON_BASE ->
                    current.getConesOnBase().forEach(cone -> {
                            Position position = cone.getPosition();
                            if (position.isInRadius((int) mouseEvent.getX(), (int) mouseEvent.getY()))
                                if (position.getLocalState() == -1) {
                                    if (this.gameService.moveConeToStartingPosition(cone))
                                        this.playThrowoutAnimation();
                                    this.gameService.continueGame();
                                }
                    });
            case CHOOSE_ON_FIELD ->
                    current.getConesOnField().forEach(cone -> {
                        Position position = cone.getPosition();
                        if (position.isInRadius((int) mouseEvent.getX(), (int) mouseEvent.getY())
                                && this.gameService.isConeMovable(cone)) {
                            Thread animateConeMovement = new Thread(new AnimateConeMovementThread(cone, position));
                            animateConeMovement.start();
                        }
                    });
        }
    }

    /**
     * Draws an outline around cones when mouse is hovered over them.
     */
    private void mouseMovedEvent(MouseEvent mouseEvent) {
        Player current = this.gameService.getCurrentPlayer();
        switch (this.gameService.getGameState()) {
            case CHOOSE_ON_BASE ->
                current.getConesOnBase().forEach(cone -> {
                    Position position = cone.getPosition();
                    if (position.isInRadius((int) mouseEvent.getX(), (int) mouseEvent.getY()))
                        cone.setCurrentImage("SELECTED");
                    else
                        cone.setCurrentImage("NORMAL");
                });
            case CHOOSE_ON_FIELD ->
                current.getConesOnField().forEach(cone -> {
                    Position position = cone.getPosition();
                    if (position.isInRadius((int) mouseEvent.getX(), (int) mouseEvent.getY())
                            && this.gameService.isConeMovable(cone))
                        cone.setCurrentImage("SELECTED");
                    else
                        cone.setCurrentImage("NORMAL");
                });
        }
    }

    private void drawBackground() {
        this.backgroundContext.drawImage(INGAME_BACKGROUND_IMAGE, 0, 0);
    }

    private void drawCones() {
        this.conesContext.clearRect(0, 0, this.conesCanvas.getWidth(), this.conesCanvas.getHeight());
        GAME_STATE state = gameService.getGameState();

        this.gameService.getPlayers().forEach(player ->
                player.getCones().forEach(cone -> {
                    Position pos = cone.getPosition();
                    if (!Objects.equals(player, this.gameService.getCurrentPlayer())) cone.setCurrentImage("NORMAL");
                    if (state.equals(DECISION)) cone.setCurrentImage("NORMAL");

                    if (cone.isVisible()) this.conesContext.drawImage(cone.getCurrentImage(),
                                    pos.getX() - CONE_OFFSET_X,
                                    pos.getY() - CONE_OFFSET_Y);}));

        if (state.equals(PLAY) || state.equals(PREPARATION))
                Platform.runLater(this::showThrowButtonForCurrentPlayer);

        Platform.runLater(() -> {
            long duration = Duration.between(this.instant, Instant.now()).toSeconds();
            this.gameService.duration = duration;
            this.infoLabel.setText("Time: " + duration);
        });
    }

    private void drawDices() {
        this.diceContext.clearRect(0, 0, this.conesCanvas.getWidth(), this.conesCanvas.getHeight());
        this.gameService.getPlayers().forEach(player -> {
            int[] coords =  DICE_COORDS.get(player.getPlayerColor());
            this.diceContext.drawImage(
                    new Image(DICE_URL + player.getDiceValue() + "_" + player.getPlayerColor() + ".png"),
                    coords[0], coords[1]);
        });
    }

    private void drawPlayerRotationUI() {
        this.uiContext.clearRect(0, 0, this.conesCanvas.getWidth(), this.conesCanvas.getHeight());
        Iterator<Player> playerIterator = this.gameService.getPlayerRotation().stream().iterator();
        int amount = this.gameService.getPlayerAmount();

        for (int i = 0; i < amount; i++) {
            Player player = playerIterator.next();
            if (player == this.gameService.getCurrentPlayer())
                this.uiContext.drawImage(new Image(CONE_UI_SELECTED_URL + player.getPlayerColor() + ".png"),
                        300 + i*64, 12);
            else this.uiContext.drawImage(new Image(CONE_UI_URL + player.getPlayerColor() + ".png"),
                    300 + i*64, 12);
        }
        this.uiContext.drawImage(INFO_TABLE_IMAGE, 572, 700);
    }

    private void playThrowoutAnimation() {
        Thread throwoutAnimationThread = new Thread(new AnimateBackgroundThread());
        throwoutAnimationThread.start();
    }

    /**
     * Support classes for playing animations in a separate thread
     */

    private class AnimateBackgroundThread implements Runnable {
        @Override
        public void run() {
            backgroundAnimationsImageView.setImage(new Image(THROWOUT_ANIM_URL));
            backgroundAnimationsImageView.setVisible(true);
            try {
                Thread.sleep(3300);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            backgroundAnimationsImageView.setVisible(false);
        }
    }

    private class DrawConesThread implements Runnable {
        @Override
        public void run() {
            while (!gameService.getGameState().equals(WIN)) {
                Platform.runLater(() -> drawCones());
                //drawCones();
                try { Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private class AnimateConeMovementThread implements Runnable {
        private final Position position;
        private final Cone cone;
        public AnimateConeMovementThread(Cone cone, Position pos) {
            this.position = pos;
            this.cone = cone;
        }
        @Override
        public void run() {
            int iter = gameService.getLastDiceValue();
            gameService.setGameState(PAUSE);
            this.moveCone(iter);

            if (gameService.isCollidedWithEnemyCone(this.cone))
                playThrowoutAnimation();

            this.cone.setVisible(true);
            animationsImageView.setVisible(false);

            gameService.checkFinalPosition(cone);
            gameService.checkWinningConditions();
            gameService.continueGame();
        }
        private void moveCone(int iter) {
            this.cone.setVisible(false);
            String color = this.cone.getPlayer().getPlayerColor();
            while (iter != 0) {
                switch (cone.getMovingDirection()) {
                    case CONE_DIRECTION_UP ->  {
                        animationsImageView.setImage(new Image(CONE_DT_ANIM_URL + color + ".gif" ));
                        animationsImageView.setX(position.getX() - 50);
                        animationsImageView.setY(position.getY() - CONE_OFFSET_Y - 70);
                    }
                    case CONE_DIRECTION_RIGHT -> {
                        animationsImageView.setImage(new Image(CONE_LR_ANIM_URL + color + ".gif" ));
                        animationsImageView.setX(position.getX() - CONE_OFFSET_X - 30);
                        animationsImageView.setY(position.getY() - CONE_OFFSET_Y - 65);
                    }
                    case CONE_DIRECTION_LEFT -> {
                        animationsImageView.setImage(new Image(CONE_RL_ANIM_URL + color + ".gif" ));
                        animationsImageView.setX(position.getX() - CONE_OFFSET_X - 85);
                        animationsImageView.setY(position.getY() - CONE_OFFSET_Y - 65);
                    }
                    case CONE_DIRECTION_DOWN -> {
                        animationsImageView.setImage(new Image(CONE_TD_ANIM_URL + color + ".gif" ));
                        animationsImageView.setX(position.getX() - CONE_OFFSET_X + 10);
                        animationsImageView.setY(position.getY() - CONE_OFFSET_Y - 20);
                    }
                }
                animationsImageView.setVisible(true);
                try {
                    if (iter == 1) {
                        Thread.sleep(1200);
                        animationsImageView.setVisible(false);
                    } else Thread.sleep(750);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                gameService.moveCone(cone);
                iter--;
            }
        }
    }
}
