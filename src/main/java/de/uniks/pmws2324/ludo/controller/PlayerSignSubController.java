package de.uniks.pmws2324.ludo.controller;

import de.uniks.pmws2324.ludo.App;
import de.uniks.pmws2324.ludo.service.GameService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

import java.io.IOException;

import static de.uniks.pmws2324.ludo.Constants.*;

public class PlayerSignSubController extends Controller {
    private final int playerNumber;
    @FXML
    Pane signControl;
    @FXML
    ImageView backgroundImageView;
    @FXML
    TextField playerNameField;

    public PlayerSignSubController(App app, GameService gameService, int playerNumber) {
        super(app, gameService);
        this.playerNumber = playerNumber;
    }

    @Override
    public void init() {
        final FXMLLoader loader = new FXMLLoader(App.class.getResource("view/PlayerSign.fxml"));
        loader.setControllerFactory(c -> this);
        try {
            this.parent = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.backgroundImageView.setImage(new Image(SETUP_PLAYER_SIGN_URL + this.playerNumber + ".png"));
    }

    public void setVisible(boolean cond) {
        this.signControl.setVisible(cond);
    }

    public boolean isVisible() {
        return this.signControl.isVisible();
    }

    public String getPlayerName() {
        return this.playerNameField.getText();
    }
}
