package gamestates;

import main.Game;
import ui.AudioOptions;
import ui.UrmButton;
import utilz.LoadSave;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import static utilz.Constants.UI.UrmButtons.*;

public class GameOptions extends State implements Statemetohds {
    private BufferedImage backgroundImg, optionsBackgroundImg;
    private UrmButton menu;
    private int bgX, bgY, bgW, bgH;

    private AudioOptions audioOptions;

    public GameOptions(Game game) {
        super(game);
        loadImg();
        loadButton();
        audioOptions = game.getAudioOptions();
    }

    private void loadButton() {
        int menuX = (int) (387 * Game.SCALE);
        int menuY = (int) (325 * Game.SCALE);

        menu = new UrmButton(menuX, menuY, URM_SIZE, URM_SIZE, 2);
    }

    private void loadImg() {
        backgroundImg = LoadSave.GetSpriteAtlas(LoadSave.MENU_BACKGROUND_IMAGE);
        optionsBackgroundImg = LoadSave.GetSpriteAtlas(LoadSave.OPTIONS_MENU);

        bgW = (int) (optionsBackgroundImg.getWidth() * Game.SCALE);
        bgH = (int) (optionsBackgroundImg.getHeight() * Game.SCALE);
        bgX = Game.GAME_WIDTH / 2 - bgW / 2;
        bgY = (int) (33 * Game.SCALE);
    }

    @Override
    public void update() {
        menu.update();
        audioOptions.update();
    }

    @Override
    public void draw(Graphics g) {
        g.drawImage(backgroundImg, 0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT, null);
        g.drawImage(optionsBackgroundImg, bgX, bgY, bgW, bgH, null);

        menu.draw(g);
        audioOptions.draw(g);
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    public void mouseDragged(MouseEvent e) {
        audioOptions.mouseDragged(e);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (isIn(menu, e))
            menu.setMousePressed(true);
        else
            audioOptions.mousePressed(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (isIn(menu, e)) {
            if (menu.isMousePressed())
                GameState.state = GameState.MENU;
        } else
            audioOptions.mouseReleased(e);
        menu.resetBooleans();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        menu.setMouseOver(false);
        if (isIn(menu, e))
            menu.setMouseOver(true);
        else
            audioOptions.mouseMoved(e);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
            GameState.state = GameState.MENU;
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    private boolean isIn(UrmButton button, MouseEvent e) {
        return button.getBounds().contains(e.getX(), e.getY());
    }

}
