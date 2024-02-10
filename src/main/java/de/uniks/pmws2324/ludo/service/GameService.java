package de.uniks.pmws2324.ludo.service;

import de.uniks.pmws2324.ludo.model.Cone;
import de.uniks.pmws2324.ludo.model.Player;
import de.uniks.pmws2324.ludo.model.Position;

import java.util.List;
import java.util.stream.Stream;

import static de.uniks.pmws2324.ludo.Constants.*;

/**
 * A main class that contains all game logic.
 */
public class GameService {
    private List<Player> players;
    private int playersAmount;
    private Player currentPlayer;

    /**
     * Constructor for the {@link GameService} class.
     */
    public GameService() {
        this.playersSetup();
    }

    /**
     * For each player, sets up the initial positions of the cones on the player base
     * (4 circles on the base) with starting positions (4 colored circles on the game field),
     * generates cones with assigned colors and puts them on the initial positions.
     */
    private void playersSetup() {
        this.players =  Stream.of(
                new Player().setPlayerColor(PLAYER_COLOR_RED),
                new Player().setPlayerColor(PLAYER_COLOR_BLUE),
                new Player().setPlayerColor(PLAYER_COLOR_YELLOW),
                new Player().setPlayerColor(PLAYER_COLOR_GREEN))
                .map(this::assignConesInitialPosition)
                .map(this::assignCones).toList();
        this.currentPlayer = this.players.get(0);
    }

    /**
     * Utility function for the {@link #playersSetup()} method. Generates initial positions
     * and starting positions.
     */
    private Player assignConesInitialPosition(Player player) {
        switch (player.getPlayerColor()) {
            case PLAYER_COLOR_RED ->
                    player.withConesInitialPositions(
                            new Position().setX(197).setY(168),
                            new Position().setX(243).setY(168),
                            new Position().setX(197).setY(214),
                            new Position().setX(243).setY(214)
                    ).setStartingPosition(new Position().setX(196).setY(350)
                            .setGlobalState(0)
                            .setLocalState(0));
            case PLAYER_COLOR_BLUE ->
                    player.withConesInitialPositions(
                            new Position().setX(613).setY(168),
                            new Position().setX(658).setY(168),
                            new Position().setX(613).setY(214),
                            new Position().setX(658).setY(214)
                    ).setStartingPosition(new Position().setX(473).setY(170)
                            .setGlobalState(10)
                            .setLocalState(0));
            case PLAYER_COLOR_YELLOW ->
                    player.withConesInitialPositions(
                            new Position().setX(197).setY(577),
                            new Position().setX(243).setY(577),
                            new Position().setX(197).setY(625),
                            new Position().setX(243).setY(625)
                    ).setStartingPosition(new Position().setX(378).setY(625)
                            .setGlobalState(20)
                            .setLocalState(0));
            case PLAYER_COLOR_GREEN ->
                    player.withConesInitialPositions(
                            new Position().setX(613).setY(577),
                            new Position().setX(658).setY(577),
                            new Position().setX(613).setY(625),
                            new Position().setX(658).setY(625)
                    ).setStartingPosition(new Position().setX(656).setY(445)
                            .setGlobalState(30)
                            .setLocalState(0));
            default ->
                    player.withConesInitialPositions(
                            new Position().setX(-1).setY(-1),
                            new Position().setX(-1).setY(-1),
                            new Position().setX(-1).setY(-1),
                            new Position().setX(-1).setY(-1)
                    ).setStartingPosition(new Position().setX(-1).setY(-1)
                            .setLocalState(0));
        }
        return player;
    }

    /**
     * Utility function for the {@link #playersSetup()} method. Generates 4 cones for the given player
     * and puts them on the respective initial position.
     */
    private Player assignCones(Player player) {
        return player.withCones(player.getConesInitialPositions().stream()
                .map(pos -> new Cone()
                        .setColor(player.getPlayerColor()).setImage()
                        .setPosition(pos).setVisible(true))
                .toList());
    }

    /**
     * Sets the amount of the players for the session.
     * @param playersAmount amount of players (2 to 4).
     */
    public void setPlayersAmount(int playersAmount) {
        this.playersAmount = playersAmount;
    }

    public void moveCone(Cone cone, Position position) {
        cone.setPosition(position);
    }

    public List<Player> getPlayers() {
        return players;
    }

    public Player getCurrentPlayer() {
        return this.currentPlayer;
    }
}
