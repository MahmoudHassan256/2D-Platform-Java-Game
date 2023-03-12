package entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import static utilz.Constants.*;

import audio.AudioPlayer;
import gamestates.Playing;
import main.Game;
import utilz.LoadSave;

import static utilz.Constants.Playerconstats.*;
import static utilz.HelpMethods.*;

public class Player extends Entity {

    private boolean left, right;
    private BufferedImage[][] animation;
    private boolean moving = false, attaking = false;
    private int[][] lvlData;
    private float xDrawOffset = 21 * Game.SCALE, yDrawOffset = 4 * Game.SCALE;
    private int tileY = 0;

    //Status Bar
    private BufferedImage status_bar;

    private int statusBarWidth = (int) (192 * Game.SCALE);
    private int statusBarHeight = (int) (58 * Game.SCALE);
    private int statusBarX = (int) (10 * Game.SCALE);
    private int statusBarY = (int) (10 * Game.SCALE);

    private int healthBarWidth = (int) (150 * Game.SCALE);
    private int healthBarHeight = (int) (4 * Game.SCALE);
    private int healthBarX = (int) (34 * Game.SCALE);
    private int healthBarY = (int) (14 * Game.SCALE);


    private int healthWidth = healthBarWidth;

    //AttackBox

    // Jumping + Gravity
    private boolean jump;
    private float jumpSpeed = -2.25f * Game.SCALE;
    private float fallSpeedAfterCollision = 0.5f * Game.SCALE;

    //Flip
    private int FlipX = 0;
    private int FlipW = 1;
    private boolean attackChecked;
    private Playing playing;

    //Power Attack
    private boolean powerAttackActive;
    private int powerAttackTick, powerGrowSpeed = 15, powerGrowTick;

    private int powerBarWidth = (int) (104 * Game.SCALE);
    private int powerBarHeight = (int) (2 * Game.SCALE);
    private int powerBarX = (int) (44 * Game.SCALE);
    private int powerBarY = (int) (34 * Game.SCALE);


    private int powerWidth = powerBarWidth;
    private int powerMaxValue = 200;
    private int powerValue = powerMaxValue;

    public Player(float x, float y, int width, int height, Playing playing) {
        super(x, y, width, height);
        this.playing = playing;
        this.state = IDLE;
        this.maxHealth = 100;
        this.currentHealth = maxHealth;
        this.walkSpeed = (1.0f * Game.SCALE);
        loadAnimations();
        initializeHitBox(20, 27);
        initializeAttackBox();
    }

    private void initializeAttackBox() {
        attackBox = new Rectangle2D.Float(x, y, (int) (20 * Game.SCALE), (int) (20 * Game.SCALE));
        resetAttackBox();
    }

    public void update() {
        updateHealthBar();
        updatePowerBar();

        if (currentHealth <= 0) {
            if (state != DEAD) {
                state = DEAD;
                animationIndex = 0;
                animationTick = 0;
                playing.setPlayerDying(true);
                playing.getGame().getAudioPlayer().playEffect(AudioPlayer.DIE);
            } else if (animationIndex == GetSpriteAmount(DEAD) - 1 && animationTick >= ANI_SPEED - 1) {
                playing.setGameOver(true);
                playing.getGame().getAudioPlayer().stopSong();
                playing.getGame().getAudioPlayer().playEffect(AudioPlayer.GAMEOVER);


            } else {
                updateAnimationTick();
            }
            return;
        }
        updateAttackBox();

        updatePos();
        if (moving) {
            checkPotionTouched();
            checkSpikeTouched();
            tileY = (int) (hitBox.y / Game.TILES_SIZE);
            if (powerAttackActive) {
                powerAttackTick++;
                if (powerAttackTick >= 35) {
                    powerAttackTick = 0;
                    powerAttackActive = false;
                }
            }
        }
        if (attaking || powerAttackActive)
            checkAttack();
        updateAnimationTick();
        setAnimation();
    }

    private void updatePowerBar() {
        powerWidth = (int) ((powerValue / (float) powerMaxValue) * powerBarWidth);
        powerGrowTick++;
        if (powerGrowTick >= powerGrowSpeed) {
            powerGrowTick = 0;
            changePower(1);
        }
    }

