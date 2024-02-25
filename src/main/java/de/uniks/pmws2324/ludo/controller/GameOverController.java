package de.uniks.pmws2324.ludo.controller;

import de.uniks.pmws2324.ludo.App;
import de.uniks.pmws2324.ludo.model.Player;
import de.uniks.pmws2324.ludo.service.GameService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.util.Iterator;
import java.util.Objects;

import static de.uniks.pmws2324.ludo.Constants.*;

public class GameOverController extends Controller {
    @FXML
    ImageView winnerImageView;
    @FXML
    Label winnerNameLabel;
    @FXML
    Group loserGroup;
    @FXML
    Group loserPlayerNames;
    @FXML
    Button quitButton;

    public GameOverController(App app, GameService gameService) {
        super(app, gameService);
    }

    @Override
    public void init() {
        final FXMLLoader loader = new FXMLLoader(App.class.getResource("view/GameOver.fxml"));
        loader.setControllerFactory(c -> this);
        try {
            this.parent = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.quitButton.setOnMouseClicked(mouseEvent -> this.app.changeScene("Menu"));

        this.winnerNameLabel.setText(this.gameService.getCurrentPlayer().getName());
        this.winnerImageView.setImage(new Image(WINNER_CONE_URL +
                this.gameService.getCurrentPlayer().getPlayerColor() + ".png"));

        Iterator<String> namesIterator = this.gameService.getPlayers().stream()
                .filter(player -> !Objects.equals(this.gameService.getCurrentPlayer(), player))
                .map(Player::getName).iterator();
        this.loserPlayerNames.getChildren().forEach(
                node -> {
                    if (namesIterator.hasNext()) ((Label) node).setText(namesIterator.next());
                    else ((Label) node).setText("");

                });

        Iterator<String> colorsIterator = this.gameService.getPlayers().stream()
                .filter(player -> !Objects.equals(this.gameService.getCurrentPlayer(), player))
                .map(Player::getPlayerColor).iterator();
        Iterator<Node> loserIterator = this.loserGroup.getChildren().stream().iterator();
        while (loserIterator.hasNext()) {
            if (colorsIterator.hasNext()) ((ImageView) loserIterator.next())
                    .setImage(new Image(LOSER_CONE_URL + colorsIterator.next() + ".png"));
            else ((ImageView) loserIterator.next())
                    .setImage(null);
        }
    }
}
