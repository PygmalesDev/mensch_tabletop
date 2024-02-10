package de.uniks.pmws2324.ludo.controller;

import de.uniks.pmws2324.ludo.App;
import de.uniks.pmws2324.ludo.model.Player;
import de.uniks.pmws2324.ludo.model.Position;
import de.uniks.pmws2324.ludo.service.GameService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;

import java.io.IOException;

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
        this.drawCones();
    }

    private void mouseClickedEvent(MouseEvent mouseEvent) {
        Player currentPlayer = this.gameService.getCurrentPlayer();
        currentPlayer.getCones().forEach(cone -> {
            Position position = cone.getPosition();
            if (position.isInRadius((int) mouseEvent.getX(), (int) mouseEvent.getY()))
                this.gameService.moveCone(cone, currentPlayer.getStartingPosition());
        });
        drawCones();
    }

    private void mouseMovedEvent(MouseEvent mouseEvent) {
        Player currentPlayer = this.gameService.getCurrentPlayer();
        currentPlayer.getCones().forEach(cone -> {
            Position position = cone.getPosition();
            if (position.isInRadius((int) mouseEvent.getX(), (int) mouseEvent.getY()))
                cone.setImage(CONDITION_SELECTED);
            else
                cone.setImage();
        });
        drawCones();
    }

    private void drawBackground() {
        this.backgroundContext.drawImage(INGAME_BACKGROUND_IMAGE, 0, 0);
    }

    private void drawCones() {
        this.conesContext.clearRect(0, 0, this.conesCanvas.getWidth(), this.conesCanvas.getHeight());
        this.gameService.getPlayers().forEach(player ->
                player.getCones().forEach(cone -> {
                    Position pos = cone.getPosition();
                    if (cone.isVisible()) this.conesContext
                            .drawImage(cone.getImage(),
                                    pos.getX() - CONE_OFFSET_X,
                                    pos.getY() - CONE_OFFSET_Y);
                }));
    }
}
