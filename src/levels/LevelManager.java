package levels;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import gamestates.GameState;
import main.Game;
import utilz.LoadSave;

public class LevelManager {
    private Game game;
    private BufferedImage[] LevelSprite;
    private ArrayList<Level> levels;
    private int lvlIndex = 0;

    public LevelManager(Game game) {
        super();
        this.game = game;
        importOutsideSprite();
        levels = new ArrayList<>();
        buildAllLevels();
    }

    private void buildAllLevels() {
        BufferedImage[] allLevels = LoadSave.GetAllLevels();
        for (BufferedImage img : allLevels) {
            levels.add(new Level(img));
        }
    }

    private void importOutsideSprite() {
        BufferedImage img = LoadSave.GetSpriteAtlas(LoadSave.LEVEL_ATLAS);
        LevelSprite = new BufferedImage[48];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 12d; j++) {
                int index = i * 12 + j;
                LevelSprite[index] = img.getSubimage(j * 32, i * 32, 32, 32);
            }
        }
    }

    public void draw(Graphics g, int xLvlOffset) {
        for (int j = 0; j < Game.TILES_IN_HEIGHT; j++) {
            for (int i = 0; i < levels.get(lvlIndex).getLvlData()[0].length; i++) {
                int index = levels.get(lvlIndex).getSpriteIndex(i, j);
                g.drawImage(LevelSprite[index], Game.TILES_SIZE * i - xLvlOffset, Game.TILES_SIZE * j, Game.TILES_SIZE, Game.TILES_SIZE, null);
            }
        }

    }

    public void update() {

    }

    public Level getCurrentLevel() {
        return levels.get(lvlIndex);
    }

    public int getAmountOfLevels() {
        return levels.size();
    }

    public void loadNextLvl() {
        lvlIndex++;
        if (lvlIndex >= levels.size()) {
            lvlIndex = 0;
            System.out.println("no more lvls");
            GameState.state = GameState.MENU;
        }
        Level newLvl = levels.get(lvlIndex);
        game.getPlaying().getEnemyManger().loadEnemies(newLvl);
        game.getPlaying().getPlayer().loadLvlData(newLvl.getLvlData());
        game.getPlaying().setLvlOffset(newLvl.getMaxLvlOffsetX());
        game.getPlaying().getObjectManager().loadObjects(newLvl);
    }

    public int getLvlIndex() {
        return lvlIndex;
    }
}
