package levels;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import main.Game;
import utilz.LoadSave;

public class LevelManager {
    private Game game;
    private BufferedImage[] LevelSprite;
    private Level levelOne;
    public LevelManager(Game game) {
        super();
        this.game=game;
        importOutsideSprite();
        levelOne=new Level(LoadSave.GetLevelData());
    }

    private void importOutsideSprite() {
        BufferedImage img =LoadSave.GetSpriteAtlas(LoadSave.LEVEL_ATLAS);
    LevelSprite=new BufferedImage[48];
    for (int i = 0; i < 4; i++) {
        for (int j = 0; j < 12d; j++) {
            int index=i*12+j;
            LevelSprite[index]=img.getSubimage(j*32, i*32, 32,32);
        }
    }
    }

    public void draw(Graphics g){
        for(int j=0;j<Game.TILES_IN_HEIGHT;j++){
            for (int i = 0; i < Game.TILES_IN_WIDTH; i++) {
             int index=levelOne.getSpriteIndex(i, j);
             g.drawImage(LevelSprite[index],Game.TILES_SIZE*i,Game.TILES_SIZE*j,Game.TILES_SIZE,Game.TILES_SIZE, null);
            }
        }

    }
    public void update(){
        
    }
    public Level getCurrentLevel(){
        return levelOne;
    }
}
