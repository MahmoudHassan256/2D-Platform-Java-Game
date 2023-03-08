package gamestates;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Float;
import java.awt.image.BufferedImage;
import java.util.Random;

import entities.EnemyManager;
import entities.Player;
import levels.LevelManager;
import main.Game;
import objects.ObjectManager;
import ui.GameOverOverlay;
import ui.LevelCompletedOverlay;
import ui.PauseOverLay;
import utilz.LoadSave;
import static utilz.Constants.Environment.*;

public class Playing extends State implements Statemetohds {
    private Player player;
    private LevelManager levelManager;
    private EnemyManager enemyManager;
    private ObjectManager objectManager;
    private PauseOverLay pauseOverLay;
    private GameOverOverlay gameOverOverlay;
    private LevelCompletedOverlay levelCompletedOverlay;
    private boolean paused = false;
    private BufferedImage backgroundImg,bigCloud,smallCloud;
    private int[] smallCloudsPos;
    private Random random=new Random();
    private  boolean gameOver=false;
    private boolean lvlCompleted=false;

    private int xLvlOffset;
    private int leftBorder = (int) (0.2 * Game.GAME_WIDTH);
    private int rightBorder = (int) (0.8 * Game.GAME_WIDTH);
    private int maxLvlOffsetX;

    public Playing(Game game) {
        super(game);
        initializeClasses();

        backgroundImg=LoadSave.GetSpriteAtlas(LoadSave.PLAYING_BG_IMG);
        bigCloud=LoadSave.GetSpriteAtlas(LoadSave.BIG_CLOUDS);
        smallCloud=LoadSave.GetSpriteAtlas(LoadSave.SMALL_CLOUDS);
        smallCloudsPos = new int[8];
        for (int i = 0; i < smallCloudsPos.length; i++) {
            smallCloudsPos[i]=(int) (90*Game.SCALE)+random.nextInt((int)(100*Game.SCALE));
        }
        calculationLvlOffset();
        loadStartLvl();
    }

    private void loadStartLvl() {
        enemyManager.loadEnemies(levelManager.getCurrentLevel());
    }

    public void loadNextLvl(){
        resetAll();
        levelManager.loadNextLvl();
        player.setSpawn(levelManager.getCurrentLevel().getPlayerSpawn());
    }
    private void calculationLvlOffset() {
        maxLvlOffsetX=levelManager.getCurrentLevel().getMaxLvlOffsetX();
    }

    private void initializeClasses() {
        levelManager = new LevelManager(game);
        enemyManager=new EnemyManager(this);
        objectManager=new ObjectManager(this);

        this.player = new Player(200, 200, (int) (64 * Game.SCALE), (int) (40 * Game.SCALE),this);
        player.loadLvlData(levelManager.getCurrentLevel().getLvlData());
        player.setSpawn(levelManager.getCurrentLevel().getPlayerSpawn());
        
        pauseOverLay = new PauseOverLay(this);
        gameOverOverlay=new GameOverOverlay(this);
        levelCompletedOverlay=new LevelCompletedOverlay(this);
        
    }

    public Player getPlayer() {
        return player;
    }

    
    public ObjectManager getObjectManager() {
        return objectManager;
    }

    @Override
    public void update() {


        if(paused){
            pauseOverLay.update();
        }else if(lvlCompleted){
            levelCompletedOverlay.update();
        }else if(!gameOver){
            levelManager.update();
            objectManager.update();
            player.update();
            enemyManager.update(levelManager.getCurrentLevel().getLvlData(),player);
            checkCloseToBorder();
        }
    
    }

    private void checkCloseToBorder() {
        int playerX = (int) player.getHitBox().x;
        int diff = playerX - xLvlOffset;
        
        if (diff > rightBorder)
            xLvlOffset += diff - rightBorder;
        else if (diff < leftBorder)
            xLvlOffset += diff - leftBorder;

        if (xLvlOffset > maxLvlOffsetX)
            xLvlOffset = maxLvlOffsetX;
        else if (xLvlOffset < 0)
            xLvlOffset = 0;
    }

    @Override
    public void draw(Graphics g) {
        g.drawImage(backgroundImg, 0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT, null);
        drawClouds(g);
        levelManager.draw(g,xLvlOffset);
        player.render(g,xLvlOffset);
        enemyManager.draw(g,xLvlOffset);
        objectManager.draw(g, xLvlOffset);
        
        if (paused){
            g.setColor(new Color(0, 0, 0,150));
            g.fillRect(0, 0, Game.GAME_WIDTH,Game.GAME_HEIGHT);
            pauseOverLay.draw(g);
        }else if(gameOver){
            gameOverOverlay.draw(g);
        }else if(lvlCompleted){
            levelCompletedOverlay.draw(g);
        }
    }

    private void drawClouds(Graphics g) {
        for (int i = 0; i < 3; i++) {
            g.drawImage(bigCloud,i*BIG_CLOUD_WIDTH-(int)(xLvlOffset*0.3), (int)(204*Game.SCALE),BIG_CLOUD_WIDTH,BIG_CLOUD_HEIGHT, null);
        }
        for (int i = 0; i < smallCloudsPos.length; i++) {
            g.drawImage(smallCloud,SMALL_CLOUD_WIDTH*4*i-(int)(xLvlOffset*0.7),smallCloudsPos[i],SMALL_CLOUD_WIDTH,SMALL_CLOUD_HEIGHT, null);
        }
        

    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if(!gameOver){
            if (e.getButton() == MouseEvent.BUTTON1) {
                player.setAttacking(true);
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if(!gameOver){
        if (paused)
        pauseOverLay.mousePressed(e);
            else if(lvlCompleted)
        levelCompletedOverlay.mousePressed(e);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if(!gameOver){
        if (paused)
            pauseOverLay.mouseReleased(e);
            else if(lvlCompleted)
            levelCompletedOverlay.mouseReleased(e);
            }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if(!gameOver){
        if (paused)
        pauseOverLay.mouseMoved(e);
        else if(lvlCompleted)
        levelCompletedOverlay.mouseMoved(e);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(gameOver)
        gameOverOverlay.keyPressed(e);
        else
        switch (e.getKeyCode()) {
            case KeyEvent.VK_A:
                player.setLeft(true);
                break;
            case KeyEvent.VK_D:
                player.setRight(true);
                break;
            case KeyEvent.VK_SPACE:
                player.setJump(true);
                break;
            case KeyEvent.VK_ESCAPE:
                paused = !paused;
                break;
        
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if(!gameOver)
        switch (e.getKeyCode()) {
            case KeyEvent.VK_A:
                player.setLeft(false);
                break;
            case KeyEvent.VK_D:
                player.setRight(false);
                break;
            case KeyEvent.VK_SPACE:
                player.setJump(false);
                break;
        }
    }

    public void unPauseGame() {
        paused = false;
    }

    public void mouseDragged(MouseEvent e) {
        if (paused)
            pauseOverLay.mouseDragged(e);
    }

    public void resetAll() {
        gameOver=false;
        paused=false;
        player.resetAll();
        lvlCompleted=false;
        enemyManager.resetAll();


        
    }

    public void checkEnemyHit(Rectangle2D.Float attackBox) {
        enemyManager.checkEnemyHit(attackBox);
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver=gameOver;
    }
    public EnemyManager getEnemyManger(){
        return enemyManager;
    }
    public void setLvlOffset(int xLvlOffset){
        this.maxLvlOffsetX=xLvlOffset;
    }

    public void setLvlCompleted(boolean lvlCompleted) {
        this.lvlCompleted=lvlCompleted; 
    }
}
