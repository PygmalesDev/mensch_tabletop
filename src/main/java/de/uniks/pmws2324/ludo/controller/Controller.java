package de.uniks.pmws2324.ludo.controller;

import de.uniks.pmws2324.ludo.App;
import de.uniks.pmws2324.ludo.service.GameService;
import javafx.scene.Parent;

import java.util.ArrayList;
import java.util.List;

public abstract class Controller {
    public App app;
    protected Parent parent;
    protected List<Controller> subControllers;

    protected GameService gameService;

    public Controller(App app, GameService gameService) {
        this.app = app;
        this.gameService = gameService;
        this.subControllers = new ArrayList<>();
    }

    public abstract void init();

    public Parent render() {
        return this.parent;
    };

    public void destroy() {
        this.subControllers.forEach(Controller::destroy);
        this.app = null;
        this.gameService = null;
    };
}
