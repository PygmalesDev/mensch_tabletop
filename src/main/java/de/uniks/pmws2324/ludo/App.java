package de.uniks.pmws2324.ludo;

import de.uniks.pmws2324.ludo.controller.Controller;
import de.uniks.pmws2324.ludo.controller.IngameController;
import de.uniks.pmws2324.ludo.controller.MenuController;
import de.uniks.pmws2324.ludo.model.Player;
import de.uniks.pmws2324.ludo.service.GameService;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class App extends Application {
    private GameService gameService;
    private List<Controller> controllers;
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.gameService = new GameService(this);
        this.controllers = new ArrayList<>();
        this.primaryStage = primaryStage;

        this.primaryStage.setResizable(false);
        this.changeScene(new MenuController(this, this.gameService), "Menu");
    }

    public void changeScene(Controller controller, String sceneName) {
        this.controllers.add(controller);
        controller.init();
        Scene currentScene = new Scene(controller.render());
        currentScene.getStylesheets().add("de/uniks/pmws2324/ludo/css/main.css");

        this.primaryStage.setTitle(sceneName);
        this.primaryStage.setScene(currentScene);
        this.primaryStage.centerOnScreen();
        this.primaryStage.show();
    }

    public void initializeGame(int playerAmount) {
        this.gameService.setPlayerAmount(playerAmount);
        //Current seed for testing: -1321092957169982059L

        //this.gameService.setSeed(new Random().nextLong());
        this.gameService.setSeed(-1321092957169982059L);
        this.gameService.setupGame();
        this.changeScene(new IngameController(this, this.gameService), "Ingame");
    }

    public GameService getGameService() {
        return gameService;
    }

    public String getSceneTitle() {
        return this.primaryStage.getTitle();
    }

    @Override
    public void stop() throws Exception {
        this.controllers.forEach(Controller::destroy);
        super.stop();
    }

    public void showWinScreen(Player currentPlayer) {
    }
}
