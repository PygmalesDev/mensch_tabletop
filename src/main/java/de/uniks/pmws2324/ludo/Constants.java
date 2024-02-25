package de.uniks.pmws2324.ludo;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Map;

public class Constants {
    public static enum GAME_STATE {
        PLAY,
        PAUSE,
        DECISION,
        PREPARATION,
        CHOOSE_ON_BASE,
        CHOOSE_ON_FIELD,
        WIN
    }

    public static final String PLAYER_COLOR_RED = "red";
    public static final String PLAYER_COLOR_BLUE = "blue";
    public static final String PLAYER_COLOR_YELLOW = "yellow";
    public static final String PLAYER_COLOR_GREEN = "green";

    public static final String CONE_DIRECTION_LEFT = "left";
    public static final String CONE_DIRECTION_DOWN = "down";
    public static final String CONE_DIRECTION_RIGHT = "right";
    public static final String CONE_DIRECTION_UP = "up";

    public static final Image INGAME_BACKGROUND_IMAGE = new Image("de/uniks/pmws2324/ludo/img/deck_background_with_cards.png");
    public static final Image INFO_TABLE_IMAGE = new Image("de/uniks/pmws2324/ludo/img/ui/information_table.png");

    public static final String CONE_IMG = "de/uniks/pmws2324/ludo/img/cones/cone_";
    public static final String CONE_IMG_SELECTED = "de/uniks/pmws2324/ludo/img/cones/cone_selected_";
    public static final String CONE_LR_ANIM_URL = "de/uniks/pmws2324/ludo/img/cones/animations/cone_lr_animation_";
    public static final String CONE_RL_ANIM_URL = "de/uniks/pmws2324/ludo/img/cones/animations/cone_rl_animation_";
    public static final String CONE_DT_ANIM_URL = "de/uniks/pmws2324/ludo/img/cones/animations/cone_dt_animation_";
    public static final String CONE_TD_ANIM_URL = "de/uniks/pmws2324/ludo/img/cones/animations/cone_td_animation_";

    public static final String DICE_THROW_BUTTON_URL = "de/uniks/pmws2324/ludo/img/ui/dice_throw_button_";
    public static final String THROWOUT_ANIM_URL ="de/uniks/pmws2324/ludo/img/ui/animations/deck_with_cards_throwout.gif";
    public static final String MENU_BACK_ANIM ="de/uniks/pmws2324/ludo/img/ui/animations/deck_menu_background.gif";

    public static final String GAMENAME_URL ="de/uniks/pmws2324/ludo/img/ui/menu_gamename.png";
    public static final String SLOGAN_URL ="de/uniks/pmws2324/ludo/img/ui/menu_slogan.png";
    public static final String PLAY_BUTTON_URL ="de/uniks/pmws2324/ludo/img/ui/menu_play_button.png";
    public static final String PLAY_BUTTON_SELECTED_URL ="de/uniks/pmws2324/ludo/img/ui/menu_play_button_selected.png";
    public static final String RULES_BUTTON_URL ="de/uniks/pmws2324/ludo/img/ui/menu_rules_button.png";
    public static final String RULES_BUTTON_SELECTED_URL ="de/uniks/pmws2324/ludo/img/ui/menu_rules_button_selected.png";
    public static final String QUIT_BUTTON_URL ="de/uniks/pmws2324/ludo/img/ui/menu_quit_button.png";
    public static final String QUIT_BUTTON_SELECTED_URL ="de/uniks/pmws2324/ludo/img/ui/menu_quit_button_selected.png";
    public static final String WHOSPLAYING_URL = "de/uniks/pmws2324/ludo/img/ui/setup_whosplaying.png";
    public static final String SETUP_ARROW_LEFT_URL = "de/uniks/pmws2324/ludo/img/ui/setup_arrow_left.png";
    public static final String SETUP_ARROW_RIGHT_URL = "de/uniks/pmws2324/ludo/img/ui/setup_arrow_right.png";
    public static final String SETUP_PLAYER_DICE_VALUE_URL = "de/uniks/pmws2324/ludo/img/ui/setup_dice_";
    public static final ImageView SETUP_BEGIN_BUTTON = new ImageView("de/uniks/pmws2324/ludo/img/ui/setup_begin_button.png");
    public static final ImageView SETUP_BEGIN_BUTTON_INACTIVE = new ImageView("de/uniks/pmws2324/ludo/img/ui/setup_begin_button_inactive.png");
    public static final String SETUP_PLAYER_SIGN_URL = "de/uniks/pmws2324/ludo/img/ui/setup_player_sign_";
    public static final String ZOOM_URL = "de/uniks/pmws2324/ludo/img/ui/animations/deck_zoom.gif";
    public static final String ZOOM_PATCH_URL = "de/uniks/pmws2324/ludo/img/ui/animations/deck_zoom_patch.png";

    public static final String DICE_URL = "de/uniks/pmws2324/ludo/img/dices/dice_";
    public static final String CONE_UI_URL = "de/uniks/pmws2324/ludo/img/ui/ui_cone_";
    public static final String CONE_UI_SELECTED_URL = "de/uniks/pmws2324/ludo/img/ui/ui_cone_selected_";

    public static final Map<String, int[]> DICE_COORDS = Map.ofEntries(
            Map.entry("red", new int[]{300-42, 193-42}),
            Map.entry("blue", new int[]{556-42, 197-42}),
            Map.entry("green", new int[]{540-42, 599-42}),
            Map.entry("yellow", new int[]{300-42, 596-42}));

    public static final int CONE_OFFSET_X = 58;
    public static final int CONE_OFFSET_Y = 56;
    public static final int TILE_OFFSET_X = 46;
    public static final int TILE_OFFSET_Y = 45;
}
