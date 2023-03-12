package main;

import java.awt.Graphics;

import audio.AudioPlayer;
import gamestates.GameOptions;
import gamestates.GameState;
import gamestates.Menu;
import gamestates.Playing;
import ui.AudioOptions;

public class Game implements Runnable {
    private GameWindow gameWindow;
    private GamePanel gamePanel;
    private Thread gameThread;
    private final int FPS_SET = 120;
    private final int UPS_SET = 200;
    private Playing playing;
    private Menu menu;
    private GameOptions gameOptions;
    private AudioOptions audioOptions;
    private AudioPlayer audioPlayer;

    public final static int TILE_DEFAULT = 32;
    public final static float SCALE = 1.5f;
    public final static int TILES_IN_WIDTH = 26;
    public final static int TILES_IN_HEIGHT = 14;
    public final static int TILES_SIZE = (int) (TILE_DEFAULT * SCALE);
    public final static int GAME_WIDTH = TILES_SIZE * TILES_IN_WIDTH;
    public final static int GAME_HEIGHT = TILES_SIZE * TILES_IN_HEIGHT;

    public Game() {
        super();
        initializeClasses();
        gamePanel = new GamePanel(this);
        gamePanel.setFocusable(true);
        gamePanel.requestFocus();
        gameWindow = new GameWindow(gamePanel);

        startGameLoop();

    }

    public AudioOptions getAudioOptions() {
        return audioOptions;
    }

    private void initializeClasses() {
        audioPlayer = new AudioPlayer();
        audioOptions = new AudioOptions(this);
        menu = new Menu(this);
        playing = new Playing(this);
        gameOptions = new GameOptions(this);
    }

    private void startGameLoop() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void update() {

        switch (GameState.state) {
            case MENU:
                menu.update();
                break;
            case PLAYING:
                playing.update();
                break;
            case OPTION:
                gameOptions.update();
                break;
            case QUIT:
            default:
                System.exit(0);
                break;

        }
    }

    public void render(Graphics g) {
        switch (GameState.state) {
            case MENU:
                menu.draw(g);
                break;
            case PLAYING:
                playing.draw(g);
                break;
            case OPTION:
                gameOptions.draw(g);
                break;
            default:
                break;

        }

    }

    @Override
    public void run() {

        double timePerFrame = 1000000000.0 / FPS_SET;
        double timePerUpdate = 1000000000.0 / UPS_SET;

        long previousTime = System.nanoTime();

        int frames = 0;
        int updates = 0;
        long lastCheck = System.currentTimeMillis();

        double deltaU = 0;
        double deltaF = 0;

        while (true) {
            long currentTime = System.nanoTime();

            deltaU += (currentTime - previousTime) / timePerUpdate;
            deltaF += (currentTime - previousTime) / timePerFrame;
            previousTime = currentTime;

            if (deltaU >= 1) {
                update();
                updates++;
                deltaU--;
            }

            if (deltaF >= 1) {
                gamePanel.repaint();
                frames++;
                deltaF--;

            }

            if (System.currentTimeMillis() - lastCheck >= 1000) {
                lastCheck = System.currentTimeMillis();
                System.out.println("FPS: " + frames + " | UPS: " + updates);
                frames = 0;
                updates = 0;
            }
        }
    }

    public void windowFocusLost() {
        if (GameState.state == GameState.PLAYING) {
            playing.getPlayer().resetDirectionBoolean();
        }
    }

    public Menu getMenu() {
        return menu;
    }

    public Playing getPlaying() {
        return playing;
    }

    public GameOptions getGameOptions() {
        return gameOptions;
    }

    public AudioPlayer getAudioPlayer() {
        return audioPlayer;
    }
}
