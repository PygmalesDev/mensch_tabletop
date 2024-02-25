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
 * This class tests the correct behavior of the {@link GameService#someConesCanMove()} along with
 * {@link GameService#isConeMovable(Cone)} ()} on three occasions. <p>
 * The {@link GameService#someConesCanMove()} method checks if the current player has any cones on the field
 * which can be moved further without colliding with friendly cones or breaking the rules of reaching the finishing field.
 * Testing situations: <p>
 * 1. Any cone on the field can move freely while the starting position is not being obscured by cone,
 * and it will not collide with friendly cone. <p>
 * 2. The cone standing on the starting field must always be moved first, if the moving position is not obstructed.
 * If that is the case, any other cone is allowed to be moved.<p>
 * 3. The finishing cone can move further only if it's position is not obstructed by other cone on the finishing field.
 */
public class TestConeMovements extends ApplicationTest {
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

    /**
     * Main testing method that calls all supportive methods.
     */
    @Test
    public void testConeMovements() {
        this.initializeGameIfNeeded();

        this.testConeFreeMovement();
        this.testConeOnStartCantMove();
    }

    /**
     * Tests if the cone on the field can move freely while the starting position is not being obscured by cone,
     *  and it will not collide with friendly cone.
     */
    @Test
    public void testConeFreeMovement() {
        // For calling the method on its own
        this.initializeGameIfNeeded();

        this.gameService.returnConesToBase();

        // Test if the cone can move from any position
        this.gameService.setPlayerTurn(this.redPlayer);
        this.gameService.moveConeToStartingPosition(this.cone1);

        Iterator<Integer> moves = Stream.of(1, 2, 3, 4, 5, 6).iterator();

        while (moves.hasNext()) {
            this.gameService.throwDiceForPLayer(this.redPlayer, moves.next());
            assertTrue(this.gameService.someConesCanMove());
            assertTrue(this.gameService.isConeMovable(this.cone1));

            this.gameService.simulateConeStepsLocal(this.cone1,
                    this.cone1.getPosition().getLocalState() + this.gameService.getLastDiceValue());
        }

        // Test if the cone on the field can't be moved because there is a cone standing on the start.
        // The cone on the start should be able to move
        moves = Stream.of(1, 2, 3, 4, 5, 6).iterator();
        this.gameService.moveConeToStartingPosition(this.cone2);

        while (moves.hasNext()) {
            this.gameService.throwDiceForPLayer(this.redPlayer, moves.next());
            assertTrue(this.gameService.someConesCanMove());
            assertTrue(this.gameService.isConeMovable(this.cone2));
            assertFalse(this.gameService.isConeMovable(this.cone1));
        }

        // Test if the cone on the field can't be moved because it will collide with friendly cone.
        // The other cone should be able to move
        this.gameService.simulateConeStepsLocal(this.cone1, 30);
        this.gameService.simulateConeStepsLocal(this.cone2, 31);

        moves = Stream.of(1, 2, 3, 4, 5, 6).iterator();
        while (moves.hasNext()) {
            this.gameService.throwDiceForPLayer(this.redPlayer, moves.next());
            assertTrue(this.gameService.someConesCanMove());
            assertTrue(this.gameService.isConeMovable(this.cone2));
            assertFalse(this.gameService.isConeMovable(this.cone1));
            this.gameService.simulateConeStepsLocal(cone2, 31 + this.gameService.getLastDiceValue());
        }
    }

    /**
     * Tests that the cone on the starting field can't be moved because it's path is obstructed by the other cone.
     * The other cones are allowed to move.
     */
    @Test
    public void testConeOnStartCantMove() {
        // For calling the method on its own
        this.initializeGameIfNeeded();

        this.gameService.returnConesToBase();

        // Test if the cone on the staring position can't be moved
        // The other cone should be movable
        this.gameService.setPlayerTurn(this.redPlayer);
        this.gameService.moveConeToStartingPosition(this.cone1);
        this.gameService.throwDiceForPLayer(this.redPlayer, 1);
        this.gameService.simulateConeStepsLocal(this.cone1, this.gameService.getLastDiceValue());

        this.gameService.moveConeToStartingPosition(cone2);

        Iterator<Integer> moves = Stream.of(1, 2, 3, 4, 5, 6).iterator();
        while (moves.hasNext()) {
            this.gameService.throwDiceForPLayer(this.redPlayer, moves.next());
            assertTrue(this.gameService.someConesCanMove());
            assertFalse(this.gameService.isConeMovable(cone2));
            assertTrue(this.gameService.isConeMovable(cone1));
            this.gameService.simulateConeStepsLocal(cone1, this.gameService.getLastDiceValue() + 1);
        }

    }

    /**
     * Tests the proper fulfillment of the finishing conditions:
     * cone can't be moved further than the last finishing tile;
     * cone can't jump over another cone on the finishing line
     */
    @Test
    public void testConeCanMoveToFinish() {
        // For calling the method on its own
        this.initializeGameIfNeeded();

        this.gameService.returnConesToBase();

        this.gameService.moveConeToStartingPosition(this.cone1);
        this.gameService.simulateConeStepsLocal(this.cone1, 39);

        // Test if the cone can't move further than the last four finishing tiles
        Iterator<Integer> moves = Stream.of(5, 6).iterator();
        while (moves.hasNext()) {
            this.gameService.throwDiceForPLayer(this.redPlayer, moves.next());
            assertFalse(this.gameService.someConesCanMove());
            assertFalse(this.gameService.isConeMovable(this.cone1));
        }

        // Test cone's movement on the tiles together with changes in the blocked state
        moves = Stream.of(1, 1, 1, 1).iterator();
        while (moves.hasNext()) {
            this.gameService.throwDiceForPLayer(this.redPlayer, moves.next());
            assertTrue(this.gameService.someConesCanMove());
            assertTrue(this.gameService.isConeMovable(this.cone1));
            this.gameService.simulateConeStepsLocal(cone1,
                    cone1.getPosition().getLocalState() + this.gameService.getLastDiceValue());
            assertEquals(cone1.getPosition().getLocalState(), this.gameService.getFinalBlockedState(this.redPlayer));
        }

        // Test another cone that will try to move to the finish
        // and if the current finished cone is immovable
        this.gameService.moveConeToStartingPosition(this.cone2);
        this.gameService.simulateConeStepsLocal(this.cone2, 39);
        this.gameService.throwDiceForPLayer(this.redPlayer, 4);
        assertFalse(this.gameService.someConesCanMove());
        assertFalse(this.gameService.isConeMovable(this.cone1));
        assertFalse(this.gameService.isConeMovable(this.cone2));

        // Move second cone to finishing tile and test if another cone can jump over it
        this.gameService.simulateConeStepsLocal(this.cone2, 41);
        assertEquals(cone2.getPosition().getLocalState(), this.gameService.getFinalBlockedState(this.redPlayer));
        this.gameService.moveConeToStartingPosition(cone3);
        this.gameService.simulateConeStepsLocal(cone3, 39);
        this.gameService.throwDiceForPLayer(this.redPlayer, 3);

        assertFalse(this.gameService.someConesCanMove());
        assertFalse(this.gameService.isConeMovable(this.cone3));
    }

    public void initializeGameIfNeeded() {
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
