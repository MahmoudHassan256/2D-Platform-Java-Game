package utilz;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import main.Game;

public class LoadSave {
    public static final String  PLAYER_ATLAS ="player_sprites.png";
    public static final String  LEVEL_ATLAS ="outside_sprites.png";
    public static final String  LEVEL_ONE_DATA ="level_one_data.png";




    public static BufferedImage GetSpriteAtlas(String filename){
        BufferedImage img=null;
        File f=new File("public/"+filename);
        try {
            img = ImageIO.read(f);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return img;
    }
    public static int[][] GetLevelData(){
        int[][] lvlData=new int[Game.TILES_IN_HEIGHT][Game.TILES_IN_WIDTH];
        BufferedImage img=GetSpriteAtlas(LEVEL_ONE_DATA);
        for (int i = 0; i < img.getHeight(); i++) {
            for (int j = 0; j < img.getWidth(); j++) {
                Color color =new Color(img.getRGB(j,i));
                int value=color.getRed();
                if(value>=48)
                value=0;
                lvlData[i][j]=value;
            }
        }
        return lvlData;
    }
}
