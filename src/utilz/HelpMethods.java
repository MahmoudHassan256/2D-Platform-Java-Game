package utilz;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.nio.Buffer;
import java.util.ArrayList;

import static utilz.Constants.ObjectConstants.*;
import static utilz.Constants.EnemyConstants.*;


import entities.Crabby;
import main.Game;
import objects.*;

public class HelpMethods {
    public static boolean canMoveHere(float x, float y, float width, float height, int[][] lvlData) {
        if (!isSolid(x, y, lvlData)) {
            if (!isSolid(x + width, y + height, lvlData)) {
                if (!isSolid(x + width, y, lvlData)) {
                    if (!isSolid(x, y + height, lvlData)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean IsAllTileWalkable(int xStart, int xEnd, int y, int[][] lvlData) {
        if (IsAllTilesClear(xStart, xEnd, y, lvlData))
            for (int i = 0; i < (xEnd - xStart); i++) {
                if (!IsTileSolid(xStart + i, y + 1, lvlData))
                    return false;
            }
        return true;
    }

    public static boolean isSightClear(int[][] lvlData, Rectangle2D.Float
            firsthitBox, Rectangle2D.Float secondhitBox, int yTile) {

        int firstXTile = (int) firsthitBox.x / Game.TILES_SIZE;
        int secondXTile;
        if(isSolid(secondhitBox.x,secondhitBox.y+secondhitBox.height+1,lvlData)){
            secondXTile= (int) secondhitBox.x / Game.TILES_SIZE;
        }
        else
        secondXTile= (int) ((secondhitBox.x+secondhitBox.width) / Game.TILES_SIZE);


        if (firstXTile > secondXTile) {
            return IsAllTileWalkable(secondXTile, firstXTile, yTile, lvlData);
        }
        return IsAllTileWalkable(firstXTile, secondXTile, yTile, lvlData);
    }

    private static boolean isSolid(float x, float y, int[][] lvlData) {
        int maxWidth = lvlData[0].length * Game.TILES_SIZE;
        if (x < 0 || x >= maxWidth) {
            return true;
        }
        if (y < 0 || y >= Game.GAME_HEIGHT) {
            return true;
        }
        float xIndex = x / Game.TILES_SIZE;
        float yIndex = y / Game.TILES_SIZE;

        return IsTileSolid((int) xIndex, (int) yIndex, lvlData);

    }

    public static boolean IsTileSolid(int xTile, int yTile, int[][] lvlData) {
        int value = lvlData[yTile][xTile];

        if (value >= 48 || value < 0 || value != 11) {
            return true;
        } else {
            return false;
        }

    }

    public static float GetEntityXPosNextToWall(Rectangle2D.Float hitBox, float xSpeed) {
        int currentTile = (int) (hitBox.x / Game.TILES_SIZE);
        if (xSpeed > 0) {
            int tileXPosition = currentTile * Game.TILES_SIZE;
            int xOffset = (int) (Game.TILES_SIZE - hitBox.width);
            return tileXPosition + xOffset - 1;
        } else {
            return currentTile * Game.TILES_SIZE;
        }
    }

    public static float GetEntityYPosUnderRoofOrAboveFloor(Rectangle2D.Float hitBox, float airSpeed) {
        int currentTile = (int) (hitBox.y / Game.TILES_SIZE);
        if (airSpeed > 0) {
            int tileYPosition = currentTile * Game.TILES_SIZE;
            int yOffset = (int) (Game.TILES_SIZE - hitBox.height);
            return tileYPosition + yOffset - 1;
        } else {
            return currentTile * Game.TILES_SIZE;
        }
    }

    public static boolean isEntityOnFloor(Rectangle2D.Float hitBox, int[][] lvlData) {
        if (!isSolid(hitBox.x, hitBox.y + hitBox.height + 1, lvlData)) {
            if (!isSolid(hitBox.x + hitBox.width, hitBox.y + hitBox.height + 1, lvlData)) {
                return false;
            }
        }
        return true;
    }

    public static boolean IsFloor(Rectangle2D.Float hitBox, float xSpeed, int[][] lvlData) {
        if (xSpeed > 0)
            return isSolid(hitBox.x + hitBox.width + xSpeed, hitBox.y + hitBox.height + 1, lvlData);
        else return isSolid(hitBox.x + xSpeed, hitBox.y + hitBox.height + 1, lvlData);
    }

    public static int[][] GetLevelData(BufferedImage img) {

        int[][] lvlData = new int[img.getHeight()][img.getWidth()];
        for (int i = 0; i < img.getHeight(); i++) {
            for (int j = 0; j < img.getWidth(); j++) {
                Color color = new Color(img.getRGB(j, i));
                int value = color.getRed();
                if (value >= 48)
                    value = 0;
                lvlData[i][j] = value;
            }
        }
        return lvlData;
    }

    public static ArrayList<Crabby> GetCrabs(BufferedImage img) {
        ArrayList<Crabby> list = new ArrayList<>();
        for (int i = 0; i < img.getHeight(); i++)
            for (int j = 0; j < img.getWidth(); j++) {
                Color color = new Color(img.getRGB(j, i));
                int value = color.getGreen();
                if (value == CRABBY)
                    list.add(new Crabby(j * Game.TILES_SIZE, i * Game.TILES_SIZE));
            }
        return list;

    }

    public static ArrayList<Potion> GetPotions(BufferedImage img) {
        ArrayList<Potion> list = new ArrayList<>();
        for (int i = 0; i < img.getHeight(); i++)
            for (int j = 0; j < img.getWidth(); j++) {
                Color color = new Color(img.getRGB(j, i));
                int value = color.getBlue();
                if (value == RED_POTION || value == BLUE_POTION)
                    list.add(new Potion(j * Game.TILES_SIZE, i * Game.TILES_SIZE, value));

            }
        return list;

    }

    public static ArrayList<Spike> GetSpikes(BufferedImage img) {
        ArrayList<Spike> list = new ArrayList<>();
        for (int i = 0; i < img.getHeight(); i++)
            for (int j = 0; j < img.getWidth(); j++) {
                Color color = new Color(img.getRGB(j, i));
                int value = color.getBlue();
                if (value == SPIKE)
                    list.add(new Spike(j * Game.TILES_SIZE, i * Game.TILES_SIZE, value));
            }
        return list;

    }

    public static ArrayList<Cannon> GetCannons(BufferedImage img) {
        ArrayList<Cannon> list = new ArrayList<>();
        for (int i = 0; i < img.getHeight(); i++)
            for (int j = 0; j < img.getWidth(); j++) {
                Color color = new Color(img.getRGB(j, i));
                int value = color.getBlue();
                if (value == CANNON_LEFT || value == CANNON_RIGHT)
                    list.add(new Cannon(j * Game.TILES_SIZE, i * Game.TILES_SIZE, value));
            }
        return list;

    }

    public static ArrayList<GameContainer> GetContainers(BufferedImage img) {
        ArrayList<GameContainer> list = new ArrayList<>();
        for (int i = 0; i < img.getHeight(); i++)
            for (int j = 0; j < img.getWidth(); j++) {
                Color color = new Color(img.getRGB(j, i));
                int value = color.getBlue();
                if (value == BOX || value == BARREL)
                    list.add(new GameContainer(j * Game.TILES_SIZE, i * Game.TILES_SIZE, value));
            }
        return list;

    }

    public static Point GetPlayerSpawn(BufferedImage img) {
        for (int i = 0; i < img.getHeight(); i++)
            for (int j = 0; j < img.getWidth(); j++) {
                Color color = new Color(img.getRGB(j, i));
                int value = color.getGreen();
                if (value == 100)
                    return new Point(j * Game.TILES_SIZE, i * Game.TILES_SIZE);
            }
        return new Point(1 * Game.TILES_SIZE, 1 * Game.TILES_SIZE);
    }

    public static boolean IsAllTilesClear(int xStart, int xEnd, int y, int[][] lvlData) {
        for (int i = 0; i < (xEnd - xStart); i++)
            if (IsTileSolid(xStart + i, y, lvlData))
                return false;
        return true;
    }

    public static boolean IsProjectileHittingLevel(Projectile p, int[][] lvlData) {
        return isSolid((p.getHitbox().x + p.getHitbox().width / 2), (p.getHitbox().y + p.getHitbox().height / 2), lvlData);
    }

    public static boolean canCannonSeePlayer(int[][] lvlData, Rectangle2D.Float firsthitBox, Rectangle2D.Float secondhitBox, int yTile) {
        int firstXTile = (int) firsthitBox.x / Game.TILES_SIZE;
        int secondXTile = (int) secondhitBox.x / Game.TILES_SIZE;

        if (firstXTile > secondXTile) {
            return IsAllTilesClear(secondXTile, firstXTile, yTile, lvlData);
        }
        return IsAllTilesClear(firstXTile, secondXTile, yTile, lvlData);
    }
}
