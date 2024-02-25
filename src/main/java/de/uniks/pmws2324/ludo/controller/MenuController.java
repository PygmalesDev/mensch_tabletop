package de.uniks.pmws2324.ludo.controller;

import de.uniks.pmws2324.ludo.App;
import de.uniks.pmws2324.ludo.service.GameService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;

import java.io.IOException;
import static de.uniks.pmws2324.ludo.Constants.*;

public class MenuController extends Controller {
    @FXML
    ImageView backgroundImageView;
    @FXML
    ImageView gamenameImageView;
    @FXML
    ImageView sloganImageView;
    @FXML
    Button playButton;
    @FXML
    Button rulesButton;
    @FXML
    Button quitButton;
    public MenuController(App app, GameService gameService) {
        super(app, gameService);
    }

    @Override
    public void init() {
        final FXMLLoader loader = new FXMLLoader(App.class.getResource("view/Menu.fxml"));
        loader.setControllerFactory(c -> this);
        try {
            this.parent = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.playButton.setGraphic(PLAY_BUTTON_URL);
        this.rulesButton.setGraphic(RULES_BUTTON_URL);
        this.quitButton.setGraphic(QUIT_BUTTON_URL);

        this.playButton.setOnMouseEntered(mouseEvent ->
            this.playButton.setGraphic(PLAY_BUTTON_SELECTED_URL));
        this.playButton.setOnMouseExited(mouseEvent ->
            this.playButton.setGraphic(PLAY_BUTTON_URL));
        this.rulesButton.setOnMouseEntered(mouseEvent ->
                this.rulesButton.setGraphic(RULES_BUTTON_SELECTED_URL));
        this.rulesButton.setOnMouseExited(mouseEvent ->
                this.rulesButton.setGraphic(RULES_BUTTON_URL));
        this.quitButton.setOnMouseEntered(mouseEvent ->
                this.quitButton.setGraphic(QUIT_BUTTON_SELECTED_URL));
        this.quitButton.setOnMouseExited(mouseEvent ->
                this.quitButton.setGraphic(QUIT_BUTTON_URL));

        this.playButton.setOnMouseClicked(mouseEvent -> {
            this.app.changeScene(new SetupController(this.app, this.gameService), "Setup");
        });
    }
}
