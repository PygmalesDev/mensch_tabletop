package de.uniks.pmws2324.ludo;

import de.uniks.pmws2324.ludo.model.Cone;
import de.uniks.pmws2324.ludo.model.Player;
import de.uniks.pmws2324.ludo.model.Position;
import de.uniks.pmws2324.ludo.service.GameService;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class SimulateGame extends ApplicationTest {
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
    public void simulateGame() {
        float estTime = 0;
        int throwouts = 0;
        this.initializeGame();
        //this.gameService.setSeed();

        do {
            Constants.GAME_STATE state = this.gameService.getGameState();
            Player current = this.gameService.getCurrentPlayer();

            switch (state) {
                case PLAY, PREPARATION -> {
                    this.gameService.throwDiceForPLayer(current);
                    this.gameService.calculateTrowOutcome();
                    estTime += 300;
                }
                case CHOOSE_ON_BASE -> {
                    Cone toPick = current.getConesOnBase().get(0);
                    this.gameService.moveConeToStartingPosition(toPick);
                    this.gameService.continueGame();
                }
                case CHOOSE_ON_FIELD -> {
                    List<Position> positions = current.getConesOnField().stream()
                            .map(Cone::getPosition).sorted(Comparator.comparing(Position::getLocalState).reversed())
                            .toList();
                    Iterator<Position> posIter = positions.stream().iterator();
                    while (posIter.hasNext()) {
                        Cone toPick = posIter.next().getCone();
                        if (this.gameService.isConeMovable(toPick)) {
                            this.gameService.simulateConeStepsLocal(toPick,
                                    toPick.getPosition().getLocalState() + this.gameService.getLastDiceValue());
                            if (this.gameService.checkCollisionWithEnemyCone(toPick)) {
                                throwouts++;
                            }
                            estTime += (this.gameService.getLastDiceValue() - 1) * 750 + 1200;
                            this.gameService.continueGame();
                            break;
                        }
                    }
                }
            }
            sleep(100);
        } while (!this.gameService.getGameState().equals(Constants.GAME_STATE.WIN)) ;
        //System.out.println("Estimated time: " + estTime/60_000 + " minutes\nThrow Outs: " + throwouts);
        sleep(60000);
    }

    public void initializeGame() {
            this.gameService.setPlayerNames(List.of("Mister_beige", "CamTheGamer", "LUDO4LIFE", "Amanda"));
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
