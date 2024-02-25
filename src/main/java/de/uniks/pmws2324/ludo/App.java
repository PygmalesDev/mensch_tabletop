package de.uniks.pmws2324.ludo;

import de.uniks.pmws2324.ludo.controller.*;
import de.uniks.pmws2324.ludo.model.Player;
import de.uniks.pmws2324.ludo.service.GameService;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.awt.*;
import java.util.*;

public class App extends Application {
    private GameService gameService;
    private Map<String, Controller> controllers;
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.gameService = new GameService(this);
        this.controllers = new HashMap<>();
        this.primaryStage = primaryStage;

        this.primaryStage.setResizable(false);
        this.changeScene("Menu");
    }

    public void changeScene(String sceneName) {
        Controller controller = null;
        if (this.controllers.containsKey(sceneName))
            controller = this.controllers.get(sceneName);
        else {
            switch (sceneName) {
                case "Menu" -> controller = new MenuController(this, this.gameService);
                case "Setup" -> controller = new SetupController(this, this.gameService);
                case "GameOver" -> controller = new GameOverController(this, this.gameService);
            }
            this.controllers.put(sceneName, controller);
        }
        controller.init();
        Scene currentScene = new Scene(controller.render());
        currentScene.getStylesheets().add("de/uniks/pmws2324/ludo/css/main.css");

        this.primaryStage.setTitle(sceneName);
        this.primaryStage.setScene(currentScene);
        this.primaryStage.centerOnScreen();
        this.primaryStage.show();
    }

    public void initializeGame(int playerAmount) {
        if (!this.controllers.containsKey("Ingame"))
            this.controllers.put("Ingame", new IngameController(this, this.gameService));

        this.gameService.setPlayerAmount(playerAmount);
        this.gameService.setSeed(new Random().nextLong());
        this.gameService.setupGame();

        this.changeScene("Ingame");
    }

    public GameService getGameService() {
        return gameService;
    }

    public String getSceneTitle() {
        return this.primaryStage.getTitle();
    }

    @Override
    public void stop() throws Exception {
        this.controllers.values().forEach(Controller::destroy);
        super.stop();
    }
}
