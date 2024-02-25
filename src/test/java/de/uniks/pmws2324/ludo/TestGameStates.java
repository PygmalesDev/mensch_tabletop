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
import static de.uniks.pmws2324.ludo.Constants.GAME_STATE.*;

/**
 * This class tests the correct behaviour of the {@link GameService#calculatePlayerChoice()} which is responsible
 * for changing the state of the game during the round. It affects what player can or cannot do.
 * There are three main situations to check: <p>
 * 1. CHOOSE_ON_BASE: Player is forced to pick a cone from the base if he rolls 6,
 * there's no friendly cone on the start, and he still has cones left on the base.
 * 2. CHOOSE_ON_FIELD: Player is allowed to move one of the cones on the game field if there are any, they satisfy
 * the moving conditions (tested in the other class) and a cone is blocking the starting position, and it can't be moved.
 * 3. If any of the conditions are applied, the move goes to the other player.
 */
public class TestGameStates extends ApplicationTest {
    private Stage stage;
    private App app;
    private GameService gameService;
    Player bluePlayer, redPlayer;
    Cone coneRed1, coneRed2, coneRed3, coneRed4, coneBlue1, coneBlue2, coneBlue3, coneBlue4;

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        this.app = new App();
        this.app.start(this.stage);
        this.gameService = this.app.getGameService();
    }

    @Test
    public void testGameStates() {
        this.initializeGameIfNeeded();

        this.testChooseOnBase();
        this.testChooseOnField();
        this.testNoConditionsApplied();
    }

    @Test
    public void testChooseOnBase() {
        // For calling the method on its own
        this.initializeGameIfNeeded();

        this.gameService.returnConesToBase();

        // The player must choose a cone on the base
        this.gameService.setPlayerTurn(this.redPlayer);
        this.gameService.throwDiceForPLayer(this.redPlayer, 6);
        this.gameService.calculatePlayerChoice();
        assertEquals(CHOOSE_ON_BASE, this.gameService.getGameState());

        // The player can't choose a cone on the base because the start is blocked
        this.gameService.moveConeToStartingPosition(this.coneRed1);
        this.gameService.throwDiceForPLayer(this.redPlayer, 6);
        this.gameService.calculatePlayerChoice();
        assertNotEquals(CHOOSE_ON_BASE, this.gameService.getGameState());

        // The player can't choose a cone on the base because there's no cones left there
        this.gameService.simulateConeStepsLocal(this.coneRed1, 1);
        this.gameService.moveConeToStartingPosition(this.coneRed2);
        this.gameService.simulateConeStepsLocal(this.coneRed2, 2);
        this.gameService.moveConeToStartingPosition(this.coneRed3);
        this.gameService.simulateConeStepsLocal(this.coneRed3, 3);
        this.gameService.moveConeToStartingPosition(this.coneRed4);
        this.gameService.simulateConeStepsLocal(this.coneRed4, 4);

        this.gameService.throwDiceForPLayer(this.redPlayer, 6);
        this.gameService.calculatePlayerChoice();
        assertNotEquals(CHOOSE_ON_BASE, this.gameService.getGameState());
    }

    @Test
    public void testChooseOnField() {
        // For calling the method on its own
        this.initializeGameIfNeeded();

        this.gameService.returnConesToBase();

        // The player doesn't roll 6, and have a cone on the field
        this.gameService.setPlayerTurn(this.redPlayer);
        this.gameService.moveConeToStartingPosition(this.coneRed1);
        this.gameService.throwDiceForPLayer(this.redPlayer, 3);
        this.gameService.calculatePlayerChoice();
        assertEquals(CHOOSE_ON_FIELD, this.gameService.getGameState());

        // The player rolls 6, but must choose a cone on field, because the starting position is blocked
        this.gameService.simulateConeStepsLocal(this.coneRed1, 15);
        this.gameService.moveConeToStartingPosition(this.coneRed2);

        this.gameService.throwDiceForPLayer(this.redPlayer, 6);
        this.gameService.calculatePlayerChoice();
        assertEquals(CHOOSE_ON_FIELD, this.gameService.getGameState());
    }

    @Test
    public void testNoConditionsApplied() {
        // For calling the method on its own
        this.initializeGameIfNeeded();

        this.gameService.returnConesToBase();

        // Player doesn't roll 6 and has no cones on the field
        this.gameService.setPlayerTurn(this.redPlayer);
        this.gameService.throwDiceForPLayer(this.redPlayer, 3);
        this.gameService.calculatePlayerChoice();
        assertNotEquals(CHOOSE_ON_BASE, this.gameService.getGameState());
        assertNotEquals(CHOOSE_ON_FIELD, this.gameService.getGameState());
        assertNotEquals(this.gameService.getCurrentPlayer(), this.redPlayer);

        // One of the player's cones reached finish, and player doesn't roll 6
        this.gameService.moveConeToStartingPosition(this.coneBlue1);
        this.gameService.simulateConeStepsLocal(this.coneBlue1, 43);
        this.gameService.throwDiceForPLayer(this.bluePlayer, 3);
        this.gameService.calculatePlayerChoice();
        assertNotEquals(CHOOSE_ON_BASE, this.gameService.getGameState());
        assertNotEquals(CHOOSE_ON_FIELD, this.gameService.getGameState());
        assertNotEquals(this.gameService.getCurrentPlayer(), this.bluePlayer);

        // All the player's cones are on the field but none of them is movable
        // Tested on both players for safety
        this.gameService.moveConeToStartingPosition(this.coneRed1);
        this.gameService.simulateConeStepsLocal(this.coneRed1, 39);
        this.gameService.moveConeToStartingPosition(this.coneRed2);
        this.gameService.simulateConeStepsLocal(this.coneRed2, 41);
        this.gameService.moveConeToStartingPosition(this.coneRed3);
        this.gameService.simulateConeStepsLocal(this.coneRed3, 42);
        this.gameService.moveConeToStartingPosition(this.coneRed4);
        this.gameService.simulateConeStepsLocal(this.coneRed4, 43);

        this.gameService.moveConeToStartingPosition(this.coneBlue2);
        this.gameService.simulateConeStepsLocal(this.coneBlue2, 39);
        this.gameService.moveConeToStartingPosition(this.coneBlue3);
        this.gameService.simulateConeStepsLocal(this.coneBlue3, 41);
        this.gameService.moveConeToStartingPosition(this.coneBlue4);
        this.gameService.simulateConeStepsLocal(this.coneBlue4, 42);

        Iterator<Player> playerIterator = Stream.of(this.redPlayer, this.bluePlayer).iterator();

        while (playerIterator.hasNext()) {
            Player player = playerIterator.next();
            this.gameService.throwDiceForPLayer(player, 3);
            this.gameService.calculatePlayerChoice();
            assertNotEquals(CHOOSE_ON_BASE, this.gameService.getGameState());
            assertNotEquals(CHOOSE_ON_FIELD, this.gameService.getGameState());
            assertNotEquals(this.gameService.getCurrentPlayer(), player);
        }

        playerIterator = Stream.of(this.redPlayer, this.bluePlayer).iterator();

        while (playerIterator.hasNext()) {
            Player player = playerIterator.next();
            this.gameService.throwDiceForPLayer(player, 6);
            this.gameService.calculatePlayerChoice();
            assertNotEquals(CHOOSE_ON_BASE, this.gameService.getGameState());
            assertNotEquals(CHOOSE_ON_FIELD, this.gameService.getGameState());
            assertNotEquals(this.gameService.getCurrentPlayer(), player);
        }
    }

    public void initializeGameIfNeeded() {
        if (!this.app.getSceneTitle().equals("Ingame")) {
            Platform.runLater(() -> this.app.initializeGame(2));
            sleep(1000);

            List<Player> players = this.gameService.getPlayers();
            this.redPlayer = players.get(0);
            this.bluePlayer = players.get(1);
            List<Cone> cones = this.redPlayer.getCones();
            this.coneRed1 = cones.get(0);
            this.coneRed2 = cones.get(1);
            this.coneRed3 = cones.get(2);
            this.coneRed4 = cones.get(3);
            cones = this.bluePlayer.getCones();
            this.coneBlue1 = cones.get(0);
            this.coneBlue2 = cones.get(1);
            this.coneBlue3 = cones.get(2);
            this.coneBlue4 = cones.get(3);
        }
    }
}
