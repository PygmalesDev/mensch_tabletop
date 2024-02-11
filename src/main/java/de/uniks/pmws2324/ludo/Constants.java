package de.uniks.pmws2324.ludo;

import javafx.scene.image.Image;

public class Constants {
    public static final String PLAYER_COLOR_RED = "red";
    public static final String PLAYER_COLOR_BLUE = "blue";
    public static final String PLAYER_COLOR_YELLOW = "yellow";
    public static final String PLAYER_COLOR_GREEN = "green";

    public static final String CONE_DIRECTION_LEFT = "left";
    public static final String CONE_DIRECTION_DOWN = "down";
    public static final String CONE_DIRECTION_RIGHT = "right";
    public static final String CONE_DIRECTION_UP = "up";

    public static final Image INGAME_BACKGROUND_IMAGE = new Image("de/uniks/pmws2324/ludo/img/deck_background.png");
    public static final Image RED_CONE_IMAGE = new Image("de/uniks/pmws2324/ludo/img/cones/red_cone.png");
    public static final Image BLUE_CONE_IMAGE = new Image("de/uniks/pmws2324/ludo/img/cones/blue_cone.png");
    public static final Image YELLOW_CONE_IMAGE = new Image("de/uniks/pmws2324/ludo/img/cones/yellow_cone.png");
    public static final Image GREEN_CONE_IMAGE = new Image("de/uniks/pmws2324/ludo/img/cones/green_cone.png");
    public static final Image RED_CONE_IMAGE_SELECTED = new Image("de/uniks/pmws2324/ludo/img/cones/red_cone_selected.png");
    public static final Image BLUE_CONE_IMAGE_SELECTED = new Image("de/uniks/pmws2324/ludo/img/cones/blue_cone_selected.png");
    public static final Image YELLOW_CONE_IMAGE_SELECTED = new Image("de/uniks/pmws2324/ludo/img/cones/yellow_cone_selected.png");
    public static final Image GREEN_CONE_IMAGE_SELECTED = new Image("de/uniks/pmws2324/ludo/img/cones/green_cone_selected.png");

    public static final String RED_CONE_LR_ANIM_URL = "de/uniks/pmws2324/ludo/img/cones/animations/red_cone_lr_animation.gif";
    public static final String RED_CONE_RL_ANIM_URL = "de/uniks/pmws2324/ludo/img/cones/animations/red_cone_rl_animation.gif";
    public static final String RED_CONE_DT_ANIM_URL = "de/uniks/pmws2324/ludo/img/cones/animations/red_cone_dt_animation.gif";
    public static final String RED_CONE_TD_ANIM_URL = "de/uniks/pmws2324/ludo/img/cones/animations/red_cone_td_animation.gif";

    public static final String CONDITION_SELECTED = "selected";
    public static final String CONDITION_MOVED = "moved";
    public static final String GAME_STATE_PLAY = "play";
    public static final String GAME_STATE_PAUSE = "pause";
    public static final int CONE_OFFSET_X = 58;
    public static final int CONE_OFFSET_Y = 56;
    public static final int TILE_OFFSET_X = 46;
    public static final int TILE_OFFSET_Y = 45;
}