    private void checkSpikeTouched() {
        playing.checkSpikeTouched(this);
    }

    private void checkPotionTouched() {
        playing.checkPotionTouched(hitBox);
    }

    private void checkAttack() {
        if (attackChecked || animationIndex != 1)
            return;
        attackChecked = true;

        if (powerAttackActive)
            attackChecked = false;
        playing.checkEnemyHit(attackBox);
        playing.checkObjectHit(attackBox);
        playing.getGame().getAudioPlayer().playAttackSound();

    }

    private void updateAttackBox() {
        if (right && left) {
            if (FlipW == 1) {
                attackBox.x = hitBox.x + hitBox.width + (int) (10 * Game.SCALE);
            } else {
                attackBox.x = hitBox.x - hitBox.width - (int) (10 * Game.SCALE);
            }
        } else if (right || (powerAttackActive && FlipW == 1)) {
            attackBox.x = hitBox.x + hitBox.width + (int) (10 * Game.SCALE);
        } else if (left || (powerAttackActive && FlipW == -1)) {
            attackBox.x = hitBox.x - hitBox.width - (int) (10 * Game.SCALE);
        }
        attackBox.y = hitBox.y + (10 * Game.SCALE);
    }

    private void updateHealthBar() {
        healthWidth = (int) ((currentHealth / (float) maxHealth) * healthBarWidth);
    }


    public void render(Graphics g, int xLvlOffset) {
        g.drawImage(animation[state][animationIndex], (int) (hitBox.x - xDrawOffset) - xLvlOffset + FlipX,
                (int) (hitBox.y - yDrawOffset), width * FlipW, height, null);

        drawUI(g);

    }

    public void setSpawn(Point spawn) {
        this.x = spawn.x;
        this.y = spawn.y;
        hitBox.x = spawn.x;
        hitBox.y = spawn.y;
    }


    private void drawUI(Graphics g) {
        g.drawImage(status_bar, statusBarX, statusBarY, statusBarWidth, statusBarHeight, null);

        g.setColor(Color.red);
        g.fillRect(healthBarX + statusBarX, healthBarY + statusBarY, healthWidth, healthBarHeight);

        g.setColor(Color.yellow);
        g.fillRect(powerBarX + statusBarX, powerBarY + statusBarY, powerWidth, powerBarHeight);
    }

    private void loadAnimations() {
        BufferedImage img = LoadSave.GetSpriteAtlas(LoadSave.PLAYER_ATLAS);
        animation = new BufferedImage[7][8];
        for (int j = 0; j < animation.length; j++) {
            for (int i = 0; i < animation[j].length; i++) {
                animation[j][i] = img.getSubimage(i * 64, j * 40, 64, 40);
            }
        }
        status_bar = LoadSave.GetSpriteAtlas(LoadSave.STAUS_BAR);
    }

    public void loadLvlData(int[][] lvlData) {
        this.lvlData = lvlData;
        if (!isEntityOnFloor(hitBox, lvlData)) {
            inAir = true;
        }
    }

    private void updateAnimationTick() {
        animationTick++;
        if (animationTick >= ANI_SPEED) {
            animationTick = 0;
            animationIndex++;
            if (animationIndex >= GetSpriteAmount(state)) {
                animationIndex = 0;
                attaking = false;
                attackChecked = false;
            }
        }
    }

    private void setAnimation() {
        int startAnimation = state;
        if (moving)
            state = RUNNING;
        else
            state = IDLE;
        if (inAir) {
            if (airSpeed < 0)
                state = JUMP;
            else
                state = FALLING;
        }
        if (powerAttackActive) {
            state = ATTACK;
            animationIndex = 1;
            animationTick = 0;
            return;
        }
        if (attaking) {
            state = ATTACK;
            if (startAnimation != ATTACK) {
                animationIndex = 1;
                animationTick = 0;
                return;
            }

        }
        if (startAnimation != state)
            resetAnimationTick();
    }

    public void resetAnimationTick() {
        animationIndex = 0;
        animationTick = 0;
    }

