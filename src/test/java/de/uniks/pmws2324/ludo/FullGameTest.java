package de.uniks.pmws2324.ludo;

import de.uniks.pmws2324.ludo.model.Cone;
import de.uniks.pmws2324.ludo.model.Player;
import de.uniks.pmws2324.ludo.model.Position;
import de.uniks.pmws2324.ludo.service.GameService;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static de.uniks.pmws2324.ludo.Constants.*;

/**
 * The testing class, which tests the UI functionality along with the game process.
 * There are three different methods for testing the groups of 2 to 4 players.
 */

class FullGameTest extends ApplicationTest {
    private Stage stage;
    private App app;
    private GameService gameService;
    private double winX, winY;
    private final List<String> testPlayerNames = List.of("Sanbo", "Fyll");

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        this.app = new App();
        this.app.start(this.stage);
        this.gameService = this.app.getGameService();

        this.winX = this.stage.getX();
        this.winY = this.stage.getY();
    }

    @Test
    public void testGame() {
        // Test the start of the game
        sleep(1000);
        assertEquals("Menu", this.app.getSceneTitle());

        // Switch to the game settings
        clickOn("#playButton");
        assertEquals("Setup", this.app.getSceneTitle());

        // Test if the beginButton is inactive for if player names are not filled in
        sleep(1000);
        Button beginButton = lookup("#beginButton").query();
        GridPane signsPane = lookup("#playerSignsGrid").query();
        List<Node> playerSigns = signsPane.getChildren();

        assertTrue(beginButton.isDisabled());
        clickOn("#rightButton");
        sleep(500);
        clickOn("#rightButton");
        sleep(500);
        assertTrue(beginButton.isDisabled());
        clickOn("#leftButton");
        sleep(500);
        clickOn("#leftButton");
        sleep(500);
        assertTrue(beginButton.isDisabled());

        Iterator<String> testNamesIterator = this.testPlayerNames.stream().iterator();

        // Fill in the names and begin game
        for (Node pane : playerSigns) {
            Pane current = (Pane) pane;
            if (pane.isVisible()) {
                TextField nameField = (TextField) current.getChildren().get(1);
                clickOn(nameField).write(testNamesIterator.next());
                sleep(500);
            }
        }
        sleep(100);
        assertFalse(beginButton.isDisabled());
        clickOn(beginButton);

        // Play the game
        assertEquals("Ingame", this.app.getSceneTitle());
        Button diceThrowButton = lookup("#diceThrowButton").query();

        while (!this.gameService.getGameState().equals(GAME_STATE.WIN)) {
            GAME_STATE state = this.gameService.getGameState();
            Player current = this.gameService.getCurrentPlayer();

            switch (state) {
                case PLAY, PREPARATION -> {
                    if (diceThrowButton.isVisible())
                        clickOn(diceThrowButton);
                }
                case CHOOSE_ON_BASE -> {
                    Cone toPick = current.getConesOnBase().get(0);
                    int coneX = toPick.getPosition().getX();
                    int coneY = toPick.getPosition().getY();
                    clickOn(this.winX + coneX, this.winY + coneY);
                }
                case CHOOSE_ON_FIELD -> {
                    List<Position> positions = current.getConesOnField().stream()
                            .map(Cone::getPosition).sorted(Comparator.comparing(Position::getLocalState).reversed())
                            .toList();
                    Iterator<Position> posIter = positions.stream().iterator();
                    while (posIter.hasNext()) {
                        Cone toPick = posIter.next().getCone();
                        if (this.gameService.isConeMovable(toPick)) {
                            int coneX = toPick.getPosition().getX();
                            int coneY = toPick.getPosition().getY();
                            clickOn(this.winX + coneX, this.winY + coneY);
                            break;
                        }
                    }
                }
            }
            sleep(300);
        }
    }
}