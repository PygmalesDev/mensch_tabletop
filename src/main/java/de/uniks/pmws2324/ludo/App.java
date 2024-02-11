package de.uniks.pmws2324.ludo;

import de.uniks.pmws2324.ludo.controller.Controller;
import de.uniks.pmws2324.ludo.controller.IngameController;
import de.uniks.pmws2324.ludo.service.GameService;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class App extends Application {
    private GameService gameService;
    private List<Controller> controllers;
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.gameService = new GameService();
        this.controllers = new ArrayList<>();
        this.primaryStage = primaryStage;

        this.primaryStage.setResizable(false);
        this.changeScene(new IngameController(this, this.gameService), "Ingame");
    }

    public void changeScene(Controller controller, String sceneName) {
        this.controllers.add(controller);
        controller.init();
        Scene currentScene = new Scene(controller.render());

        this.primaryStage.setTitle(sceneName);
        this.primaryStage.setScene(currentScene);
        this.primaryStage.centerOnScreen();
        this.primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        this.controllers.forEach(Controller::destroy);
        super.stop();
    }
}
