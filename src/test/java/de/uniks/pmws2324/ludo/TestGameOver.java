package de.uniks.pmws2324.ludo;


import de.uniks.pmws2324.ludo.model.Cone;
import de.uniks.pmws2324.ludo.model.Player;
import de.uniks.pmws2324.ludo.service.GameService;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Utility class (not a part of the project), used to test the correct switch to the end game screen
 * after someone wins the game.
 */
public class TestGameOver extends ApplicationTest {
    private Stage stage;
    private App app;
    private GameService gameService;
    Player redPlayer;
    Cone cone1, cone2, cone3, cone4;

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        this.app = new App();
        this.app.start(this.stage);
        this.gameService = this.app.getGameService();
    }

    @Test
    public void testGameOver() {
        this.initializeGame();
        this.gameService.setPlayerTurn(this.redPlayer);
        this.gameService.moveConeToStartingPosition(this.cone1);
        this.gameService.simulateConeStepsLocal(this.cone1, 40);
        this.gameService.moveConeToStartingPosition(this.cone2);
        this.gameService.simulateConeStepsLocal(this.cone2, 41);
        this.gameService.moveConeToStartingPosition(this.cone3);
        this.gameService.simulateConeStepsLocal(this.cone3, 42);
        this.gameService.moveConeToStartingPosition(this.cone4);
        this.gameService.simulateConeStepsLocal(this.cone4, 43);
        sleep(1000);
        assertTrue(this.gameService.checkWinningConditions());

        sleep(1000);
        assertEquals("GameOver", this.app.getSceneTitle());
    }

    public void initializeGame() {
        if (!this.app.getSceneTitle().equals("Ingame")) {
            this.gameService.setPlayerNames(Constants.TEST_PLAYER_NAMES);
            Platform.runLater(() -> this.app.initializeGame(2));
            sleep(1000);

            List<Player> players = this.gameService.getPlayers();
            this.redPlayer = players.get(0);
            List<Cone> cones = this.redPlayer.getCones();
            this.cone1 = cones.get(0);
            this.cone2 = cones.get(1);
            this.cone3 = cones.get(2);
            this.cone4 = cones.get(3);
        }
    }

}
