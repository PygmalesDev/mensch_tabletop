package de.uniks.pmws2324.ludo.model;

import org.fulib.builder.ClassModelDecorator;
import org.fulib.builder.ClassModelManager;
import org.fulib.builder.reflect.Link;

import java.util.List;

public class GenModel implements ClassModelDecorator {
    class Player {
        String name;
        String playerColor;
        int diceValue;
        @Link("player")
        List<Cone> cones;
        @Link("player")
        Position startingPosition;
        @Link("initialPlayer")
        List<Position> conesInitialPositions;
    }
    class Cone {
        String color;
        String movingDirection;
        boolean movable;
        boolean visible;
        boolean readyForFinishing;
        @Link("cones")
        Player player;
        @Link("cone")
        Position position;
    }
    class Position {
        int x;
        int y;
        int globalState;
        int localState;
        String condition;
        @Link("position")
        Cone cone;
        @Link("startingPosition")
        Player player;
        @Link("conesInitialPositions")
        Player initialPlayer;

    }

    @Override
    public void decorate(ClassModelManager m) {
        m.haveNestedClasses(GenModel.class);
    }
}
