package de.uniks.pmws2324.ludo.service;

import de.uniks.pmws2324.ludo.App;
import de.uniks.pmws2324.ludo.controller.IngameController;
import de.uniks.pmws2324.ludo.controller.MenuController;
import de.uniks.pmws2324.ludo.model.Cone;
import de.uniks.pmws2324.ludo.model.Player;
import de.uniks.pmws2324.ludo.model.Position;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

import static de.uniks.pmws2324.ludo.Constants.*;
import static de.uniks.pmws2324.ludo.Constants.GAME_STATE.*;

/**
 * A main class that contains all game logic.
 */
public class GameService {
    private final App app;
    private int playerAmount;
    private int lastDiceValue;
    private Player currentPlayer;
    private GAME_STATE gameState;
    private List<Player> players;
    private List<Player> playerRotation;
    private List<String> playerNames;
    private Map<String, Integer> finalBlockedState;
    private Iterator<Player> playerIterator;
    private Random rng = new Random();

    public GameService(App app) {this.app = app;}

    public void setSeed(long seed) {
        this.rng = new Random(seed);
        //System.out.println("Seed: " + seed);
    }

    public void setupGame() {
        this.setGameState(PREPARATION);
        this.playersSetup();
        this.setInitialPlayerRotation();
        this.finalBlockedState = new HashMap<>();
        this.finalBlockedState.put("red", 44);
        this.finalBlockedState.put("blue", 44);
        this.finalBlockedState.put("green", 44);
        this.finalBlockedState.put("yellow", 44);
    }

