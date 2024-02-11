package de.uniks.pmws2324.ludo.service;

import de.uniks.pmws2324.ludo.model.Cone;
import de.uniks.pmws2324.ludo.model.Player;
import de.uniks.pmws2324.ludo.model.Position;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

import static de.uniks.pmws2324.ludo.Constants.*;

/**
 * A main class that contains all game logic.
 */
public class GameService {
    private List<Player> players;
    private int playersAmount;
    private Iterator<Player> playerIterator;
    private Player currentPlayer;
    private String gameState;
    private Random rng = new Random();
    private int diceValue;

    /**
     * Constructor for the {@link GameService} class.
     */
    public GameService() {
        this.setGameState(GAME_STATE_PLAY);
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
                new Player().setPlayerColor(PLAYER_COLOR_GREEN),
                new Player().setPlayerColor(PLAYER_COLOR_YELLOW))
                .map(this::assignConesInitialPosition)
                .map(this::assignCones).toList();
        this.playerIterator = this.players.iterator();
        this.currentPlayer = this.playerIterator.next();
    }

    /**
     * Utility function for the {@link #playersSetup()} method. Generates initial positions
     * and starting positions.
     */
    private Player assignConesInitialPosition(Player player) {
        switch (player.getPlayerColor()) {
            case PLAYER_COLOR_RED ->
                    player.withConesInitialPositions(
                            new Position().setX(197).setY(168).setLocalState(-1),
                            new Position().setX(243).setY(168).setLocalState(-1),
                            new Position().setX(197).setY(214).setLocalState(-1),
                            new Position().setX(243).setY(214).setLocalState(-1)
                    ).setStartingPosition(new Position().setX(196).setY(350)
                            .setGlobalState(0)
                            .setLocalState(-1));
            case PLAYER_COLOR_BLUE ->
                    player.withConesInitialPositions(
                            new Position().setX(613).setY(168).setLocalState(-1),
                            new Position().setX(658).setY(168).setLocalState(-1),
                            new Position().setX(613).setY(214).setLocalState(-1),
                            new Position().setX(658).setY(214).setLocalState(-1)
                    ).setStartingPosition(new Position().setX(473).setY(170)
                            .setGlobalState(10)
                            .setLocalState(-1));
            case PLAYER_COLOR_GREEN ->
                    player.withConesInitialPositions(
                            new Position().setX(613).setY(577).setLocalState(-1),
                            new Position().setX(658).setY(577).setLocalState(-1),
                            new Position().setX(613).setY(625).setLocalState(-1),
                            new Position().setX(658).setY(625).setLocalState(-1)
                    ).setStartingPosition(new Position().setX(656).setY(445)
                            .setGlobalState(20)
                            .setLocalState(-1));
            case PLAYER_COLOR_YELLOW ->
                    player.withConesInitialPositions(
                            new Position().setX(197).setY(577).setLocalState(-1),
                            new Position().setX(243).setY(577).setLocalState(-1),
                            new Position().setX(197).setY(625).setLocalState(-1),
                            new Position().setX(243).setY(625).setLocalState(-1)
                    ).setStartingPosition(new Position().setX(378).setY(625)
                            .setGlobalState(30)
                            .setLocalState(-1));
            default ->
                    player.withConesInitialPositions(
                            new Position().setX(-1).setY(-1),
                            new Position().setX(-1).setY(-1),
                            new Position().setX(-1).setY(-1),
                            new Position().setX(-1).setY(-1)
                    ).setStartingPosition(new Position().setX(-1).setY(-1)
                            .setLocalState(-1));
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

    public void moveConeToStart(Cone cone) {
        cone.setPosition(this.currentPlayer.getStartingPosition().clone());
        cone.getPosition().setLocalState(0);
        this.setConeMovingDirection(cone);
    }

    public void moveCone(Cone cone) {
        Position pos = cone.getPosition();

        if (pos.getGlobalState() == 40)
            pos.setGlobalState(0);

        switch (cone.getMovingDirection()) {
            case CONE_DIRECTION_RIGHT -> pos.setX(pos.getX()+TILE_OFFSET_X);
            case CONE_DIRECTION_LEFT -> pos.setX(pos.getX()-TILE_OFFSET_X);
            case CONE_DIRECTION_UP -> pos.setY(pos.getY()-TILE_OFFSET_Y);
            case CONE_DIRECTION_DOWN -> pos.setY(pos.getY()+TILE_OFFSET_Y);
        }

        if (pos.getLocalState() == 38) {
            switch (cone.getColor()) {
                case PLAYER_COLOR_RED -> cone.setMovingDirection(CONE_DIRECTION_RIGHT);
                case PLAYER_COLOR_BLUE -> cone.setMovingDirection(CONE_DIRECTION_DOWN);
                case PLAYER_COLOR_YELLOW -> cone.setMovingDirection(CONE_DIRECTION_UP);
                case PLAYER_COLOR_GREEN ->cone.setMovingDirection(CONE_DIRECTION_LEFT);
            }
        }
        if (pos.getLocalState() == 44) {
            System.out.println("player won");
        }

        pos.setGlobalState(pos.getGlobalState()+1);
        pos.setLocalState(pos.getLocalState()+1);
        this.setConeMovingDirection(cone);
    }

    private void setConeMovingDirection(Cone cone) {
        switch (cone.getPosition().getGlobalState()) {
            case 0, 8, 14 -> cone.setMovingDirection(CONE_DIRECTION_RIGHT);
            case 4, 30, 38 -> cone.setMovingDirection(CONE_DIRECTION_UP);
            case 10, 18, 24 -> cone.setMovingDirection(CONE_DIRECTION_DOWN);
            case 20, 28, 34 -> cone.setMovingDirection(CONE_DIRECTION_LEFT);
        }
    }

    public List<Player> getPlayers() {
        return players;
    }

    public Player getCurrentPlayer() {
        return this.currentPlayer;
    }

    public void setNextPlayer() {
        if (!this.playerIterator.hasNext())
            this.playerIterator = this.players.iterator();
        this.currentPlayer = this.playerIterator.next();
    }

    public void setGameState(String state) {
        if (state.equals(GAME_STATE_PLAY) || state.equals(GAME_STATE_PAUSE))
            this.gameState = state;
    }

    public String getGameState() {
        return this.gameState;
    }

    public void throwDice() {
        this.diceValue = rng.nextInt(1, 7);
    }
    public int getDiceValue() {
       return this.diceValue;
    }


}
