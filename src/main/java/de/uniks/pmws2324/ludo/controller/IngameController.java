package de.uniks.pmws2324.ludo.controller;

import de.uniks.pmws2324.ludo.App;
import de.uniks.pmws2324.ludo.model.Cone;
import de.uniks.pmws2324.ludo.model.Player;
import de.uniks.pmws2324.ludo.model.Position;
import de.uniks.pmws2324.ludo.service.GameService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.util.Objects;

import static de.uniks.pmws2324.ludo.Constants.*;
public class IngameController extends Controller {
    private GraphicsContext backgroundContext;
    private GraphicsContext conesContext;
    @FXML
    Canvas backgroundCanvas;
    @FXML
    Canvas conesCanvas;
    @FXML
    Canvas mouseInteractionCanvas;
    @FXML
    ImageView animationsImageView;
    @FXML
    Label diceLabel;

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

        this.backgroundContext = this.backgroundCanvas.getGraphicsContext2D();
        this.conesContext = this.conesCanvas.getGraphicsContext2D();
        this.mouseInteractionCanvas.setOnMouseMoved(this::mouseMovedEvent);
        this.mouseInteractionCanvas.setOnMouseClicked(this::mouseClickedEvent);

        this.drawBackground();
        Thread drawConesThread = new Thread(new DrawConesThread());
        drawConesThread.start();
    }

    /**
     * Sets up the logic for clicking on the cones.
     */
    private void mouseClickedEvent(MouseEvent mouseEvent) {
        if (this.gameService.getGameState().equals(GAME_STATE_PLAY)) {
            Player currentPlayer = this.gameService.getCurrentPlayer();
            currentPlayer.getCones().forEach(cone -> {
                Position position = cone.getPosition();
                if (position.isInRadius((int) mouseEvent.getX(), (int) mouseEvent.getY()))
                    if (position.getLocalState() == -1) {
                        this.gameService.moveConeToStart(cone);
                        this.gameService.setNextPlayer();
                    } else {
                        this.gameService.throwDice();
                        this.showDiceValue();
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
        if (this.gameService.getGameState().equals(GAME_STATE_PLAY)) {
            Player currentPlayer = this.gameService.getCurrentPlayer();
            currentPlayer.getCones().forEach(cone -> {
                Position position = cone.getPosition();
                    if (position.isInRadius((int) mouseEvent.getX(), (int) mouseEvent.getY()))
                        cone.setImage(CONDITION_SELECTED);
                    else
                        cone.setImage();
            });
        }
    }

    private void drawBackground() {
        this.backgroundContext.drawImage(INGAME_BACKGROUND_IMAGE, 0, 0);
    }

    private void drawCones() {
        this.conesContext.clearRect(0, 0, this.conesCanvas.getWidth(), this.conesCanvas.getHeight());
        this.gameService.getPlayers().forEach(player ->
                player.getCones().forEach(cone -> {
                    Position pos = cone.getPosition();
                    if (!Objects.equals(player, this.gameService.getCurrentPlayer())) cone.setImage();
                    if (cone.isVisible()) this.conesContext.drawImage(cone.getImage(),
                                    pos.getX() - CONE_OFFSET_X,
                                    pos.getY() - CONE_OFFSET_Y);
                }));
    }

    private void showDiceValue() {
        this.diceLabel.setText(this.gameService.getCurrentPlayer().getName() + " throws " + this.gameService.getDiceValue());
    }

    private class DrawConesThread implements Runnable {
        @Override
        public void run() {
            while (true) {
                drawCones();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private class AnimateConeMovementThread implements Runnable {
        private Position position;
        private Cone cone;
        public AnimateConeMovementThread(Cone cone, Position pos) {
            this.position = pos;
            this.cone = cone;
        }
        @Override
        public void run() {
            int iter = gameService.getDiceValue();
            gameService.setGameState(GAME_STATE_PAUSE);
            while (iter != 0) {
                System.out.println(position.getLocalState());
                switch (cone.getMovingDirection()) {
                    case CONE_DIRECTION_UP ->  {
                        animationsImageView.setImage(new Image(RED_CONE_DT_ANIM_URL));
                        animationsImageView.setX(position.getX() - 50);
                        animationsImageView.setY(position.getY() - CONE_OFFSET_Y - 70);
                    }
                    case CONE_DIRECTION_RIGHT -> {
                        animationsImageView.setImage(new Image(RED_CONE_LR_ANIM_URL));
                        animationsImageView.setX(position.getX() - CONE_OFFSET_X - 30);
                        animationsImageView.setY(position.getY() - CONE_OFFSET_Y - 65);
                    }
                    case CONE_DIRECTION_LEFT -> {
                        animationsImageView.setImage(new Image(RED_CONE_RL_ANIM_URL));
                        animationsImageView.setX(position.getX() - CONE_OFFSET_X - 85);
                        animationsImageView.setY(position.getY() - CONE_OFFSET_Y - 65);
                    }
                    case CONE_DIRECTION_DOWN -> {
                        animationsImageView.setImage(new Image(RED_CONE_TD_ANIM_URL));
                        animationsImageView.setX(position.getX() - CONE_OFFSET_X + 10);
                        animationsImageView.setY(position.getY() - CONE_OFFSET_Y - 20);
                    }
                }
                animationsImageView.setVisible(true);

                this.cone.setVisible(false);
                try {
                    if (iter == 1) {
                        Thread.sleep(4200);
                        animationsImageView.setVisible(false);
                    }
                    else Thread.sleep(2400);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                gameService.moveCone(cone);
                iter--;
            }
            this.cone.setVisible(true);
            gameService.setNextPlayer();
            gameService.setGameState(GAME_STATE_PLAY);
        }
    }
}