    /**
     * For each player, sets up the initial positions of the cones on the player base
     * (4 circles on the base) with starting positions (4 colored circles on the game field),
     * generates cones with assigned colors and puts them on the initial positions.
     */
    private void playersSetup() {
        List<Player> playerBatch =  Stream.of(
                        new Player().setPlayerColor(PLAYER_COLOR_RED),
                        new Player().setPlayerColor(PLAYER_COLOR_BLUE),
                        new Player().setPlayerColor(PLAYER_COLOR_GREEN),
                        new Player().setPlayerColor(PLAYER_COLOR_YELLOW))
                .map(this::setInitialDiceValue)
                .map(this::assignConesInitialPosition)
                .map(this::assignCones).toList();
        Iterator<Player> playerIterator = playerBatch.iterator();
        Iterator<String> playerNamesIterator = this.playerNames.iterator();
        int amount = this.playerAmount;
        this.players = new ArrayList<>();
        while (amount != 0) {
            this.players.add(playerIterator.next()
                    .setName(playerNamesIterator.next()));
            amount--;
        }
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
                        .setColor(player.getPlayerColor())
                        .loadImageNormal().loadImageSelected()
                        .setCurrentImage("NORMAL")
                        .setPosition(pos).setVisible(true))
                .toList());
    }

    /**
     * Sets the amount of the players for the session.
     * @param playerAmount amount of players (2 to 4).
     */
    public void setPlayerAmount(int playerAmount) {
        this.playerAmount = playerAmount;
    }

    /**
     * Sets players' initial rotation (red-blue-green-yellow by default) before they threw their dices.
     */
    private void setInitialPlayerRotation() {
        this.playerRotation = this.players;
        this.setPlayerIterator();
        this.currentPlayer = playerIterator.next();
    }

    /**
     * Sorts players' rotation after everyone threw their dice.
     */
    public void sortPlayerRotation() {
        this.playerRotation = this.playerRotation.stream()
                .sorted(Comparator.comparing(Player::getDiceValue).reversed())
                .toList();
    }

    private void setPlayerIterator() {
        this.playerIterator = playerRotation.iterator();
    }

    /**
     * Checks what should be done with the result of the dice roll.
     */
    public void calculateTrowOutcome() {
        this.throwDiceForPLayer(this.currentPlayer);
        switch (this.gameState) {
            case PREPARATION -> {
                if (this.players.stream()
                        .filter(player -> player.getDiceValue() != this.lastDiceValue)
                        .toList().size() == this.playerAmount -1)
                    this.setNextPlayer();
            }
            case PLAY -> this.calculatePlayerChoice();
        }
    }

    public void calculatePlayerChoice() {
        this.setGameState(DECISION);

        if (this.lastDiceValue == 6) {
            // Decides if the player has cones on base and can move one to the start
            if (this.currentPlayer.hasConesOnBase() && !this.currentPlayer.isStartBlockedByYourself())
                this.setGameState(CHOOSE_ON_BASE);
            else if (this.currentPlayer.hasConesOnField() && this.someConesCanMove())
                this.setGameState(CHOOSE_ON_FIELD);
        } else if (this.currentPlayer.hasConesOnField() && this.someConesCanMove())
            this.setGameState(CHOOSE_ON_FIELD);

        if (this.gameState.equals(DECISION))
            continueGame();
    }

    /**
     * Continues the game.
     * If the last player has rolled a 6, he can make another throw.
     */
    public void continueGame() {
        if (this.lastDiceValue < 6 || !this.someConesCanMove())
            this.setNextPlayer();
        this.setGameState(PLAY);
    }

    public void setNextPlayer() {
        if (!this.playerIterator.hasNext()) {
            if (this.gameState.equals(PREPARATION)) {
                this.sortPlayerRotation();
                this.setGameState(PLAY);
                this.lastDiceValue = -1;
            }
            this.setPlayerIterator();
        }
        this.currentPlayer = this.playerIterator.next();
    }

    /**
     * Removes cone from the field and returns it to the base.
     */
    public void throwOut(Cone cone) {
        cone.getPlayer().getConesInitialPositions().stream()
                .filter(position -> Objects.isNull(position.getCone()))
                .findFirst().ifPresent(cone::setPosition);
    }

    /**
     * Moves cone to the player's starting position. Creates a special position, that will be used to calculate
     * the behaviour of cone on the game field. If this cone hits an enemy cone on the start, kicks it to base and
     * returns true for the {@link IngameController} to play the throw out animation.
     */
    public boolean moveConeToStartingPosition(Cone cone) {
        AtomicBoolean outcome = new AtomicBoolean(false);
        this.getEnemyCones().stream()
                .filter(another -> !another.getPlayer().getConesInitialPositions().contains(another.getPosition()))
                .filter(another -> another.getPosition().getGlobalState()
                        == this.currentPlayer.getStartingPosition().getGlobalState())
                .findFirst().ifPresent(another -> {
                    this.throwOut(another);
                     outcome.set(true);
                });

        cone.setPosition(this.currentPlayer.getStartingPosition().clone());
        cone.getPosition().setLocalState(0);
        this.setConeMovingDirection(cone);
        return outcome.get();
    }

    /**
     * Checks if some of the current's player cones on field are able to make a move.
     */

    public boolean someConesCanMove() {
        return this.currentPlayer.getConesOnField().stream()
                .anyMatch(this::isConeMovable);
    }

    /**
     * Checks if the cone can be placed on one of the final positions.
     */
    public boolean checkConeCanMoveToEnd(Cone cone) {
        int localState = cone.getPosition().getLocalState();
        int calculated = localState + this.getLastDiceValue();
        String color = cone.getPlayer().getPlayerColor();

        return  localState < 34
                || calculated <= 39
                || calculated < this.finalBlockedState.get(color)
                || localState >= this.finalBlockedState.get(color) && calculated < 44;
    }

    /**
     * Checks if the final blocked state needs to be changed when cones move on the final fields.
     */
    public void checkFinalBlockedState(Cone cone) {
        int localState = cone.getPosition().getLocalState();
        String color = cone.getPlayer().getPlayerColor();

        if (localState > 39 && localState < this.finalBlockedState.get(color)
                || (localState > this.finalBlockedState.get(color) && localState < 44))
            this.changeFinalBlockedState(cone);

        this.currentPlayer.getConesOnField()
                .stream().filter(cone1 -> {
                  int state = cone.getPosition().getLocalState();
                  return state > 39 && state < this.finalBlockedState.get(color);})
                .findFirst().ifPresent(this::changeFinalBlockedState);

    }

    /**
     * Changes final blocked state to cone's current local state.
     */
    private void changeFinalBlockedState(Cone cone) {
        this.finalBlockedState.replace(cone.getPlayer().getPlayerColor(), cone.getPosition().getLocalState());
    }

    /**
     * Checks if the cone will collide with player's other cone after the move is finished.
     */
    public boolean checkCollisionWithFriendlyCone(Cone cone) {
        return this.getCurrentPlayer().getConesOnField().stream()
                .filter(another -> !Objects.equals(another, cone))
                .anyMatch(another ->
                        another.getPosition().getLocalState() == cone.getPosition().getLocalState() + this.lastDiceValue);
    }

    /**
     * Checks if the cone will collide with an enemy cone after the move is finished.
     */
    public boolean checkCollisionWithEnemyCone(Cone cone) {
        AtomicBoolean toReturn = new AtomicBoolean(false);
        this.getEnemyCones().stream()
                .filter(another -> !another.getPlayer().getConesInitialPositions().contains(another.getPosition()))
                .filter(another -> another.getPosition().getGlobalState() != -1 && another.getPosition().getGlobalState()
                        == cone.getPosition().getGlobalState())
                .findFirst().ifPresent(another -> {
                    this.throwOut(another);
                    toReturn.set(true);
                });
        return toReturn.get();
    }

    /**
     * Checks if all the current player's cones are standing on the final fields.
     */
    public void checkWinningConditions() {
         if (this.currentPlayer.getCones().stream()
                 .map(Cone::getPosition)
                 .map(Position::getLocalState)
                 .allMatch(state -> state > 39)) {
             this.setGameState(WIN);
             this.app.changeScene(new MenuController(this.app, this), "Menu");
         }
    }

    /**
     * Moves cone one step further. Changes local and global states of the cone's position for calculations.
     * Changes the coordinates of position, so it could be drawn on the screen in the correct place.
     */
    public void moveCone(Cone cone) {
        Position pos = cone.getPosition();

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

        if (pos.getLocalState() < 39)
            pos.setGlobalState(pos.getGlobalState() + 1);
        else
            pos.setGlobalState(-1);
        pos.setLocalState(pos.getLocalState()+1);

        if (pos.getGlobalState() == 40)
            pos.setGlobalState(0);

        this.setConeMovingDirection(cone);
    }

    /**
     * Supportive method for {@link #moveCone(Cone)}, changes the moving direction of the cone's position.
     */
    private void setConeMovingDirection(Cone cone) {
        switch (cone.getPosition().getGlobalState()) {
            case 0, 8, 14 -> cone.setMovingDirection(CONE_DIRECTION_RIGHT);
            case 4, 30, 38 -> cone.setMovingDirection(CONE_DIRECTION_UP);
            case 10, 18, 24 -> cone.setMovingDirection(CONE_DIRECTION_DOWN);
            case 20, 28, 34 -> cone.setMovingDirection(CONE_DIRECTION_LEFT);
        }
    }

    /**
     * Checks if the cone can be moved.
     */
    public boolean isConeMovable(Cone cone) {
        return (this.isConeJustPlaced(cone) && this.isConeJustPlacedAndMovable())
                || (this.checkConeCanMoveToEnd(cone) && !this.checkCollisionWithFriendlyCone(cone)
                            && !this.isConeJustPlacedAndMovable());
    }

    /**
     * Checks if the selected cone was just placed on the starting position.
     */
    private boolean isConeJustPlaced(Cone cone) {
        return cone.getPosition().getLocalState() == 0;
    }

    /**
     * Checks if a cone was placed on the starting position and it can be moved.
     */
    private boolean isConeJustPlacedAndMovable() {
        Optional<Cone> justPlaced = this.currentPlayer.getCones().stream()
                .filter(this::isConeJustPlaced)
                .findFirst();
        return justPlaced.isPresent() && !this.checkCollisionWithFriendlyCone(justPlaced.get());
    }

    /**
     * Returns a list of cones that belong to other players.
     */
    private List<Cone> getEnemyCones() {
        return this.players.stream()
                .filter(player -> !player.getPlayerColor().equals(this.currentPlayer.getPlayerColor()))
                .map(Player::getCones).flatMap(List::stream).toList();
    }

    public int getPlayerAmount() {
        return playerAmount;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public Player getCurrentPlayer() {
        return this.currentPlayer;
    }

    public void setGameState(GAME_STATE state) {
            this.gameState = state;
    }

    public GAME_STATE getGameState() {
        return this.gameState;
    }

    public int throwDice(int predicted) {
        this.lastDiceValue = predicted;
        return predicted;
    }

    public int throwDice() {
        this.lastDiceValue = rng.nextInt(1, 7);
        return this.lastDiceValue;
    }

    public Player setInitialDiceValue(Player player) {
        player.setDiceValue(this.throwDice(0));
        return player;
    }

    public void throwDiceForPLayer(Player player) {
        player.setDiceValue(this.throwDice());
    }

    public void throwDiceForPLayer(Player player, int predicted) {
        player.setDiceValue(this.throwDice(predicted));
    }

    public int getLastDiceValue() {
       return this.lastDiceValue;
    }

    public List<Player> getPlayerRotation() {
        return playerRotation;
    }

    public void setPlayerNames(List<String> playerNames) {
        this.playerNames = playerNames;
    }

    public int getFinalBlockedState(Player player) {
        return this.finalBlockedState.get(player.getPlayerColor());
    }

    // ----------------------- CONVENIENCE TESTING METHODS -----------------------
    // These support methods deem to simulate special game situations and do not affect the game logic in any way

    /**
     * Sets the play turn to the given player.
     */
    public void setPlayerTurn(Player player) {
        this.currentPlayer = player;
    }

    /**
     * Replicates the behavior of AnimateConeMovementThread inside the {@link IngameController},
     * which calls {@link GameService#moveCone(Cone)} and {@link GameService#checkCollisionWithEnemyCone(Cone)}.
     *  Uses local states (between friendly cones) to check the movement.
     */
    public void simulateConeStepsLocal(Cone cone, int stepsTo) {
        Position initial = cone.getPosition();
        while (initial.getLocalState() != stepsTo)
            this.moveCone(cone);
        this.checkFinalBlockedState(cone);
    }

    /**
     * Replicates the behavior of AnimateConeMovementThread inside the {@link IngameController},
     * which calls {@link GameService#moveCone(Cone)} and {@link GameService#checkCollisionWithEnemyCone(Cone)}.
     * Uses global states (between all cones on board) to check the movement.
     */
    public void simulateConeStepsGlobal(Cone cone, int stepsTo) {
        Position initial = cone.getPosition();
        while (initial.getGlobalState() != stepsTo)
            this.moveCone(cone);
        this.checkCollisionWithEnemyCone(cone);
    }

    /**
     * Returns all the current player's cones that are placed on field to the base.
     */
    public void returnConesToBase() {
        this.currentPlayer.getConesOnField().forEach(this::throwOut);
    }
}
