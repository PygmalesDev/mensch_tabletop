package de.uniks.pmws2324.ludo.controller;

import de.uniks.pmws2324.ludo.App;
import de.uniks.pmws2324.ludo.model.Cone;
import de.uniks.pmws2324.ludo.model.Player;
import de.uniks.pmws2324.ludo.model.Position;
import de.uniks.pmws2324.ludo.service.GameService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import static de.uniks.pmws2324.ludo.Constants.*;
import static de.uniks.pmws2324.ludo.Constants.GAME_STATE.*;
import static java.lang.Thread.sleep;

public class IngameController extends Controller {
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
    Label stateLabel;
    @FXML
    Group playerNames;
    @FXML
    ImageView backgroundAnimationsImageView;
    @FXML
    Label infoLabel;
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

        this.conesContext = this.conesCanvas.getGraphicsContext2D();
        this.diceContext = this.diceCanvas.getGraphicsContext2D();
        this.uiContext = this.uiCanvas.getGraphicsContext2D();
        this.mouseInteractionCanvas.setOnMouseMoved(this::mouseMovedEvent);
        this.mouseInteractionCanvas.setOnMouseClicked(this::mouseClickedEvent);
        this.diceThrowButton.setOnMouseClicked(this::throwButtonClickedEvent);
        this.diceThrowButton.setVisible(false);

        this.drawDices();
        this.drawPlayerRotationUI();
        this.setPlayerNames();

        Thread drawConesThread = new Thread(new TimerThread());
        drawConesThread.start();
    }

    /**
     * Calls the {@link GameService#calculateTrowOutcome()} to throw the dice and draws new dice values on screen.
     */
    private void throwButtonClickedEvent(MouseEvent mouseEvent) {
        this.gameService.calculateTrowOutcome();
        this.drawDices();
        this.drawPlayerRotationUI();
        this.diceThrowButton.setVisible(false);
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
     * Draws an outline around cones when mouse is hovered over.
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

    /**
     * Shows the throw button with the current player's colored dice.
     */
    private void showThrowButtonForCurrentPlayer() {
        this.diceThrowButton.setGraphic(
                new ImageView(DICE_THROW_BUTTON_URL + this.gameService.getCurrentPlayer().getPlayerColor() + ".png"));
        this.diceThrowButton.setVisible(true);
    }

    /**
     * Draws the cones on the game board and the throw button if it is visible.
     */
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
        this.stateLabel.setText(this.gameService.getGameState().toString().replace("_", " "));
    }

    /**
     * Draws a colored dice with the player's last dice value.
     */
    private void drawDices() {
        this.diceContext.clearRect(0, 0, this.conesCanvas.getWidth(), this.conesCanvas.getHeight());
        this.gameService.getPlayers().forEach(player -> {
            int[] coords =  DICE_COORDS.get(player.getPlayerColor());
            this.diceContext.drawImage(
                    new Image(DICE_URL + player.getDiceValue() + "_" + player.getPlayerColor() + ".png"),
                    coords[0], coords[1]);
        });
    }

    /**
     * Draws the current player rotation. Lights up the cone of the current player.
     */
    private void drawPlayerRotationUI() {
        this.uiContext.clearRect(0, 0, this.conesCanvas.getWidth(), this.conesCanvas.getHeight());
        Iterator<Player> playerIterator = this.gameService.getPlayerRotation().stream().iterator();
        int amount = this.gameService.getPlayerAmount();

        for (int i = 0; i < amount; i++) {
            Player player = playerIterator.next();
            if (player == this.gameService.getCurrentPlayer())
                this.uiContext.drawImage(new Image(CONE_UI_SELECTED_URL + player.getPlayerColor() + ".png"),
                        300 + i*64 + (192 - 64*amount), 12);
            else this.uiContext.drawImage(new Image(CONE_UI_URL + player.getPlayerColor() + ".png"),
                    300 + i*64 + (192 - 64*amount), 12);
        }
    }

    /**
     * Creates a thread for playing the throw out animation.
     */
    private void playThrowoutAnimation() {
        Thread throwoutAnimationThread = new Thread(new AnimateThrowOutThread());
        throwoutAnimationThread.start();
    }

    /**
     * Sets up the players' name on labels.
     */
    private void setPlayerNames() {
        List<Node> playerNameLabels = this.playerNames.getChildren();
        Iterator<Player> playerIterator = this.gameService.getPlayers().iterator();
        for (Node node : playerNameLabels) {
            Label label = (Label) node;
            if (playerIterator.hasNext()) label.setText(playerIterator.next().getName());
            else label.setText("");

        }
    }

    /**
     * Switches to the game over screen.
     */
    private void switchToGameOverScreen() {
        this.app.changeScene("GameOver");
    }

    // ------------------------- SUPPORT CLASSES -------------------------

    /**
     * A thread to play the throw out animation on the screen.
     */
    private class AnimateThrowOutThread implements Runnable {
        @Override
        public void run() {
            backgroundAnimationsImageView.setImage(new Image(THROWOUT_ANIM_URL));
            backgroundAnimationsImageView.setVisible(true);
            try {
                sleep(3600);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            backgroundAnimationsImageView.setVisible(false);
        }
    }

    /**
     * A thread for drawing cones on the board, drawing the throw button and
     * checking the winning conditions to switch to the game over screen.
     */
    private class TimerThread implements Runnable {
        @Override
        public void run() {
            while (!gameService.getGameState().equals(WIN)) {
                Platform.runLater(IngameController.this::drawCones);
                if (gameService.checkWinningConditions())
                    Platform.runLater(IngameController.this::switchToGameOverScreen);
                try { sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * A thread that animates a movement of the cone. <p>
     * Calls {@link GameService#moveCone(Cone)} to move the one step at a time. <p>
     * After the move is finished, calls {@link GameService#checkCollisionWithEnemyCone(Cone)} to play the throw out animation,
     * {@link GameService#checkFinalBlockedState(Cone)} to calculate new final blocked state for the player and
     * {@link GameService#continueGame()} to pass the move to the next player.
     */
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

            if (gameService.checkCollisionWithEnemyCone(this.cone))
                playThrowoutAnimation();

            this.cone.setVisible(true);
            animationsImageView.setVisible(false);

            gameService.checkFinalBlockedState(cone);
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
                        sleep(1200);
                        animationsImageView.setVisible(false);
                    } else sleep(750);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                gameService.moveCone(cone);
                iter--;
            }
        }
    }
}
