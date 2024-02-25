package de.uniks.pmws2324.ludo;

import de.uniks.pmws2324.ludo.model.Cone;
import de.uniks.pmws2324.ludo.model.Player;
import de.uniks.pmws2324.ludo.service.GameService;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This class tests the correct behavior of the {@link GameService#throwOut(Cone)} on
 * two occasions. <p>
 * A so called "Throw-Out" is a special situation, when one of the current player's cones lands
 * on the another player's cone on the game field. <p>
 * This can occur in two different ways: when a cone is being placed on the starting position, or
 * an enemy cone stands on the tile, which the current cone will land on.
 * In both cases, the victim cone should return to the base and be placed on one of the free tiles. <p>
 * Both scenarios will be tested here.
 */
public class TestThrowOut extends ApplicationTest {
    private Stage stage;
    private App app;
    private GameService gameService;
    Player bluePlayer, redPlayer;
    Cone victimCone, hunterCone;

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        this.app = new App();
        this.app.start(this.stage);
        this.gameService = this.app.getGameService();
    }

    /**
     * Main testing method that calls all supportive methods.
     */
    @Test
    public void testThrowOuts() {
        this.initializeGameIfNeeded();

        this.testThrowOutFromStartingPosition();
        this.testThrowOutFromGameField();
    }

    /**
     * Supportive test method which tests the throw-out from the starting position.
     */
    @Test
    public void testThrowOutFromStartingPosition() {
        // For calling the method on its own
        this.initializeGameIfNeeded();

        this.gameService.setPlayerTurn(this.bluePlayer);
        this.gameService.moveConeToStartingPosition(this.victimCone);
        this.gameService.simulateConeStepsGlobal(this.victimCone, 0);
        assertEquals(0, this.victimCone.getPosition().getGlobalState());

        this.gameService.setPlayerTurn(this.redPlayer);
        this.gameService.moveConeToStartingPosition(this.hunterCone);
        this.testReturnToBase();
    }

    /**
     * Supportive test method which tests the throw-out from the position on the game field.
     */
    @Test
    public void testThrowOutFromGameField() {
        // For calling the method on its own
        this.initializeGameIfNeeded();

        // Some random positions to test on
        Iterator<Integer> positions = Stream.of(5, 7, 12, 22, 37).iterator();

        while (positions.hasNext()) {
            int throwOutPosition = positions.next();
            this.gameService.setPlayerTurn(this.bluePlayer);
            this.gameService.moveConeToStartingPosition(this.victimCone);
            this.gameService.simulateConeStepsGlobal(this.victimCone, throwOutPosition);
            assertEquals(throwOutPosition, this.victimCone.getPosition().getGlobalState());

            this.gameService.setPlayerTurn(this.redPlayer);
            this.gameService.moveConeToStartingPosition(this.hunterCone);
            this.gameService.simulateConeStepsGlobal(this.hunterCone, throwOutPosition);
            this.testReturnToBase();
        }
    }

    /**
     * Supportive test method which tests if all the cones were returned to the base properly.
     */
    @Test
    public void testReturnToBase() {
        // For calling the method on its own
        this.initializeGameIfNeeded();

        // Test if the cone has returned to the base
        assertEquals(-1, this.victimCone.getPosition().getLocalState());

        //Test if all the cones were returned to the base and no cone is still on the field
        assertEquals(4, this.bluePlayer.getConesOnBase().size());
        assertEquals(0, this.bluePlayer.getConesOnField().size());
    }

    public void initializeGameIfNeeded() {
        if (!this.app.getSceneTitle().equals("Ingame")) {
            this.gameService.setPlayerNames(Constants.TEST_PLAYER_NAMES);
            Platform.runLater(() -> this.app.initializeGame(2));
            sleep(1000);

            List<Player> players = this.gameService.getPlayers();
            this.redPlayer = players.get(0);
            this.bluePlayer = players.get(1);
            this.victimCone = this.bluePlayer.getCones().get(0);
            this.hunterCone = this.redPlayer.getCones().get(0);
        }
    }
}
