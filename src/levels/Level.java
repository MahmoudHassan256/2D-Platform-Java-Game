package levels;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import static utilz.HelpMethods.*;


import entities.Crabby;
import main.Game;

public class Level {
    private BufferedImage image;
    private ArrayList<Crabby> crabbies;
    private int lvlTilesWide;
    private int maxTilesOffset;
    private int maxLvlOffsetX;
    private Point playerSpawn;

    private int[][] lvlData;

    public Level(BufferedImage image) {
        super();
        this.image=image;
        createLvlDate();
        createEnemies();
        calculateLvlOffset();
        calculatePlayerSpawn();
    }
    private void calculatePlayerSpawn() {
        playerSpawn=GetPlayerSpawn(image);   
    }
    private void createEnemies() {
        crabbies=GetCrabs(image);
    }
    private void createLvlDate() {
        lvlData=GetLevelData(image);
    }
    private void calculateLvlOffset(){
        lvlTilesWide=image.getWidth();
        maxTilesOffset=lvlTilesWide-Game.TILES_IN_WIDTH;
        maxLvlOffsetX=Game.TILES_SIZE* maxTilesOffset;
    }
    public ArrayList<Crabby> getCrabbies() {
        return crabbies;
    }
    public int getMaxLvlOffsetX() {
        return maxLvlOffsetX;
    }
    public int[][] getLvlData() {
        return lvlData;
    }
    public int getSpriteIndex(int x,int y){
        return lvlData[y][x];
    }
    public Point getPlayerSpawn() {
        return playerSpawn;
    }


}
