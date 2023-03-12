package entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import main.Game;

public abstract class Entity {

    protected float x, y;
    protected int width, height;
    protected Rectangle2D.Float hitBox;
    protected int animationTick, animationIndex;
    protected int state;
    protected float airSpeed;
    protected boolean inAir = false;
    protected int maxHealth;
    protected int currentHealth;
    protected Rectangle2D.Float attackBox;
    protected float walkSpeed;


    public Entity(float x, float y, int width, int height) {

        this.x = x;
        this.y = y;
        this.height = height;
        this.width = width;

    }

    protected void drawHitBox(Graphics g, int xLvlOffset) {
        g.setColor(Color.RED);
        g.drawRect((int) hitBox.x - xLvlOffset, (int) hitBox.y, (int) hitBox.width, (int) hitBox.height);
    }

    protected void initializeHitBox(int width, int height) {
        hitBox = new Rectangle2D.Float(x, y, (int) (width * Game.SCALE), (int) (height * Game.SCALE));
    }

    public Rectangle2D.Float getHitBox() {
        return hitBox;
    }

    protected void drawAttackBox(Graphics g, int xLvlOffset) {
        g.setColor(Color.red);
        g.drawRect((int) attackBox.x - xLvlOffset, (int) attackBox.y, (int) attackBox.width, (int) attackBox.height);
    }

    protected int getAniIndex() {
        return animationIndex;
    }

    public int getCurrentHealth() {
        return currentHealth;
    }
}