    public void setAttacking(boolean attaking) {
        this.attaking = attaking;
    }

    private void updatePos() {

        moving = false;
        if (jump) {
            jump();
        }
        if (!inAir)
            if (!powerAttackActive)
                if ((!left && !right) || (left && right))
                    return;
        float xSpeed = 0;

        if (left) {
            xSpeed -= walkSpeed;
            FlipX = width;
            FlipW = -1;
        }
        if (right) {
            xSpeed += walkSpeed;
            FlipX = 0;
            FlipW = 1;
        }
        if (powerAttackActive) {
            if ((!left && !right) || (left && right)) {
                if (FlipW == -1)
                    xSpeed = -walkSpeed;
                else
                    xSpeed = walkSpeed;

            }
            xSpeed *= 3;
        }
        if (!inAir) {
            if (!isEntityOnFloor(hitBox, lvlData)) {
                inAir = true;
            }
        }

        if (inAir && !powerAttackActive) {
            if (canMoveHere(hitBox.x, hitBox.y + airSpeed, hitBox.width, hitBox.height, lvlData)) {
                hitBox.y += airSpeed;
                airSpeed += GRAVITY;
                updateXPos(xSpeed);
            } else {
                hitBox.y = GetEntityYPosUnderRoofOrAboveFloor(hitBox, airSpeed);
                if (airSpeed > 0) {
                    resetInAin();
                } else {
                    airSpeed = fallSpeedAfterCollision;
                }
                updateXPos(xSpeed);
            }

        } else {
            updateXPos(xSpeed);
        }
        moving = true;
    }

    private void jump() {
        if (inAir)
            return;
        playing.getGame().getAudioPlayer().playEffect(AudioPlayer.JUMP);
        inAir = true;
        airSpeed = jumpSpeed;
    }

    private void resetInAin() {
        inAir = false;
        airSpeed = 0;
    }

    private void updateXPos(float xSpeed) {
        if (canMoveHere(hitBox.x + xSpeed, hitBox.y, hitBox.width, hitBox.height, lvlData)) {
            hitBox.x += xSpeed;
        } else {
            hitBox.x = GetEntityXPosNextToWall(hitBox, xSpeed);
            if (powerAttackActive) {
                powerAttackActive = false;
                powerAttackTick = 0;
            }
        }

    }

    public void changeHealth(int value) {
        currentHealth += value;
        if (currentHealth <= 0) {
            currentHealth = 0;
        } else if (currentHealth > maxHealth) {
            currentHealth = maxHealth;
        }
    }


    public boolean isLeft() {
        return left;
    }

    public void setLeft(boolean left) {
        this.left = left;
    }

    public boolean isRight() {
        return right;
    }

    public void setRight(boolean right) {
        this.right = right;
    }


    public void setJump(boolean jump) {
        this.jump = jump;
    }

    public void resetDirectionBoolean() {
        left = false;
        right = false;
    }

    public void resetAll() {
        resetDirectionBoolean();
        inAir = false;
        attaking = false;
        moving = false;
        airSpeed = 0f;
        state = IDLE;
        currentHealth = maxHealth;
        hitBox.x = x;
        hitBox.y = y;
        resetAttackBox();

        if (!isEntityOnFloor(hitBox, lvlData)) {
            inAir = true;
        }
    }

    private void resetAttackBox() {
        if (FlipW == 1) {
            attackBox.x = hitBox.x + hitBox.width + (int) (10 * Game.SCALE);
        } else {
            attackBox.x = hitBox.x - hitBox.width - (int) (10 * Game.SCALE);
        }
    }

    public void changePower(int bluePotionValue) {
        powerValue += bluePotionValue;
        if (powerValue >= powerMaxValue)
            powerValue = powerMaxValue;
        else if (powerValue < 0) {
            powerValue = 0;
        }
    }

    public void kill() {
        currentHealth = 0;

    }

    public int getTileY() {
        return tileY;
    }

    public void powerAttack() {
        if (powerAttackActive)
            return;
        if (powerValue >= 60) {
            powerAttackActive = true;
            changePower(-60);
        }
    }

}
